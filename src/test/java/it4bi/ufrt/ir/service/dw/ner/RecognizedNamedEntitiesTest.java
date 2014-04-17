package it4bi.ufrt.ir.service.dw.ner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.junit.Test;

import com.google.common.collect.PeekingIterator;

public class RecognizedNamedEntitiesTest {

	@Test
	public void nextNamedEntityOf_empty() {
		Iterable<NamedEntity> nes = Collections.emptyList();
		RecognizedNamedEntities namedEntities = RecognizedNamedEntities.from(nes);

		PeekingIterator<NamedEntity> res = namedEntities.of(NamedEntityClass.LOCATION);

		assertFalse(res.hasNext());
	}

	@Test
	public void namedEntityOf() {
		NamedEntity ne1 = new NamedEntity("token1", NamedEntityClass.LOCATION);
		NamedEntity ne2 = new NamedEntity("token2", NamedEntityClass.ORGANIZATION);
		NamedEntity ne3 = new NamedEntity("token3", NamedEntityClass.LOCATION);
		Collection<NamedEntity> nes = Arrays.asList(ne1, ne2, ne3);
		RecognizedNamedEntities namedEntities = RecognizedNamedEntities.from(nes);

		PeekingIterator<NamedEntity> res = namedEntities.of(NamedEntityClass.LOCATION);

		assertEquals(ne1, res.next());
		assertEquals(ne3, res.next());
		assertFalse(res.hasNext());
	}

	@Test
	public void namedEntityOf_removable() {
		NamedEntity ne1 = new NamedEntity("token1", NamedEntityClass.LOCATION);
		NamedEntity ne2 = new NamedEntity("token2", NamedEntityClass.ORGANIZATION);
		NamedEntity ne3 = new NamedEntity("token3", NamedEntityClass.LOCATION);
		Collection<NamedEntity> nes = Arrays.asList(ne1, ne2, ne3);
		RecognizedNamedEntities namedEntities = RecognizedNamedEntities.from(nes);

		PeekingIterator<NamedEntity> res = namedEntities.of(NamedEntityClass.LOCATION);
		res.next();
		res.remove();
		
		res = namedEntities.of(NamedEntityClass.LOCATION);
		assertEquals(ne3, res.next());
		assertFalse(res.hasNext());
	}
	
	@Test
	public void namedEntityOf_copyable() {
		NamedEntity ne1 = new NamedEntity("token1", NamedEntityClass.LOCATION);
		NamedEntity ne2 = new NamedEntity("token2", NamedEntityClass.ORGANIZATION);
		NamedEntity ne3 = new NamedEntity("token3", NamedEntityClass.LOCATION);
		Collection<NamedEntity> nes = Arrays.asList(ne1, ne2, ne3);
		RecognizedNamedEntities namedEntities = RecognizedNamedEntities.from(nes);
		RecognizedNamedEntities copy = namedEntities.copy();
		
		Iterator<NamedEntity> oldColIt = namedEntities.of(NamedEntityClass.LOCATION);
		oldColIt.next();
		oldColIt.remove();
		
		Iterator<NamedEntity> copyIt = copy.of(NamedEntityClass.LOCATION);
		assertEquals(ne1, copyIt.next());
	}
	
}
