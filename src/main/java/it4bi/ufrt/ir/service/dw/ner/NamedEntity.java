package it4bi.ufrt.ir.service.dw.ner;

import java.util.Arrays;

/**
 * A token - named enity - extracted from some free text query along with associated class of this token
 * 
 * @see NamedEntityClass
 */
public class NamedEntity {

	private final String token;
	private final NamedEntityClass cls;

	public NamedEntity(String token, String cls) {
		this.token = token;
		this.cls = NamedEntityClass.valueOf(cls);
	}

	public NamedEntity(String token, NamedEntityClass cls) {
		this.token = token;
		this.cls = cls;
	}

	public String getToken() {
		return token;
	}

	public NamedEntityClass getNerClass() {
		return cls;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof NamedEntity) {
			NamedEntity that = (NamedEntity) o;
			return token.equals(that.token) && cls.equals(that.cls);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Arrays.asList(token, cls).hashCode();
	}

	@Override
	public String toString() {
		return "(" + token + " -> " + cls + ")";
	}

}