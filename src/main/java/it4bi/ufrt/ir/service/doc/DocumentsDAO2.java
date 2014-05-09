package it4bi.ufrt.ir.service.doc;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.lucene.search.ScoreDoc;
import org.apache.mahout.clustering.spectral.AffinityMatrixInputJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;



@Repository
public class DocumentsDAO2 {

	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	

	
	
	@Autowired
	public DocumentsDAO2(@Qualifier("appJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	
	private class TagRowMapper implements RowMapper {

		@Override
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			Tag tag = new Tag();
			tag.setTag(rs.getString("tagText"));
			tag.setTagId(Integer.parseInt(rs.getString("tagID")));
			return tag;
		}
		
	}
	
	
	
	private class DocumentRecordRowMapper implements RowMapper {
		public int docID;
		
		public DocumentRecordRowMapper(int docID) {
			this.docID = docID;
		}

		@Override
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			DocumentRecord docRecord = new DocumentRecord();
			docRecord.setDocId(rs.getInt("docID"));
			docRecord.setDocPath(rs.getString("docPath"));
			docRecord.setDocTitle(rs.getString("docTitle"));
			
			List<Tag> tags = getTagsByDocID(docID);
			
			docRecord.setTags(tags);
			
			return docRecord;
		}
		
	}
	
	

	public DOCUSER_ASSOC_TYPE getUserDocAssociation(int docID, int userID) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("docID", new Integer(docID));
		parameters.put("userID", new Integer(userID));
		
		DOCUSER_ASSOC_TYPE assoc_type = null;
		try {
			assoc_type = this.jdbcTemplate.queryForObject(
					"select * from UserDocs where userID = :userID and docID = :docID", parameters, new RowMapper<DOCUSER_ASSOC_TYPE>() {

						@Override
						public DOCUSER_ASSOC_TYPE mapRow(ResultSet rs, int rowNum) throws SQLException {
							
							if(rs.getString("isOwned").equals("1")) return DOCUSER_ASSOC_TYPE.OWNS;
							else if(rs.getString("isLiked").equals("1")) return DOCUSER_ASSOC_TYPE.LIKES;
							else return null;// shouldn't happen
						}
					});
		}
		catch (EmptyResultDataAccessException e) {
			return null;
		}
		
