package it4bi.ufrt.ir.service.spell;

import java.io.StringReader;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.standard.StandardTokenizer;


import com.aliasi.spell.JaroWinklerDistance;


public class SpellCheckerTest {

	public static void main(String[] args) {
		try{
		
	
		FIFASpellChecker s = FIFASpellChecker.getInstance();
		
		/*
		String word = "Playe";
		SpellCheckerResult r = s.checkSpellingAndSuggest(word, 3,true);
		System.out.println( r.toString());
		System.out.println("done! "+ word);
		*/
		

		 String test = "This is a tst. How about that?! Huh?";
		 test = "barzil defende ronaldio";
	     QueryAutoCorrectionResult qr = s.autoCorrectQuery(test, true, 3);
	     System.out.println(qr.toString());

		
		}catch(Exception e){
			e.printStackTrace();
		}
		

	}
	
	
	
		 


}
