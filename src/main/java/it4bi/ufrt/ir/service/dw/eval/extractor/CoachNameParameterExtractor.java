package it4bi.ufrt.ir.service.dw.eval.extractor;

import it4bi.ufrt.ir.service.dw.db.DatawarehouseDao;
import it4bi.ufrt.ir.service.dw.db.Person;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Class that takes care of extracting coach name form user's queries 
 * @see NameParameterExtractor
 */
public class CoachNameParameterExtractor extends NameParameterExtractor {

	private static final Logger LOGGER = LoggerFactory.getLogger(CoachNameParameterExtractor.class);
	
	@Autowired
	private DatawarehouseDao dao;

	@Override
	public List<Person> candidates(String name) {
		LOGGER.debug("retrieving candidates");
		return dao.allCoaches();
	}

}
