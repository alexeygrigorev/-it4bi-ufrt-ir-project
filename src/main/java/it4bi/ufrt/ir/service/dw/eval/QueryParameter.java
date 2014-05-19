package it4bi.ufrt.ir.service.dw.eval;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}
