package it4bi.ufrt.ir.service.dw.nlp;

import it4bi.ufrt.ir.service.dw.UserQuery;
import it4bi.ufrt.ir.service.dw.ner.NamedEntitiesRecognizer;
import it4bi.ufrt.ir.service.dw.ner.RecognizedNamedEntities;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueryPreprocessor {

	private final NamedEntitiesRecognizer nerRecognizer;
	private final Tokenizer tokenizer;
	
	@Autowired
	public QueryPreprocessor(NamedEntitiesRecognizer nerRecognizer, Tokenizer tokenizer) {
		this.nerRecognizer = nerRecognizer;
		this.tokenizer = tokenizer;
	}

	public UserQuery preprocess(String query) {
		RecognizedNamedEntities namedEntities = nerRecognizer.recognize(query);
		List<String> tokens = tokenizer.tokenizeAndStem(query);
		return new UserQuery(query, namedEntities, tokens);
	}

}
