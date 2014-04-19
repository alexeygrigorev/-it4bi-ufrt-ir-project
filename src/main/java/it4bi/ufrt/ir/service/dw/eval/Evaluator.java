package it4bi.ufrt.ir.service.dw.eval;

import it4bi.ufrt.ir.service.dw.db.QueryTemplateDao;
import it4bi.ufrt.ir.service.dw.eval.extractor.ExtractorInstantiator;
import it4bi.ufrt.ir.service.dw.ner.NamedEntity;
import it4bi.ufrt.ir.service.dw.ner.NamedEntitiesRecognizer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Evaluator {

	private static final Logger LOGGER = LoggerFactory.getLogger(Evaluator.class);

	private final NamedEntitiesRecognizer nerRecognizer;
	private final QueryTemplateDao queryTemplateDao;
	private final ExtractorInstantiator extractorInstantiator;

	@Autowired
	public Evaluator(NamedEntitiesRecognizer nerRecognizer, QueryTemplateDao queryTemplateDao,
			ExtractorInstantiator extractorInstantiator) {
		this.nerRecognizer = nerRecognizer;
		this.queryTemplateDao = queryTemplateDao;
		this.extractorInstantiator = extractorInstantiator;
	}

	public AllEvaluationResults evaluate(String query) {
		LOGGER.debug("Evaluating query \"{}\"", query);

		GlobalEvaluationContext context = new GlobalEvaluationContext(query, extractorInstantiator);

		List<NamedEntity> namedEntities = nerRecognizer.recognize(query);
		context.setNamedEntities(namedEntities);

		AllEvaluationResults allResults = new AllEvaluationResults();
		List<QueryTemplate> templates = queryTemplateDao.all();
		for (QueryTemplate template : templates) {
			EvaluationResult evaluationResult = template.evaluate(query, context);
			allResults.add(evaluationResult);
		}

		return allResults;
	}

}
