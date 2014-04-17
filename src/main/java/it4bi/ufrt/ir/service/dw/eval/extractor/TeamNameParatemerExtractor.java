package it4bi.ufrt.ir.service.dw.eval.extractor;

import it4bi.ufrt.ir.service.dw.db.DatawarehouseDao;
import it4bi.ufrt.ir.service.dw.eval.EvalResult;
import it4bi.ufrt.ir.service.dw.eval.QueryParameter;
import it4bi.ufrt.ir.service.dw.ner.NamedEntity;
import it4bi.ufrt.ir.service.dw.ner.NamedEntityClass;

import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;

public class TeamNameParatemerExtractor implements ParameterExtractor {

	private final DatawarehouseDao dao;

	@Autowired
	public TeamNameParatemerExtractor(DatawarehouseDao dao) {
		this.dao = dao;
	}

	@Override
	public ExtractionAttempt tryExtract(String query, QueryParameter parameter, EvalResult result) {
		Iterator<NamedEntity> it = result.namedEntitiesOf(NamedEntityClass.LOCATION);

		while (it.hasNext()) {
			NamedEntity location = it.next();
			String canonicalCountryName = dao.canonicalCountry(location.getToken());

			if (canonicalCountryName == null) {
				continue;
			}

			ExtractionAttempt match = ExtractionAttempt.successful(parameter, canonicalCountryName);
			it.remove();
			return match;
		}

		return ExtractionAttempt.notSuccessful(parameter);
	}

	public DatawarehouseDao getDao() {
		return dao;
	}
}
