package com.guielements.pages;

import com.guielements.base.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class GUIElementsPage {

    private static final Logger log = LogManager.getLogger(GUIElementsPage.class);

    // ── Page Elements using @FindBy (POM) ────────────────────────────────────

    @FindBy(id = "name")
    private WebElement nameField;

    @FindBy(id = "email")
    private WebElement emailField;

    @FindBy(id = "phone")
    private WebElement phoneField;

    @FindBy(id = "textarea")
    private WebElement addressField;

    @FindBy(id = "male")
    private WebElement maleRadio;

    @FindBy(id = "female")
    private WebElement femaleRadio;

    @FindBy(id = "monday")
    private WebElement mondayCheckbox;

    @FindBy(id = "wednesday")
    private WebElement wednesdayCheckbox;

    @FindBy(id = "friday")
    private WebElement fridayCheckbox;

    @FindBy(id = "country")
    private WebElement countryDropdown;

    @FindBy(id = "colors")
    private WebElement colorsDropdown;

    @FindBy(id = "datepicker")
    private WebElement datePicker;

    @FindBy(xpath = "//button[text()='Simple Alert']")
    private WebElement simpleAlertBtn;

    @FindBy(xpath = "//button[text()='Confirmation Alert']")
    private WebElement confirmAlertBtn;

    @FindBy(xpath = "//button[text()='Prompt Alert']")
    private WebElement promptAlertBtn;

    @FindBy(xpath = "//button[text()='Point Me']")
    private WebElement hoverBtn;

    @FindBy(xpath = "//button[contains(text(),'Double') or @ondblclick]")
    private WebElement doubleClickBtn;

    @FindBy(id = "draggable")
    private WebElement draggable;

    @FindBy(id = "droppable")
    private WebElement droppable;

    // Constructor — PageFactory initializes all @FindBy elements
    public GUIElementsPage() {
        PageFactory.initElements(DriverManager.getDriver(), this);
    }

    // ── Text Fields ───────────────────────────────────────────────────────────

    public void enterName(String name) {
        waitVisible(nameField).clear();
        nameField.sendKeys(name);
        log.info("Name entered: " + name);
    }

    public void enterEmail(String email) {
        waitVisible(emailField).clear();
        emailField.sendKeys(email);
        log.info("Email entered: " + email);
    }

    public void enterPhone(String phone) {
        waitVisible(phoneField).clear();
        phoneField.sendKeys(phone);
        log.info("Phone entered: " + phone);
    }

    public void enterAddress(String address) {
        waitVisible(addressField).clear();
        addressField.sendKeys(address);
        log.info("Address entered: " + address);
    }

    // ── Radio Button ──────────────────────────────────────────────────────────

    public void selectGender(String gender) {
        if (gender.equalsIgnoreCase("male")) {
            waitClickable(maleRadio);
            if (!maleRadio.isSelected()) maleRadio.click();
        } else {
            waitClickable(femaleRadio);
            if (!femaleRadio.isSelected()) femaleRadio.click();
        }
        log.info("Gender selected: " + gender);
    }

    // ── Checkboxes ────────────────────────────────────────────────────────────

    public void selectDays(String[] days) {
        for (String day : days) {
            switch (day.trim().toLowerCase()) {
                case "monday":
                    if (!mondayCheckbox.isSelected()) mondayCheckbox.click();
                    break;
                case "wednesday":
                    if (!wednesdayCheckbox.isSelected()) wednesdayCheckbox.click();
                    break;
                case "friday":
                    if (!fridayCheckbox.isSelected()) fridayCheckbox.click();
                    break;
            }
            log.info("Day checked: " + day.trim());
        }
    }

    // ── Dropdowns ─────────────────────────────────────────────────────────────

    public void selectCountry(String country) {
        waitClickable(countryDropdown);
        new Select(countryDropdown).selectByVisibleText(country);
        log.info("Country selected: " + country);
    }

    public void selectColor(String color) {
        waitClickable(colorsDropdown);
        new Select(colorsDropdown).selectByVisibleText(color);
        log.info("Color selected: " + color);
    }

    // ── Date Picker ───────────────────────────────────────────────────────────

    public void enterDate(String date) {
        waitClickable(datePicker);
        datePicker.clear();
        datePicker.sendKeys(date);
        datePicker.sendKeys(Keys.TAB);
        log.info("Date entered: " + date);
    }

    // ── Alerts ────────────────────────────────────────────────────────────────

    public String handleSimpleAlert() {
        dismissAlertIfPresent();
        scrollAndClick(simpleAlertBtn);
        Alert alert = DriverManager.getWait().until(ExpectedConditions.alertIsPresent());
        String text = alert.getText();
        alert.accept();
        log.info("Simple Alert accepted: " + text);
        return text;
    }

    public String handleConfirmationAlert() {
        dismissAlertIfPresent();
        scrollAndClick(confirmAlertBtn);
        Alert alert = DriverManager.getWait().until(ExpectedConditions.alertIsPresent());
        String text = alert.getText();
        alert.accept();
        log.info("Confirmation Alert accepted: " + text);
        return text;
    }

    public String handlePromptAlert(String input) {
        dismissAlertIfPresent();
        scrollAndClick(promptAlertBtn);
        Alert alert = DriverManager.getWait().until(ExpectedConditions.alertIsPresent());
        String text = alert.getText();
        alert.sendKeys(input);
        alert.accept();
        log.info("Prompt Alert accepted. Input: " + input);
        return text;
    }

    // ── Mouse Hover ───────────────────────────────────────────────────────────

    public void performMouseHover() {
        dismissAlertIfPresent();
        scrollIntoView(hoverBtn);
        waitClickable(hoverBtn);
        new Actions(DriverManager.getDriver()).moveToElement(hoverBtn).perform();
        sleep(1500);
        log.info("Mouse Hover performed on Point Me button");
        try {
            List<WebElement> links = DriverManager.getDriver().findElements(
                By.xpath("//div[contains(@class,'tooltip')]//a | //ul[contains(@style,'block')]//a")
            );
            if (!links.isEmpty()) {
                links.get(0).click();
                log.info("Hover sub-item clicked");
            }
        } catch (Exception e) {
            log.info("Hover performed — no sub-item found");
        }
    }

    // ── Double Click ──────────────────────────────────────────────────────────

    public void performDoubleClick() {
        dismissAlertIfPresent();
        scrollIntoView(doubleClickBtn);
        waitClickable(doubleClickBtn);
        new Actions(DriverManager.getDriver()).doubleClick(doubleClickBtn).perform();
        sleep(800);
        dismissAlertIfPresent();
        log.info("Double Click performed");
    }

    // ── Drag and Drop ─────────────────────────────────────────────────────────

    public void performDragAndDrop() {
        dismissAlertIfPresent();
        DriverManager.getWait().until(ExpectedConditions.visibilityOf(draggable));
        scrollIntoView(draggable);
        new Actions(DriverManager.getDriver()).dragAndDrop(draggable, droppable).perform();
        sleep(800);
        log.info("Drag and Drop performed");
    }

    // ── Slider ────────────────────────────────────────────────────────────────

    public void handleSlider(int steps) {
        dismissAlertIfPresent();
        js("window.scrollTo(0, document.body.scrollHeight)");
        sleep(1000);
        try {
            WebElement sliderTrack = DriverManager.getWait().until(
                ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//div[contains(@class,'ui-slider')] | //div[@id='slider']")
                )
            );
            scrollIntoView(sliderTrack);
            WebElement handle = sliderTrack.findElement(
                By.xpath(".//span[contains(@class,'ui-slider-handle')]")
            );
            handle.click();
            for (int i = 0; i < steps; i++) handle.sendKeys(Keys.ARROW_RIGHT);
            log.info("Slider moved " + steps + " steps");
        } catch (Exception e) {
            log.warn("jQuery slider not found, trying START button: " + e.getMessage());
            try {
                WebElement startBtn = DriverManager.getDriver()
                    .findElement(By.xpath("//button[text()='START']"));
                scrollIntoView(startBtn);
                startBtn.click();
                sleep(2000);
                log.info("START button clicked as slider fallback");
            } catch (Exception ex) {
                log.error("Slider fallback also failed: " + ex.getMessage());
            }
        }
    }

    // ── Private Helpers ───────────────────────────────────────────────────────

    // Dismiss any open alert silently before interacting with page
    public void dismissAlertIfPresent() {
        try {
            DriverManager.getDriver().switchTo().alert().accept();
            sleep(300);
        } catch (Exception ignored) {}
    }

    private WebElement waitVisible(WebElement element) {
        return DriverManager.getWait().until(ExpectedConditions.visibilityOf(element));
    }

    private void waitClickable(WebElement element) {
        DriverManager.getWait().until(ExpectedConditions.elementToBeClickable(element));
    }

    private void scrollIntoView(WebElement element) {
        js("arguments[0].scrollIntoView({block:'center'});", element);
        sleep(400);
    }

    private void scrollAndClick(WebElement element) {
        scrollIntoView(element);
        waitClickable(element);
        element.click();
        sleep(1000);
    }

    private void js(String script, Object... args) {
        ((JavascriptExecutor) DriverManager.getDriver()).executeScript(script, args);
    }

    private void sleep(int ms) {
        try { Thread.sleep(ms); } catch (Exception ignored) {}
    }
}
