package it4bi.ufrt.ir.service.autocomplete;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AutocompleteService {
	
	@Value("${autocomplete.dict.location.countries}")
	private String dictCountries;
	
	@Value("${autocomplete.dict.location.coaches}")
	private String dictCoaches;
	
	@Value("${autocomplete.dict.location.players}")
	private String dictPlayers;
	
	@Value("${autocomplete.dict.location.referees}")
	private String dictReferees;
	
	@Value("${autocomplete.dict.location.uncategorized}")
	private String dictUncategorized;			

	public List<AutocompleteEntry> getList(){
		ArrayList<AutocompleteEntry> entries = new ArrayList<AutocompleteEntry>();
		
		try {
			entries.addAll(createListFromFile(dictUncategorized, ""));
			entries.addAll(createListFromFile(dictCountries, "Countries"));
			entries.addAll(createListFromFile(dictCoaches, "Coaches"));
			entries.addAll(createListFromFile(dictPlayers, "Players"));
			entries.addAll(createListFromFile(dictReferees, "Referees"));
		} catch (IOException e) {
			entries.add(new AutocompleteEntry("Dictionaries not found", ""));
			e.printStackTrace();
		}				
		
		return entries;
	}
	
	private List<AutocompleteEntry> createListFromFile(String filePath, String category) throws IOException{
		ArrayList<AutocompleteEntry> entries = new ArrayList<AutocompleteEntry>();
		
		File dict = new File(filePath);
		BufferedReader br = new BufferedReader(new FileReader(dict));
		String line;
		while ((line = br.readLine()) != null) {
			AutocompleteEntry entry = new AutocompleteEntry(line, category);
			entries.add(entry);
		}
		br.close();
		return entries;
	}
	
}
