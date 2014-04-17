package it4bi.ufrt.ir.service.dw.eval.extractor;

import org.apache.commons.lang3.Validate;

import it4bi.ufrt.ir.service.dw.eval.QueryParameter;

public class ExtractionAttempt {

	private boolean successful = false;
	private QueryParameter parameter;
	private String value;

	private ExtractionAttempt(boolean successful, QueryParameter parameter, String value) {
		this.successful = successful;
		this.parameter = parameter;
		this.value = value;
	}

	public static ExtractionAttempt notSuccessful(QueryParameter parameter) {
		return new ExtractionAttempt(false, parameter, null);
	}

	public static ExtractionAttempt successful(QueryParameter parameter, String value) {
		return new ExtractionAttempt(true, parameter, value);
	}

	public boolean isSuccessful() {
		return successful;
	}

	public QueryParameter getParameter() {
		return parameter;
	}

	public String getValue() {
		Validate.validState(successful);
		return value;
	}
}
