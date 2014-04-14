package it4bi.ufrt.ir.service.dw.eval;

import it4bi.ufrt.ir.service.dw.eval.extractor.ExtractionAttempt;
import it4bi.ufrt.ir.service.dw.eval.extractor.ParameterExtractor;

public class QueryParameter {

	private final String name;
	private final Class<? extends ParameterExtractor> extractorClass;
	// TODO: not sure if needed
	private final String parameterType;

	public QueryParameter(String name, Class<? extends ParameterExtractor> extractorClass,
			String parameterType) {
		this.name = name;
		this.extractorClass = extractorClass;
		this.parameterType = parameterType;
	}

	public ExtractionAttempt tryRecognize(String query, EvalContext context, EvalResult result) {
		// TODO: try to get it right away, without many nested calls
		ParameterExtractor extractor = context.createExtractor(this);
		ExtractionAttempt attempt = extractor.tryExtract(query, this, result);
		result.recordAttempt(attempt);
		return attempt;
	}

	public String getName() {
		return name;
	}

	public Class<? extends ParameterExtractor> getExtractorClass() {
		return extractorClass;
	}

	@Override
	public String toString() {
		return "QueryParameter [name=" + name + ", extractorClass=" + extractorClass + ", parameterType="
				+ parameterType + "]";
	}

}
