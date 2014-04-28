package it4bi.ufrt.ir.service.dw.eval.extractor;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import it4bi.ufrt.ir.service.dw.UserQuery;
import it4bi.ufrt.ir.service.dw.eval.EvaluationResult;
import it4bi.ufrt.ir.service.dw.eval.QueryParameter;
import it4bi.ufrt.ir.service.dw.eval.QueryTemplate;
import it4bi.ufrt.ir.service.dw.ner.NamedEntity;
import it4bi.ufrt.ir.service.dw.ner.NamedEntityClass;
import it4bi.ufrt.ir.service.dw.ner.RecognizedNamedEntities;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:dwTestContextRealDb.xml")
public class PlayerNameParameterExtractorTest {

	private static final Class<PlayerNameParameterExtractor> EXTRACTOR_CLASS = PlayerNameParameterExtractor.class;
	Set<String> emptySet = Collections.<String> emptySet();

	@Autowired
	ExtractorInstantiator extractorInstantiator;

	@Test
	public void testFindClosest_CristianoRonaldo() {
		runTest("Cristiano Ronaldo");
	}

	@Test
	public void testFindClosest_Ronaldo() {
		runTest("Ronaldo");
	}

	@Test
	public void testFindClosest_Beckham() {
		runTest("David Beckham");
	}

	private void runTest(String token) {
		PlayerNameParameterExtractor extractor = extractorInstantiator.instantiate(EXTRACTOR_CLASS);
		NamedEntity ne1 = new NamedEntity(token, NamedEntityClass.PERSON);
		RecognizedNamedEntities namedEntities = RecognizedNamedEntities.from(ne1);
		UserQuery query = new UserQuery("Games of " + token, namedEntities);
		QueryParameter parameter = new QueryParameter("playerName", EXTRACTOR_CLASS, "PLAYER_NAME");
		List<QueryParameter> parameters = Arrays.asList(parameter);
		QueryTemplate queryTemplate = new QueryTemplate(1, emptySet, ":playerName", ":playerName", parameters);
		EvaluationResult result = new EvaluationResult(queryTemplate, query);

		ExtractionAttempt attempt = extractor.tryExtract(query, parameter, result);
		assertTrue(attempt.isSuccessful());
		assertEquals(token, attempt.getValue());
	}

}
