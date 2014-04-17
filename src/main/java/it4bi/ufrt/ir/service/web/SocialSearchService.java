package it4bi.ufrt.ir.service.web;

import it4bi.ufrt.ir.service.doc.DocumentRecord;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SocialSearchService {
	
	public List<SocialSearchRecord> search(String query,SocialSearchType type) throws SocialSearchException {
		
		return SocialMentionAPI.search(query, type).getRecords();
	}

}