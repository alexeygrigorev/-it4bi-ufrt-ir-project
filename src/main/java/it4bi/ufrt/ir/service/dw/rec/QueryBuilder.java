package it4bi.ufrt.ir.service.dw.rec;

import it4bi.ufrt.ir.service.dw.eval.EvaluationResult;
import it4bi.ufrt.ir.service.dw.eval.QueryParameter;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

public class QueryBuilder {

	private static final String SQL = "select top 1 {0} as no, {4} as parNo, parameter_value "
			+ "from ParameterStatistics " + "where user_id = {1,number,#} and parameter_name = {2} and "
			+ "parameter_value not in ({3}) order by [count] desc";
	private static final String SQL_EMPTY = "select top 1 {0} as no, {3} as parNo, "
			+ "parameter_value from ParameterStatistics "
			+ "where user_id = {1,number,#} and parameter_name = {2} order by [count] desc";

	private final int userId;
	private final List<EvaluationResult> unsatisfied;

	private final List<EvaluationResult> index = Lists.newArrayList();
	private final List<QueryParameter> index2 = Lists.newArrayList();

	private final List<String> queryBuilder = Lists.newArrayList();

	public QueryBuilder(List<EvaluationResult> unsatisfied, int userId) {
		this.unsatisfied = unsatisfied;
		this.userId = userId;
	}

	public QueryBuilder.Result build() {
		for (EvaluationResult er : unsatisfied) {
			EvaluationResult copy = er.recommendationCopy();

			List<QueryParameter> params = copy.unsatisfiedParams();

			for (QueryParameter param : params) {
				String parameterType = param.getParameterType();
				Collection<String> usedValues = copy.getUsedValues(parameterType);

				String sql = paraterize(parameterType, usedValues);
				queryBuilder.add(wrap(sql));

				index2.add(param);
			}
			index.add(copy);
		}

		return new Result(this);
	}

	public static class Result {
		private QueryBuilder that;

		public Result(QueryBuilder that) {
			this.that = that;
		}

		public String query() {
			return StringUtils.join(that.queryBuilder, " union all ");
		}
		
		public QueryParameter getParam(int id) {
			return that.index2.get(id);
		}

		public EvaluationResult getResult(int id) {
			return that.index.get(id);
		}
		
		public List<EvaluationResult> getResults() {
			return that.index;
		}
	}
	
	private String paraterize(String parameterType, Collection<String> usedValues) {
		if (usedValues.isEmpty()) {
			return MessageFormat.format(SQL_EMPTY, index.size(), userId, quote(parameterType),
					index2.size());
		} else {
			return MessageFormat.format(SQL, index.size(), userId, quote(parameterType),
					quoteJoin(usedValues), index2.size());
		}
	}

	private static String wrap(String sql) {
		return "select * from (" + sql + ") a";
	}

	private static String quoteJoin(Collection<String> usedValues) {
		Iterator<String> it = usedValues.iterator();
		StringBuilder sb = new StringBuilder(quote(it.next()));

		while (it.hasNext()) {
			sb.append(',').append(quote(it.next()));
		}

		return sb.toString();
	}

	public static String quote(String q) {
		return "'" + q + "'";
	}

}
