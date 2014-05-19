package it4bi.ufrt.ir.service.dw.rec;

import it4bi.ufrt.ir.service.dw.MatchedQueryTemplate;
import it4bi.ufrt.ir.service.dw.UserQuery;
import it4bi.ufrt.ir.service.dw.eval.AllEvaluationResults;
import it4bi.ufrt.ir.service.dw.eval.EvaluationResult;
import it4bi.ufrt.ir.service.dw.eval.QueryParameter;
import it4bi.ufrt.ir.service.dw.eval.extractor.ExtractionAttempt;
import it4bi.ufrt.ir.service.users.User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

@Component
public class QueryRecommender {

	private static final Logger LOGGER = LoggerFactory.getLogger(QueryRecommender.class);
	private static final RowMapper<String> STRING_MAPPER = new SingleColumnRowMapper<String>(String.class);

	private static final String UPDATE_PARAM_COUNT_QUERY = "if not exists "
			+ "(SELECT * FROM ParameterStatistics WHERE "
			+ "user_id = :user and parameter_name = :paramName and parameter_value = :paramValue)"
			+ "insert into ParameterStatistics values (:user, :paramName, :paramValue, 1)"
			+ "else update ParameterStatistics set count = count + 1 where user_id = :user and "
			+ "parameter_name = :paramName and parameter_value = :paramValue";

	@Autowired
	@Qualifier("appJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	public void captureParameters(User user, AllEvaluationResults results) {
		List<EvaluationResult> successful = results.getSuccessful();
		List<Map<String, ?>> params = toMapOfParams(user, successful);

		LOGGER.debug("logging captured parameters: {}", params);

		@SuppressWarnings("unchecked")
		Map<String, ?>[] batchValues = params.toArray(new Map[params.size()]);

		jdbcTemplate.batchUpdate(UPDATE_PARAM_COUNT_QUERY, batchValues);
	}

	private List<Map<String, ?>> toMapOfParams(User user, List<EvaluationResult> successful) {
		Multimap<String, String> all = allFoundParamsDeduplicated(successful);

		List<Map<String, ?>> params = Lists.newArrayList();
		for (Entry<String, String> e : all.entries()) {
			Map<String, ?> p = ImmutableMap.of("user", user.getID(), "paramName", e.getKey(), "paramValue",
					e.getValue());
			params.add(p);
		}
		return params;
	}

	private Multimap<String, String> allFoundParamsDeduplicated(List<EvaluationResult> successful) {
		Multimap<String, String> all = HashMultimap.create();
		for (EvaluationResult er : successful) {
			all.putAll(er.getUsedValues());
		}
		return all;
	}

	public List<MatchedQueryTemplate> recommend(UserQuery query, User user, AllEvaluationResults results) {
		
		
		
		List<MatchedQueryTemplate> result = Lists.newArrayList();

		List<EvaluationResult> notSuccessful = results.almostSuccessful();
		for (EvaluationResult er : notSuccessful) {
			EvaluationResult withRecommendation = recommend(user, er);
			if (withRecommendation.isSatisfied()) {
				result.add(withRecommendation.asDto());
			}
		}

		Collections.sort(result);
		return result;
	}

	
	public List<MatchedQueryTemplate> recommend2(UserQuery query, User user, AllEvaluationResults results) {
		List<MatchedQueryTemplate> result = Lists.newArrayList();

		List<EvaluationResult> notSuccessful = results.almostSuccessful();

		for (EvaluationResult er : notSuccessful) {
			EvaluationResult withRecommendation = recommend(user, er);
			if (withRecommendation.isSatisfied()) {
				result.add(withRecommendation.asDto());
			}
		}

		Collections.sort(result);
		return result;
	}

	private EvaluationResult recommend(User user, EvaluationResult er) {
		EvaluationResult copy = er.recommendationCopy();

		String sql = "select top 1 parameter_value from ParameterStatistics "
				+ "where user_id = :user and parameter_name = :paramName and "
				+ "parameter_value not in (:usedValues) order by [count] desc";
		String sqlEmpty = "select top 1 parameter_value from ParameterStatistics "
				+ "where user_id = :user and parameter_name = :paramName " + "order by [count] desc";

		List<QueryParameter> params = copy.unsatisfiedParams();

		for (QueryParameter param : params) {
			String parameterType = param.getParameterType();
			Collection<String> usedValues = copy.getUsedValues(parameterType);

			Map<String, ?> paramMap;
			String sqlToRun;
			if (usedValues.isEmpty()) {
				paramMap = ImmutableMap.of("user", user.getID(), "paramName", parameterType);
				sqlToRun = sqlEmpty;
			} else {
				paramMap = ImmutableMap.of("user", user.getID(), "paramName", parameterType, "usedValues",
						usedValues);
				sqlToRun = sql;
			}

			List<String> res = jdbcTemplate.query(sqlToRun, paramMap, STRING_MAPPER);
			LOGGER.debug("for query parameter {} looking up a suggestion with params={}, obtained {}", param,
					paramMap, res);

			if (!res.isEmpty()) {
				String suggestion = res.get(0);
				ExtractionAttempt attempt = ExtractionAttempt.successful(param, suggestion);
				copy.record(attempt);
				LOGGER.debug("using {} to enrich {}", suggestion, copy);
			}
		}

		LOGGER.debug("the template {} is enriched: {}", copy.getQueryTemplate(), copy.isSatisfied());
		return copy;
	}

}
