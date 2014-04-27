package it4bi.ufrt.ir.service.dw;

import java.util.Map;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Maps;

public class MatchedQueryTemplate implements Comparable<MatchedQueryTemplate> {

	private int templateId;
	private Map<String, String> parameters = Maps.newHashMap();
	private String name;
	private int relevance;

	public MatchedQueryTemplate() {
	}

	public MatchedQueryTemplate(int templateId, Map<String, String> parameters, String name, int relevance) {
		this.templateId = templateId;
		this.parameters = parameters;
		this.name = name;
		this.relevance = relevance;
	}

	public int getTemplateId() {
		return templateId;
	}

	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getRelevance() {
		return relevance;
	}

	public void setRelevance(int relevance) {
		this.relevance = relevance;
	}

	@Override
	public int compareTo(MatchedQueryTemplate o) {
		Validate.notNull(o);
		return ComparisonChain.start()
			.compare(parameters.size(), o.parameters.size())
			.compare(relevance, o.relevance)
			.result();
	}

	@Override
	public String toString() {
		return "MatchedQueryTemplate [templateId=" + templateId + ", parameters=" + parameters + ", name="
				+ name + ", relevance=" + relevance + "]";
	}

}
