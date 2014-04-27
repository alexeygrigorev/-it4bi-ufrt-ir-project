package it4bi.ufrt.ir.service.dw.eval.extractor;

import it4bi.ufrt.ir.service.dw.UserQuery;
import it4bi.ufrt.ir.service.dw.db.DatawarehouseDao;
import it4bi.ufrt.ir.service.dw.eval.EvaluationResult;
import it4bi.ufrt.ir.service.dw.eval.QueryParameter;
import it4bi.ufrt.ir.service.dw.ner.NamedEntity;
import it4bi.ufrt.ir.service.dw.ner.NamedEntityClass;

import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Optional;

/**
 * Extractor that knows how to extract country names from free text
 */
public class TeamNameParatemerExtractor implements ParameterExtractor {

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

			if (!canonicalCountry.isPresent()) {
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
