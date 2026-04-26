package utils;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StateCodeConstantsUtils {
	
	public static final Set<Integer> VALID_STATE_CODES = createValidStateCodes();
	
	private static Set<Integer> createValidStateCodes() {
		
		// TODO : Need to learn these things
		Set<Integer> codes = IntStream.rangeClosed(1, 38)
										.boxed()
										.collect(Collectors.toSet());
		
		codes.addAll(Set.of(96,97,99));
		return Set.copyOf(codes);
	}
	
}