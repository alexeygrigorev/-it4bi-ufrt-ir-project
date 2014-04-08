package it4bi.ufrt.ir.service.dw.ner;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

public class NerRecognizer {

	private static final String DEFAULT_SERIALIZED_CLASSIFIER = "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz";
	private final CRFClassifier<CoreLabel> classifier;

	private NerRecognizer(CRFClassifier<CoreLabel> classifier) {
		this.classifier = classifier;
	}

	public static NerRecognizer readDefault() {
		CRFClassifier<CoreLabel> classifier = CRFClassifier
				.getClassifierNoExceptions(DEFAULT_SERIALIZED_CLASSIFIER);
		return new NerRecognizer(classifier);
	}

	public List<NamedEntity> recognize(String query) {
		List<List<CoreLabel>> out = classifier.classify(query);
		List<NamedEntity> results = new ArrayList<>();

		for (List<CoreLabel> list : out) {
			List<NamedEntity> subresult = groupBy(list);
			results.addAll(subresult);
		}

		return results;
	}

	private List<NamedEntity> groupBy(List<CoreLabel> list) {
		List<NamedEntity> results = new ArrayList<>();

		String prevLabel = null;
		String token = "";

		for (CoreLabel cl : list) {
			String cls = cl.get(CoreAnnotations.AnswerAnnotation.class);

			if (cls.equals("O")) {
				if (prevLabel != null) {
					results.add(new NamedEntity(token.trim(), prevLabel));
				}

				prevLabel = null;
				token = "";
				continue;
			}

			if (prevLabel == null || cls.equals(prevLabel)) {
				prevLabel = cls;
				token = token + " " + cl.value();
			} else {
				results.add(new NamedEntity(token.trim(), prevLabel));
				prevLabel = cls;
				token = cl.value();
			}
		}

		if (prevLabel != null) {
			results.add(new NamedEntity(token.trim(), prevLabel));
		}

		return results;
	}

}
