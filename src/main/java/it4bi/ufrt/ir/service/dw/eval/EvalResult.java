package it4bi.ufrt.ir.service.dw.eval;

import it4bi.ufrt.ir.service.dw.eval.extractor.ExtractionAttempt;
import it4bi.ufrt.ir.service.dw.ner.NamedEntity;
import it4bi.ufrt.ir.service.dw.ner.NamedEntityClass;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * Not Thread safe!
 */
public class EvalResult {

	private boolean success = true;

	private final Multimap<NamedEntityClass, NamedEntity> namedEntitites = LinkedHashMultimap.create();
	private final Map<String, String> foundParams = Maps.newHashMap();
	private final Set<String> allNames = Sets.newHashSet();

	public void addNamedEntities(Collection<NamedEntity> nes) {
		for (NamedEntity ne : nes) {
			namedEntitites.put(ne.getNerClass(), ne);
		}
	}

	public Optional<NamedEntity> nextNamedEntityOf(NamedEntityClass neClass) {
		Collection<NamedEntity> collection = namedEntitites.get(neClass);
		Iterator<NamedEntity> it = collection.iterator();

		if (!it.hasNext()) {
			return Optional.absent();
		}

		NamedEntity next = it.next();
		it.remove();
		return Optional.of(next);
	}
	
	public Optional<NamedEntity> peekNamedEntityOf(NamedEntityClass neClass) {
		Collection<NamedEntity> collection = namedEntitites.get(neClass);
		Iterator<NamedEntity> it = collection.iterator();

		if (!it.hasNext()) {
			return Optional.absent();
		}

		NamedEntity next = it.next();
		return Optional.of(next);
	}
		

	public void recordAttempt(ExtractionAttempt attempt) {
		String name = attempt.getParameter().getName();
		allNames.add(name);

		if (!attempt.isSuccessful()) {
			success = false;
		} else {
			foundParams.put(name, attempt.getValue());
		}
	}

	public boolean isSatisfied() {
		return success;
	}

	public int unsatisfiedParams() {
		Set<String> foundParamsNames = foundParams.keySet();
		SetView<String> diff = Sets.difference(allNames, foundParamsNames);
		return diff.size();
	}

}
