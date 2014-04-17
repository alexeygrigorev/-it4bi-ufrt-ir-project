package it4bi.ufrt.ir.service.dw.eval;

import java.util.List;

import com.google.common.collect.Lists;

//TODO consider renaming
public class AllResults {

	private final List<EvalResult> successful = Lists.newArrayList();
	private final List<Query> queries = Lists.newArrayList();
	private final List<EvalResult> notSuccessful = Lists.newArrayList();

	public void add(EvalResult result) {
		if (result.isSatisfied()) {
			successful.add(result);
			queries.add(result.toQueryWithFoundParameters());
		} else {
			notSuccessful.add(result);
		}
	}

	public List<Query> getQueries() {
		return queries;
	}

}
