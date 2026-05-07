package testRunner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.Test;
import utilities.RetryAnalyzer;

@Test(retryAnalyzer = RetryAnalyzer.class)
@CucumberOptions(
        features  = "features",          // root-level features/ folder
        glue      = {"stepDefinitions"},        // Hooks + step defs all in stepdefs
        plugin    = {
                "pretty",
                "html:target/cucumber-html-report.html",
                "json:target/cucumber-report.json"
        },
        monochrome = true,
        dryRun     = false
)
public class TestRunner extends AbstractTestNGCucumberTests {
}