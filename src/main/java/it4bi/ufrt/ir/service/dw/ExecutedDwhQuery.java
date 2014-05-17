package it4bi.ufrt.ir.service.dw;

import java.util.ArrayList;
import java.util.List;

/**
 * The result of executing a {@link MatchedQueryTemplate} against the data warehouse <br>
 * <br>
 * This is a dto object and to be passed to the client side
 */
public class ExecutedDwhQuery {

	private final String dem = ";";
	private List<String> columnNames;
	private List<List<String>> rows;
	// The same as rows, but internal array is now string delimeted by ; 
	private List<String> rows2;
	private String queryName;

	public ExecutedDwhQuery() {
	}
	
	public ExecutedDwhQuery(List<String> columnNames, List<List<String>> rows) {
		this.columnNames = columnNames;
		this.rows = rows;
		this.rows2 = mergeArrayIntoString(rows);
	}
	
	private List<String> mergeArrayIntoString(List<List<String>> donor) {
		List<String> r = new ArrayList<String>();
		
		for (List<String> donorRow : donor) {
            String donorRowStr = "";
			for (String donorStr : donorRow) {
				donorRowStr = donorRowStr + this.dem + donorStr;
			}
			donorRowStr = donorRowStr.substring(1);
			r.add(donorRowStr);
		}
		return r;
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
	
	public List<String> getRows2() {
		return rows2;
	}

	public void setRows2(List<String> rows2) {
		this.rows2 = rows2;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}
	
	public String getQueryName() {
		return queryName;
	}
}
