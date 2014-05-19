package it4bi.ufrt.ir.service.dw;

import it4bi.ufrt.ir.service.dw.db.DatawarehouseDao;
import it4bi.ufrt.ir.service.dw.db.QueryTemplateDao;
import it4bi.ufrt.ir.service.dw.eval.AllEvaluationResults;
import it4bi.ufrt.ir.service.dw.eval.Evaluator;
import it4bi.ufrt.ir.service.dw.eval.QueryTemplate;
import it4bi.ufrt.ir.service.dw.nlp.QueryPreprocessor;
import it4bi.ufrt.ir.service.dw.rec.QueryRecommender;
import it4bi.ufrt.ir.service.users.User;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Optional;

@Component
public class DatawarehouseService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DatawarehouseService.class);
	
	private final QueryPreprocessor queryPreprocessor;
	private final Evaluator evaluator;
	private final QueryRecommender queryRecommender;

	private final DatawarehouseDao datawarehouseDao;
	private final QueryTemplateDao queryTemplateDao;

	@Autowired
	public DatawarehouseService(QueryPreprocessor queryPreprocessor, Evaluator evaluator,
			DatawarehouseDao datawarehouseDao, QueryTemplateDao queryTemplateDao, 
			QueryRecommender queryRecommender) {
		this.queryPreprocessor = queryPreprocessor;
		this.evaluator = evaluator;
		this.datawarehouseDao = datawarehouseDao;
		this.queryTemplateDao = queryTemplateDao;
		this.queryRecommender = queryRecommender;
	}

	public DwhDtoResults find(String freeTextQuery, User user) {
		LOGGER.debug("query {} for user {}", freeTextQuery, user);
		
		UserQuery query = queryPreprocessor.preprocess(freeTextQuery);
		AllEvaluationResults results = evaluator.evaluate(query);
		List<MatchedQueryTemplate> matched = results.getMatchedTemplates();
		queryRecommender.captureParameters(user, results);

		List<MatchedQueryTemplate> recommended = queryRecommender.recommend(query, user, results);
		DwhDtoResults dwhDtoResults = new DwhDtoResults(matched, recommended);

		LOGGER.debug("result of {} is {}", freeTextQuery, dwhDtoResults);
		return dwhDtoResults;
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
