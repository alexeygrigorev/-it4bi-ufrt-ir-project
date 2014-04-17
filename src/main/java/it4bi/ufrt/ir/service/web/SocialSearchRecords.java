package it4bi.ufrt.ir.service.web;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "items")
@XmlAccessorType (XmlAccessType.FIELD)
public class SocialSearchRecords {
	
	@XmlElement(name = "item")
    private List<SocialSearchRecord> records = new ArrayList<SocialSearchRecord>();
 
    public List<SocialSearchRecord> getRecords() {
        return records;
    }
 
    public void setEmployees(List<SocialSearchRecord> records) {
        this.records = records;
    }

}
