package it4bi.ufrt.ir.service.dw.ner;

import java.util.Arrays;

public class NamedEntity {

	private final String token;
	private final String cls;

	public NamedEntity(String token, String cls) {
		this.token = token;
		this.cls = cls;
	}

	public String getToken() {
		return token;
	}

	public String getNerClass() {
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
}