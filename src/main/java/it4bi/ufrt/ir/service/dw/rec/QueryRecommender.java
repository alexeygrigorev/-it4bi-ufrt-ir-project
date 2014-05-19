package it4bi.ufrt.ir.service.dw.rec;

import it4bi.ufrt.ir.service.dw.MatchedQueryTemplate;
import it4bi.ufrt.ir.service.dw.UserQuery;
import it4bi.ufrt.ir.service.dw.eval.AllEvaluationResults;
import it4bi.ufrt.ir.service.dw.eval.EvaluationResult;
import it4bi.ufrt.ir.service.dw.eval.QueryParameter;
import it4bi.ufrt.ir.service.dw.eval.extractor.ExtractionAttempt;
import it4bi.ufrt.ir.service.users.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

@Component
public class QueryRecommender {

	private static final Logger LOGGER = LoggerFactory.getLogger(QueryRecommender.class);

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
		List<EvaluationResult> unsatisfied = results.almostSuccessful();
		QueryBuilder.Result res = new QueryBuilder(unsatisfied, user.getID()).build();

		jdbcTemplate.query(res.query(), new TemplateEnricherCallback(res));

		List<EvaluationResult> enriched = res.getResults();
		List<MatchedQueryTemplate> result = Lists.newArrayList();

		for (EvaluationResult er : enriched) {
			if (er.isSatisfied()) {
				result.add(er.asDto());
			}
		}

		Collections.sort(result);
		return result;
	}

	public class TemplateEnricherCallback implements RowCallbackHandler {

		private QueryBuilder.Result res;

		public TemplateEnricherCallback(QueryBuilder.Result res) {
			this.res = res;
		}

		@Override
		public void processRow(ResultSet rs) throws SQLException {
			int evalResIdx = rs.getInt(1);
			int paramIdx = rs.getInt(2);
			String value = rs.getString(3);

			QueryParameter queryParameter = res.getParam(paramIdx);
			ExtractionAttempt attempt = ExtractionAttempt.successful(queryParameter, value);
			EvaluationResult er = res.getResult(evalResIdx);
			er.record(attempt);
		}
	}

}
