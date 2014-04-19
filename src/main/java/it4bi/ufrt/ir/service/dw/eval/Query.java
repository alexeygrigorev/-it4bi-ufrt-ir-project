package it4bi.ufrt.ir.service.dw.eval;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

public class Query {

	private final QueryTemplate template;
	private final Map<String, String> params;

	public Query(QueryTemplate template, Map<String, String> params) {
		this.template = template;
		this.params = params;
	}

	public String getTitle() {
		String[] searchList = new String[params.size()];
		String[] replacementList = new String[params.size()];

		int i = 0;
		for (Entry<String, String> e : params.entrySet()) {
			searchList[i] = ":" + e.getKey();
			replacementList[i] = e.getValue();
			i++;
		}

		String name = template.getName();
		return StringUtils.replaceEachRepeatedly(name, searchList, replacementList);
	}

	public Map<String, String> getSqlQueryParams() {
		return params;
	}

	public String getSqlQuery() {
		return template.getSqlTemplate();
	}

	public int getQueryTemplateId() {
		return template.getId();
	}

}
