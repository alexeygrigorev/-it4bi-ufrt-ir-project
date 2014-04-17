package it4bi.ufrt.ir.service.dw.eval;

import static org.junit.Assert.*;
import it4bi.ufrt.ir.service.dw.eval.QueryTemplate;
import it4bi.ufrt.ir.service.dw.eval.QueryTemplateDao;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:dwTestContextRealDb.xml")
public class QueryTemplateDaoTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(QueryTemplateDaoTest.class);

	@Autowired
	private QueryTemplateDao queryTemplateDao;

	@Test
	public void retrievesSomething() {
		List<QueryTemplate> allResults = queryTemplateDao.all();
		LOGGER.info("retrieved templates: {}", allResults);
		assertFalse(allResults.isEmpty());
	}

}
