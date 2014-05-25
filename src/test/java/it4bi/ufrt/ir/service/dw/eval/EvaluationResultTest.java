package it4bi.ufrt.ir.service.dw.eval;

import static org.junit.Assert.assertEquals;
import it4bi.ufrt.ir.service.dw.UserQuery;
import it4bi.ufrt.ir.service.dw.eval.extractor.ExtractionAttempt;
import it4bi.ufrt.ir.service.dw.eval.extractor.TeamNameParatemerExtractor;
import it4bi.ufrt.ir.service.dw.ner.NamedEntity;
import it4bi.ufrt.ir.service.dw.ner.NamedEntityClass;
import it4bi.ufrt.ir.service.dw.ner.RecognizedNamedEntities;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class EvaluationResultTest {

	@Test
	public void unsatisfiedParams() {
		Collection<String> keywords = Collections.singleton("test");
		String sqlTemplate = ":param1 :param2 :param3";
		String name = "template";
		QueryParameter p1 = new QueryParameter("param1", TeamNameParatemerExtractor.class, "TEAM_NAME");
		QueryParameter p2 = new QueryParameter("param2", TeamNameParatemerExtractor.class, "TEAM_NAME");
		QueryParameter p3 = new QueryParameter("param3", TeamNameParatemerExtractor.class, "TEAM_NAME");
		List<QueryParameter> parameters = Arrays.asList(p1, p2, p3);
		QueryTemplate queryTemplate = new QueryTemplate(1, keywords, sqlTemplate, name, parameters);

		RecognizedNamedEntities ne = emptyNamedEntities();
		EvaluationResult res = new EvaluationResult(queryTemplate, new UserQuery("query", ne));

		res.record(ExtractionAttempt.successful(p2, "value2"));

		Set<String> unsatisfiedParamNames = res.unsatisfiedParamNames();
		Set<String> expectedUnsatisfiedParamNames = ImmutableSet.of("param1", "param3");
		assertEquals(expectedUnsatisfiedParamNames, unsatisfiedParamNames);

		Set<QueryParameter> unsatisfiedParams = Sets.newHashSet(res.unsatisfiedParams());
		Set<QueryParameter> expectedUnsatisfiedParams = ImmutableSet.of(p1, p3);
		assertEquals(expectedUnsatisfiedParams, unsatisfiedParams);
	}

	private RecognizedNamedEntities emptyNamedEntities() {
		Multimap<NamedEntityClass, NamedEntity> namedEntitites = HashMultimap.create();
		return new RecognizedNamedEntities(namedEntitites);
	}

}
