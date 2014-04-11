package it4bi.ufrt.ir.service.dw.eval;

import static org.junit.Assert.*;
import it4bi.ufrt.ir.service.dw.ner.NamedEntity;
import it4bi.ufrt.ir.service.dw.ner.NamedEntityClass;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import com.google.common.base.Optional;

public class EvalResultTest {

	@Test
	public void nextNamedEntityOf_empty() {
		EvalResult result = new EvalResult();
		Optional<NamedEntity> res = result.nextNamedEntityOf(NamedEntityClass.LOCATION);
		assertFalse(res.isPresent());
	}

	@Test
	public void nextNamedEntityOf() {
		EvalResult result = new EvalResult();
		NamedEntity ne1 = new NamedEntity("token1", NamedEntityClass.LOCATION);
		NamedEntity ne2 = new NamedEntity("token2", NamedEntityClass.ORGANIZATION);
		NamedEntity ne3 = new NamedEntity("token3", NamedEntityClass.LOCATION);
		Collection<NamedEntity> nes = Arrays.asList(ne1, ne2, ne3);
		result.addNamedEntities(nes);

		Optional<NamedEntity> res1 = result.nextNamedEntityOf(NamedEntityClass.LOCATION);
		assertEquals(ne1, res1.get());

		Optional<NamedEntity> res2 = result.nextNamedEntityOf(NamedEntityClass.LOCATION);
		assertEquals(ne3, res2.get());

		Optional<NamedEntity> res3 = result.nextNamedEntityOf(NamedEntityClass.LOCATION);
		assertFalse(res3.isPresent());
	}

	@Test
	public void peekNamedEntityOf() {
		EvalResult result = new EvalResult();
		NamedEntity ne1 = new NamedEntity("token1", NamedEntityClass.LOCATION);
		NamedEntity ne2 = new NamedEntity("token2", NamedEntityClass.ORGANIZATION);
		NamedEntity ne3 = new NamedEntity("token3", NamedEntityClass.LOCATION);
		Collection<NamedEntity> nes = Arrays.asList(ne1, ne2, ne3);
		result.addNamedEntities(nes);

		Optional<NamedEntity> res1 = result.peekNamedEntityOf(NamedEntityClass.LOCATION);
		assertEquals(ne1, res1.get());

		Optional<NamedEntity> res2 = result.peekNamedEntityOf(NamedEntityClass.LOCATION);
		assertEquals(ne1, res2.get());

		Optional<NamedEntity> res3 = result.nextNamedEntityOf(NamedEntityClass.LOCATION);
		assertEquals(ne1, res3.get());

		Optional<NamedEntity> res4 = result.peekNamedEntityOf(NamedEntityClass.LOCATION);
		assertEquals(ne3, res4.get());
	}

}
