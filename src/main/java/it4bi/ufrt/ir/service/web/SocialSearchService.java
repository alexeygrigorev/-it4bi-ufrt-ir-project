package it4bi.ufrt.ir.service.web;

import it4bi.ufrt.ir.service.spell.FIFASpellChecker;
import it4bi.ufrt.ir.service.spell.QueryAutoCorrectionResult;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SocialSearchService {
	
	@Autowired
	FIFASpellChecker s;

	public List<SocialSearchRecord> search(String query, SocialSearchType type,
			boolean calcSentiment, int maxCount) throws SocialSearchException {

		try {

			String correctedQuery = "";
			QueryAutoCorrectionResult qr = s.autoCorrectQuery(query, true, 3);			

			correctedQuery = query;			
			if (qr.getIsCorrected()) {
				correctedQuery = qr.getCorrectedQuery();
			}

			List<SocialSearchRecord> recs = SocialMentionAPI.search(correctedQuery, type).getRecords();

			if (maxCount < recs.size() && maxCount > 0) {
				recs = recs.subList(0, maxCount);
			}

			if (calcSentiment) {
				calculateSentiment(recs);
			}

			return recs;
		} catch (Exception ex) {
			throw new SocialSearchException(ex.getMessage());
		}
	}

	private static void calculateSentiment(List<SocialSearchRecord> recs)
			throws SocialSearchException {
		String[] sentimentResults = null;
		String sentimentField = null;
		for (SocialSearchRecord rec : recs) {

			sentimentField = rec.getTitle();
			if (sentimentField.equals("")) {
				sentimentField = rec.getDescription();
			}

			if (sentimentField.equals("")) {
				rec.setSentiment(getHighlevelSentiment("undefined"));
				continue;
			}

			sentimentResults = TextalyticsAPI
					.calculateSentiment(sentimentField);
			// [0] code, [1] status description, [2] sentiment

			System.out.println("Sentiment: " + sentimentResults[2]);
			if (!sentimentResults[0].equalsIgnoreCase("0"))
				throw new SocialSearchException(
						"Sentiment Analysis API status code is no OK. Returned code "
								+ sentimentResults[0] + ": "
								+ sentimentResults[1]);

			rec.setSentiment(getHighlevelSentiment(sentimentResults[2]));
		}
	}

	private static String getHighlevelSentiment(String apiSentiment) {

		String pos = "Positive";
		String neg = "Negative";
		String neut = "Neutral";
		String lcaseSentiment = apiSentiment.toLowerCase().trim();
		switch (lcaseSentiment) {
		case "p+":
			return pos;
		case "p":
			return pos;
		case "neu":
			return neut;
		case "n":
			return neg;
		case "n+":
			return neg;
		default:
			return "undefined sentiment";
		}
	}
}