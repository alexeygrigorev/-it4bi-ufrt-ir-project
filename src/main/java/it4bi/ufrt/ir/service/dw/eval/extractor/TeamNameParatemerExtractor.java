package it4bi.ufrt.ir.service.dw.eval.extractor;

import it4bi.ufrt.ir.service.dw.UserQuery;
import it4bi.ufrt.ir.service.dw.db.DatawarehouseDao;
import it4bi.ufrt.ir.service.dw.eval.EvaluationResult;
import it4bi.ufrt.ir.service.dw.eval.QueryParameter;
import it4bi.ufrt.ir.service.dw.ner.NamedEntity;
import it4bi.ufrt.ir.service.dw.ner.NamedEntityClass;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Optional;

/**
 * Extractor that knows how to extract country names from free text
 */
public class TeamNameParatemerExtractor implements ParameterExtractor {

	private static final Logger LOGGER = LoggerFactory.getLogger(TeamNameParatemerExtractor.class);

	private final DatawarehouseDao dao;

	@Autowired
	public TeamNameParatemerExtractor(DatawarehouseDao dao) {
		this.dao = dao;
	}

	@Override
	public ExtractionAttempt tryExtract(UserQuery query, QueryParameter parameter, EvaluationResult result) {
		Iterator<NamedEntity> it = result.namedEntitiesOf(NamedEntityClass.LOCATION);

		while (it.hasNext()) {
			NamedEntity location = it.next();
			Optional<String> canonicalCountry = dao.canonicalCountry(location.getToken());
			LOGGER.debug("canonical name for {} is {}", location.getToken(), canonicalCountry);

			if (!canonicalCountry.isPresent()) {
				LOGGER.debug("no canonical country is found for {}", location.getToken());
				continue;
			}

			ExtractionAttempt match = ExtractionAttempt.successful(parameter, canonicalCountry.get());
			it.remove();
			return match;
		}

		return ExtractionAttempt.notSuccessful(parameter);
	}

	public DatawarehouseDao getDao() {
		return dao;
	}
}
