package it4bi.ufrt.ir.service.dw;

import java.util.List;

public class DwhDtoResults {

	private List<MatchedQueryTemplate> matched;
	private List<MatchedQueryTemplate> recommended;

	public DwhDtoResults() {
	}

	public DwhDtoResults(List<MatchedQueryTemplate> matched, List<MatchedQueryTemplate> recommended) {
		this.matched = matched;
		this.recommended = recommended;
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

}
