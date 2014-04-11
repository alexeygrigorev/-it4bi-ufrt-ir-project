package it4bi.ufrt.ir.service.dw.eval;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.Maps;

public class QueryTemplate {

	private final int id;
	private final String keywords;
	private final String sqlTemplate;
	private final String name;
	private final List<QueryParameter> parameters;
	private final Map<String, QueryParameter> parametersMap;

	public QueryTemplate(int id, String keywords, String sqlTemplate, String name,
			List<QueryParameter> parameters) {
		this.id = id;
		this.keywords = keywords;
		this.sqlTemplate = sqlTemplate;
		this.name = name;
		this.parameters = parameters;
		this.parametersMap = build(parameters);
		validate();
	}

	private static Map<String, QueryParameter> build(List<QueryParameter> parameters) {
		Map<String, QueryParameter> result = Maps.newLinkedHashMap();
		for (QueryParameter param : parameters) {
			result.put(param.getName(), param);
		}
		return result;
	}

	private void validate() {
		for (QueryParameter param : parameters) {
			Validate.validState(sqlTemplate.contains(":" + param.getName()),
					"template %s expected to contain param %s", sqlTemplate, param.getName());
		}
	}

	public EvalResult evaluate(String query, EvalContext context) {
		EvalResult result = new EvalResult();

		for (QueryParameter param : parameters) {
			param.tryRecognize(query, context, result);
		}

		return result;
	}

	@Override
	public String toString() {
		return "QueryTemplate [id=" + id + ", keywords=" + keywords + ", sqlTemplate=" + sqlTemplate
				+ ", name=" + name + ", parameters=" + parameters + "]";
	}

}
