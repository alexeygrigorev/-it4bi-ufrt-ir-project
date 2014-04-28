package it4bi.ufrt.ir.service.dw.db;

import java.util.Set;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * Some person from the data warehouse. Can be Player, Coach or Referee.
 */
public class Person {
	private final int id;
	private final String fullName;
	private final Set<String> fullNameTokens;
	private final PersonType type;

	public static enum PersonType {
		PLAYER, COACH, REFEREE;
	}

	public Person(int id, String fullName, PersonType type) {
		this.id = id;
		this.fullName = fullName;
		this.fullNameTokens = tokenizeName(fullName);
		this.type = type;
	}

	public static Set<String> tokenizeName(String name) {
		String[] split = name.split("\\s+");
		Set<String> result = Sets.newLinkedHashSet();
		for (String word : split) {
			result.add(word.toLowerCase());
		}
		return result;
	}

	/**
	 * using Jaccards coefficient tells if the given name is the same as the name of this entity.
	 * For calculating the coefficient, the names are seen as set of words. 
	 * 
	 * @param freeTextName name in free text form
	 * @return confidence from 0% to 100%
	 */
	public double sameName(String freeTextName) {
		return jaccard(freeTextName);
	}

	private double jaccard(String freeTextName) {
		Set<String> tokenized = tokenizeName(freeTextName);
		SetView<String> intersection = Sets.intersection(fullNameTokens, tokenized);
		SetView<String> union = Sets.union(fullNameTokens, tokenized);
		double score = intersection.size();
		double maxScore = union.size();
		return score / maxScore;
	}

	public boolean isSameName(String freeTextName, double confidence) {
		return sameName(freeTextName) >= confidence;
	}

	public int getId() {
		return id;
	}

	public String getFullName() {
		return fullName;
	}

	public PersonType getType() {
		return type;
	}

}
