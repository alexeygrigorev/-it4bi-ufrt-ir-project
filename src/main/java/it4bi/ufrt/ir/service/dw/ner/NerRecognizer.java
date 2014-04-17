package it4bi.ufrt.ir.service.dw.ner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.Validate;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

public class NerRecognizer {

	private final LazyClassifier classifier;

	public static NerRecognizer loadDefault() {
		return new NerRecognizer(LazyClassifier.defaultClassifier());
	}

	private NerRecognizer(LazyClassifier classifier) {
		this.classifier = classifier;
	}

	public NerRecognizer init() {
		classifier.init();
		return this;
	}

	public List<NamedEntity> recognize(String query) {
		List<List<CoreLabel>> out = classifier.classify(query);
		List<List<CoreLabel>> groups = groupAllByClass(out);
		return extractNamedEntities(groups);
	}

	private List<List<CoreLabel>> groupAllByClass(List<List<CoreLabel>> out) {
		List<List<CoreLabel>> groups = new ArrayList<>();

		for (List<CoreLabel> list : out) {
			List<List<CoreLabel>> subresult = groupByClass(list);
			groups.addAll(subresult);
		}

		return groups;
	}

	private List<List<CoreLabel>> groupByClass(List<CoreLabel> list) {
		if (list.isEmpty()) {
			return Collections.emptyList();
		}

		List<List<CoreLabel>> results = new ArrayList<>();
		Iterator<CoreLabel> iterator = list.iterator();

		List<CoreLabel> accumulator = new ArrayList<>();
		results.add(accumulator);

		CoreLabel first = iterator.next();
		accumulator.add(first);

		String currentClass = classOf(first);

		while (iterator.hasNext()) {
			CoreLabel next = iterator.next();
			String cls = classOf(next);

			if (!cls.equals(currentClass)) {
				currentClass = cls;
				accumulator = new ArrayList<>();
				results.add(accumulator);
			}

			accumulator.add(next);
		}

		return results;
	}

	private List<NamedEntity> extractNamedEntities(List<List<CoreLabel>> groups) {
		List<NamedEntity> results = new ArrayList<>();

		for (List<CoreLabel> group : groups) {
			CoreLabel first = group.get(0);
			String cls = classOf(first);
			if (!"O".equals(cls)) {
				NamedEntity ne = namedEntityFrom(group);
				results.add(ne);
			}
		}

		return results;
	}

	private NamedEntity namedEntityFrom(List<CoreLabel> group) {
		Validate.isTrue(!group.isEmpty());

		Iterator<CoreLabel> it = group.iterator();
		CoreLabel first = it.next();
		String cls = classOf(first);

		StringBuilder result = new StringBuilder(first.value());
		while (it.hasNext()) {
			String value = it.next().value();
			result.append(" ").append(value);
		}

		String token = result.toString();

		return new NamedEntity(token, cls);
	}

	private static String classOf(CoreLabel token) {
		return token.get(CoreAnnotations.AnswerAnnotation.class);
	}

}
