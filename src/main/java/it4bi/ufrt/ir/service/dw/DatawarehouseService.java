package it4bi.ufrt.ir.service.dw;

import it4bi.ufrt.ir.service.dw.db.DatawarehouseDao;
import it4bi.ufrt.ir.service.dw.db.QueryTemplateDao;
import it4bi.ufrt.ir.service.dw.eval.AllEvaluationResults;
import it4bi.ufrt.ir.service.dw.eval.Evaluator;
import it4bi.ufrt.ir.service.dw.eval.QueryTemplate;
import it4bi.ufrt.ir.service.dw.nlp.QueryPreprocessor;
import it4bi.ufrt.ir.service.users.User;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Optional;

@Component
public class DatawarehouseService {

	private final QueryPreprocessor queryPreprocessor;
	private final Evaluator evaluator;

	private final DatawarehouseDao datawarehouseDao;
	private final QueryTemplateDao queryTemplateDao;

	@Autowired
	public DatawarehouseService(QueryPreprocessor queryPreprocessor, Evaluator evaluator,
			DatawarehouseDao datawarehouseDao, QueryTemplateDao queryTemplateDao) {
		this.queryPreprocessor = queryPreprocessor;
		this.evaluator = evaluator;
		this.datawarehouseDao = datawarehouseDao;
		this.queryTemplateDao = queryTemplateDao;
	}

	public DwhDtoResults find(String freeTextQuery, User user) {
		UserQuery query = queryPreprocessor.preprocess(freeTextQuery);
		AllEvaluationResults results = evaluator.evaluate(query);
		List<MatchedQueryTemplate> matched = results.getMatchedTemplates();

		List<MatchedQueryTemplate> recommended = Collections.emptyList();
		return new DwhDtoResults(matched, recommended);
	}

	public ExecutedDwhQuery execute(MatchedQueryTemplate template) {
		int templateId = template.getTemplateId();
		Optional<QueryTemplate> query = queryTemplateDao.byId(templateId);
		Validate.isTrue(query.isPresent(), "unexisting query template with id=%d", templateId);
		ExecutedDwhQuery res = datawarehouseDao.execute(query.get(), template.getParameters());
		res.setQueryName(template.getName());
		return res;
	}

}
