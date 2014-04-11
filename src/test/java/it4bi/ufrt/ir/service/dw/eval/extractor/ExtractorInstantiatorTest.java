package it4bi.ufrt.ir.service.dw.eval.extractor;

import static org.junit.Assert.*;
import it4bi.ufrt.ir.service.dw.eval.QueryParameter;
import it4bi.ufrt.ir.service.dw.eval.extractor.ExtractorInstantiator;
import it4bi.ufrt.ir.service.dw.eval.extractor.ParameterExtractor;
import it4bi.ufrt.ir.service.dw.eval.extractor.TeamNameParatemerExtractor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:dwTestContextRealDb.xml")
// TODO: replace to smth lighter
public class ExtractorInstantiatorTest {

	@Autowired
	ExtractorInstantiator instantiator;

	@Test
	public void test() {
		String name = "test";
		Class<? extends ParameterExtractor> extractorClass = TeamNameParatemerExtractor.class;
		String parameterType = "TYPE";
		QueryParameter parameter = new QueryParameter(name, extractorClass, parameterType);
		TeamNameParatemerExtractor teamNameExtractor = (TeamNameParatemerExtractor) instantiator.instantiate(parameter);
		assertNotNull(teamNameExtractor.getDao()); 
	}

}
