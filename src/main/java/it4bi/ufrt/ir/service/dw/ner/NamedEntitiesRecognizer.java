package it4bi.ufrt.ir.service.dw.ner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.Validate;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

/**
 * Recognizes named entities in a free text. The classes that can be recognized are in
 * {@link NamedEntityClass}
 * 
 * @see NamedEntity
 * @see NamedEntityClass
 */
public class NamedEntitiesRecognizer {

	private final LazyClassifier classifier;

	/**
	 * Loads the default lazy classifier. Run {@link #init()} if you need to make sure it gets initialized
	 * before used, otherwise it's initialized only when you try to use it
	 * 
	 * @return
	 */
	public static NamedEntitiesRecognizer loadDefault() {
		return new NamedEntitiesRecognizer(LazyClassifier.defaultClassifier());
	}

	private NamedEntitiesRecognizer(LazyClassifier classifier) {
		this.classifier = classifier;
	}

	/**
	 * Recognizes all named entities for the given query
	 * 
	 * @param query free text user query
	 * @return all found entities
	 */
	public List<NamedEntity> recognize(String query) {
		List<List<CoreLabel>> out = classifier.classify(query);
		List<List<CoreLabel>> groups = groupAllByClass(out);
		return extractNamedEntities(groups);
	}

	/**
	 * In a list, a group is a sequence of labels that belong to the same class. This methods finds such
	 * groups and puts each in a separate list
	 * 
	 * @param out to group
	 * @return labels grouped by class
	 */
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

	/**
	 * Within the given list of groups, finds all that don't belong to class "O", and then forms
	 * {@link NamedEntity} classes from them
	 * 
	 * @param groups
	 * @return
	 */
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

	/**
	 * Joins all labels within the given group on " " and then forms a {@link NamedEntity} from the result
	 */
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

	/**
	 * Call this method if you need to make sure the recognizer is fully initialized before you use it
	 * 
	 * @return this instance
	 */
	public NamedEntitiesRecognizer init() {
		classifier.init();
		return this;
	}

}
