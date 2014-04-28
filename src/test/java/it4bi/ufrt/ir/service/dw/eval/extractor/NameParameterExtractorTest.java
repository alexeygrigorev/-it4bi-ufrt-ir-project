package it4bi.ufrt.ir.service.dw.eval.extractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import it4bi.ufrt.ir.service.dw.db.Person;
import it4bi.ufrt.ir.service.dw.db.Person.PersonType;

import java.util.Arrays;

import org.junit.Test;

import com.google.common.base.Optional;

public class NameParameterExtractorTest {

	@Test
	public void test() {
		String name = "Cristiano Ronaldo";
		Person p1 = new Person(1, "David Backham", PersonType.PLAYER);
		Person p2 = new Person(2, "Ronaldo", PersonType.PLAYER);
		Person p3 = new Person(3, "Cristiano Ronaldo", PersonType.PLAYER);

		Optional<Person> closest = NameParameterExtractor.findClosest(name, Arrays.asList(p1, p2, p3));
		assertTrue(closest.isPresent());
		assertEquals(3, closest.get().getId());

	}

}
