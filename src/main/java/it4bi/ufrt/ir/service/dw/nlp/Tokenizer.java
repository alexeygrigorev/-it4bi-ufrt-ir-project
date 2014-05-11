package it4bi.ufrt.ir.service.dw.nlp;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.springframework.stereotype.Component;

import com.google.common.base.Throwables;

@Component
public class Tokenizer {

	public List<String> tokenizeAndStem(String string) {
		try {
			return doTokenize(string);
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	private List<String> doTokenize(String string) throws IOException {
		TokenStream stream = new StandardTokenizer(Version.LUCENE_47, new StringReader(string));
		// don't care about stop words
		stream = new PorterStemFilter(stream);

		List<String> tokens = new ArrayList<String>();

		stream.reset();
		while (stream.incrementToken()) {
			String term = stream.getAttribute(CharTermAttribute.class).toString();
			tokens.add(term.toLowerCase());
		}
		stream.end();

		stream.close();
		return tokens;
	}

}
