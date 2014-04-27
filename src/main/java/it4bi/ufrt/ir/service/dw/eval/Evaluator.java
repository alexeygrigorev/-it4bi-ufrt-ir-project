package it4bi.ufrt.ir.service.dw.eval;

import it4bi.ufrt.ir.service.dw.UserQuery;
import it4bi.ufrt.ir.service.dw.db.QueryTemplateDao;
import it4bi.ufrt.ir.service.dw.eval.extractor.ExtractorInstantiator;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Evaluator {

	private static final Logger LOGGER = LoggerFactory.getLogger(Evaluator.class);

	private final QueryTemplateDao queryTemplateDao;
	private final ExtractorInstantiator extractorInstantiator;

	@Autowired
	public Evaluator(QueryTemplateDao queryTemplateDao, ExtractorInstantiator extractorInstantiator) {
		this.queryTemplateDao = queryTemplateDao;
		this.extractorInstantiator = extractorInstantiator;
	}

	public AllEvaluationResults evaluate(UserQuery query) {
		LOGGER.debug("Evaluating query \"{}\"", query.getFreeTextQuery());

		GlobalEvaluationContext context = new GlobalEvaluationContext(query, extractorInstantiator);

		AllEvaluationResults allResults = new AllEvaluationResults();
		List<QueryTemplate> templates = queryTemplateDao.all();
		for (QueryTemplate template : templates) {
			EvaluationResult evaluationResult = template.evaluate(query, context);
			allResults.add(evaluationResult);
		}

		return allResults;
	}

}
