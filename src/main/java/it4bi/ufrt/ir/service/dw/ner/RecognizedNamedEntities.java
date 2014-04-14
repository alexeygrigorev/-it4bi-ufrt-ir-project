package it4bi.ufrt.ir.service.dw.ner;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import com.google.common.base.Optional;
import com.google.common.collect.Iterators;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.PeekingIterator;

public class RecognizedNamedEntities {

	private final Multimap<NamedEntityClass, NamedEntity> namedEntitites;

	public RecognizedNamedEntities(Multimap<NamedEntityClass, NamedEntity> namedEntitites) {
		this.namedEntitites = namedEntitites;
	}

	public static RecognizedNamedEntities from(Iterable<NamedEntity> nes) {
		Multimap<NamedEntityClass, NamedEntity> namedEntitites = LinkedHashMultimap.create();

		for (NamedEntity ne : nes) {
			namedEntitites.put(ne.getNerClass(), ne);
		}

		return new RecognizedNamedEntities(namedEntitites);
	}

	public static RecognizedNamedEntities from(NamedEntity... nes) {
		return from(Arrays.asList(nes));
	}

	public PeekingIterator<NamedEntity> of(NamedEntityClass neClass) {
		Collection<NamedEntity> collection = namedEntitites.get(neClass);
		Iterator<NamedEntity> it = collection.iterator();
		return Iterators.peekingIterator(it);
	}

	public RecognizedNamedEntities copy() {
		Multimap<NamedEntityClass, NamedEntity> newMap = LinkedHashMultimap.create(namedEntitites);
		return new RecognizedNamedEntities(newMap);
	}

	@Deprecated
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

	@Deprecated
	public Optional<NamedEntity> peekNamedEntityOf(NamedEntityClass neClass) {
		Collection<NamedEntity> collection = namedEntitites.get(neClass);
		Iterator<NamedEntity> it = collection.iterator();

		if (!it.hasNext()) {
			return Optional.absent();
		}

		NamedEntity next = it.next();
		return Optional.of(next);
	}

}
