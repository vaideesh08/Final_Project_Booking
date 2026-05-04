package stepDefinitions;

import base.DriverManager;
import io.cucumber.java.en.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import pagedObjects.CruisePage;
import utils.ExcelUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class CruiseSteps {

    private static final Logger log = LogManager.getLogger(CruiseSteps.class);

    CruisePage cruisePage;
    JavascriptExecutor js;

    private static final String EXCEL_PATH = "testdata/HotelDetails.xlsx";
    private static final String SHEET_NAME = "TestCase2";

    @Given("I am on Booking.com homepage for cruise search")
    public void open_homepage() {
        log.info("Step: I am on Booking.com homepage for cruise search");
        cruisePage = new CruisePage(DriverManager.getDriver());
        js = (JavascriptExecutor) DriverManager.getDriver();
    }

    @When("I search cruises in {string}")
    public void search_cruises(String city) {
        log.info("Step: I search cruises in '" + city + "'");
        cruisePage.closePopup();
        cruisePage.clickAttractions();
        cruisePage.enterDestination(city);
    }

    @When("For the next {int} days")
    public void for_the_next_days(Integer numberOfDays) {
        log.info("Step: For the next " + numberOfDays + " days");
        LocalDate checkIn  = LocalDate.now().plusDays(numberOfDays);
        LocalDate checkOut = checkIn.plusDays(numberOfDays);
        log.info("Check-in: " + checkIn + " | Check-out: " + checkOut);
        cruisePage.selectCheckInDate(checkIn);
        cruisePage.selectCheckOutDate(checkOut);
    }

    @When("Apply filter of {string}")
    public void apply_filter_of_cruises_and_boats(String filterName) {
        log.info("Step: Apply filter of '" + filterName + "'");
        cruisePage.clickSearch();
        cruisePage.expandCruiseFilter(js);
    }

    @Then("Cruise results should be displayed")
    public void verify_results() throws IOException {
        log.info("Step: Cruise results should be displayed");

        // Print to console
        cruisePage.printCruiseDetails(js);

        // Scrape and write to Excel
        log.info("Writing cruise results to Excel: " + EXCEL_PATH + " | Sheet: " + SHEET_NAME);
        List<String[]> cruiseData = cruisePage.getCruiseData(js);
        ExcelUtils excel = new ExcelUtils();
        excel.openExcel(EXCEL_PATH, SHEET_NAME);
        for (int i = 0; i < cruiseData.size(); i++) {
            String[] row = cruiseData.get(i);
            excel.setData(i + 1, 0, row[0]); // Cruise Name
            excel.setData(i + 1, 1, row[1]); // Price
            excel.setData(i + 1, 2, row[2]); // Duration
            excel.setData(i + 1, 3, row[3]); // Languages
        }
        excel.saveData(EXCEL_PATH);

        log.info("Cruise data written to Excel successfully — " + cruiseData.size() + " rows");
        System.out.println("Cruises written to Excel: " + EXCEL_PATH);
        System.out.println("Cruises displayed successfully!");
    }
}