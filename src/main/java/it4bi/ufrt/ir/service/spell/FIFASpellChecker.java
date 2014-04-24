package it4bi.ufrt.ir.service.spell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.aliasi.spell.JaroWinklerDistance;
import com.google.common.base.Throwables;

@Component
public class FIFASpellChecker {

	private static final Version VERSION = Version.LUCENE_47;
	private static final Logger LOGGER = LoggerFactory.getLogger(FIFASpellChecker.class);

	@Value("${spellchecker.dict.index.combined}")
	private File combinedDictLuceneIndex;

	@Value("${spellchecker.dict.index.fifa}")
	private File fifaDictLuceneIndex;

	private SpellChecker spellChecker = null;
	private SpellChecker fifaSpellChecker = null;

	@PostConstruct
	public void init() throws IOException {
		Analyzer analyzer = new EnglishAnalyzer(VERSION);

		createIndexFoldersIfNeeded();
		
		String combinedDictSource = "./Dictionaries/Combined_Dict_ANSI.txt";
		spellChecker = initSpellChecker(analyzer, combinedDictSource, combinedDictLuceneIndex);

		String fifaDictSource = "./Dictionaries/FIFA_DWH_Dictionary_ANSI.txt";
		fifaSpellChecker = initSpellChecker(analyzer, fifaDictSource, fifaDictLuceneIndex);
	}

	private void createIndexFoldersIfNeeded() {
		if (!combinedDictLuceneIndex.exists()) {
			combinedDictLuceneIndex.mkdirs();
		}

		if (!fifaDictLuceneIndex.exists()) {
			fifaDictLuceneIndex.mkdirs();
		}
	}

	private static SpellChecker initSpellChecker(Analyzer analyzer, String source, File luceneIndexDir)
			throws UnsupportedEncodingException, FileNotFoundException, IOException {
		LOGGER.info("Constructing dictionary from {}...", source);
		IndexWriterConfig config = new IndexWriterConfig(VERSION, analyzer);
		
		PlainTextDictionary fifaDictionary = new PlainTextDictionary(utfFileReader(source));
		
		FSDirectory directory = FSDirectory.open(luceneIndexDir);
		SpellChecker spellChecker = new SpellChecker(directory);
		spellChecker.indexDictionary(fifaDictionary, config, false);

		LOGGER.debug("Done constructing dictionary from {}...", source);
		return spellChecker;
	}

	private static InputStreamReader utfFileReader(String combinedDictSource) throws UnsupportedEncodingException,
			FileNotFoundException {
		File file = new File(combinedDictSource);
		return new InputStreamReader(new FileInputStream(file), "UTF-8");
	}

	/*
	 * The main function to be used that auto correct the entire search query
	 */
	public QueryAutoCorrectionResult autoCorrectQuery(String searchQuery,
			boolean suggest, int numberOfSuggestions) {

		List<String> tokinz = tokenizeSentence(searchQuery);
		String correctedQuery = searchQuery.toLowerCase();
		boolean isCorrected = false;
		List<SpellCheckerResult> wordCorrections = new ArrayList<SpellCheckerResult>();

		SpellCheckerResult wr = null;
		for (String tok : tokinz) {

			wr = checkSpellingAndSuggest(tok, numberOfSuggestions, true);
			if (wr.isCorrected()) {
				// System.out.println(wr.toString());
				// for each corrected word, replace the misspelled one by the
				// suggested one
				correctedQuery = correctedQuery.replaceAll(wr.getOriginalWord()
						.toLowerCase(), wr.getCorrectedWord().toLowerCase());
				wordCorrections.add(wr);
				isCorrected = true;
			}
		}

		// construct n query suggestions
		String[] querySuggestions = null;
		if (suggest && isCorrected) {
			querySuggestions = new String[numberOfSuggestions];

			for (int i = 0; i < numberOfSuggestions; i++) {

				String sugstion = searchQuery.toLowerCase();
				// loop on all mistakes and get index number i of their
				// suggestions and replace it in the original query
				for (SpellCheckerResult r : wordCorrections) {

					if (r.getSuggestions() == null)
						continue;

					String suggestedWord = null;
					if (r.getSuggestions().length > i)
						suggestedWord = r.getSuggestions()[i];
					else
						suggestedWord = r.getSuggestions()[0];

					sugstion = sugstion.replaceAll(r.getOriginalWord()
							.toLowerCase(), suggestedWord.toLowerCase());
				}

				querySuggestions[i] = sugstion;

			}
		}

		if (!isCorrected)
			correctedQuery = null;

		QueryAutoCorrectionResult qr = new QueryAutoCorrectionResult();
		qr.setOriginalQuery(searchQuery);
		qr.setCorrectedQuery(correctedQuery);
		qr.setCorrected(isCorrected);
		qr.setSuggestions(querySuggestions);

		return qr;
	}

