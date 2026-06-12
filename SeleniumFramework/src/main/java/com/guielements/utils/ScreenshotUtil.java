package com.guielements.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotUtil {

    private static final Logger log = LogManager.getLogger(ScreenshotUtil.class);

    // Saves screenshot to screenshots/ folder using absolute path
    // Returns the full path where screenshot was saved
    public static String captureScreenshot(WebDriver driver, String testName) {
        String timestamp   = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String projectRoot = System.getProperty("user.dir");
        String folderPath  = projectRoot + File.separator + "screenshots";
        String fileName    = testName + "_" + timestamp + ".png";
        String fullPath    = folderPath + File.separator + fileName;

        try {
            // Create screenshots directory if it does not exist
            Files.createDirectories(Paths.get(folderPath));

            // Take screenshot and save to file
            File src  = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File dest = new File(fullPath);
            Files.copy(src.toPath(), dest.toPath());

            log.info("Screenshot saved at: " + fullPath);

        } catch (Exception e) {
            log.error("Screenshot save failed: " + e.getMessage());
        }

        return fullPath;
    }

    // Returns screenshot as Base64 string for embedding in Extent Report HTML
    public static String captureBase64(WebDriver driver) {
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            log.error("Base64 screenshot failed: " + e.getMessage());
            return "";
        }
    }
}
