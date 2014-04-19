package it4bi.ufrt.ir.service.dw.eval.extractor;

import it4bi.ufrt.ir.service.dw.eval.EvaluationResult;
import it4bi.ufrt.ir.service.dw.eval.QueryParameter;

/**
 * Interface that allows defining parameter extractors from free text query
 * 
 * @see ExtractionAttempt
 * @see QueryParameter
 */
public interface ParameterExtractor {

	/**
	 * From the given text query tries to extract parameter
	 * 
	 * @param query to parse
	 * @param queryParameter to extract
	 * @param result evaluation context
	 * @return attempt, successful or not
	 */
	ExtractionAttempt tryExtract(String query, QueryParameter queryParameter, EvaluationResult result);

}
