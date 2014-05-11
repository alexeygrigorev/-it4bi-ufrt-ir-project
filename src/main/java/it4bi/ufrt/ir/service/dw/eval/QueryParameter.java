package it4bi.ufrt.ir.service.dw.eval;

import it4bi.ufrt.ir.service.dw.eval.extractor.ParameterExtractor;

public class QueryParameter {

	private final String name;
	private final Class<? extends ParameterExtractor> extractorClass;
	private final String parameterType;

	public QueryParameter(String name, Class<? extends ParameterExtractor> extractorClass,
			String parameterType) {
		this.name = name;
		this.extractorClass = extractorClass;
		this.parameterType = parameterType;
	}

	public String getName() {
		return name;
	}

	public Class<? extends ParameterExtractor> getExtractorClass() {
		return extractorClass;
	}

	public String getParameterType() {
		return parameterType;
	}

	@Override
	public String toString() {
		return "QueryParameter [name=" + name + ", extractorClass=" + extractorClass + ", parameterType="
				+ parameterType + "]";
	}

}
