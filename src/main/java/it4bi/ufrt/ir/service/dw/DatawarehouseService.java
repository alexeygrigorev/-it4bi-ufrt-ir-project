package it4bi.ufrt.ir.service.dw;

import it4bi.ufrt.ir.service.dw.eval.AllEvaluationResults;
import it4bi.ufrt.ir.service.dw.eval.Evaluator;
import it4bi.ufrt.ir.service.dw.nlp.QueryPreprocessor;
import it4bi.ufrt.ir.service.users.User;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DatawarehouseService {

	private final QueryPreprocessor queryPreprocessor;
	private final Evaluator evaluator;

	@Autowired
	public DatawarehouseService(QueryPreprocessor queryPreprocessor, Evaluator evaluator) {
		this.queryPreprocessor = queryPreprocessor;
		this.evaluator = evaluator;
	}

	public DwhDtoResults find(String freeTextQuery, User user) {
		UserQuery query = queryPreprocessor.preprocess(freeTextQuery);
		AllEvaluationResults results = evaluator.evaluate(query);
		List<MatchedQueryTemplate> matched = results.getMatchedTemplates();

		List<MatchedQueryTemplate> recommended = Collections.emptyList();
		return new DwhDtoResults(matched, recommended);
	}

	public ExecutedDwhQuery execute(MatchedQueryTemplate template) {
		return new ExecutedDwhQuery();
	}

}
