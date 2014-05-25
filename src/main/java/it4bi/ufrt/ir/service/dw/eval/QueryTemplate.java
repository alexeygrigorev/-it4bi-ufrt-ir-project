package it4bi.ufrt.ir.service.dw.eval;

import it4bi.ufrt.ir.service.dw.UserQuery;
import it4bi.ufrt.ir.service.dw.eval.extractor.ExtractionAttempt;
import it4bi.ufrt.ir.service.dw.eval.extractor.ParameterExtractor;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.google.common.collect.Sets;

public class QueryTemplate {

	private final int id;
	private final Set<String> keywords;
	private final String sqlTemplate;
	private final String name;
	private final List<QueryParameter> parameters;

	public QueryTemplate(int id, Collection<String> keywords, String sqlTemplate, String name,
			List<QueryParameter> parameters) {
		this.id = id;
		this.keywords = Sets.newLinkedHashSet(keywords);
		this.sqlTemplate = sqlTemplate;
		this.name = name;
		this.parameters = parameters;
		validate();
	}

	private void validate() {
		for (QueryParameter param : parameters) {
			Validate.validState(sqlTemplate.contains(":" + param.getName()),
					"template %s expected to contain param %s", sqlTemplate, param.getName());
		}
	}

	public EvaluationResult evaluate(UserQuery query, GlobalEvaluationContext context) {
		EvaluationResult result = context.createResultFor(this);

		for (QueryParameter parameter : parameters) {
			ParameterExtractor extractor = context.createExtractorFor(parameter);
			ExtractionAttempt attempt = extractor.tryExtract(query, parameter, result);
			result.record(attempt);
		}

		return result;
	}

	public List<QueryParameter> getParameters() {
		return parameters;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public Set<String> getKeywords() {
		return keywords;
	}

	public String getSqlTemplate() {
		return sqlTemplate;
	}

	@Override
	public String toString() {
		return "QueryTemplate [id=" + id + ", keywords=" + keywords + ", sqlTemplate="
				+ StringUtils.abbreviate(sqlTemplate, 20) + ", name=" + name + ", parameters=" + parameters
				+ "]";
	}

}
