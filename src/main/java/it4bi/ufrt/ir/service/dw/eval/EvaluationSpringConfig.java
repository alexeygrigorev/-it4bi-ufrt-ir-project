package it4bi.ufrt.ir.service.dw.eval;

import it4bi.ufrt.ir.service.dw.ner.NerRecognizer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EvaluationSpringConfig {

	@Bean
	public NerRecognizer nerRecognizer() {
		return NerRecognizer.loadDefault();
	}

}
