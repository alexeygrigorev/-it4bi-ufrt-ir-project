package it4bi.ufrt.ir.service.dw.db;

import it4bi.ufrt.ir.service.dw.eval.QueryParameter;
import it4bi.ufrt.ir.service.dw.eval.QueryTemplate;
import it4bi.ufrt.ir.service.dw.eval.extractor.ParameterExtractor;
import it4bi.ufrt.ir.service.dw.nlp.Tokenizer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * Data access layer object for getting the data about Query Templates
 * 
 * @see QueryTemplate
 */
@Repository
public class QueryTemplateDao {

	private static final String PARAMETERS = "SELECT qtp.template_id, qtp.parameter_name, "
			+ "qtp.parameter_type, qp.java_class " + "FROM QueryTemplateParameter qtp, QueryParameter qp "
			+ "WHERE qp.parameter_type = qtp.parameter_type";

	private static final String QUERY_TEMPLATE = "SELECT id, keywords, query, name FROM QueryTemplate;";

	private static final String PARAMETERS_BY_ID = "SELECT qtp.template_id, qtp.parameter_name, "
			+ "qtp.parameter_type, qp.java_class " + "FROM QueryTemplateParameter qtp, QueryParameter qp "
			+ "WHERE qp.parameter_type = qtp.parameter_type and qtp.template_id = :id";

	private static final String QUERY_TEMPLATE_BY_ID = "SELECT id, keywords, query, name FROM QueryTemplate "
			+ "WHERE id = :id;";

	@Autowired
	@Qualifier("appJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Autowired
	private Tokenizer tokenizer;

	@Cacheable(value = "template_by_id", key = "#root.args[0]")
	public Optional<QueryTemplate> byId(int id) {
		Map<String, ?> idParam = ImmutableMap.of("id", id);

		List<QueryTemplateParameter> parameters = jdbcTemplate.query(PARAMETERS_BY_ID, idParam,
				new QueryTemplateParametersMapper());

		Multimap<Integer, QueryParameter> index = indexByTemplateId(parameters);

		List<QueryTemplate> templates = jdbcTemplate.query(QUERY_TEMPLATE_BY_ID, idParam,
				new QueryTemplateMapper(index));

		if (templates.isEmpty()) {
			return Optional.absent();
		}

		return Optional.of(templates.get(0));
	}

	@Cacheable("templates")
	public List<QueryTemplate> all() {
		List<QueryTemplateParameter> parameters = jdbcTemplate.query(PARAMETERS,
				new QueryTemplateParametersMapper());

		Multimap<Integer, QueryParameter> index = indexByTemplateId(parameters);

		List<QueryTemplate> templates = jdbcTemplate.query(QUERY_TEMPLATE, new QueryTemplateMapper(index));

		return templates;
	}

	private Multimap<Integer, QueryParameter> indexByTemplateId(List<QueryTemplateParameter> parameters) {
		Multimap<Integer, QueryParameter> index = LinkedHashMultimap.create();
		for (QueryTemplateParameter qp : parameters) {
			index.put(qp.queryTemplateId, qp.parameter);
		}
		return index;
	}

	private class QueryTemplateMapper implements RowMapper<QueryTemplate> {
		private final Multimap<Integer, QueryParameter> index;

		private QueryTemplateMapper(Multimap<Integer, QueryParameter> index) {
			this.index = index;
		}

		@Override
		public QueryTemplate mapRow(ResultSet rs, int idx) throws SQLException {
			int id = rs.getInt(1);
			String keywordsRaw = rs.getString(2);
			List<String> keywords = tokenizer.tokenizeAndStem(keywordsRaw);

			String sqlTemplate = rs.getString(3);
			String name = rs.getString(4);
			List<QueryParameter> parameters = Lists.newArrayList(index.get(id));
			return new QueryTemplate(id, keywords, sqlTemplate, name, parameters);
		}
	}

	private static class QueryTemplateParametersMapper implements RowMapper<QueryTemplateParameter> {
		@Override
		public QueryTemplateParameter mapRow(ResultSet rs, int rowNum) throws SQLException {
			int queryTemplateId = rs.getInt(1);
			String name = rs.getString(2);
			String parameterType = rs.getString(3);
			String className = rs.getString(4);
			Class<? extends ParameterExtractor> extractorClass = toClass(className);

			QueryParameter parameter = new QueryParameter(name, extractorClass, parameterType);
			return new QueryTemplateParameter(queryTemplateId, parameter);
		}

		@SuppressWarnings("unchecked")
		private static Class<? extends ParameterExtractor> toClass(String className) {
			try {
				return (Class<? extends ParameterExtractor>) Class.forName(className);
			} catch (Exception e) {
				throw Throwables.propagate(e);
			}
		}
	}

	private static class QueryTemplateParameter {
		private final int queryTemplateId;
		private final QueryParameter parameter;

		public QueryTemplateParameter(int queryTemplateId, QueryParameter parameter) {
			this.queryTemplateId = queryTemplateId;
			this.parameter = parameter;
		}
	}

}