		return assoc_type;
	}
	
	
	public void updateUserTagsScores(int userID, List<Tag> tags, float deltaScore) {
		
		StringBuilder query = new StringBuilder();
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userID", userID);
		params.put("deltaScore", deltaScore);
		
		
		for(int ctr = 0; ctr < tags.size(); ctr++) {
			String tagText = tags.get(ctr).getTag();
			query.append("exec updateUserTagScore :userID, :deltaScore, '" + tagText + "'\n");
		}
		
		
		this.jdbcTemplate.update(query.toString(), params);
	
	}
	
	public float getUserTagScore(int userID, int tagID) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("tagID", tagID);
		parameters.put("userID", userID);
		
		float score = 0f;
		try {
			score = this.jdbcTemplate.queryForObject(
					"select score from UserTags where userID = :userID and tagID = :tagID", parameters, Float.class);
		}
		catch (EmptyResultDataAccessException e) {
			return score; 
		}
		
		return score;
	}
	
	public void updateUserTagScore(int userID, int tagID, float deltaScore) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("userID", userID);
		parameters.put("tagID", tagID);
		parameters.put("deltaScore", deltaScore);
		
		this.jdbcTemplate.update(
				"if not exists (select * from UserTags where userID = :userID and tagID = :tagID)"
				+ "insert into UserTags values (:userID, :tagID, :deltaScore)"
				+ "else update UserTags set score = score + :deltaScore where userID = :userID and tagID = :tagID", parameters);
		
	}
	
	public void insertUserDocAssociation(int docID, int userID, DOCUSER_ASSOC_TYPE assocType) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("docID", new Integer(docID));
		parameters.put("userID", new Integer(userID));
		if(assocType.equals(DOCUSER_ASSOC_TYPE.LIKES)) {
			parameters.put("isLiked", new Boolean(true));
			parameters.put("isOwned", new Boolean(false));
		}
		else if(assocType.equals(DOCUSER_ASSOC_TYPE.OWNS)) {
			parameters.put("isLiked", new Boolean(false));
			parameters.put("isOwned", new Boolean(true));
		}
		
		this.jdbcTemplate.update(
				"insert into UserDocs values (:docID, :userID, :isOwned, :isLiked)", parameters);
		
	}
	
	public void insertDocumentRecord(DocumentRecord documentRecord) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("docTitle", documentRecord.getDocTitle());
		parameters.put("docPath", documentRecord.getDocPath());
		parameters.put("uploaderID", new Integer(documentRecord.getUploaderId()));
		parameters.put("mime", documentRecord.getMime());
		
		this.jdbcTemplate.update(
				"insert into Documents (docTitle, docPath, uploaderID, mime) values (:docTitle, :docPath, :uploaderID, :mime)", parameters);
		
		//Integer docID = this.jdbcTemplate.queryForObject("select SCOPE_IDENTITY()", parameters, Integer.class);  // this doesn't work probably due to multi-threading facilities of Spring..
		Integer docID = this.jdbcTemplate.queryForObject("select IDENT_CURRENT('Documents')", parameters, Integer.class);
		
		//System.out.println(docID);
		documentRecord.setDocId(docID);
		
	}

	public DocumentRecord getDocByID(int docID) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("docID", new Integer(docID));
		
		
		DocumentRecord docRecord = (DocumentRecord) this.jdbcTemplate.queryForObject(
				"select * from Documents where docID = :docID", parameters, new DocumentRecordRowMapper(docID));
		
		return docRecord;
	}
		
	public Tag getTagByTagText(String tagText) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("tagText", tagText);
		
		Tag tag = null;
		try {
			tag = (Tag) this.jdbcTemplate.queryForObject(
					"select * from Tags where tagText = :tagText", parameters, new TagRowMapper());
		}
		catch (EmptyResultDataAccessException e) {
			return null;
		}
		
		return tag;
	}
	
	public List<Tag> getTagsByDocID(int docID) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("docID", new Integer(docID));
		
		
		List<Tag> tags = new ArrayList<Tag>();
		List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(
				"select tagText, Tags.tagID from DocTags,Tags where DocTags.tagID = Tags.tagID and DocTags.docID = :docID",
				parameters);
		
		for(Map row : rows) {
			Tag tag = new Tag();
			tag.setTag(String.valueOf(row.get("tagText")));
			tag.setTagId(Integer.parseInt(String.valueOf(row.get("tagID"))));
			tags.add(tag);
		}
				
		return tags;
	}

	public void updateTags(List<Tag> tags, int docID) {
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("docID", docID);
		
		int ctr = 0;
		for(Tag tag : tags) {
			params.put("tag" + ++ctr, tag.getTag());
		}
		
		
		this.jdbcTemplate.update("updateDocTags :docID, :tag1, :tag2, :tag3, :tag4", params);
	}
	
	public void updateTags_slow(List<Tag> tags, int docID) {
		
		for(Tag tag : tags) {
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("tagText", tag.getTag());
					
			Integer tagID = this.jdbcTemplate.queryForObject(
						"if not exists (select * from Tags where tagText = :tagText)"
						+ " insert into Tags (tagText) values (:tagText); select tagID from Tags where tagText = :tagText", params, Integer.class);
					
			
			//Integer tagID = this.jdbcTemplate.queryForObject("select IDENT_CURRENT('Tags')", params, Integer.class); 
			tag.setTagId(tagID);
			
			// now curTag cannot be null
			// updating TagsDocs association table
					
			params = new HashMap<String, Object>();
			params.put("docID", docID);
			params.put("tagID", tagID);
					
			this.jdbcTemplate.update("insert into DocTags values (:docID, :tagID)", params);
		
		}
			
	}
	
	//needed for a secondary functionality
	public void getUploadedDocs(int userID) {
		
	}


	public int getTagCount() {
		
		return this.jdbcTemplate.queryForObject("select count(*) from Tags", new HashMap<String, Object>(), Integer.class);
		
	}


	public int getUserCount() {
		return this.jdbcTemplate.queryForObject("select count(*) from Documents", new HashMap<String, Object>(), Integer.class);
		
	}


	public float getUserDocAffinity(int userID, int docID) { // HERE WE HAVE PERFORMANCE LEAK!
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("docID", docID);
		params.put("userID", userID);
		
		Float affinity = this.jdbcTemplate.queryForObject("select avg(score) from Documents doc, DocTags doctags, UserTags usertags where doc.docID = :docID and doc.docID = doctags.docID and doctags.tagID = usertags.tagID and usertags.userID = :userID", params, Float.class);
		
		if(affinity == null) affinity = 0f;
		return affinity/5f;
		
	}


	public List<Long> getAllUserIDs() {
		
		List<Map<String, Object>> rows;
		List<Long> userIDs = new ArrayList<Long>();
		
		rows = this.jdbcTemplate.queryForList("select userID from Users", new HashMap<String, Object>());
		
		
		for(Map<String, Object> row : rows) {
			//Integer userID = Integer.parseInt(String.valueOf(row.get("userID")));
			Integer userID = (Integer) (row.get("userID"));
			userIDs.add((long) userID);
		}
		
		return userIDs;
	}


	public List<ImmutablePair<Integer, Float>> getTagScoresByUserID(int userID) {
		
		List<Map<String, Object>> rows;
		List<ImmutablePair<Integer, Float>> tagScores = new ArrayList<ImmutablePair<Integer, Float>>();
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userID", userID);
		
		rows = this.jdbcTemplate.queryForList("select tagID, score from UserTags where userID = :userID", params);
		
		for(Map<String, Object> row : rows) {
			Integer tagID = (Integer) row.get("tagID");
			Float score = (Float) row.get("score");
			
			ImmutablePair<Integer, Float> tagScore = new ImmutablePair<>(tagID, score);
			tagScores.add(tagScore);
		}
		
		return tagScores;
		
	}


	public List<Long> getAllTagIDs() {
		List<Map<String, Object>> rows;
		List<Long> tagIDs = new ArrayList<Long>();
		
		rows = this.jdbcTemplate.queryForList("select tagID from Tags", new HashMap<String, Object>());
		
		
		for(Map<String, Object> row : rows) {
			//Integer userID = Integer.parseInt(String.valueOf(row.get("userID")));
			Integer tagID = (Integer) (row.get("tagID"));
			tagIDs.add((long) tagID);
		}
		
		return tagIDs;
		
	}


	public List<ImmutablePair<Integer, Float>> getAllTagScoresByTagID(int tagID) {
		
		List<Map<String, Object>> rows;
		List<ImmutablePair<Integer, Float>> tagScores = new ArrayList<ImmutablePair<Integer, Float>>();
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("tagID", tagID);
		
		rows = this.jdbcTemplate.queryForList("select userID, score from UserTags where tagID = :tagID", params);
		
		for(Map<String, Object> row : rows) {
			Integer userID = (Integer) row.get("userID");
			Float score = (Float) row.get("score");
			
			ImmutablePair<Integer, Float> tagScore = new ImmutablePair<>(userID, score);
			tagScores.add(tagScore);
		}
		
		return tagScores;
	}



	public int getNumUsersRankedACertainTag(int tagID) {
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("tagID", tagID);
		
		return this.jdbcTemplate.queryForObject("select count(userID) from UserTags where tagID = :tagID", params, Integer.class);
	}


	public int getNumUsersRankedACertainTagPair(int tagID1, int tagID2) {
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("tagID1", tagID1);
		params.put("tagID2", tagID2);
		
		return this.jdbcTemplate.queryForObject("select count(userID) from UserTags u1, UserTags u2 where u1.tagID = :tagID1 and u2.tagID = :tagID2 and u1.userID = u2.userID", params, Integer.class);
		
	}


	public void removeUserTagScore(int userID, int tagID) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("userID", userID);
		parameters.put("tagID", tagID);
		
		this.jdbcTemplate.update(
				"if exists (select * from UserTags where userID = :userID and tagID = :tagID)"
				+ "delete from UserTags where userID = :userID and tagID = :tagID", parameters);
		
		
	}


	public List<DocUserAssociation> getUserDocAssociationSummary(int userID, ScoreDoc[] hits) {
		
		List<DocUserAssociation> docUserAssociationList = new ArrayList<DocUserAssociation>();
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userID", userID);
		
		for(int ctr = 0; ctr < hits.length; ctr++) {
			params.put("docID", hits[ctr].doc);
			DocUserAssociation doc_user_assoc = this.jdbcTemplate.queryForObject("getUserDocAssoc :userID, :docID", params, new RowMapper<DocUserAssociation>() {

				@Override
				public DocUserAssociation mapRow(ResultSet rs, int rowNum) throws SQLException {
					
					String aff_string = rs.getString("affinity");
					Float affinity = null;
					if(aff_string == null) affinity = 0f;
					else affinity = Float.parseFloat(rs.getString("affinity"));
					String assoc_type_string = rs.getString("assoc_type");
					DOCUSER_ASSOC_TYPE assoc_type = null;
					if(assoc_type_string.equals("Likes")) assoc_type = DOCUSER_ASSOC_TYPE.LIKES;
					else if(assoc_type_string.equals("Owns")) assoc_type = DOCUSER_ASSOC_TYPE.OWNS;
					
					return new DocUserAssociation(assoc_type, affinity);
				}
			});
			
			docUserAssociationList.add(doc_user_assoc);
			
		}
		
		return docUserAssociationList;
	}
}
