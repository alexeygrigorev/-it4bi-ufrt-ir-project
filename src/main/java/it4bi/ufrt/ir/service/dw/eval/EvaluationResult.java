package it4bi.ufrt.ir.service.dw.eval;

import it4bi.ufrt.ir.service.dw.MatchedQueryTemplate;
import it4bi.ufrt.ir.service.dw.UserQuery;
import it4bi.ufrt.ir.service.dw.eval.extractor.ExtractionAttempt;
import it4bi.ufrt.ir.service.dw.ner.NamedEntity;
import it4bi.ufrt.ir.service.dw.ner.NamedEntityClass;
import it4bi.ufrt.ir.service.dw.ner.RecognizedNamedEntities;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.google.common.collect.Maps;
import com.google.common.collect.PeekingIterator;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * The results of trying to apply a certain {@link QueryTemplate} to the user query in the free text form
 */
public class EvaluationResult {

	private final QueryTemplate queryTemplate;
	private final UserQuery userQuery;
	private final RecognizedNamedEntities namedEntities;

	private boolean success = true;

	private final Map<String, String> foundParams = Maps.newHashMap();
	private final Set<String> processedParameters = Sets.newHashSet();
	private final Set<String> allParameters = Sets.newHashSet();

	public EvaluationResult(QueryTemplate queryTemplate, UserQuery userQuery) {
		this.queryTemplate = queryTemplate;
		this.userQuery = userQuery;
		this.namedEntities = userQuery.getNamedEntities();
		List<QueryParameter> parameters = queryTemplate.getParameters();
		for (QueryParameter queryParameter : parameters) {
			allParameters.add(queryParameter.getName());
		}
	}

	public PeekingIterator<NamedEntity> namedEntitiesOf(NamedEntityClass neClass) {
		return namedEntities.of(neClass);
	}

	public void record(ExtractionAttempt attempt) {
		String name = attempt.getParameter().getName();
		processedParameters.add(name); // TODO: needed?

		if (attempt.isSuccessful()) {
			foundParams.put(name, attempt.getValue());
		} else {
			success = false;
		}
	}

	public boolean isSatisfied() {
		return success && unsatisfiedParams() == 0;
	}

	public int unsatisfiedParams() {
		Set<String> foundParamsNames = foundParams.keySet();
		SetView<String> diff = Sets.difference(allParameters, foundParamsNames);
		return diff.size();
	}

	/**
	 * @return
	 * @throws IllegalStateException if not all parameters were satisfied during the evaluation
	 */
	public MatchedQueryTemplate asDto() {
		Validate.validState(isSatisfied(), "all parameters must be satisfied");

		int relevance = calcRelevance();

		String name = parametrizeName(queryTemplate.getName(), foundParams);
		return new MatchedQueryTemplate(queryTemplate.getId(), foundParams, name, relevance);
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

	public String parametrizeName(String name, Map<String, String> params) {
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

}
