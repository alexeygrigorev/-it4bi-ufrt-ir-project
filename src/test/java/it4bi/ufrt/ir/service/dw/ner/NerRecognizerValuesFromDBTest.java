package it4bi.ufrt.ir.service.dw.ner;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Lists;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:dwTestContextRealDb.xml")
public class NerRecognizerValuesFromDBTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(NerRecognizerValuesFromDBTest.class);

	@Autowired
	@Qualifier("dwhJdbcTemplate")
	private NamedParameterJdbcTemplate dwhJdbcTemplate;

	@Autowired
	NerRecognizer nerRecognizer;

	@Test
	public void justOne() {
		List<String> players = dwhJdbcTemplate.query("SELECT PlayerName FROM DimPlayer;", stringMapper());
		List<Pair<String, NamedEntity>> rejected = Lists.newArrayList();

		for (String player : players) {
			LOGGER.debug("trying {}", player);
			String query = "Matches of " + player;
			List<NamedEntity> recognzed = nerRecognizer.recognize(query);
			NamedEntity expected = new NamedEntity(player, NamedEntityClass.PERSON);
			if (!recognzed.contains(expected)) {
				LOGGER.debug("{} is not recognized, found {}", player, recognzed);
				if (recognzed.isEmpty()) {
					rejected.add(Pair.of(player, (NamedEntity) null));
				} else {
					rejected.add(Pair.of(player, recognzed.get(0)));
				}
			}
		}

		double ratio = 1 - (rejected.size() + 0.0) / players.size();
		LOGGER.info("size of rejected list is {} out of {}, ratio: {}", rejected.size(), players.size(),
				ratio);

		assertTrue(ratio > 0.9);
	}

	private SingleColumnRowMapper<String> stringMapper() {
		return new SingleColumnRowMapper<String>(String.class);
	}

	public static void assertContains(List<NamedEntity> results, String token, NamedEntityClass cls) {
		assertTrue(results.contains(new NamedEntity(token, cls)));
	}
}
