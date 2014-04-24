// TODO: Uncomment
//package it4bi.ufrt.ir.service.spell;
//
//import java.io.File;
//
//import java.io.IOException;
//import java.io.StringReader;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//import org.apache.lucene.analysis.Token;
//import org.apache.lucene.analysis.standard.StandardTokenizer;
//import org.apache.lucene.search.spell.PlainTextDictionary;
//import org.apache.lucene.search.spell.SpellChecker;
//import org.apache.lucene.store.Directory;
//import org.apache.lucene.store.FSDirectory;
//import org.springframework.beans.factory.annotation.Value;
//
//import com.aliasi.spell.JaroWinklerDistance;
//
//public class FIFASpellChecker {
//
//	@Value("${dict.combined}")
//	private String COMBINED_DICT;
//
//	@Value("${dict.FIFA}")
//	private String FIFA_DICT;
//
//	private static FIFASpellChecker instance = null;
//	private File dir = null;
//	private File fifaDir = null;
//	private Directory directory = null;
//	private Directory fifaDirectory = null;
//	private SpellChecker spellChecker = null;
//	private PlainTextDictionary combinedDictionary;
//	private PlainTextDictionary fifaDictionary;
//	private SpellChecker fifaSpellChecker;
//
//	private FIFASpellChecker() throws IOException {
//		// Exists only to defeat instantiation.
//		dir = new File("\\combinedspellchecker5\\");
//		fifaDir = new File("\\fifaonlyspellchekcer2\\");
//		directory = FSDirectory.getDirectory(dir);
//		spellChecker = new SpellChecker(directory);
//
//		// use lucene embeded dictioary and DWH files
//		combinedDictionary = new PlainTextDictionary(new File(COMBINED_DICT));
//		fifaDictionary = new PlainTextDictionary(new File(FIFA_DICT));
//
//		System.out.println("Constructing..");
//		spellChecker.indexDictionary(combinedDictionary);
//
//		fifaDirectory = FSDirectory.getDirectory(fifaDir);
//		fifaSpellChecker = new SpellChecker(fifaDirectory);
//		fifaSpellChecker.indexDictionary(fifaDictionary);
//	}
//
//	public static FIFASpellChecker getInstance() throws IOException {
//
//		if (instance == null) {
//			instance = new FIFASpellChecker();
//
//		}
//		return instance;
//	}
//
//	/*
//	 * The main function to be used that auto correct the entire search query
//	 */
//	public QueryAutoCorrectionResult autoCorrectQuery(String searchQuery,
//			boolean suggest, int numberOfSuggestions) throws IOException {
//
//		String[] tokinz = tokenizeSentence(searchQuery);
//		String correctedQuery = searchQuery.toLowerCase();
//		boolean isCorrected = false;
//		List<SpellCheckerResult> wordCorrections = new ArrayList<SpellCheckerResult>();
//
//		SpellCheckerResult wr = null;
//		for (String tok : tokinz) {
//
//			wr = checkSpellingAndSuggest(tok, numberOfSuggestions, true);
//			if (wr.isCorrected()) {
//				// System.out.println(wr.toString());
//				// for each corrected word, replace the misspelled one by the
//				// suggested one
//				correctedQuery = correctedQuery.replaceAll(wr.getOriginalWord()
//						.toLowerCase(), wr.getCorrectedWord().toLowerCase());
//				wordCorrections.add(wr);
//				isCorrected = true;
//			}
//		}
//
//		// construct n query suggestions
//		String[] querySuggestions = null;
//		if (suggest && isCorrected) {
//			querySuggestions = new String[numberOfSuggestions];
//
//			for (int i = 0; i < numberOfSuggestions; i++) {
//
//				String sugstion = searchQuery.toLowerCase();
//				// loop on all mistakes and get index number i of their
//				// suggestions and replace it in the original query
//				for (SpellCheckerResult r : wordCorrections) {
//
//					if (r.getSuggestions() == null)
//						continue;
//
//					String suggestedWord = null;
//					if (r.getSuggestions().length > i)
//						suggestedWord = r.getSuggestions()[i];
//					else
//						suggestedWord = r.getSuggestions()[0];
//
//					sugstion = sugstion.replaceAll(r.getOriginalWord()
//							.toLowerCase(), suggestedWord.toLowerCase());
//				}
//
//				querySuggestions[i] = sugstion;
//
//			}
//		}
//
//		if (!isCorrected)
//			correctedQuery = null;
//
//		QueryAutoCorrectionResult qr = new QueryAutoCorrectionResult();
//		qr.setOriginalQuery(searchQuery);
//		qr.setCorrectedQuery(correctedQuery);
//		qr.setCorrected(isCorrected);
//		qr.setSuggestions(querySuggestions);
//
//		return qr;
//	}
//
//	public SpellCheckerResult checkSpellingAndSuggest(String word,
//			int suggestionsNumber, boolean ingnoreCase) {
//
//		try {
//
//			if (ingnoreCase)
//				word = word.toLowerCase();
//
//			boolean exist = spellChecker.exist(word);
//			boolean isCorrected = false;
//
//			String[] suggestions = null;
//			String correction = null;
//
//			if (!exist) {
//
//				if (suggestionsNumber <= 0)
//					suggestionsNumber = 1;
//
//				suggestions = spellChecker.suggestSimilar(word,
//						suggestionsNumber);
//				// custom rank the suggestions
//				suggestions = rankWords(word, suggestions);
//
//				if (suggestions.length > 0) {
//					correction = suggestions[0];
//					isCorrected = true;
//				} else {
//					isCorrected = false;
//				}
//
//			}
//
//			SpellCheckerResult r = new SpellCheckerResult();
//			r.setOriginalWord(word);
//			r.setFound(exist);
//			r.setCorrected(isCorrected);
//			r.setCorrectedWord(correction);
//			r.setSuggestions(suggestions);
//
//			return r;
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//
//		return null;
//	}
//
//	/*
//	 * will sort the similar words (suggestions) compared to the original one
//	 */
//	public String[] rankWords(String word, String[] similarWords)
//			throws IOException {
//
//		// ideas, rank based on JARO_WINKLER_DISTANCE
//		// or based on existence in our custom DWH dictionary plus matches vocab
//		// (winner, loser, final..etc)
//		JaroWinklerDistance jaroWinkler = JaroWinklerDistance.JARO_WINKLER_DISTANCE;
//		ArrayList<RankedString> rankedSuggestions = new ArrayList<RankedString>();
//		for (String s : similarWords) {
//			double score = jaroWinkler.proximity(word, s);
//			// if the word is from fifa dictioanry give it higher rank
//			if (fifaSpellChecker.exist(s.toLowerCase())) {
//				score = score + 1;
//			}
//
//			rankedSuggestions.add(new RankedString(s, score));
//		}
//
//		Collections.sort(rankedSuggestions);
//
//		ArrayList<String> rankedStrings = new ArrayList<String>();
//		for (RankedString s : rankedSuggestions)
//			rankedStrings.add(s.getWord());
//
//		return rankedStrings.toArray(similarWords);
//	}
//
//	public String[] tokenizeSentence(String sentence) throws IOException {
//
//		StringReader reader = new StringReader(sentence);
//		StandardTokenizer tokenizer = new StandardTokenizer(reader);
//
//		List<String> tokinz = new ArrayList<String>();
//
//		Token t = tokenizer.next();
//		boolean hasNext = true ? t != null : false;
//		while (hasNext) {
//			tokinz.add(String.copyValueOf(t.termBuffer()).trim());
//			t = tokenizer.next();
//			hasNext = true ? t != null : false;
//		}
//
//		return tokinz.toArray(new String[tokinz.size()]);
//	}
//}
