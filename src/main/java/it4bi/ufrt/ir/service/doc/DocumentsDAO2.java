package it4bi.ufrt.ir.service.doc;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



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
	
	

	public DOCUSER_ASSOC getUserDocAssociation(int docID, int userID) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("docID", new Integer(docID));
		parameters.put("userID", new Integer(userID));
		
		DOCUSER_ASSOC assoc_type = null;
		try {
			assoc_type = this.jdbcTemplate.queryForObject(
					"select * from UserDocs where userID = :userID and docID = :docID", parameters, new RowMapper<DOCUSER_ASSOC>() {

						@Override
						public DOCUSER_ASSOC mapRow(ResultSet rs, int rowNum) throws SQLException {
							
							if(rs.getString("isOwned").equals("1")) return DOCUSER_ASSOC.OWNS;
							else if(rs.getString("isLiked").equals("1")) return DOCUSER_ASSOC.LIKES;
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
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userID", userID);
		params.put("deltaScore", deltaScore);
		
		int ctr = 0;
		for(Tag tag : tags) {
			params.put("tag" + ++ctr, tag.getTag());
		}
		
		this.jdbcTemplate.update("updateUserTagScores :userID, :deltaScore, :tag1, :tag2, :tag3, :tag4", params);
	
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
	
	public void updateUserTagScore(int userID, String tagID, float deltaScore) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("userID", userID);
		parameters.put("tagID", tagID);
		parameters.put("deltaScore", deltaScore);
		
		this.jdbcTemplate.update(
				"if not exists (select * from UserTags where userID = :userID and tagID = :tagID)"
				+ "insert into UserTags values (:userID, :tagID, :deltaScore)"
				+ "else update UserTags set score = score + :deltaScore where userID = :userID and tagID = :tagID", parameters);
		
	}
	
	public void insertUserDocAssociation(int docID, int userID, DOCUSER_ASSOC assocType) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("docID", new Integer(docID));
		parameters.put("userID", new Integer(userID));
		if(assocType.equals(DOCUSER_ASSOC.LIKES)) {
			parameters.put("isLiked", new Boolean(true));
			parameters.put("isOwned", new Boolean(false));
		}
		else if(assocType.equals(DOCUSER_ASSOC.OWNS)) {
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
}
