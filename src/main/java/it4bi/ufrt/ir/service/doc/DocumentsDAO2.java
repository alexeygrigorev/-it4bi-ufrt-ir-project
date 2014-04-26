package it4bi.ufrt.ir.service.doc;

import it4bi.ufrt.ir.service.users.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jersey.repackaged.com.google.common.collect.ImmutableMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.ParameterMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
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
			return null;
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
			
			return null;
		}
		
	}
	
	
	
	//public void insertUserDocAssociation(int)
	
	public void insertDocumentRecord(DocumentRecord documentRecord) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("docTitle", documentRecord.getDocTitle());
		parameters.put("docPath", documentRecord.getDocPath());
		parameters.put("uploaderID", new Integer(documentRecord.getUploaderId()));
		parameters.put("mime", documentRecord.getMime());
		
		this.jdbcTemplate.update(
				"insert into Documents (docTitle, docPath, uploaderID, mime) values (:docTitle, docPath, :uploaderID, :mime)", parameters);
		
		
		
		for(Tag tag : documentRecord.getTags()) {
			Tag curTag = getTagByTagText(tag.tag);
			if(curTag == null) {  // means tag doesn't exist in the db, inserting tags
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("tagText", tag.getTag());
				
				this.jdbcTemplate.update(
						"insert into Tags (tagText) values (:tagText) ", params);
				
				curTag = getTagByTagText(tag.tag);
			}
			
			// now curTag cannot be null
			// updating TagsDocs association table
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("docID", documentRecord.getDocId());
			params.put("tagID", curTag.getTagId());
			
			this.jdbcTemplate.update(
					"insert into TagDocs values (:docID, :tagID)", params);
			
		}
		
		
		
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
		
		Tag tag = (Tag) this.jdbcTemplate.queryForObject(
				"select * from Tags where tagText = :tagText", parameters, new TagRowMapper());
		
		return tag;
	}
	
	public List<Tag> getTagsByDocID(int docID) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("docID", new Integer(docID));
		
		
		List<Tag> tags = new ArrayList<Tag>();
		List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(
				"select (tagText, tagID) from TagDocs,Tags where TagDocs.tagID = Tags.tagID and TagDocs.docID = :docID",
				parameters);
		
		for(Map row : rows) {
			Tag tag = new Tag();
			tag.setTag(String.valueOf(row.get("tagText")));
			tag.setTagId(Integer.parseInt(String.valueOf(row.get("tagID"))));
			tags.add(tag);
		}
				
		return tags;
	}
	
	
}
