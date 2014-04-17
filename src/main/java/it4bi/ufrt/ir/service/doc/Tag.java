package it4bi.ufrt.ir.service.doc;

public class Tag {
	public int getTagId() {
		return tagId;
	}

	public String getTag() {
		return tag;
	}

	public int tagId;
	static public int tagIdCounter = 10000;
	public String tag;
	
	public Tag(String tag) {
		this.tag = tag;
		tagId = tagIdCounter++;
	}
}
