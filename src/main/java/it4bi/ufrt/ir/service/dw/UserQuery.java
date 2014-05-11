package it4bi.ufrt.ir.service.dw;

import java.util.List;

import it4bi.ufrt.ir.service.dw.ner.RecognizedNamedEntities;

/**
 * Query issued by an user along with some pre-processed stuff for this query
 * 
 * @see RecognizedNamedEntities
 * 
 */
public class UserQuery {

	private final String query;
	private final RecognizedNamedEntities namedEntities;
	private List<String> tokens;

	public UserQuery(String query, RecognizedNamedEntities namedEntities) {
		this.query = query;
		this.namedEntities = namedEntities;
	}

	public UserQuery(String query, RecognizedNamedEntities namedEntities, List<String> tokens) {
		this.query = query;
		this.namedEntities = namedEntities;
		this.tokens = tokens;
	}

	public String getFreeTextQuery() {
		return query;
	}

	public RecognizedNamedEntities getNamedEntities() {
		return namedEntities.copy();
	}

	public List<String> getStemmedTokens() {
		return tokens;
	}

	@Override
	public String toString() {
		return "UserQuery [query=" + query + ", namedEntities=" + namedEntities + ", tokens=" + tokens + "]";
	}
}
