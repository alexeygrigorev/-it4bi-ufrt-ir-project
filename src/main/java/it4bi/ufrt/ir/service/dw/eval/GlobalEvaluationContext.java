package it4bi.ufrt.ir.service.dw.eval;

import it4bi.ufrt.ir.service.dw.UserQuery;
import it4bi.ufrt.ir.service.dw.eval.extractor.ExtractorInstantiator;
import it4bi.ufrt.ir.service.dw.eval.extractor.ParameterExtractor;

// TODO consider renaming
public class GlobalEvaluationContext {

	private final UserQuery query;
	private final ExtractorInstantiator extractorInstantiator;

	public GlobalEvaluationContext(UserQuery query, ExtractorInstantiator extractorInstantiator) {
		this.query = query;
		this.extractorInstantiator = extractorInstantiator;
	}

	public ParameterExtractor createExtractorFor(QueryParameter parameter) {
		return extractorInstantiator.instantiate(parameter);
	}

	public UserQuery getQuery() {
		return query;
	}

	public EvaluationResult createResultFor(QueryTemplate queryTemplate) {
		return new EvaluationResult(queryTemplate, query);
	}

}
