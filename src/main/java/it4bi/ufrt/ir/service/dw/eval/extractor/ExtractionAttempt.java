package it4bi.ufrt.ir.service.dw.eval.extractor;

import org.apache.commons.lang3.Validate;

import it4bi.ufrt.ir.service.dw.eval.QueryParameter;

/**
 * Holds the results of attempting to extract a parameter of some {@link ParameterExtractor}. Can be
 * successful or not.
 * 
 * @see ParameterExtractor
 * @see QueryParameter
 */
public class ExtractionAttempt {

	private boolean successful = false;
	private QueryParameter parameter;
	private String value;

	private ExtractionAttempt(boolean successful, QueryParameter parameter, String value) {
		this.successful = successful;
		this.parameter = parameter;
		this.value = value;
	}

	/**
	 * @param parameter that wasn't extracted
	 * @return a non-successful extraction attempt
	 */
	public static ExtractionAttempt notSuccessful(QueryParameter parameter) {
		return new ExtractionAttempt(false, parameter, null);
	}

	/**
	 * @param parameter that was extracted
	 * @param value the extracted value for this parameter
	 * @return a successful extraction attempt
	 */
	public static ExtractionAttempt successful(QueryParameter parameter, String value) {
		return new ExtractionAttempt(true, parameter, value);
	}

	/**
	 * @return <code>true</code> if attempt was successful, <code>false</code> otherwise
	 */
	public boolean isSuccessful() {
		return successful;
	}

	/**
	 * @return parameter for which the value was extracted
	 */
	public QueryParameter getParameter() {
		return parameter;
	}

	/**
	 * @return value extracted by parameter extractor
	 * @throws IllegalStateException if getting the value from an unsuccessful attempt
	 */
	public String getValue() {
		Validate.validState(successful);
		return value;
	}
}
