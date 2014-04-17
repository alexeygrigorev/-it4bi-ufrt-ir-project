package it4bi.ufrt.ir.service.web;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "root")
@XmlAccessorType (XmlAccessType.FIELD)
public class SocialMentionXMLResult {
	
	@XmlElement(name = "items")
    private SocialSearchRecords results = null;
	
	@XmlElement(name = "count")
	private int resultsCount = 0;

	public SocialSearchRecords getRecords() {
		return results;
	}

	public void setRecords(SocialSearchRecords records) {
		this.results = records;
	}

	public int getRecordsCount() {
		return resultsCount;
	}

	public void setRecordsCount(int recordsCount) {
		this.resultsCount = recordsCount;
	}
 
   

}
