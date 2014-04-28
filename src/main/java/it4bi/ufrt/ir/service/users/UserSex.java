package it4bi.ufrt.ir.service.users;

public enum UserSex {
	MALE,
	FEMALE;
	
	public static UserSex fromDbValue(String dbValue) {
		if ("M".equals(dbValue)) {
			return MALE;
		} else if ("F".equals(dbValue)) {
			return FEMALE;
		} else {
			throw new IllegalArgumentException("Unknown input for dbValue: " + dbValue);
		}
	}
}
