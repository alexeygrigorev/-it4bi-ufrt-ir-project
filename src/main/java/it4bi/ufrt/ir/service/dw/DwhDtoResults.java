package it4bi.ufrt.ir.service.dw;

import java.util.List;

import com.google.common.collect.Ordering;

/**
 * The result of matching user queries against the query templates. Also contains recommendations for an user
 * of other query templates, ones that are not matched directly, but can be potentially interesting. <br>
 * <br>
 * This is a dto object and to be passed to the client side
 */
public class DwhDtoResults {

	private List<MatchedQueryTemplate> matched;
	private List<MatchedQueryTemplate> recommended;

	public DwhDtoResults() {
	}

	public DwhDtoResults(List<MatchedQueryTemplate> matched, List<MatchedQueryTemplate> recommended) {
		this.matched = sort(matched);
		this.recommended = sort(recommended);
	}

	private static List<MatchedQueryTemplate> sort(List<MatchedQueryTemplate> list) {
		return Ordering.natural().sortedCopy(list);
	}

	public List<MatchedQueryTemplate> getMatched() {
		return matched;
	}

	public void setMatched(List<MatchedQueryTemplate> matched) {
		this.matched = matched;
	}

	public List<MatchedQueryTemplate> getRecommended() {
		return recommended;
	}

	public void setRecommended(List<MatchedQueryTemplate> recommended) {
		this.recommended = recommended;
	}

	@Override
	public String toString() {
		return "DwhDtoResults [matched=" + matched + ", recommended=" + recommended + "]";
	}

}
