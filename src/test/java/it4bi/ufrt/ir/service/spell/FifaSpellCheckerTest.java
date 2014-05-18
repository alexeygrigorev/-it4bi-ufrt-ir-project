package it4bi.ufrt.ir.service.spell;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spellCheckerTestContext.xml")
public class FifaSpellCheckerTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(FifaSpellCheckerTest.class);
	
	@Autowired
	FIFASpellChecker spellChecker;

	@Test
	public void test() throws Exception {
		String test = "barzil defende ronaldio";
		QueryAutoCorrectionResult qr = spellChecker.autoCorrectQuery(test, true, 3);
		assertEquals("brazil defender ronaldo", qr.getCorrectedQuery());
	}

	@Test
	public void notAutocorrect() throws Exception {
		String test = "Matches of France and Brazil";
		QueryAutoCorrectionResult qr = spellChecker.autoCorrectQuery(test, true, 3);
		LOGGER.debug("autocorrected query for {} is {}", test, qr.getCorrectedQuery());
		assertFalse(qr.getIsCorrected());
	}
}
