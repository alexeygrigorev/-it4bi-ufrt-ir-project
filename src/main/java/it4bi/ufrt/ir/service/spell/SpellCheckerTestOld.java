package it4bi.ufrt.ir.service.spell;

import java.io.File;
import java.io.IOException;

public class SpellCheckerTestOld {

	public static void main(String[] args) throws IOException {
		// Exists only to defeat instantiation.

		FIFASpellChecker spellChecker = new FIFASpellChecker();
		spellChecker.setCombinedDictLuceneIndex(new File("C:/IRProject/CombinedSpellCheckerIndex/"));
		spellChecker.setFifaDictLuceneIndex(new File("C:/IRProject/FifaSpellCheckerIndex/"));
		spellChecker.init();

		/*
		 * String word = "Playe"; SpellCheckerResult r =
		 * s.checkSpellingAndSuggest(word, 3,true); System.out.println(
		 * r.toString()); System.out.println("done! "+ word);
		 */

		String test = "This is a tst. How about that?! Huh?";
		test = "barzil defende ronaldio";
		QueryAutoCorrectionResult qr = spellChecker.autoCorrectQuery(test, true, 3);
		System.out.println(qr.toString());
	}
}
