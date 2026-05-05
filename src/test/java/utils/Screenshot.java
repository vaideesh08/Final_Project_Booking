package utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.io.FileHandler;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Screenshot {
    private static final String FOLDER =
            System.getProperty("user.dir") + "/screenshots/";

    public static String screenShotTC(WebDriver driver, String fileName) {
        new File(FOLDER).mkdirs();
        String timestamp   = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss").format(new Date());
        File   src         = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String destination = FOLDER + fileName + "_" + timestamp + ".png";
        try {
            FileHandler.copy(src, new File(destination));
            return destination;
        } catch (IOException e) {
            throw new RuntimeException("Screenshot failed: " + e.getMessage());
        }
    }
}