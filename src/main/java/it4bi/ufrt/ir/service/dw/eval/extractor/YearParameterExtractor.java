package it4bi.ufrt.ir.service.dw.eval.extractor;

import it4bi.ufrt.ir.service.dw.UserQuery;
import it4bi.ufrt.ir.service.dw.eval.EvaluationResult;
import it4bi.ufrt.ir.service.dw.eval.QueryParameter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */
public class YearParameterExtractor implements ParameterExtractor {

	private static final Logger LOGGER = LoggerFactory.getLogger(YearParameterExtractor.class);

	private static final Pattern YEAR_PATTERN = Pattern.compile("\\d{4}");

	@Override
	public ExtractionAttempt tryExtract(UserQuery query, QueryParameter parameter, EvaluationResult result) {
		String parameterType = parameter.getParameterType();
		for (String token : query.getStemmedTokens()) {
			Matcher matcher = YEAR_PATTERN.matcher(token);
			if (!matcher.matches()) {
				continue;
			}

			LOGGER.debug("value {} is a valid year", token);
			if (result.isNotAlreadyUsed(token, parameterType)) {
				result.markValueUsed(token, parameterType);
				LOGGER.debug("value {} hasn't been used yet - binding it to :{}", token,
						parameter.getName());
				return ExtractionAttempt.successful(parameter, token);
			} else {
				LOGGER.debug("value {} has been already used by other extractor, skipping it", token);
			}
		}
		return ExtractionAttempt.notSuccessful(parameter);
	}

}
