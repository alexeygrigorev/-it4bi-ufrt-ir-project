package it4bi.ufrt.ir.service.spell;

import java.util.Arrays;

public class QueryAutoCorrectionResult {
	
	private String originalQuery;
	private boolean isCorrected;
	private String correctedQuery;
	private String [] suggestions;
	public String getOriginalQuery() {
		return originalQuery;
	}
	public void setOriginalQuery(String originalQuery) {
		this.originalQuery = originalQuery;
	}
	public boolean isCorrected() {
		return isCorrected;
	}
	public void setCorrected(boolean isCorrected) {
		this.isCorrected = isCorrected;
	}
	public String getCorrectedQuery() {
		return correctedQuery;
	}
	public void setCorrectedQuery(String correctedQuery) {
		this.correctedQuery = correctedQuery;
	}
	public String[] getSuggestions() {
		return suggestions;
	}
	public void setSuggestions(String[] suggestions) {
		this.suggestions = suggestions;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("QueryAutoCorrectionResult [originalQuery=");
		builder.append(originalQuery);
		builder.append(", isCorrected=");
		builder.append(isCorrected);
		builder.append(", correctedQuery=");
		builder.append(correctedQuery);
		builder.append(", suggestions=");
		builder.append(Arrays.toString(suggestions));
		builder.append("]");
		return builder.toString();
	}
	
	

}
