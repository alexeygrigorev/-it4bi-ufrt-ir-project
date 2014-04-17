package it4bi.ufrt.ir.service.dw.eval;

import it4bi.ufrt.ir.service.dw.eval.extractor.ExtractionAttempt;
import it4bi.ufrt.ir.service.dw.ner.NamedEntity;
import it4bi.ufrt.ir.service.dw.ner.NamedEntityClass;
import it4bi.ufrt.ir.service.dw.ner.RecognizedNamedEntities;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.Maps;
import com.google.common.collect.PeekingIterator;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

//TODO consider renaming
public class EvalResult {

	private final QueryTemplate queryTemplate;
	private final RecognizedNamedEntities namedEntities;

	private boolean success = true;

	private final Map<String, String> foundParams = Maps.newHashMap();
	private final Set<String> processedParameters = Sets.newHashSet();
	private final Set<String> allParameters = Sets.newHashSet();

	public EvalResult(QueryTemplate queryTemplate, RecognizedNamedEntities namedEntities) {
		this.queryTemplate = queryTemplate;
		this.namedEntities = namedEntities;
		List<QueryParameter> parameters = queryTemplate.getParameters();
		for (QueryParameter queryParameter : parameters) {
			allParameters.add(queryParameter.getName());
		}
	}

	public PeekingIterator<NamedEntity> namedEntitiesOf(NamedEntityClass neClass) {
		return namedEntities.of(neClass);
	}

	public void recordAttempt(ExtractionAttempt attempt) {
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

	public Query toQueryWithFoundParameters() {
		Validate.validState(isSatisfied(), "all parameters must be satisfied");
		return new Query(queryTemplate, foundParams);
	}

	public QueryTemplate getQueryTemplate() {
		return queryTemplate;
	}

	public Map<String, String> getFoundParams() {
		return foundParams;
	}

}
