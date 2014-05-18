package it4bi.ufrt.ir.service.dw.eval.extractor;

import org.apache.commons.lang3.Validate;

import it4bi.ufrt.ir.service.dw.eval.QueryParameter;

/**
 * Holds the results of attempting to extract a parameter of some {@link ParameterExtractor}. Can be
 * successful or not.<br>
 * 
 * @see ParameterExtractor
 * @see QueryParameter
 */
public class ExtractionAttempt {

	private final boolean successful;
	private final QueryParameter parameter;
	private final String value;
	private final double score;

	private ExtractionAttempt(boolean successful, QueryParameter parameter, String value, double score) {
		this.successful = successful;
		this.parameter = parameter;
		this.value = value;
		this.score = score;
	}

	/**
	 * @param parameter that wasn't extracted
	 * @return a non-successful extraction attempt
	 */
	public static ExtractionAttempt notSuccessful(QueryParameter parameter) {
		return new ExtractionAttempt(false, parameter, null, 0.0);
	}

	/**
	 * @param parameter that was extracted
	 * @param value the extracted value for this parameter
	 * @return a successful extraction attempt
	 */
	public static ExtractionAttempt successful(QueryParameter parameter, String value) {
		return new ExtractionAttempt(true, parameter, value, 1.0);
	}

	/**
	 * @param parameter that was extracted
	 * @param value the extracted value for this parameter
	 * @return a successful extraction attempt
	 */
	public static ExtractionAttempt successful(QueryParameter parameter, String value, double confidence) {
		return new ExtractionAttempt(true, parameter, value, confidence);
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

	public double getScore() {
		return score;
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
