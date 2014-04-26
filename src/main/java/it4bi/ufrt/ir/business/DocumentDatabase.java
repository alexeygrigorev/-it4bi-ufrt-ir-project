package it4bi.ufrt.ir.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import it4bi.ufrt.ir.service.doc.DOCUSER_ASSOC;
import it4bi.ufrt.ir.service.doc.DocumentRecord;
import it4bi.ufrt.ir.service.doc.Tag;


public class DocumentDatabase {
    
    private static final float MAX_SCORE = 5f;  // this will be hardcoded in DB, and will be a trigger that ensures no usertag score exceeds this limit
	private static Map<Integer, DocumentRecord> documents = new HashMap<Integer, DocumentRecord>();
    private static Map<String,Tag> tagDictionary = new HashMap<String,Tag>();
    private static Map<Pair<Integer,Integer>, Float> tagsUsersTable = new HashMap<Pair<Integer,Integer>, Float>();
    private static Map<Pair<Integer,Integer>, DOCUSER_ASSOC> docsUsersTable = new HashMap<Pair<Integer,Integer>, DOCUSER_ASSOC>();
	
    
    public static int currentTagDictionarySize() {
		return tagDictionary.size();
	}
	
	

	public static void insertTag(Tag tag) {
		
		if(tagDictionary.containsKey(tag)) return;
		else tagDictionary.put(tag.getTag(), tag);
		
	}
	
	
	
	public static Tag getTag(String tag) {
		return tagDictionary.get(tag);	
	}
	
	public static void updateTagUserValue(Integer tagId, Integer userId, float delta) {  // this will translate into an update query
		
		ImmutablePair<Integer, Integer> key = new ImmutablePair<Integer, Integer>(tagId, userId);
		
		if(!tagsUsersTable.containsKey(key))
			tagsUsersTable.put(new ImmutablePair<Integer, Integer>(tagId, userId), delta); 
		else {
			Float score = tagsUsersTable.get(key);
			float temp = score + delta;
			if(score + temp <= MAX_SCORE) score += temp;
					
		}
	}
    
    
	
	public static void insertDoc(DocumentRecord doc) {
		documents.put(doc.getDocId(),doc);
	}



	public static void insertDocUserValue(int docId, int userID,DOCUSER_ASSOC type) {
		docsUsersTable.put(new ImmutablePair<Integer, Integer>(docId, userID), type);
		
	}

	
	public static float calculateUserDocAffinity(List<Tag> tags, Integer userId) {  // can be written an SQL query for this
		
		Float score = 0f;
		
		for(Tag tag : tags) {
			ImmutablePair<Integer, Integer> key = new ImmutablePair<Integer, Integer>(tag.getTagId(), userId);
			if(tagsUsersTable.containsKey(key)) score += tagsUsersTable.remove(key);
					
		}
		
		return score/tags.size();
	}



	public static DocumentRecord getDocByDocId(int docId) {
		
		return documents.get(docId);
		
	}



	public static DOCUSER_ASSOC getUserDocAssociation(int docID, int userID) {
		
		ImmutablePair<Integer, Integer> key = new ImmutablePair<Integer, Integer>(docID, userID);
		return docsUsersTable.get(key);
	}
}

