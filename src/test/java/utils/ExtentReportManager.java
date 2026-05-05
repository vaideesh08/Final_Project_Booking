package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ExtentReportManager implements ITestListener {
    ExtentReports report;
    ExtentSparkReporter spark;
    ExtentTest test;

    public void onStart(ITestContext result) {
        String filePath = System.getProperty("user.dir")
                + "\\target\\extentReport\\report.html";

        spark = new ExtentSparkReporter(filePath);
        spark.config().setDocumentTitle("Automation Test Report");
        spark.config().setReportName("Regression Suite Results");
        spark.config().setTheme(
                com.aventstack.extentreports.reporter.configuration.Theme.DARK);
        spark.config().setEncoding("utf-8");

        report = new ExtentReports();
        report.attachReporter(spark);
        report.setSystemInfo("OS", System.getProperty("os.name"));
        report.setSystemInfo("Java Version", System.getProperty("java.version"));
        report.setSystemInfo("Browser", "Chrome");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test = report.createTest(result.getName());
        test.log(Status.PASS, "Test Passed: " + result.getName());
        test.assignCategory(result.getMethod().getRealClass().getSimpleName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        test = report.createTest(result.getMethod().getMethodName());
        test.log(Status.FAIL, "Test Failed: " + result.getMethod().getMethodName());
        test.log(Status.FAIL, result.getThrowable());

        String screenshotPath = System.getProperty("user.dir")
                + "/screenshots/" + result.getMethod().getMethodName() + ".png";
        test.addScreenCaptureFromPath(screenshotPath);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        test = report.createTest(result.getMethod().getMethodName());
        test.log(Status.SKIP, "Test Skipped: " + result.getMethod().getMethodName());
    }

    public void onFinish(ITestContext result) {
        report.flush();
    }
}