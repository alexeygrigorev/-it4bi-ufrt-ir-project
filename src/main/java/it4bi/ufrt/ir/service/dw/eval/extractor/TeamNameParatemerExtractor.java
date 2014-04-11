package it4bi.ufrt.ir.service.dw.eval.extractor;

import it4bi.ufrt.ir.service.dw.db.DatawarehouseDao;
import it4bi.ufrt.ir.service.dw.eval.EvalResult;
import it4bi.ufrt.ir.service.dw.eval.QueryParameter;
import it4bi.ufrt.ir.service.dw.ner.NamedEntity;
import it4bi.ufrt.ir.service.dw.ner.NamedEntityClass;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Optional;

public class TeamNameParatemerExtractor implements ParameterExtractor {

	private final DatawarehouseDao dao;

	@Autowired
	public TeamNameParatemerExtractor(DatawarehouseDao dao) {
		this.dao = dao;
	}

	@Override
	public ExtractionAttempt tryExtract(String query, QueryParameter parameter, EvalResult result) {
		Optional<NamedEntity> next = result.nextNamedEntityOf(NamedEntityClass.LOCATION);
		while (next.isPresent()) {
			NamedEntity location = next.get();
			String canonicalCountryName = dao.canonicalCountry(location.getToken());

			if (canonicalCountryName == null) {
				// keep trying
				next = result.nextNamedEntityOf(NamedEntityClass.LOCATION);
				continue;
			}

			return ExtractionAttempt.successful(parameter, canonicalCountryName);
		}

		return ExtractionAttempt.notSuccessful(parameter);
	}

	public DatawarehouseDao getDao() {
		return dao;
	}
}
