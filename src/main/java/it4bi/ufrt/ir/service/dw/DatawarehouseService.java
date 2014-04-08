package it4bi.ufrt.ir.service.dw;

import it4bi.ufrt.ir.service.dw.evaluation.QueryTemplateConfigurationDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DatawarehouseService {

	@Autowired
	private QueryTemplateConfigurationDao configurationDao;
	
	// stuff
	
}
