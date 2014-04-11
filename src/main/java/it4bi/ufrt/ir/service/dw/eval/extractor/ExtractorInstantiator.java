package it4bi.ufrt.ir.service.dw.eval.extractor;

import it4bi.ufrt.ir.service.dw.eval.QueryParameter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

@Component
public class ExtractorInstantiator {

	@Autowired
	private AutowireCapableBeanFactory factory;

	public ParameterExtractor instantiate(QueryParameter parameter) {
		return instantiate(parameter.getExtractorClass());
	}

	public <T extends ParameterExtractor> T instantiate(Class<T> extractorClass) {
		return factory.createBean(extractorClass);
	}

}
