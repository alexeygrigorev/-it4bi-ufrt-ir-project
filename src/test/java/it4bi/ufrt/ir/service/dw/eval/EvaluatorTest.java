package it4bi.ufrt.ir.service.dw.eval;

import static org.junit.Assert.*;

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

	@Test
	public void test() {
		AllResults results = evaluator.evaluate("Matches of Russia");
		List<Query> queries = results.getQueries();
		Query query = queries.get(0);
		assertEquals(1, query.getQueryTemplateId());
		assertEquals("Standings of Russian Federation by cups", query.getTitle());
	}

}
