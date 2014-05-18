package it4bi.ufrt.ir.service.dw;

import it4bi.ufrt.ir.service.dw.eval.QueryTemplate;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Maps;

/**
 * Result of a successful match of user's query with some query template. <br>
 * <br>
 * This is a dto object and to be passed to and from the client side
 * 
 * @see QueryTemplate
 */
public class MatchedQueryTemplate implements Comparable<MatchedQueryTemplate> {

	private int templateId;
	private Map<String, String> parameters = Maps.newHashMap();
	private String name;
	private double relevance;
	private double additionalRelevance = 0;

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

	public double getRelevance() {
		return relevance;
	}

	public void setRelevance(double relevance) {
		this.relevance = relevance;
	}

	public double getAdditionalRelevance() {
		return additionalRelevance;
	}

	public void addRelevance(double add) {
		this.additionalRelevance = this.additionalRelevance + add;
	}

	public void setAdditionalRelevance(double additionalRelevance) {
		this.additionalRelevance = additionalRelevance;
	}

	public double getTotalRelevance() {
		return relevance + additionalRelevance;
	}

	@Override
	public int compareTo(MatchedQueryTemplate o) {
		Validate.notNull(o);
		return ComparisonChain.start()
				.compare(parameters.size(), o.parameters.size(), Collections.reverseOrder())
				.compare(getTotalRelevance(), o.getTotalRelevance(), Collections.reverseOrder()).result();
	}

	@Override
	public String toString() {
		return "MatchedQueryTemplate [templateId=" + templateId + ", parameters=" + parameters + ", name="
				+ name + ", relevance=" + relevance + ", additionalRelevance=" + additionalRelevance
				+ ", getTotalRelevance()=" + getTotalRelevance() + "]";
	}

}
