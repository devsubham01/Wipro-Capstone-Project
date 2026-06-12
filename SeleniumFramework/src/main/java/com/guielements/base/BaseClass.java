package com.guielements.base;

import com.guielements.utils.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.time.Duration;

public class BaseClass {

    private static final Logger log = LogManager.getLogger(BaseClass.class);

    /**
     * @BeforeClass — runs once per test class per browser.
     * @Parameters("browser") reads browser value from testng.xml.
     * ThreadLocal in DriverManager keeps Chrome and Firefox separate during parallel run.
     */
    @Parameters("browser")
    @BeforeClass
    public void setUp(@Optional("chrome") String browser) {
        log.info("Launching browser: " + browser);

        WebDriver driver = createDriver(browser);
        int explicitWait = Integer.parseInt(ConfigReader.getProperty("explicit.wait"));
        int pageLoad     = Integer.parseInt(ConfigReader.getProperty("page.load.timeout"));

        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoad));

        // Store driver and wait in ThreadLocal via DriverManager
        DriverManager.setDriver(driver);
        DriverManager.setWait(new WebDriverWait(driver, Duration.ofSeconds(explicitWait)));

        // Navigate to URL once — all tests in this class share this session
        driver.get(ConfigReader.getProperty("url"));
        log.info("Navigated to: " + ConfigReader.getProperty("url"));

        // Wait for page to load
        try { Thread.sleep(3000); } catch (Exception ignored) {}
    }

    // Creates and returns the correct WebDriver based on browser parameter
    private WebDriver createDriver(String browser) {
        switch (browser.toLowerCase()) {
            case "edge":
                // WebDriverManager can't download EdgeDriver on restricted networks.
                // Manually set the path to msedgedriver.exe downloaded from:
                // https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/
                try {
                    WebDriverManager.edgedriver().setup(); // Try auto-download first
                } catch (Exception e) {
                    // Fallback: use manually downloaded driver at C:\drivers\msedgedriver.exe
                    System.setProperty("webdriver.edge.driver", "C:\\drivers\\msedgedriver.exe");
                    log.warn("WebDriverManager failed for Edge, using local driver: C:\\drivers\\msedgedriver.exe");
                }
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.addArguments("--start-maximized");
                edgeOptions.addArguments("--disable-notifications");
                return new EdgeDriver(edgeOptions);
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions ffOptions = new FirefoxOptions();
                ffOptions.addArguments("--disable-notifications");
                return new FirefoxDriver(ffOptions);
            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chOptions = new ChromeOptions();
                chOptions.addArguments("--start-maximized");
                chOptions.addArguments("--disable-notifications");
                return new ChromeDriver(chOptions);
        }
    }

    @AfterClass
    public void tearDown() {
        if (DriverManager.getDriver() != null) {
            DriverManager.getDriver().quit();
            DriverManager.removeDriver(); // Clean up ThreadLocal
            log.info("Browser closed");
        }
    }
}
