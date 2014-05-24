package it4bi.ufrt.ir.service.autocomplete;

public class AutocompleteEntry {

	private String label;
	private String category;
	
	public AutocompleteEntry(){
		
	}
	
	public AutocompleteEntry(String label, String category) {
		this.label = label;
		this.category = category;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
}
