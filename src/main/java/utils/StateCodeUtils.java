package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StateCodeUtils {
	
	// Interstate means pos != sellerState
	// Intrastate means pos == sellerState

    private static final Logger log = LoggerFactory.getLogger(StateCodeUtils.class);

    /**
     * Validate if the given code is a valid GSTIN state code.
     */
    public static boolean isValidGSTINStateCode(String code) {

        if (code == null || code.isBlank()) {
            String message = String.format(
                    "Invalid input: statecode=%s. Null/Blank values are not allowed.",
                    code
            );
            throw new IllegalArgumentException(message);
        }

        try {
            int stateCode = Integer.parseInt(code);
            return StateCodeConstantsUtils.VALID_STATE_CODES.contains(stateCode);

        } catch (NumberFormatException e) {
            log.error("Invalid state code format: code={}, Must be numeric.", code, e);
            return false;
        }
    }

    /**
     * Check if it's an Inter-state transaction (buyerState != sellerState)
     */
    public static boolean isInterState(String buyer, String seller) {

        validateNotNull(buyer, seller);

        try {
            Integer buyerCode = Integer.parseInt(buyer);
            Integer sellerCode = Integer.parseInt(seller);

            return !buyerCode.equals(sellerCode); // ← returns true when they DON'T match

        } catch (NumberFormatException e) {
            log.error("Invalid state code format: buyer={}, seller={}, Must be numeric.", buyer, seller, e);
            throw new IllegalArgumentException("Invalid State Code Format", e);
        }
    }

    /**
     * Common null check used by all functions
     */
    private static void validateNotNull(String buyer, String seller) {

        if (buyer == null || seller == null) {
            String message = String.format(
                    "Invalid input: buyer=%s, seller=%s. Null values are not allowed.",
                    buyer, seller
            );
            throw new IllegalArgumentException(message);
        }
    }
}