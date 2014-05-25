package it4bi.ufrt.ir.service.dw.rec;

import it4bi.ufrt.ir.service.dw.MatchedQueryTemplate;
import it4bi.ufrt.ir.service.dw.UserQuery;
import it4bi.ufrt.ir.service.dw.eval.AllEvaluationResults;
import it4bi.ufrt.ir.service.dw.eval.EvaluationResult;
import it4bi.ufrt.ir.service.dw.eval.QueryParameter;
import it4bi.ufrt.ir.service.dw.eval.QueryTemplate;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

@Component
public class QueryRecommender {

	private static final Logger LOGGER = LoggerFactory.getLogger(QueryRecommender.class);

	private static final String UPDATE_PARAM_COUNT_QUERY = "if not exists "
			+ "(SELECT * FROM ParameterStatistics WHERE "
			+ "user_id = :user and parameter_name = :paramName and parameter_value = :paramValue)"
			+ "insert into ParameterStatistics values (:user, :paramName, :paramValue, :inc)"
			+ "else update ParameterStatistics set count = count + :inc where user_id = :user and "
			+ "parameter_name = :paramName and parameter_value = :paramValue";

	@Autowired
	@Qualifier("appJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Async
	public void captureParametersForClick(User user, QueryTemplate query, MatchedQueryTemplate template) {
		LOGGER.debug("captureParametersForClick for {}", user);

		List<QueryParameter> parameters = query.getParameters();
		Map<String, QueryParameter> byName = Maps.newHashMap();
		for (QueryParameter qp : parameters) {
			byName.put(qp.getName(), qp);
		}

		Multimap<String, String> valueByType = HashMultimap.create();
		for (Entry<String, String> e : template.getParameters().entrySet()) {
			String paramName = e.getKey();
			QueryParameter param = byName.get(paramName);
			valueByType.put(param.getParameterType(), e.getValue());
		}

		List<Map<String, ?>> all = fromMultimap(user, valueByType, 3);
		executeUpdate(all);
	}

	private List<Map<String, ?>> fromMultimap(User user, Multimap<String, String> all, int inc) {
		List<Map<String, ?>> params = Lists.newArrayList();
		for (Entry<String, String> e : all.entries()) {
			Map<String, ?> p = ImmutableMap.of("user", user.getID(), "paramName", e.getKey(), "paramValue",
					e.getValue(), "inc", inc);
			params.add(p);
		}
		return params;
	}

	@Async
	public void captureParametersForQuery(User user, AllEvaluationResults results) {
		LOGGER.debug("captureParametersForQuery for {}", user);
		List<EvaluationResult> successful = results.getSuccessful();
		List<Map<String, ?>> params = toMapOfParams(user, successful);

		executeUpdate(params);
	}

	private void executeUpdate(List<Map<String, ?>> params) {
		LOGGER.debug("logging captured parameters: {}", params);

		@SuppressWarnings("unchecked")
		Map<String, ?>[] batchValues = params.toArray(new Map[params.size()]);

		jdbcTemplate.batchUpdate(UPDATE_PARAM_COUNT_QUERY, batchValues);
	}

	private List<Map<String, ?>> toMapOfParams(User user, List<EvaluationResult> successful) {
		Multimap<String, String> all = allFoundParamsDeduplicated(successful);
		return fromMultimap(user, all, 1);
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