	public SpellCheckerResult checkSpellingAndSuggest(String word,
			int suggestionsNumber, boolean ingnoreCase) {

		try {

			if (ingnoreCase)
				word = word.toLowerCase();

			boolean exist = spellChecker.exist(word);
			boolean isCorrected = false;

			String[] suggestions = null;
			String correction = null;

			if (!exist) {

				if (suggestionsNumber <= 0)
					suggestionsNumber = 1;

				suggestions = spellChecker.suggestSimilar(word,
						suggestionsNumber);
				// custom rank the suggestions
				suggestions = rankWords(word, suggestions);

				if (suggestions.length > 0) {
					correction = suggestions[0];
					isCorrected = true;
				} else {
					isCorrected = false;
				}

			}

			SpellCheckerResult r = new SpellCheckerResult();
			r.setOriginalWord(word);
			r.setFound(exist);
			r.setCorrected(isCorrected);
			r.setCorrectedWord(correction);
			r.setSuggestions(suggestions);

			return r;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	/*
	 * will sort the similar words (suggestions) compared to the original one
	 */
	public String[] rankWords(String word, String[] similarWords)
			throws IOException {

		// ideas, rank based on JARO_WINKLER_DISTANCE
		// or based on existence in our custom DWH dictionary plus matches vocab
		// (winner, loser, final..etc)
		JaroWinklerDistance jaroWinkler = JaroWinklerDistance.JARO_WINKLER_DISTANCE;
		ArrayList<RankedString> rankedSuggestions = new ArrayList<RankedString>();
		for (String s : similarWords) {
			double score = jaroWinkler.proximity(word, s);
			// if the word is from fifa dictioanry give it higher rank
			if (fifaSpellChecker.exist(s.toLowerCase())) {
				score = score + 1;
			}

			rankedSuggestions.add(new RankedString(s, score));
		}

		Collections.sort(rankedSuggestions);

		ArrayList<String> rankedStrings = new ArrayList<String>();
		for (RankedString s : rankedSuggestions)
			rankedStrings.add(s.getWord());

		return rankedStrings.toArray(similarWords);
	}

	public static List<String> tokenizeSentence(String sentence) {
		try {
			return tokenizeInner(sentence);
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	private static List<String> tokenizeInner(String sentence) throws IOException {
		StringReader reader = new StringReader(sentence);
		try (TokenStream stream = new StandardTokenizer(Version.LUCENE_47, reader)) {
			stream.reset();
			List<String> tokens = new ArrayList<String>();

			while (stream.incrementToken()) {
				String term = stream.getAttribute(CharTermAttribute.class).toString();
				tokens.add(term);
			}
			stream.end();
			return tokens;
		}
	}

	void setCombinedDictLuceneIndex(File combinedDictLuceneIndex) {
		this.combinedDictLuceneIndex = combinedDictLuceneIndex;
	}

	void setFifaDictLuceneIndex(File fifaDictLuceneIndex) {
		this.fifaDictLuceneIndex = fifaDictLuceneIndex;
	}
}
