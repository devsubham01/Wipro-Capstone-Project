package com.guielements.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.guielements.base.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ExtentReportManager implements ITestListener {

    private static final Logger log = LogManager.getLogger(ExtentReportManager.class);

    // volatile + static ensures only ONE instance is created across all threads
    private static volatile ExtentReports extent;

    // ThreadLocal gives each parallel thread its own ExtentTest node
    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    public static ExtentTest getTest() {
        return extentTest.get();
    }

    // synchronized + null check ensures ExtentReports is created only ONCE
    // even when two browser threads call onStart at the same time
    @Override
    public synchronized void onStart(ITestContext context) {
        if (extent != null) return; // Already initialized — skip second call

        try {
            // Build absolute path using File.separator for Windows compatibility
            String projectRoot = System.getProperty("user.dir");
            String reportsDir  = projectRoot + File.separator + "reports";
            String reportFile  = reportsDir  + File.separator + "ExtentReport.html";

            // Create reports directory if it does not exist
            Files.createDirectories(Paths.get(reportsDir));

            ExtentSparkReporter spark = new ExtentSparkReporter(reportFile);
            spark.config().setTheme(Theme.DARK);
            spark.config().setDocumentTitle("GUI Elements Automation Report");
            spark.config().setReportName("Selenium Hybrid Framework — Test Results");
            spark.config().setEncoding("UTF-8");

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("OS",        System.getProperty("os.name"));
            extent.setSystemInfo("Java",      System.getProperty("java.version"));
            extent.setSystemInfo("Tester",    "Automation Engineer");
            extent.setSystemInfo("URL",       ConfigReader.getProperty("url"));
            extent.setSystemInfo("Framework", "Selenium + TestNG + POM + Data-Driven");

            log.info("Extent Report will be saved at: " + reportFile);

        } catch (Exception e) {
            log.error("Failed to initialize Extent Report: " + e.getMessage());
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        String browser  = result.getTestContext().getCurrentXmlTest().getParameter("browser");
        String testName = result.getMethod().getMethodName() + " [" + browser + "]";
        String desc     = result.getMethod().getDescription();

        ExtentTest test = extent.createTest(testName, desc);
        extentTest.set(test);
        log.info("Test started: " + testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        extentTest.get().pass("Test PASSED ✓");
        log.info("PASSED: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        log.error("FAILED: " + testName + " => " + result.getThrowable().getMessage());

        try {
            // Capture screenshot as Base64 and embed directly in Extent Report
            String base64 = ScreenshotUtil.captureBase64(DriverManager.getDriver());

            // Also save physical screenshot file to screenshots/ folder
            String filePath = ScreenshotUtil.captureScreenshot(DriverManager.getDriver(), testName);

            extentTest.get().fail("Test FAILED: " + result.getThrowable().getMessage());

            if (base64 != null && !base64.isEmpty()) {
                extentTest.get().fail("Failure Screenshot:",
                    MediaEntityBuilder.createScreenCaptureFromBase64String(base64).build());
                log.info("Screenshot embedded in report and saved at: " + filePath);
            }
        } catch (Exception e) {
            extentTest.get().fail(result.getThrowable());
            log.error("Screenshot capture failed: " + e.getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        if (extentTest.get() != null) {
            String reason = result.getThrowable() != null
                ? result.getThrowable().getMessage() : "No reason provided";
            extentTest.get().skip("Test SKIPPED: " + reason);
        }
        log.warn("SKIPPED: " + result.getMethod().getMethodName());
    }

    // synchronized + check ensures flush happens only ONCE after all threads finish
    @Override
    public synchronized void onFinish(ITestContext context) {
        if (extent != null) {
            extent.flush();
            log.info("Extent Report saved successfully.");
            extent = null; // Reset so it can be re-initialized on next run
        }
    }
}
