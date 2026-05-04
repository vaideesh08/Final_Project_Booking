package stepDefinitions;

import io.cucumber.java.en.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pagedObjects.HotelSearchPage;
import utilities.ExcelUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class HotelSearchSteps {

    private static final Logger log = LogManager.getLogger(HotelSearchSteps.class);

    HotelSearchPage hotelPage;

    private static final String EXCEL_PATH = "testdata/HotelDetails.xlsx";
    private static final String SHEET_NAME = "TestCase1";

    @Given("I am on Booking.com homepage for hotel search")
    public void open_homepage() {
        log.info("Step: I am on Booking.com homepage for hotel search");
        hotelPage = new HotelSearchPage(DriverManager.getDriver());
    }

    @When("I search hotels in {string} for {int} days for {int} people")
    public void search_hotels(String city, Integer numberOfDays, Integer numberOfPeople) {
        log.info("Step: I search hotels in '" + city
                + "' for " + numberOfDays + " days for " + numberOfPeople + " people");

        hotelPage.closePopup();
        hotelPage.enterDestination(city);
        hotelPage.selectNairobiOption();

        LocalDate checkIn  = LocalDate.now().plusDays(1);
        LocalDate checkOut = checkIn.plusDays(numberOfDays);
        log.info("Check-in: " + checkIn + " | Check-out: " + checkOut);
        hotelPage.selectCheckInDate(checkIn);
        hotelPage.selectCheckOutDate(checkOut);

        hotelPage.openPersonsSelector();
        hotelPage.setAdults(numberOfPeople);
    }

    @When("Sort by top reviewed and apply filter for {string}")
    public void sort_by_top_reviewed_and_apply_filter_for(String filterName) {
        log.info("Step: Sort by top reviewed and apply filter for '" + filterName + "'");
        hotelPage.clickSearch();
        hotelPage.sortByTopReviewed();
        hotelPage.filterFreeWifi();
    }

    @Then("Hotel results should be displayed")
    public void verify_results() throws IOException {
        log.info("Step: Hotel results should be displayed");

        // Print to console
        hotelPage.printHotelResults();

        // Scrape and write to Excel
        log.info("Writing hotel results to Excel: " + EXCEL_PATH + " | Sheet: " + SHEET_NAME);
        List<String[]> hotelData = hotelPage.getHotelData();
        ExcelUtils excel = new ExcelUtils();
        excel.openExcel(EXCEL_PATH, SHEET_NAME);
        for (int i = 0; i < hotelData.size(); i++) {
            String[] row = hotelData.get(i);
            excel.setData(i + 1, 0, row[0]); // Name
            excel.setData(i + 1, 1, row[1]); // Distance
            excel.setData(i + 1, 2, row[2]); // Price
        }
        excel.saveData(EXCEL_PATH);

        log.info("Hotel data written to Excel successfully — " + hotelData.size() + " rows");
        System.out.println("Hotels written to Excel: " + EXCEL_PATH);
        System.out.println("Hotels displayed successfully!");
    }
}