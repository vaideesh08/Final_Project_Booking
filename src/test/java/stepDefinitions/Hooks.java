package stepDefinitions;

import factory.DriverManager;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.ConfigReader;
import utilities.Screenshot;

import java.io.File;

public class Hooks {

    private static final Logger log = LogManager.getLogger(Hooks.class);

    private static final String REPORT_DIR  = "reports";
    private static final String REPORT_FILE = "reports/CucumberExtentReport.html";
    private static ExtentReports extent;

    static {
        try {
            new File(REPORT_DIR).mkdirs();

            ExtentSparkReporter spark = new ExtentSparkReporter(REPORT_FILE);
            spark.config().setDocumentTitle("Automation Test Report");
            spark.config().setReportName("Booking.com Test Results");
            spark.config().setTheme(Theme.DARK);
            spark.config().setEncoding("utf-8");

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("OS",   System.getProperty("os.name"));
            extent.setSystemInfo("Java", System.getProperty("java.version"));

            log.info("ExtentReports initialized → " + new File(REPORT_FILE).getAbsolutePath());

        } catch (Exception e) {
            log.error("ExtentReports initialization FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private ExtentTest test;

    @Before
    public void setUp(Scenario scenario) {
        log.info("========================================");
        log.info("Scenario STARTED: " + scenario.getName());
        log.info("========================================");

        if (extent != null) {
            test = extent.createTest(scenario.getName());
        }

        String browser = ConfigReader.getProperty("browser");
        DriverManager.initializeDriver(browser);
        DriverManager.getDriver().get(ConfigReader.getProperty("app.url"));
    }

    @After
    public void tearDown(Scenario scenario) {
        try {
            String screenshotPath = Screenshot.screenShotTC(
                    DriverManager.getDriver(),
                    scenario.getName().replaceAll(" ", "_")
            );
            log.info("Screenshot saved at: " + screenshotPath);

            if (test != null) {
                test.addScreenCaptureFromPath(
                        new File(screenshotPath).getAbsolutePath(), "End of scenario");

                if (scenario.isFailed()) {
                    test.log(Status.FAIL, "Scenario FAILED: " + scenario.getName());
                    log.error("Scenario FAILED: " + scenario.getName());
                } else {
                    test.log(Status.PASS, "Scenario PASSED: " + scenario.getName());
                    log.info("Scenario PASSED: " + scenario.getName());
                }
            }
        } catch (Exception e) {
            log.error("Screenshot/reporting error: " + e.getMessage());
        } finally {
            if (extent != null) {
                extent.flush();
                log.info("ExtentReports flushed → " + new File(REPORT_FILE).getAbsolutePath());
            }
            DriverManager.quitDriver();
            log.info("========================================");
            log.info("Scenario ENDED: " + scenario.getName());
            log.info("========================================");
        }
    }
}