package it4bi.ufrt.ir.service.web;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "item")
@XmlAccessorType (XmlAccessType.FIELD)

public class SocialSearchRecord {
	
	@XmlElement(name = "id")
	private String ID;
	
	private String title;
	private String description;
	private String link;
	private String timestamp;
	private String user;
	
	@XmlElement(name = "userlink")
	private String user_link;
	private String source;
	
	@XmlElement(name = "type")
	private String socialMentionType;
	
	private SocialSearchType socialSource;
	
	private String sentiment;
	
	public SocialSearchRecord(){
		
	}
	
	
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getUserLink() {
		return user_link;
	}
	public void setUserLink(String userLink) {
		this.user_link = userLink;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public SocialSearchType getSocialSource() {
		return socialSource;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SocialSearchRecord [ID=");
		builder.append(ID);
		builder.append(", title=");
		builder.append(title);
		builder.append(", description=");
		builder.append(description);
		builder.append(", link=");
		builder.append(link);
		builder.append(", timestamp=");
		builder.append(timestamp);
		builder.append(", user=");
		builder.append(user);
		builder.append(", user_link=");
		builder.append(user_link);
		builder.append(", source=");
		builder.append(source);
		builder.append(", type=");
		builder.append(socialMentionType);
		builder.append(", socialSource=");
		builder.append(socialSource);
		builder.append(", sentiment=");
		builder.append(sentiment);
		builder.append("]");
		return builder.toString();
	}
	public String getSocialMentionType() {
		return socialMentionType;
	}
	public void setSocialMentionType(String socialMentionType) {
		this.socialMentionType = socialMentionType;
		
	}
	
	public SocialSearchRecord mapSocialSearchType (){
		
		if("news".equalsIgnoreCase(socialMentionType)){
			socialSource = SocialSearchType.NEWS;
			return this;
		}
		
		if("videos".equalsIgnoreCase(socialMentionType)){
			socialSource = SocialSearchType.VIDEOS;
			return this;
		}
			
		if("facebook".equalsIgnoreCase(source)){
			socialSource = SocialSearchType.FACEBOOK;
			return this;
		}
		
		if("twitter".equalsIgnoreCase(source)){
			socialSource = SocialSearchType.TWITTER;
			return this;
		}
		
		
		
		return this;
	}


	public String getSentiment() {
		return sentiment;
	}


	public void setSentiment(String sentiment) {
		this.sentiment = sentiment;
	}
	
	
	
	

}
