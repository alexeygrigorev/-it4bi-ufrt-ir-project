package it4bi.ufrt.ir.service.dw.db;

import static org.junit.Assert.*;
import it4bi.ufrt.ir.service.dw.db.Person.PersonType;

import org.junit.Test;

public class PersonTest {

	@Test
	public void sameName_exact() {
		Person person = new Person(1, "Abdel Kader Ben Bouali", PersonType.PLAYER);
		double confidence = person.sameName("Abdel Kader Ben Bouali");
		assertEquals(1.0, confidence, 0.001);
	}

	@Test
	public void sameName_half() {
		Person person = new Person(1, "Abdel Kader Ben Bouali", PersonType.PLAYER);
		double confidence = person.sameName("Abdel Bouali");
		assertEquals(0.5, confidence, 0.001);
	}

	@Test
	public void sameName_half_differentOrder() {
		Person person = new Person(1, "Abdel Kader Ben Bouali", PersonType.PLAYER);
		double confidence = person.sameName("Bouali Abdel");
		assertEquals(0.5, confidence, 0.001);
	}
}
