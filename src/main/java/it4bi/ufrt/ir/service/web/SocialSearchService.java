package it4bi.ufrt.ir.service.web;

import it4bi.ufrt.ir.service.spell.FIFASpellChecker;
import it4bi.ufrt.ir.service.spell.QueryAutoCorrectionResult;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class SocialSearchService {
	
	public List<SocialSearchRecord> search(String query, SocialSearchType type, boolean calcSentiment,int maxCount ) throws SocialSearchException {	
		
		try{
			
		String correctedQuery = "";
		FIFASpellChecker s = FIFASpellChecker.getInstance();
		 QueryAutoCorrectionResult qr = s.autoCorrectQuery(query, true, 3);
		 
		 if(qr.isCorrected())
			correctedQuery = qr.getCorrectedQuery();
		 else
			 correctedQuery = query;
		
		
		List<SocialSearchRecord> recs = SocialMentionAPI.search(correctedQuery, type).getRecords();
		
		if(maxCount<recs.size() && maxCount >0)
			recs = recs.subList(0, maxCount);
		
		if(calcSentiment){
		String [] sentimentResults = null;
		String sentimentField = null;
		for(SocialSearchRecord rec: recs){
			
			sentimentField = rec.getTitle();
			sentimentResults = TextalyticsAPI.calculateSentiment(sentimentField);
			//[0] code, [1] status description, [2] sentiment

			System.out.println("Sentiment: "+ sentimentResults[2]);
			if(!sentimentResults[0].equalsIgnoreCase("0"))
				throw new SocialSearchException("Sentiment Analysis API status code is no OK. Retruned code "+sentimentResults[0]+": "+sentimentResults[1]);
		
			rec.setSentiment(getHighlevelSentiment(sentimentResults[2]));
		}
		
		}
		
		return recs;
		}catch(Exception ex){
			throw new SocialSearchException(ex.getMessage());
		}
	}
	
	
	 private static String getHighlevelSentiment(String apiSentiment){
		  
		  String pos = "Positive";
		  String neg = "Negative";
		  String neut = "Neutral";
		  String lcaseSentiment = apiSentiment.toLowerCase().trim();
		  switch (lcaseSentiment){
		  case "p+": return pos;
		  case "p": return pos;
		  case "neu": return neut;
		  case "n": return neg;
		  case "n+": return neg;
		  default: return "undefined sentiment";  
		  }
		  
	  }
}