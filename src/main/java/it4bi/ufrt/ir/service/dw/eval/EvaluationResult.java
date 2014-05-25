package it4bi.ufrt.ir.service.dw.eval;

import it4bi.ufrt.ir.service.dw.MatchedQueryTemplate;
import it4bi.ufrt.ir.service.dw.UserQuery;
import it4bi.ufrt.ir.service.dw.eval.extractor.ExtractionAttempt;
import it4bi.ufrt.ir.service.dw.eval.extractor.ParameterExtractor;
import it4bi.ufrt.ir.service.dw.ner.NamedEntity;
import it4bi.ufrt.ir.service.dw.ner.NamedEntityClass;
import it4bi.ufrt.ir.service.dw.ner.RecognizedNamedEntities;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.PeekingIterator;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * The results of trying to apply a certain {@link QueryTemplate} to the user query in the free text form.<br>
 * <br>
 * Also acts as a context of evaluating the query - in the sense that it keeps some data needed for processing
 * it across several {@link ParameterExtractor} classes
 */
public class EvaluationResult {

	private final QueryTemplate queryTemplate;
	private final UserQuery userQuery;
	private final RecognizedNamedEntities namedEntities;

	private boolean success = true;

	private final Map<String, String> foundParams = Maps.newHashMap();
	private final Set<String> processedParameters = Sets.newHashSet();
	private final Set<String> allParameters = Sets.newHashSet();
	
	private double additionalScore = 0.0;

	/**
	 * keeps internal data needed for processing multiple values of the same parameter extractor
	 */
	private final Multimap<String, String> usedValues = LinkedHashMultimap.create();

	public EvaluationResult(QueryTemplate queryTemplate, UserQuery userQuery) {
		this.queryTemplate = queryTemplate;
		this.userQuery = userQuery;
		this.namedEntities = userQuery.getNamedEntities();
		List<QueryParameter> parameters = queryTemplate.getParameters();
		for (QueryParameter queryParameter : parameters) {
			allParameters.add(queryParameter.getName());
		}
	}

	/**
	 * Marks the captured value as used - used for evaluating the same query across several extractors to
	 * avoid extracting the same value twice
	 * 
	 * @param value
	 * @param parameterType
	 */
	private void markValueUsed(String value, String parameterType) {
		usedValues.put(parameterType, value);
	}

	/**
	 * Checks if this value was marked as used (with {@link #markValueUsed(String, String)} method) prior to
	 * the call of this method
	 * 
	 * @param value
	 * @param parameterType
	 * @return <code>true</code> if it's been used before, <code>false</code> otherwise
	 */
	public boolean isNotAlreadyUsed(String value, String parameterType) {
		Collection<String> usedValuesOfTheType = usedValues.get(parameterType);
		return !usedValuesOfTheType.contains(value);
	}

	public Multimap<String, String> getUsedValues() {
		return usedValues;
	}
	
	public Collection<String> getUsedValues(String parameterType) {
		return usedValues.get(parameterType);
	}

	public PeekingIterator<NamedEntity> namedEntitiesOf(NamedEntityClass neClass) {
		return namedEntities.of(neClass);
	}

	/**
	 * Records an attempt, and if it's a successful one, then populated the internal parameter storage
	 * 
	 * @param attempt to record
	 */
	public void record(ExtractionAttempt attempt) {
		String name = attempt.getParameter().getName();
		processedParameters.add(name);

		if (attempt.isSuccessful()) {
			foundParams.put(name, attempt.getValue());
			additionalScore = additionalScore + attempt.getScore();
			String parameterType = attempt.getParameter().getParameterType();
			markValueUsed(attempt.getValue(), parameterType);
		} else {
			success = false;
		}
	}

	/**
	 * @return <code>true</code> if all parameters of the underlying {@link QueryTemplate} are matched with
	 *         something from the user query
	 */
	public boolean isSatisfied() {
		return success && unsatisfiedParamsCount() == 0;
	}

	/**
	 * @return the number of parameters that weren't satisfied during the free text query parsing
	 */
	public int unsatisfiedParamsCount() {
		Set<String> diff = unsatisfiedParamNames();
		return diff.size();
	}

	public Set<String> unsatisfiedParamNames() {
		Set<String> foundParamsNames = foundParams.keySet();
		return Sets.difference(allParameters, foundParamsNames);
	}
	
	public List<QueryParameter> unsatisfiedParams() {
		Set<String> names = unsatisfiedParamNames();
		List<QueryParameter> parameters = queryTemplate.getParameters();

		List<QueryParameter> result = Lists.newArrayList();
		for (QueryParameter qp : parameters) {
			if (names.contains(qp.getName())) {
				result.add(qp);
			}
		}
		
		return result;
	}

	/**
	 * @return a dto object to be trasfered to the client side
	 * @throws IllegalStateException if not all parameters were satisfied during the evaluation
	 */
	public MatchedQueryTemplate asDto() {
		Validate.validState(isSatisfied(), "all parameters must be satisfied");

		int relevance = calcRelevance();
		
		String name = parametrizeName(queryTemplate.getName(), foundParams);
		MatchedQueryTemplate res = new MatchedQueryTemplate(queryTemplate.getId(), foundParams, name, relevance);
		res.addRelevance(additionalScore);

		return res;
	}

	private int calcRelevance() {
		SetView<String> matchedKeywords = matchedKeywords();
		return matchedKeywords.size();
	}

	private SetView<String> matchedKeywords() {
		Set<String> keywords = queryTemplate.getKeywords();
		Set<String> stemmedTokens = Sets.newLinkedHashSet(userQuery.getStemmedTokens());
		return Sets.intersection(keywords, stemmedTokens);
	}

	public static String parametrizeName(String name, Map<String, String> params) {
		String[] searchList = new String[params.size()];
		String[] replacementList = new String[params.size()];

		int i = 0;
		for (Entry<String, String> e : params.entrySet()) {
			searchList[i] = ":" + e.getKey();
			replacementList[i] = e.getValue();
			i++;
		}

		return StringUtils.replaceEachRepeatedly(name, searchList, replacementList);
	}

	public QueryTemplate getQueryTemplate() {
		return queryTemplate;
	}

	public Map<String, String> getFoundParams() {
		return foundParams;
	}

	public EvaluationResult recommendationCopy() {
		EvaluationResult copy = new EvaluationResult(queryTemplate, userQuery);
		copy.usedValues.putAll(usedValues);
		copy.foundParams.putAll(foundParams);
		copy.additionalScore = additionalScore;
		copy.success = true;
		return copy;
	}

}
