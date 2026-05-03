package utils;

import java.util.Map;
import java.util.Set;

public class PinValidator {

	// TODO Need to learn this

	public static boolean isValidPinForState(String pin, String state) {
		
		System.out.println("Level 1");
		
		if (pin == null || pin.length() < 6 || state == null) {
			
			System.out.println("Returning false from round 1...");
			return false;
		}

		System.out.println("Level 1.5");
		
		String prefix = pin.substring(0, 2);

		System.out.println("Level 2");
		
		Map<String, Set<String>> map = PinStateLoader.getPinStateMap();

		System.out.println("Level 2.5");
		
		if (!map.containsKey(prefix)) {
			System.out.println("Returning false from round 2...");
			return false; // unknown prefix
		}

		System.out.println("Level 3");
		
		return map.get(prefix).contains(state.toUpperCase());
	}

}
