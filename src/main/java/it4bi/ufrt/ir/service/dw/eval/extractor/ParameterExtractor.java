package it4bi.ufrt.ir.service.dw.eval.extractor;

import it4bi.ufrt.ir.service.dw.eval.EvalResult;
import it4bi.ufrt.ir.service.dw.eval.QueryParameter;

public interface ParameterExtractor {

	ExtractionAttempt tryExtract(String query, QueryParameter queryParameter, EvalResult result);

}
