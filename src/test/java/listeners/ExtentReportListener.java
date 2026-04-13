package listeners;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

/**
 * ExtentReportListener — Automatically generates HTML reports for every test run.
 * 
 * HOW IT WORKS:
 * 1. TestNG calls onStart()       → We create the report file
 * 2. TestNG calls onTestStart()   → We create a new entry in the report for each test
 * 3. TestNG calls onTestSuccess/Failure/Skip → We log the result
 * 4. TestNG calls onFinish()      → We save the report to disk
 * 
 * HOW TO USE:
 * Add this listener in your testng.xml:
 *   <listeners>
 *       <listener class-name="listeners.ExtentReportListener"/>
 *   </listeners>
 */
public class ExtentReportListener implements ITestListener {

	private static final Logger logger = LogManager.getLogger(ExtentReportListener.class);
	
	private static ExtentReports extent;
	private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
	// ThreadLocal makes it safe for parallel test execution

	// ───── SUITE STARTS ─────
	@Override
	public void onStart(ITestContext context) {

		// Create a unique report file name with timestamp
		String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
		String reportPath = "reports/TestReport_" + timestamp + ".html";

		// Configure the HTML reporter
		ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
		spark.config().setDocumentTitle("E-Invoice API Test Report");
		spark.config().setReportName("E-Invoice Test Automation Results");
		spark.config().setTheme(Theme.DARK);
		spark.config().setTimelineEnabled(true);

		// Create the ExtentReports instance and attach the reporter
		extent = new ExtentReports();
		extent.attachReporter(spark);

		// Add system/environment info to the report
		extent.setSystemInfo("Project", "E-Invoice API Testing");
		extent.setSystemInfo("Environment", "QA");
		extent.setSystemInfo("Tester", "Omkar Gharat");
		extent.setSystemInfo("Framework", "Rest Assured + TestNG");

		logger.info("📊 Extent Report initialized: {}", reportPath);
		
		// Log which test suite/class is about to run
		logger.info("════════════════════════════════════════════");
		logger.info("  TEST SUITE: {}", context.getName());
		logger.info("  Total tests to run: {}", context.getAllTestMethods().length);
		logger.info("════════════════════════════════════════════");
	}

	// ───── EACH TEST STARTS ─────
	@Override
	public void onTestStart(ITestResult result) {

		// Create a new test entry in the report
		// Uses the test's description if available, otherwise uses method name
		String testName = result.getMethod().getDescription();
		if (testName == null || testName.isEmpty()) {
			testName = result.getMethod().getMethodName();
		}

		ExtentTest extentTest = extent.createTest(testName);
		test.set(extentTest);

		logger.info("────────────────────────────────────────────");
		logger.info("▶ RUNNING: {} ({})", testName, result.getMethod().getMethodName());
		logger.info("────────────────────────────────────────────");
	}

	// ───── TEST PASSED ─────
	@Override
	public void onTestSuccess(ITestResult result) {

		test.get().log(Status.PASS, "✅ Test PASSED: " + result.getMethod().getMethodName());
		logger.info("✅ PASSED: {} ({}ms)", result.getMethod().getMethodName(), 
				result.getEndMillis() - result.getStartMillis());
	}

	// ───── TEST FAILED ─────
	@Override
	public void onTestFailure(ITestResult result) {

		test.get().log(Status.FAIL, "❌ Test FAILED: " + result.getMethod().getMethodName());
		test.get().log(Status.FAIL, "Cause: " + result.getThrowable().getMessage());

		// Log the full stack trace so you can debug from the report itself
		test.get().fail(result.getThrowable());

		logger.error("❌ FAILED: {} ({}ms)", result.getMethod().getMethodName(),
				result.getEndMillis() - result.getStartMillis());
		logger.error("   Cause: {}", result.getThrowable().getMessage());
	}

	// ───── TEST SKIPPED ─────
	@Override
	public void onTestSkipped(ITestResult result) {

		test.get().log(Status.SKIP, "⏭ Test SKIPPED: " + result.getMethod().getMethodName());
		
		if (result.getThrowable() != null) {
			test.get().skip(result.getThrowable());
		}

		logger.warn("⏭ SKIPPED: {}", result.getMethod().getMethodName());
	}

	// ───── SUITE ENDS ─────
	@Override
	public void onFinish(ITestContext context) {

		// Write everything to the HTML file
		extent.flush();
		
		logger.info("════════════════════════════════════════════");
		logger.info("  TEST SUITE COMPLETE: {}", context.getName());
		logger.info("  Passed: {} | Failed: {} | Skipped: {}", 
				context.getPassedTests().size(),
				context.getFailedTests().size(),
				context.getSkippedTests().size());
		logger.info("📊 Extent Report generated successfully!");
		logger.info("════════════════════════════════════════════");
	}
}
