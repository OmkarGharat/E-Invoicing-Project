package utils;

import java.util.regex.Pattern;

public class GstinValidator {

	// After — strictly 01–37
	private static final String GSTIN_REGEX = "^(0[1-9]|[12][0-9]|3[0-7])[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$";

	private static final Pattern GSTIN_PATTERN = Pattern.compile(GSTIN_REGEX);

	private static final String CHAR_SET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	/**
	 * Complete GSTIN validation → format + checksum
	 */
	public static boolean isValidGstin(String gstin) {
		
		return isValidFormat(gstin) && isValidChecksum(gstin);
	}

	/**
	 * Only checks the regex format of GSTIN.
	 */
	private static boolean isValidFormat(String gstin) {
		
		if (gstin == null || gstin.trim().isEmpty()) {
			return false;
		}
		
		return GSTIN_PATTERN.matcher(gstin.trim().toUpperCase()).matches();
	}

	/**
	 * Validates checksum as per GSTIN algorithm. Assumes format is already valid.
	 */
	private static boolean isValidChecksum(String gstin) {

		String input = gstin.trim().toUpperCase();
		char[] chars = input.toCharArray();

		int factor = 1;
		int total = 0;

		for (int i = 0; i < 14; i++) {
			int codePoint = CHAR_SET.indexOf(chars[i]);
			int digit = codePoint * factor;

			total += (digit / 36) + (digit % 36);
			factor = (factor == 1) ? 2 : 1;
		}

		int remainder = total % 36;
		int checkCodePoint = (36 - remainder) % 36;
		char expectedCheckChar = CHAR_SET.charAt(checkCodePoint);

		return chars[14] == expectedCheckChar;
	}
}