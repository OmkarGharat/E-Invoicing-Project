package utils;

import java.io.File;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PinStateLoader {

	// TODO Need to learn this

	private static Map<String, Set<String>> pinStateMap;

	static {
		try {
			ObjectMapper mapper = new ObjectMapper();
			pinStateMap = mapper.readValue(new File("src/test/resources/testdata/pin_prefix_state.json"),
					new TypeReference<Map<String, Set<String>>>() {
					});
		} catch (Exception e) {
			throw new RuntimeException("Failed to load PIN-State mapping", e);
		}
	}

	public static Map<String, Set<String>> getPinStateMap() {
		return pinStateMap;
	}
}