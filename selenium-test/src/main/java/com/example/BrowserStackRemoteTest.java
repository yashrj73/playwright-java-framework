package com.example;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.MutableCapabilities;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class BrowserStackRemoteTest {
    public WebDriver driver;

    private static final String USERNAME = System.getenv("BROWSERSTACK_USERNAME");
    private static final String ACCESS_KEY = System.getenv("BROWSERSTACK_ACCESS_KEY");
    private static final String WEBSITE_USERNAME = System.getenv("BROWSERSTACK_WEBSITE_USERNAME");
    private static final String WEBSITE_PASSWORD = System.getenv("BROWSERSTACK_WEBSITE_PASSWORD");
    private static final String URL = "https://" + WEBSITE_USERNAME + ":" + WEBSITE_PASSWORD + "@hub-cloud.browserstack.com/wd/hub";
    
    @SuppressWarnings("deprecation")
    public void setUp(String browser, String browser_version, String os, String os_version) throws Exception {
    //System.out.println("Bstack URL..."+URL);
    System.out.println("Setting up test environment...");
    System.out.println("Test Type: BrowserStack");
    System.out.println("Browser: " + browser);
    System.out.println("Browser Version: " + browser_version);
    System.out.println("OS: " + os);
    System.out.println("OS Version: " + os_version);

    // Setting up W3C compliant capabilities
    MutableCapabilities capabilities = new MutableCapabilities();
    System.out.println("========Logs 1");
    capabilities.setCapability("browserName", browser); // W3C compliant key
    capabilities.setCapability("browserVersion", browser_version); // W3C compliant key
    System.out.println("========Logs 2");
    Map<String, Object> browserstackOptions = new HashMap<>();
    browserstackOptions.put("os", os);
    browserstackOptions.put("osVersion", os_version);
    browserstackOptions.put("build", "Build 1");
    browserstackOptions.put("name", "Sample Test");
    browserstackOptions.put("consoleLogs", "info");
    System.out.println("========Logs 3");
    capabilities.setCapability("bstack:options", browserstackOptions); // Adding BrowserStack-specific options
    System.out.println("========Logs 4");
    driver = new RemoteWebDriver(new URL("https://" + WEBSITE_USERNAME + ":" + WEBSITE_PASSWORD + "@hub-cloud.browserstack.com/wd/hub"), capabilities);
    System.out.println("WebDriver initialized successfully.");
    System.out.println("========Logs 5");
}


    // Positive Test Cases
    public void testSuccessfulLogin() {
        try {
            System.out.println("Executing test: Verify successful login to BrowserStack");
            driver.get("https://www.browserstack.com/users/sign_in");

            // Directly find elements without waiting
            WebElement emailField = driver.findElement(By.xpath("//input[@id='user_email_login']"));
            WebElement passwordField = driver.findElement(By.xpath("//input[@id='user_password']"));
            WebElement loginButton = driver.findElement(By.xpath("//input[@id='user_submit']"));

            emailField.sendKeys(WEBSITE_USERNAME);
            passwordField.sendKeys(WEBSITE_PASSWORD);
            loginButton.click();

            WebElement dashboardHeader = driver.findElement(By.xpath("//h1[contains(text(), 'Live')]"));
            if (!dashboardHeader.isDisplayed()) {
                throw new Exception("Dashboard is not displayed after login");
            }
            System.out.println("Login successful. Dashboard is displayed.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testLiveDashboardAccess() {
        try {
            System.out.println("Executing test: Verify navigation to Live Dashboard");
            // First login
            testSuccessfulLogin();

            // Navigate to Live Dashboard
            WebElement liveDashboardLink = driver.findElement(By.xpath("//a[contains(@href, '/live')]"));
            liveDashboardLink.click();

            // Verify Live Dashboard elements
            WebElement newSessionButton = driver.findElement(By.xpath("//button[contains(text(), 'New Session')]"));
            if (!newSessionButton.isDisplayed()) {
                throw new Exception("New Session button is not visible");
            }
            System.out.println("Navigated to Live Dashboard successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testBrowserLaunch() {
        try {
            System.out.println("Executing test: Verify browser launch in Live Dashboard");
            // Navigate to Live Dashboard
            testLiveDashboardAccess();

            // Start new session
            WebElement newSessionButton = driver.findElement(By.xpath("//button[contains(text(), 'New Session')]"));
            newSessionButton.click();

            // Select OS and Browser
            driver.findElement(By.xpath("//div[contains(@class, 'browser-icon')]")).click();

            // Verify browser session started
            WebElement browserWindow = driver.findElement(By.className("browser-session"));
            if (!browserWindow.isDisplayed()) {
                throw new Exception("Browser session window is not displayed");
            }
            System.out.println("Browser session launched successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Negative Test Cases
    public void testLoginWithInvalidCredentials() {
        try {
            System.out.println("Executing test: Verify login failure with invalid credentials");
            driver.get("https://www.browserstack.com/users/sign_in");

            WebElement emailField = driver.findElement(By.id("user_email_login"));
            WebElement passwordField = driver.findElement(By.id("user_password"));
            WebElement loginButton = driver.findElement(By.id("user_submit"));

            emailField.sendKeys("invalid@email.com");
            passwordField.sendKeys("invalidpassword");
            loginButton.click();

            WebElement errorMessage = driver.findElement(By.className("error-msg"));
            if (!errorMessage.isDisplayed()) {
                throw new Exception("Error message not displayed for invalid credentials");
            }
            System.out.println("Error message displayed for invalid credentials.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testLoginWithEmptyCredentials() {
        try {
            System.out.println("Executing test: Verify login failure with empty credentials");
            driver.get("https://www.browserstack.com/users/sign_in");

            WebElement loginButton = driver.findElement(By.id("user_submit"));
            loginButton.click();

            WebElement errorMessage = driver.findElement(By.className("error-msg"));
            if (!errorMessage.isDisplayed()) {
                throw new Exception("Error message not displayed for empty credentials");
            }
            System.out.println("Error message displayed for empty credentials.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testSessionTimeout() {
        try {
            System.out.println("Executing test: Verify session timeout handling");
            // First login successfully
            testSuccessfulLogin();

            // Clear cookies to simulate session timeout
            driver.manage().deleteAllCookies();

            // Try to access Live Dashboard
            driver.get("https://live.browserstack.com");

            // Verify redirect to login page
            WebElement loginForm = driver.findElement(By.id("sign-in-form"));
            if (!loginForm.isDisplayed()) {
                throw new Exception("Not redirected to login page after session timeout");
            }
            System.out.println("Redirected to login page after session timeout.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tearDown() {
        System.out.println("Tearing down the test environment...");
        if (driver != null) {
            driver.quit();
            System.out.println("WebDriver closed successfully.");
        }
    }

    public static void main(String[] args) {
        BrowserStackRemoteTest test = new BrowserStackRemoteTest();
        try {
            test.setUp("chrome", "latest", "Windows", "10");
            
            // Run test cases
            System.out.println("==========Print 1");
            test.testSuccessfulLogin();
            System.out.println("==========Print 2");
            test.testLiveDashboardAccess();
            System.out.println("==========Print 3");
            test.testBrowserLaunch();
            System.out.println("==========Print 4");
            test.testLoginWithInvalidCredentials();
            System.out.println("==========Print 5");
            test.testLoginWithEmptyCredentials();
            System.out.println("==========Print 6");
            test.testSessionTimeout();
            System.out.println("==========Print 7");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            test.tearDown();
        }
    }
}
