package it4bi.ufrt.ir.service.dw.eval;

import it4bi.ufrt.ir.service.dw.eval.extractor.ExtractorInstantiator;
import it4bi.ufrt.ir.service.dw.ner.NamedEntity;
import it4bi.ufrt.ir.service.dw.ner.NerRecognizer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Evaluator {

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

	public void evaluate(String query) {
		List<NamedEntity> namedEntities = nerRecognizer.recognize(query);
		EvalContext context = new EvalContext(query, extractorInstantiator);
		context.setNamedEntities(namedEntities);

		List<QueryTemplate> templates = queryTemplateDao.all();
		for (QueryTemplate template : templates) {
			EvalResult result = template.evaluate(query, context);
			if (result.isSatisfied()) {
				// produce smth and return
			}
		}
	}

}
