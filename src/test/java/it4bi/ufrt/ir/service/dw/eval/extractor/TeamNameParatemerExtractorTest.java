package it4bi.ufrt.ir.service.dw.eval.extractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import it4bi.ufrt.ir.service.dw.eval.EvalResult;
import it4bi.ufrt.ir.service.dw.eval.QueryParameter;
import it4bi.ufrt.ir.service.dw.ner.NamedEntity;
import it4bi.ufrt.ir.service.dw.ner.NamedEntityClass;

import java.util.Arrays;

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
		ParameterExtractor extractor = extractorInstantiator.instantiate(parameter);

		EvalResult result = new EvalResult();
		NamedEntity ne1 = new NamedEntity("Moscow", NamedEntityClass.LOCATION);
		NamedEntity ne2 = new NamedEntity("Russia", NamedEntityClass.LOCATION);
		result.addNamedEntities(Arrays.asList(ne1, ne2));

		ExtractionAttempt attempt = extractor.tryExtract("Mathes of Russia", parameter, result);
		assertTrue(attempt.isSuccessful());
		assertEquals("Russian Federation", attempt.getValue());
	}

}
