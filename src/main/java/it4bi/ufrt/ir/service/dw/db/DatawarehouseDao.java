package it4bi.ufrt.ir.service.dw.db;

import java.util.List;

import jersey.repackaged.com.google.common.collect.ImmutableMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DatawarehouseDao {

	private static final SingleColumnRowMapper<String> STRING_ROW_MAPPER = new SingleColumnRowMapper<String>(
			String.class);

	private static final String CANONICAL_COUNTRIES_QUERY = "SELECT "
			+ "CannonicalName FROM ADW_WorldCupAnalytics_STG.dbo.[Countries Map] "
			+ "WHERE Entry = :countryName;";

	private final NamedParameterJdbcTemplate jdbcTemplate;

	@Autowired
	public DatawarehouseDao(@Qualifier("dwhJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Cacheable(value = "canonicalCountry", key = "country")
	public String canonicalCountry(String country) {
		ImmutableMap<String, String> parameters = ImmutableMap.of("countryName", country);
		List<String> result = jdbcTemplate.query(CANONICAL_COUNTRIES_QUERY, parameters, STRING_ROW_MAPPER);

		if (result.isEmpty()) {
			return null;
		}

		return result.get(0);
	}

}
