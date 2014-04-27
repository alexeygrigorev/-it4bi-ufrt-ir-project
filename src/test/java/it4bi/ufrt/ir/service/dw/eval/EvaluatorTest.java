package it4bi.ufrt.ir.service.dw.eval;

import static org.junit.Assert.*;
import it4bi.ufrt.ir.service.dw.MatchedQueryTemplate;
import it4bi.ufrt.ir.service.dw.UserQuery;
import it4bi.ufrt.ir.service.dw.nlp.QueryPreprocessor;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:dwTestContextRealDb.xml")
public class EvaluatorTest {

	@Autowired
	Evaluator evaluator;

	@Autowired
	QueryPreprocessor queryPreprocessor;

	@Test
	public void test() {
		UserQuery userTextQuery = queryPreprocessor.preprocess("Matches of Russia");
		AllEvaluationResults results = evaluator.evaluate(userTextQuery);
		List<MatchedQueryTemplate> queries = results.getMatchedTemplates();
		MatchedQueryTemplate matchedDwhQuery = queries.get(0);

		assertEquals(1, matchedDwhQuery.getTemplateId());
		assertEquals("Standings of Russian Federation by cups", matchedDwhQuery.getName());
	}

}
