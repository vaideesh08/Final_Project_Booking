package factory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import utilities.ConfigReader;

import java.time.Duration;

public class DriverManager {

    private static final Logger log = LogManager.getLogger(DriverManager.class);
    private static WebDriver driver;

    public static WebDriver getDriver() {
        return driver;
    }

    public static WebDriver initializeDriver(String browser) {
        log.info("Initializing browser: " + browser);

        switch (browser.toLowerCase()) {
            case "chrome":
                driver = new ChromeDriver();
                log.info("ChromeDriver started successfully");
                break;
            case "edge":
                driver = new EdgeDriver();
                log.info("EdgeDriver started successfully");
                break;
            case "firefox":
                driver = new FirefoxDriver();
                log.info("FirefoxDriver started successfully");
                break;
            default:
                log.error("No matching browser found: " + browser);
                return null;
        }

        String url = ConfigReader.getProperty("app.url");
        driver.get(url);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
        driver.manage().deleteAllCookies();
        log.info("Browser maximized, implicit wait set to 20s, cookies cleared");
        log.info("Navigated to: " + url);

        return driver;
    }

    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
            log.info("Driver quit successfully");
        }
    }
}