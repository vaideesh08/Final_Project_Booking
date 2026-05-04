package pagedObjects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CruisePage {

    private static final Logger log = LogManager.getLogger(CruisePage.class);

    WebDriver driver;
    WebDriverWait wait;

    @FindBy(xpath = "//button[@aria-label='Dismiss sign-in info.']")
    private WebElement dismissPopup;

    @FindBy(id = "attractions")
    private WebElement attractionsLink;

    @FindBy(xpath = "//input[@data-testid='search-input-field']")
    private WebElement searchInput;

    @FindBy(xpath = "//div[contains(text(),'London')]")
    private WebElement londonOption;

    @FindBy(xpath = "//button[@data-testid='search-button']")
    private WebElement searchButton;

    @FindBy(xpath = "//span[text()='Tours ']/ancestor::span/following-sibling::button")
    private WebElement arrowButton;

    @FindBy(xpath = "//span[text()='Boat tours & cruises']")
    private WebElement cruiseFilter;

    @FindBy(xpath = "//div[@data-testid='card'][.//a[contains(text(),'Cruise')]]")
    private List<WebElement> cruises;

    public CruisePage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(30));
        PageFactory.initElements(driver, this);
        log.info("CruisePage initialized");
    }

    public void closePopup() {
        dismissPopup.click();
        log.info("Sign-in popup dismissed");
    }

    public void clickAttractions() {
        attractionsLink.click();
        log.info("Clicked on Attractions tab");
    }

    public void clickSearch() {
        searchButton.click();
        log.info("Clicked Search button");
    }

    public void enterDestination(String city) {
        log.info("Entering destination: " + city);
        searchInput.click();
        searchInput.sendKeys(city);
        wait.until(ExpectedConditions.visibilityOf(londonOption)).click();
        log.info("Destination selected: " + city);
    }

    public void selectCheckInDate(LocalDate date) {
        driver.findElement(By.xpath(String.format("//span[@data-date='%s']", date))).click();
        log.info("Check-in date selected: " + date);
    }

    public void selectCheckOutDate(LocalDate date) {
        driver.findElement(By.xpath(String.format("//span[@data-date='%s']", date))).click();
        log.info("Check-out date selected: " + date);
    }

    public void expandCruiseFilter(JavascriptExecutor js) {
        log.info("Expanding cruise filter");
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", arrowButton);
        wait.until(ExpectedConditions.visibilityOf(arrowButton)).click();
        wait.until(ExpectedConditions.visibilityOf(cruiseFilter)).click();
        log.info("Boat tours & cruises filter applied");
    }

    public void printCruiseDetails(JavascriptExecutor js) {
        int top = 3;
        log.info("Printing top " + top + " cruise results");
        String cur = driver.getWindowHandle();

        for (int i = 0; i < cruises.size() && i < top; i++) {
            System.out.println("\n" + "-".repeat(110));
            String title = cruises.get(i).findElement(By.xpath(".//a")).getText();
            System.out.println((i + 1) + ". " + title);
            log.info("Cruise " + (i + 1) + ": " + title);

            try {
                String price = cruises.get(i)
                        .findElement(By.xpath(".//div[contains(@class,'e7addce19e')]")).getText();
                System.out.println("Price : " + price);
                log.info("Price: " + price);
            } catch (Exception e) {
                System.out.println("Price not given");
                log.warn("Price not available for: " + title);
            }

            cruises.get(i).click();
            for (String w : driver.getWindowHandles()) {
                if (!w.equals(cur)) {
                    driver.switchTo().window(w);
                    try {
                        String duration = driver.findElement(By.xpath(
                                "//div[@class='e7addce19e' and contains(text(),'Duration')]")).getText();
                        System.out.println(duration);
                        log.info("Duration: " + duration);
                    } catch (Exception e) {
                        System.out.println("Duration not given");
                        log.warn("Duration not available for: " + title);
                    }

                    List<WebElement> langs = driver.findElements(By.xpath(
                            "//h3[text()='Audio guide available in multiple languages']"
                                    + "/following-sibling::div/descendant::div[@class='a9918d47bf']"));
                    if (langs.isEmpty()) {
                        System.out.println("No Languages Offered!");
                        log.warn("No languages found for: " + title);
                    } else {
                        StringBuilder sb = new StringBuilder("Languages Offered: ");
                        langs.forEach(l -> { System.out.print(l.getText() + " "); sb.append(l.getText()).append(" "); });
                        log.info(sb.toString().trim());
                    }

                    driver.close();
                    driver.switchTo().window(cur);
                    break;
                }
            }
        }
        System.out.println("\n" + "-".repeat(110));
    }

    public List<String[]> getCruiseData(JavascriptExecutor js) {
        List<String[]> results = new ArrayList<>();
        int top = 3;
        log.info("Scraping data for top " + top + " cruises");
        String cur = driver.getWindowHandle();

        for (int i = 0; i < cruises.size() && i < top; i++) {
            String title     = cruises.get(i).findElement(By.xpath(".//a")).getText();
            String price     = "Price not present";
            String duration  = "Duration not given";
            String languages = "No Languages Offered";

            try {
                price = cruises.get(i)
                        .findElement(By.xpath(".//div[contains(@class,'e7addce19e')]")).getText();
            } catch (Exception e) {
                log.warn("Price not on card for: " + title);
            }

            cruises.get(i).click();
            for (String w : driver.getWindowHandles()) {
                if (!w.equals(cur)) {
                    driver.switchTo().window(w);
                    try {
                        duration = driver.findElement(By.xpath(
                                "//div[@class='e7addce19e' and contains(text(),'Duration')]")).getText();
                    } catch (Exception e) {
                        log.warn("Duration not available for: " + title);
                    }

                    List<WebElement> langEls = driver.findElements(By.xpath(
                            "//h3[text()='Audio guide available in multiple languages']"
                                    + "/following-sibling::div/descendant::div[@class='a9918d47bf']"));
                    if (!langEls.isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        langEls.forEach(l -> sb.append(l.getText()).append(" "));
                        languages = sb.toString().trim();
                    }

                    driver.close();
                    driver.switchTo().window(cur);
                    break;
                }
            }

            log.info("Cruise scraped — Name: " + title + " | Price: " + price
                    + " | Duration: " + duration + " | Languages: " + languages);
            results.add(new String[]{title, price, duration, languages});
        }

        log.info("Total cruises scraped: " + results.size());
        return results;
    }
}