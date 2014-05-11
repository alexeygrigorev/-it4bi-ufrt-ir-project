package it4bi.ufrt.ir.service.dw;

import static org.junit.Assert.*;

import java.util.List;

import it4bi.ufrt.ir.service.users.User;
import it4bi.ufrt.ir.service.users.UserSex;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:dwTestContextRealDb.xml")
public class DatawarehouseServiceTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(DatawarehouseServiceTest.class);

	User user = new User("Richard", "Kondor", "Italy", UserSex.MALE, "1995-05-28");

	@Autowired
	DatawarehouseService datawarehouseService;

	@Test
	public void test() {
		String freeTextQuery = "All standings of Russia";
		DwhDtoResults res = datawarehouseService.find(freeTextQuery, user);
		List<MatchedQueryTemplate> matched = res.getMatched();

		LOGGER.debug("Obtained result: {}", matched);

		MatchedQueryTemplate first = matched.get(0);

		assertEquals(1, first.getTemplateId());
		assertEquals("Standings of Russian Federation by cups", first.getName());
		assertEquals(1, first.getRelevance()); // "standings" should match
	}

}
