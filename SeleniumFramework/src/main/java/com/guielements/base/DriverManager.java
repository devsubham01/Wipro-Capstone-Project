package com.guielements.base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * DriverManager holds WebDriver and WebDriverWait in ThreadLocal variables.
 * This ensures each parallel thread (Chrome / Firefox) gets its own driver
 * and they never interfere with each other.
 */
public class DriverManager {

    // ThreadLocal ensures each thread has its own WebDriver instance
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static ThreadLocal<WebDriverWait> wait = new ThreadLocal<>();

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void setDriver(WebDriver webDriver) {
        driver.set(webDriver);
    }

    public static WebDriverWait getWait() {
        return wait.get();
    }

    public static void setWait(WebDriverWait webDriverWait) {
        wait.set(webDriverWait);
    }

    // Called in @AfterClass to clean up ThreadLocal after test class finishes
    public static void removeDriver() {
        driver.remove();
        wait.remove();
    }
}
