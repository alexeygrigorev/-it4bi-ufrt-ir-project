package it4bi.ufrt.ir.service.dw;

import java.util.List;

/**
 * The result of executing a {@link MatchedQueryTemplate} against the data warehouse <br>
 * <br>
 * This is a dto object and to be passed to the client side
 */
public class ExecutedDwhQuery {

	private List<String> columnNames;
	private List<List<String>> rows;
	private String queryName;

	public ExecutedDwhQuery() {
	}
	
	public ExecutedDwhQuery(List<String> columnNames, List<List<String>> rows) {
		this.columnNames = columnNames;
		this.rows = rows;
	}

	public List<String> getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}

	public List<List<String>> getRows() {
		return rows;
	}

	public void setRows(List<List<String>> rows) {
		this.rows = rows;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}
	
	public String getQueryName() {
		return queryName;
	}
}
