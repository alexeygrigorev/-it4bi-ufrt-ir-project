package it4bi.ufrt.ir.service.dw.eval.extractor;

import it4bi.ufrt.ir.service.dw.UserQuery;
import it4bi.ufrt.ir.service.dw.db.Person;
import it4bi.ufrt.ir.service.dw.eval.EvaluationResult;
import it4bi.ufrt.ir.service.dw.eval.QueryParameter;
import it4bi.ufrt.ir.service.dw.ner.NamedEntity;
import it4bi.ufrt.ir.service.dw.ner.NamedEntityClass;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import com.google.common.base.Optional;
import com.google.common.collect.ComparisonChain;

/**
 * Class that knows how to extract a certain type of {@link Person} given some list of people. It traverses
 * the list and uses some similarity (see {@link Person#sameName(String)}) measure to locate the most similar entity from the given list. Typically
 * the lists are from the data warehouse. There are several implemetations, each of which knows how to extract
 * some specific type of person
 * 
 * @see Person
 * @see PlayerNameParameterExtractor
 * @see CoachNameParameterExtractor
 * @see RefereeNameParameterExtractor
 */
public abstract class NameParameterExtractor implements ParameterExtractor {

	@Override
	public ExtractionAttempt tryExtract(UserQuery query, QueryParameter parameter, EvaluationResult result) {
		Iterator<NamedEntity> it = result.namedEntitiesOf(NamedEntityClass.PERSON);
		while (it.hasNext()) {
			NamedEntity next = it.next();
			List<Person> candidates = candidates(next.getToken());
			Optional<Person> closest = findClosest(next.getToken(), candidates);
			if (closest.isPresent()) {
				return ExtractionAttempt.successful(parameter, closest.get().getFullName());
			}
		}

		return ExtractionAttempt.notSuccessful(parameter);
	}

	public abstract List<Person> candidates(String name);

	public static Optional<Person> findClosest(String name, List<Person> candidates) {
		PriorityQueue<RevevantPerson> matches = new PriorityQueue<>();
		for (Person person : candidates) {
			double relevance = person.sameName(name);
			if (relevance > 0.0) {
				matches.add(new RevevantPerson(relevance, person));
			}
		}

		if (matches.isEmpty()) {
			return Optional.absent();
		} else {
			return Optional.of(matches.poll().person);
		}
	}

	private static class RevevantPerson implements Comparable<RevevantPerson> {
		private double relevance;
		private Person person;

		public RevevantPerson(double relevance, Person person) {
			this.relevance = relevance;
			this.person = person;
		}

		@Override
		public int compareTo(RevevantPerson o) {
			return ComparisonChain.start().compare(relevance, o.relevance, Collections.reverseOrder())
					.compare(person.getFullName(), person.getFullName()).result();
		}
	}

}
