package it4bi.ufrt.ir.service.spell;

import java.util.Arrays;

public class SpellCheckerResult {

	private String originalWord;
	private boolean isFound;
	private boolean isCorrected;
	private String correctedWord;
	private String [] suggestions;
	
	public  SpellCheckerResult(){
		
	}
	public String getOriginalWord() {
		return originalWord;
	}
	public void setOriginalWord(String originalWord) {
		this.originalWord = originalWord;
	}
	public boolean isCorrected() {
		return isCorrected;
	}
	public void setCorrected(boolean isCorrected) {
		this.isCorrected = isCorrected;
	}
	public String getCorrectedWord() {
		return correctedWord;
	}
	public void setCorrectedWord(String correctedWord) {
		this.correctedWord = correctedWord;
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
		builder.append("SpellCheckerResult [originalWord=");
		builder.append(originalWord);
		builder.append(", isFound=");
		builder.append(isFound);
		builder.append(", isCorrected=");
		builder.append(isCorrected);
		builder.append(", correctedWord=");
		builder.append(correctedWord);
		builder.append(", suggestions=");
		builder.append(Arrays.toString(suggestions));
		builder.append("]");
		return builder.toString();
	}
	public boolean isFound() {
		return isFound;
	}
	public void setFound(boolean isFound) {
		this.isFound = isFound;
	}
	
	
}
