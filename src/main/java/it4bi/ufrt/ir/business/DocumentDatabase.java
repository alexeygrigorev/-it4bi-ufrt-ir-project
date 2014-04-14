package it4bi.ufrt.ir.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import it4bi.ufrt.ir.service.doc.DocumentRecord;


public class DocumentDatabase {
    
    private static List<DocumentRecord> documents = new ArrayList<DocumentRecord>();
    private static Map<String,Tag> tagDictionary = new HashMap<String,Tag>();
    private static Map<Pair<Integer,Integer>, Integer> tagsUsersTable = new HashMap<Pair<Integer,Integer>, Integer>();

	public static int currentTagDictionarySize() {
		return tagDictionary.size();
	}
	
	

	public static void insertTag(String tag) {
		
		if(tagDictionary.containsKey(tag)) return;
		else tagDictionary.put(tag, new Tag(tag));
		
	}
	
	public static void getTagId(String tag) {
		
		tagDictionary.get(tag).getTagId();
		
	}
	
	public static void insertTagUserValue(Integer tagId, Integer userId) {
		
		if(tagsUsersTable.containsKey(new ImmutablePair<Integer, Integer>(tagId, userId))) return;
		else tagsUsersTable.put(new ImmutablePair<Integer, Integer>(tagId, userId), 1);
		
	}
    
    
	public static List<DocumentRecord> getDocuments() {
		return documents;
	}
	
	public static void insertDoc(DocumentRecord doc) {
		documents.add(doc);
	}
}

