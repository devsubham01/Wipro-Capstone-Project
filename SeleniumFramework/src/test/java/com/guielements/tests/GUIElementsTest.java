package com.guielements.tests;

import com.guielements.base.BaseClass;
import com.guielements.pages.GUIElementsPage;
import com.guielements.utils.ConfigReader;
import com.guielements.utils.ExcelReader;
import com.guielements.utils.ExtentReportManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

public class GUIElementsTest extends BaseClass {

    private static final Logger log = LogManager.getLogger(GUIElementsTest.class);

    private GUIElementsPage page;

    // Test data variables — loaded from Excel
    private String name, email, phone, address, gender, days, country, color, date;

    /**
     * @BeforeClass order=1 runs AFTER BaseClass @BeforeClass (order=0 default).
     * This ensures WebDriver is ready before we load the page object and Excel data.
     */
    @BeforeClass(alwaysRun = true)
    public void initPageAndData() {

        // ── Load test data from Excel ─────────────────────────────────────────
        try {
            Map<String, String> data = ExcelReader.getFirstRow(
                ConfigReader.getProperty("testdata.path"),
                ConfigReader.getProperty("testdata.sheet")
            );
            name    = data.get("Name");
            email   = data.get("Email");
            phone   = data.get("Phone");
            address = data.get("Address");
            gender  = data.get("Gender");
            days    = data.get("Days");
            country = data.get("Country");
            color   = data.get("Color");
            date    = data.get("Date");
            log.info("Excel data loaded successfully");

        } catch (Exception e) {
            // Fallback hardcoded values if Excel is missing
            log.warn("Excel load failed, using defaults: " + e.getMessage());
            name    = "John Doe";
            email   = "johndoe@example.com";
            phone   = "9876543210";
            address = "123, Baker Street, London";
            gender  = "male";
            days    = "monday,wednesday,friday";
            country = "India";
            color   = "Green";
            date    = "06/08/2026";
        }

        // ── Initialize Page Object ────────────────────────────────────────────
        page = new GUIElementsPage();
        page.dismissAlertIfPresent();
        log.info("Page object initialized");
    }

    @Test(priority = 1, description = "Enter Name, Email, Phone, Address in text fields")
    public void testTextFields() {
        ExtentReportManager.getTest().info("Filling text fields from Excel data");
        page.enterName(name);
        page.enterEmail(email);
        page.enterPhone(phone);
        page.enterAddress(address);
        ExtentReportManager.getTest().pass("Text fields filled: " + name + " | " + email);
    }

    @Test(priority = 2, description = "Select Male radio button for Gender")
    public void testRadioButton() {
        ExtentReportManager.getTest().info("Selecting gender radio: " + gender);
        page.selectGender(gender);
        ExtentReportManager.getTest().pass("Gender selected: " + gender);
    }

    @Test(priority = 3, description = "Select Monday, Wednesday, Friday checkboxes")
    public void testCheckboxes() {
        ExtentReportManager.getTest().info("Selecting day checkboxes: " + days);
        page.selectDays(days.split(","));
        ExtentReportManager.getTest().pass("Days checked: " + days);
    }

    @Test(priority = 4, description = "Select Country and Color from dropdowns")
    public void testDropdowns() {
        ExtentReportManager.getTest().info("Selecting dropdowns");
        page.selectCountry(country);
        page.selectColor(color);
        ExtentReportManager.getTest().pass("Country: " + country + " | Color: " + color);
    }

    @Test(priority = 5, description = "Enter date using date picker")
    public void testDatePicker() {
        ExtentReportManager.getTest().info("Entering date: " + date);
        page.enterDate(date);
        ExtentReportManager.getTest().pass("Date entered: " + date);
    }

    @Test(priority = 6, description = "Handle Simple Alert — click OK")
    public void testSimpleAlert() {
        ExtentReportManager.getTest().info("Handling Simple Alert");
        String text = page.handleSimpleAlert();
        Assert.assertNotNull(text, "Simple alert text should not be null");
        ExtentReportManager.getTest().pass("Simple Alert text: " + text);
    }

    @Test(priority = 7, description = "Handle Confirmation Alert — click OK")
    public void testConfirmationAlert() {
        ExtentReportManager.getTest().info("Handling Confirmation Alert");
        String text = page.handleConfirmationAlert();
        Assert.assertNotNull(text, "Confirmation alert text should not be null");
        ExtentReportManager.getTest().pass("Confirmation Alert text: " + text);
    }

    @Test(priority = 8, description = "Handle Prompt Alert — type input and click OK")
    public void testPromptAlert() {
        ExtentReportManager.getTest().info("Handling Prompt Alert");
        String text = page.handlePromptAlert("TestUser");
        Assert.assertNotNull(text, "Prompt alert text should not be null");
        ExtentReportManager.getTest().pass("Prompt Alert text: " + text);
    }

    @Test(priority = 9, description = "Mouse hover on Point Me button")
    public void testMouseHover() {
        ExtentReportManager.getTest().info("Performing mouse hover");
        page.performMouseHover();
        ExtentReportManager.getTest().pass("Mouse Hover performed on Point Me button");
    }

    @Test(priority = 10, description = "Double click action")
    public void testDoubleClick() {
        ExtentReportManager.getTest().info("Performing double click");
        page.performDoubleClick();
        ExtentReportManager.getTest().pass("Double Click performed");
    }

    @Test(priority = 11, description = "Drag and drop element to target")
    public void testDragAndDrop() {
        ExtentReportManager.getTest().info("Performing drag and drop");
        page.performDragAndDrop();
        ExtentReportManager.getTest().pass("Drag and Drop performed");
    }

    @Test(priority = 12, description = "Move slider 10 steps using arrow keys")
    public void testSlider() {
        ExtentReportManager.getTest().info("Moving slider");
        page.handleSlider(10);
        ExtentReportManager.getTest().pass("Slider moved 10 steps");
    }
}
