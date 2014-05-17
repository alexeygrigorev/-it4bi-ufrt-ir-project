package it4bi.ufrt.ir.service.dw;

import static org.junit.Assert.*;

import java.util.Arrays;
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
	public void template1() {
		String freeTextQuery = "All standings of Russia";
		DwhDtoResults res = datawarehouseService.find(freeTextQuery, user);
		List<MatchedQueryTemplate> matched = res.getMatched();

		LOGGER.debug("Obtained result: {}", matched);

		MatchedQueryTemplate first = matched.get(0);

		assertEquals(1, first.getTemplateId());
		assertEquals("Standings of Russian Federation by cups", first.getName());
		assertEquals(1, first.getRelevance()); // "standings" should match
	}

	@Test
	public void template2() {
		String freeTextQuery = "Matches of France and Brazil";
		DwhDtoResults res = datawarehouseService.find(freeTextQuery, user);
		List<MatchedQueryTemplate> matched = res.getMatched();

		LOGGER.debug("Obtained result: {}", matched);

		MatchedQueryTemplate first = matched.get(0);

		assertEquals(2, first.getTemplateId());
		assertEquals("Matches of France vs Brazil", first.getName());
		assertEquals(1, first.getRelevance()); // "Matches" should match
	}

	@Test
	public void execute_template2() {
		String freeTextQuery = "Matches of France and Brazil";
		DwhDtoResults res = datawarehouseService.find(freeTextQuery, user);
		List<MatchedQueryTemplate> matched = res.getMatched();

		MatchedQueryTemplate first = matched.get(0);
		ExecutedDwhQuery result = datawarehouseService.execute(first);

		List<String> expectedColumns = Arrays.asList("Team 1", "Team 2", "Date", "Score", "Stadium",
				"Attendance", "Fill Rate");
		assertEquals(expectedColumns, result.getColumnNames());

		List<String> row1 = Arrays.asList("France", "Brazil", "1958-06-24", "2:5", "-", "27100", "-");
		List<String> row2 = Arrays.asList("France", "Brazil", "1986-06-21", "1:1", "-", "-", "-");
		List<String> row3 = Arrays.asList("France", "Brazil", "1998-07-12", "3:0",
				"Stade de France, Saint-Denis, France", "80000", "0.98");
		List<String> row4 = Arrays.asList("France", "Brazil", "2006-07-01", "1:0",
				"Commerzbank-Arena, Frankfurt, Germany", "48000", "0.92");
		List<List<String>> expectedRows = Arrays.asList(row1, row2, row3, row4);

		assertEquals(expectedRows, result.getRows());
	}

	@Test
	public void template3() {
		String freeTextQuery = "Cristiano Ronaldo matches per cup";
		DwhDtoResults res = datawarehouseService.find(freeTextQuery, user);
		List<MatchedQueryTemplate> matched = res.getMatched();

		LOGGER.debug("Obtained result: {}", matched);

		MatchedQueryTemplate first = matched.get(0);

		assertEquals(3, first.getTemplateId());
		assertEquals("Participation of Cristiano Ronaldo per cup", first.getName());
		assertEquals(2, first.getRelevance()); // "Matches" and "cup" should match

		ExecutedDwhQuery result = datawarehouseService.execute(first);
		List<String> expectedColumns = Arrays.asList("Player Name", "Cup", "Country", "Age",
				"Matches Played", "Minutes Played", "Position");
		assertEquals(expectedColumns, result.getColumnNames());
	}

	@Test
	public void template4() {
		String freeTextQuery = "referee Corver Charles";
		DwhDtoResults res = datawarehouseService.find(freeTextQuery, user);
		List<MatchedQueryTemplate> matched = res.getMatched();

		LOGGER.debug("Obtained result: {}", matched);

		MatchedQueryTemplate first = matched.get(0);

		assertEquals(4, first.getTemplateId());
		assertEquals("All matches of referee Charles Corver", first.getName());
		assertEquals(1, first.getRelevance()); // "referee" should match

		ExecutedDwhQuery result = datawarehouseService.execute(first);
		List<String> expectedColumns = Arrays.asList("Cup", "Date", "Name", "Nationality", "Match", "Stage",
				"Role");
		assertEquals(expectedColumns, result.getColumnNames());
	}

	@Test
	public void template5() {
		String freeTextQuery = "all matches of year 1998";
		DwhDtoResults res = datawarehouseService.find(freeTextQuery, user);
		List<MatchedQueryTemplate> matched = res.getMatched();

		LOGGER.debug("Obtained result: {}", matched);

		MatchedQueryTemplate first = matched.get(0);

		assertEquals(5, first.getTemplateId());
		assertEquals("All matches of cup 1998", first.getName());
		assertEquals(3, first.getRelevance()); // "all", "matches" and "year" should match

		ExecutedDwhQuery result = datawarehouseService.execute(first);
		List<String> expectedColumns = Arrays.asList("Cup", "Date", "Team 1", "Team 2", "Score", "Stage");
		assertEquals(expectedColumns, result.getColumnNames());
	}

	@Test
	public void template6() {
		String freeTextQuery = "positions of teams coached by Diego Maradona";
		DwhDtoResults res = datawarehouseService.find(freeTextQuery, user);
		List<MatchedQueryTemplate> matched = res.getMatched();

		LOGGER.debug("Obtained result: {}", matched);

		MatchedQueryTemplate first = matched.get(0);

		assertEquals(6, first.getTemplateId());
		assertEquals("Standings of teams coached by Diego MARADONA", first.getName());
		assertEquals(2, first.getRelevance()); // "position", "coach" should match

		ExecutedDwhQuery result = datawarehouseService.execute(first);
		List<String> expectedColumns = Arrays.asList("Year", "Name", "Country", "Position", "Team Standing",
				"Was Player");
		assertEquals(expectedColumns, result.getColumnNames());
	}

	@Test
	public void template7() {
		String freeTextQuery = "matches of Diego Maradona as a coach";
		DwhDtoResults res = datawarehouseService.find(freeTextQuery, user);
		List<MatchedQueryTemplate> matched = res.getMatched();

		LOGGER.debug("Obtained result: {}", matched);

		MatchedQueryTemplate first = matched.get(0);

		assertEquals(7, first.getTemplateId());
		assertEquals("Matches of teams coached by Diego MARADONA", first.getName());
		assertEquals(2, first.getRelevance()); // "match", "coach" should match

		ExecutedDwhQuery result = datawarehouseService.execute(first);
		List<String> expectedColumns = Arrays.asList("Year", "Date", "Name", "Coached Team", "Opponent Team",
				"Score", "Result");
		assertEquals(expectedColumns, result.getColumnNames());
	}
}
