package it4bi.ufrt.ir.service.dw.eval;

import it4bi.ufrt.ir.service.dw.MatchedQueryTemplate;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * All the results found after processing some user query in the free text form, returned by {@link Evaluator}
 * 
 * @see Evaluator
 * @see EvaluationResult
 */
public class AllEvaluationResults {

	private final List<EvaluationResult> successful = Lists.newArrayList();
	private final List<MatchedQueryTemplate> matchedTemplates = Lists.newArrayList();
	private final List<EvaluationResult> notSuccessful = Lists.newArrayList();

	public void add(EvaluationResult result) {
		if (result.isSatisfied()) {
			successful.add(result);
			matchedTemplates.add(result.asDto());
		} else {
			notSuccessful.add(result);
		}
	}

	public List<MatchedQueryTemplate> getMatchedTemplates() {
		return matchedTemplates;
	}


}
