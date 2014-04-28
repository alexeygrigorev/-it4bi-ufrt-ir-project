package it4bi.ufrt.ir.service.dw.db;

import it4bi.ufrt.ir.service.dw.db.Person.PersonType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import jersey.repackaged.com.google.common.collect.ImmutableMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Data access layer object for accessing the data warehouse
 */
@Repository
public class DatawarehouseDao {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DatawarehouseDao.class);

	private static final SingleColumnRowMapper<String> STRING_ROW_MAPPER = new SingleColumnRowMapper<String>(
			String.class);

	private static final String CANONICAL_COUNTRIES_QUERY = "SELECT "
			+ "CannonicalName FROM ADW_WorldCupAnalytics_STG.dbo.[Countries Map] "
			+ "WHERE Entry = :countryName;";

	@Autowired
	@Qualifier("dwhJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Cacheable(value = "canonicalCountry", key = "#root.args[0]")
	public Optional<String> canonicalCountry(String country) {
		ImmutableMap<String, String> parameters = ImmutableMap.of("countryName", country);
		List<String> result = jdbcTemplate.query(CANONICAL_COUNTRIES_QUERY, parameters, STRING_ROW_MAPPER);

		if (result.isEmpty()) {
			return Optional.absent();
		}

		return Optional.of(result.get(0));
	}

	public List<Person> allPeople() {
		return Lists.newArrayList(Iterables.concat(allPlayers(), allCoaches(), allReferees()));
	}

	@Cacheable(value = "allPlayers")
	public List<Person> allPlayers() {
		return executePersonQuery("SELECT Player_SID, PlayerName FROM DimPlayer;", PersonType.PLAYER);
	}

	@Cacheable(value = "allCoaches")
	public List<Person> allCoaches() {
		return executePersonQuery("SELECT Coach_SID, Name FROM DimCoach;", PersonType.COACH);
	}

	@Cacheable(value = "allReferees")
	public List<Person> allReferees() {
		return executePersonQuery("SELECT Referee_SID, RefereeName FROM DimReferee;", PersonType.REFEREE);
	}

	private List<Person> executePersonQuery(String query, final PersonType type) {
		LOGGER.debug("Hitting the db for person type {}", type);
		
		return jdbcTemplate.query(query, new RowMapper<Person>() {
			@Override
			public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
				int id = rs.getInt(1);
				String name = rs.getString(2);
				return new Person(id, name, type);
			}
		});
	}

}
