package it4bi.ufrt.ir.service.dw.eval.extractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import it4bi.ufrt.ir.service.dw.eval.EvaluationResult;
import it4bi.ufrt.ir.service.dw.eval.QueryParameter;
import it4bi.ufrt.ir.service.dw.eval.QueryTemplate;
import it4bi.ufrt.ir.service.dw.ner.NamedEntity;
import it4bi.ufrt.ir.service.dw.ner.NamedEntityClass;
import it4bi.ufrt.ir.service.dw.ner.RecognizedNamedEntities;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:dwTestContextRealDb.xml")
public class TeamNameParatemerExtractorTest {

	@Autowired
	ExtractorInstantiator extractorInstantiator;

	@Test
	public void testTryExtract() {
		Class<TeamNameParatemerExtractor> extractorClass = TeamNameParatemerExtractor.class;
		QueryParameter parameter = new QueryParameter("team", extractorClass, "TEAM_TYPE");
		QueryTemplate queryTemplate = new QueryTemplate(1, "", ":team", "",
				Collections.singletonList(parameter));

		ParameterExtractor extractor = extractorInstantiator.instantiate(parameter);

		NamedEntity someGarbage = new NamedEntity("Moscow", NamedEntityClass.LOCATION);
		NamedEntity country = new NamedEntity("Russia", NamedEntityClass.LOCATION);
		RecognizedNamedEntities nes = RecognizedNamedEntities.from(someGarbage, country);

		EvaluationResult result = new EvaluationResult(queryTemplate, nes);

		ExtractionAttempt attempt = extractor.tryExtract("Mathes of Russia", parameter, result);
		assertTrue(attempt.isSuccessful());
		assertEquals("Russian Federation", attempt.getValue());
	}

	@Test
	public void testTryExtract_twoTeams() {
		Class<TeamNameParatemerExtractor> extractorClass = TeamNameParatemerExtractor.class;
		QueryParameter teamAparam = new QueryParameter("teamA", extractorClass, "TEAM_TYPE");
		ParameterExtractor teamAExtractor = extractorInstantiator.instantiate(teamAparam);
		QueryParameter teamBparam = new QueryParameter("teamB", extractorClass, "TEAM_TYPE");
		ParameterExtractor teamBExtractor = extractorInstantiator.instantiate(teamBparam);

		QueryTemplate queryTemplate = new QueryTemplate(1, "", ":teamA :teamB", "", Arrays.asList(teamAparam,
				teamBparam));

		NamedEntity ne1 = new NamedEntity("Russia", NamedEntityClass.LOCATION);
		NamedEntity ne2 = new NamedEntity("England", NamedEntityClass.LOCATION);
		RecognizedNamedEntities nes = RecognizedNamedEntities.from(ne1, ne2);

		EvaluationResult result = new EvaluationResult(queryTemplate, nes);

		ExtractionAttempt attempt1 = teamAExtractor.tryExtract("Mathes of Russia vs England", teamAparam,
				result);
		assertTrue(attempt1.isSuccessful());
		assertEquals("Russian Federation", attempt1.getValue());

		ExtractionAttempt attempt2 = teamBExtractor.tryExtract("Mathes of Russia vs England", teamAparam,
				result);
		assertTrue(attempt2.isSuccessful());
		assertEquals("England", attempt2.getValue());
	}

}
