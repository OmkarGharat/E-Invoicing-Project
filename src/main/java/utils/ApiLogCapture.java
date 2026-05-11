package utils;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

/**
 * ApiLogCapture — a RestAssured Filter that silently records the full
 * request + response for every API call into a ThreadLocal buffer.
 *
 * HOW IT WORKS:
 *   - Attached to every request via BaseRequest
 *   - Does NOT print anything during normal execution
 *   - ExtentReportListener reads the buffer ONLY when a test fails
 *   - Buffer is cleared at the start of each test (call clearLog())
 *
 * WHY ThreadLocal?
 *   - Each thread (test) gets its own independent buffer
 *   - Safe for parallel test execution — no cross-contamination
 */
public class ApiLogCapture implements Filter {

    // Each thread gets its own StringBuilder — safe for parallel runs
    private static final ThreadLocal<StringBuilder> logBuffer =
            ThreadLocal.withInitial(StringBuilder::new);

    /**
     * Call this at the START of each test to clear the previous test's log.
     * (Called by ExtentReportListener.onTestStart)
     */
    public static void clearLog() {
        logBuffer.get().setLength(0);
    }

    /**
     * Returns the full captured log for the current thread.
     * (Called by ExtentReportListener.onTestFailure)
     */
    public static String getLog() {
        return logBuffer.get().toString();
    }

    /**
     * This method is called by RestAssured BEFORE sending the request
     * and AFTER receiving the response. We build a human-readable summary.
     */
    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {

        // ── Execute the actual HTTP call ──
        Response response = ctx.next(requestSpec, responseSpec);

        // ── Build a human-readable log entry ──
        StringBuilder sb = logBuffer.get();

        sb.append("\n");
        sb.append("┌─────────────────── REQUEST ───────────────────┐\n");
        sb.append(String.format("  Method  : %s%n", requestSpec.getMethod()));
        sb.append(String.format("  URL     : %s%n", requestSpec.getURI()));

        // Only show auth header — skip noisy ones like Content-Type
        requestSpec.getHeaders().forEach(h -> {
            if (h.getName().equalsIgnoreCase("x-api-key")
                    || h.getName().equalsIgnoreCase("Authorization")) {
                // Mask the value for security
                sb.append(String.format("  Auth    : %s = [MASKED]%n", h.getName()));
            }
        });

        // Show body only if present
        String body = requestSpec.getBody() != null
                ? requestSpec.getBody().toString() : null;
        if (body != null && !body.isBlank()) {
            sb.append("  Body    :\n");
            // Indent each line of the JSON body by 4 spaces
            for (String line : body.split("\n")) {
                sb.append("    ").append(line.stripTrailing()).append("\n");
            }
        } else {
            sb.append("  Body    : <none>\n");
        }

        sb.append("├─────────────────── RESPONSE ──────────────────┤\n");
        sb.append(String.format("  Status  : %d %s%n",
                response.getStatusCode(), response.getStatusLine().trim()));

        String responseBody = response.getBody().asPrettyString();
        if (responseBody != null && !responseBody.isBlank()) {
            sb.append("  Body    :\n");
            for (String line : responseBody.split("\n")) {
                sb.append("    ").append(line.stripTrailing()).append("\n");
            }
        }

        sb.append("└───────────────────────────────────────────────┘\n");

        return response;
    }
}
