package it4bi.ufrt.ir.service.dw.ner;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class NerRecognizerTest {

	private static NerRecognizer nerRecognizer = NerRecognizer.readDefault();

	@Test
	public void justOne() {
		String query = "Matches of Diego Maradonna";
		List<NamedEntity> results = nerRecognizer.recognize(query);
		assertContains(results, "Diego Maradonna", "PERSON");
	}

	public static void assertContains(List<NamedEntity> results, String token, String cls) {
		assertTrue(results.contains(new NamedEntity(token, cls)));
	}

	@Test
	public void namesCommaSeparated() {
		String query = "Matches of Diego Maradonna, Guus Hiddink and David Beckham";
		List<NamedEntity> results = nerRecognizer.recognize(query);
		assertContains(results, "Diego Maradonna", "PERSON");
		assertContains(results, "Guus Hiddink", "PERSON");
		assertContains(results, "David Beckham", "PERSON");
	}

	@Test
	public void nameWithTypo() {
		String query = "Matches of David Bekham";
		List<NamedEntity> results = nerRecognizer.recognize(query);
		assertContains(results, "David Bekham", "PERSON");
	}

	@Test
	public void countryName() {
		String query = "Matches of England";
		List<NamedEntity> results = nerRecognizer.recognize(query);
		assertContains(results, "England", "LOCATION");
	}

}
