package it4bi.ufrt.ir.service.web;




import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SocialSearchService {
	
	@Autowired
	public List<SocialSearchRecord> search(String query,SocialSearchType type) throws SocialSearchException {
		
		return SocialMentionAPI.search(query, type).getRecords();
	}

}