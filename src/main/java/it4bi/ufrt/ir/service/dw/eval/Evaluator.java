package it4bi.ufrt.ir.service.dw.eval;

import it4bi.ufrt.ir.service.dw.eval.extractor.ExtractorInstantiator;
import it4bi.ufrt.ir.service.dw.ner.NamedEntity;
import it4bi.ufrt.ir.service.dw.ner.NerRecognizer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Evaluator {

	private static final Logger LOGGER = LoggerFactory.getLogger(Evaluator.class);

	private final NerRecognizer nerRecognizer;
	private final QueryTemplateDao queryTemplateDao;
	private final ExtractorInstantiator extractorInstantiator;

	@Autowired
	public Evaluator(NerRecognizer nerRecognizer, QueryTemplateDao queryTemplateDao,
			ExtractorInstantiator extractorInstantiator) {
		this.nerRecognizer = nerRecognizer;
		this.queryTemplateDao = queryTemplateDao;
		this.extractorInstantiator = extractorInstantiator;
	}

	public AllResults evaluate(String query) {
		LOGGER.debug("Evaluating query \"{}\"", query);

		List<NamedEntity> namedEntities = nerRecognizer.recognize(query);
		EvalContext context = new EvalContext(query, extractorInstantiator);
		context.setNamedEntities(namedEntities);

		AllResults allResults = new AllResults();
		List<QueryTemplate> templates = queryTemplateDao.all();
		for (QueryTemplate template : templates) {
			EvalResult result = template.evaluate(query, context);
			allResults.add(result);
		}

		return allResults;
	}

}
