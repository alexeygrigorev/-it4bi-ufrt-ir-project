package it4bi.ufrt.ir.service.dw.nlp;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class TokenizerTest {

	@Test
	public void test() {
		Tokenizer tokenizer = new Tokenizer();

		List<String> result = tokenizer.tokenizeAndStem("Also, ex-Golden Stater from Oxnard. "
				+ "He can't be contending ...");
		List<String> expected = Arrays.asList("Also", "ex", "Golden", "Stater", "from", "Oxnard", "He",
				"can't", "be", "contend");
		assertEquals(expected, result);
	}
}
