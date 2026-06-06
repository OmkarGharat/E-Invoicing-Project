package utils;

import java.util.Map;
import java.util.Set;

public class PinValidator {

	// TODO Need to learn this

	public static boolean isValidPinForState(String pin, String state) {

		if (pin == null || pin.length() < 6 || state == null) {
			return false;
		}

		String prefix = pin.substring(0, 2);
		Map<String, Set<String>> map = PinStateLoader.getPinStateMap();

		if (!map.containsKey(prefix)) {
			return false; // unknown PIN prefix
		}

		// Normalize state code to 2 digits: Stcd can be "9" or "09" per GST spec.
		// Both are valid in the API but the JSON uses zero-padded codes.
		String normalizedState = state.trim();
		if (normalizedState.length() == 1) {
			normalizedState = "0" + normalizedState;
		}

		return map.get(prefix).contains(normalizedState);
	}

}
