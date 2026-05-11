package utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class CountryCodeUtils {

	private static final Set<String> VALID_COUNTRY_CODES;

	static {

		Set<String> codes = new HashSet<>(Set.of(Locale.getISOCountries()));
		codes.addAll(Set.of("ZZ", "UN", "OT"));

		VALID_COUNTRY_CODES = Collections.unmodifiableSet(codes);
	}

	public static boolean isValidCountryCode(String code) {
		return VALID_COUNTRY_CODES.contains(code);
	}

}
