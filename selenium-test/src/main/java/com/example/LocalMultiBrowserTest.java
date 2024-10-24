package com.example;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.MutableCapabilities;

import java.net.MalformedURLException;
import java.net.URL;

public class LocalMultiBrowserTest {
    private WebDriver driver;
    private static final String WEBSITE_USERNAME = System.getenv("BROWSERSTACK_WEBSITE_USERNAME");
    private static final String WEBSITE_PASSWORD = System.getenv("BROWSERSTACK_WEBSITE_PASSWORD");

    public void setup(String browser, boolean runOnGrid) throws MalformedURLException {
        System.out.println("==========Received Browser Parameter2: " + browser);
        System.out.println("==========Running on Grid2: " + runOnGrid);
        System.out.println("==========Received WEBSITE_USERNAME: " + WEBSITE_USERNAME);
        System.out.println("==========Running on WEBSITE_PASSWORD: " + WEBSITE_PASSWORD);
        if (runOnGrid) {
            String gridUrl = "http://localhost:4444/wd/hub";
            DesiredCapabilities capabilities = new DesiredCapabilities();

            switch (browser.toLowerCase()) {
                case "chrome":
                    capabilities.setBrowserName("chrome");
                    break;
                case "firefox":
                    capabilities.setBrowserName("firefox");
                    FirefoxOptions options = new FirefoxOptions();
                    options.setCapability("platformName", "mac");
                    options.setBinary("/Applications/Firefox.app/Contents/MacOS/firefox");
                    capabilities.merge(options);
                    break;
                case "safari":
                    capabilities.setBrowserName("safari");
                    break;
                default:
                    throw new IllegalArgumentException("Invalid browser specified");
            }
            // Set up RemoteWebDriver with the Grid URL
            driver = new RemoteWebDriver(new URL(gridUrl), capabilities);
        } else {
            // Run locally if not on Grid
            switch (browser.toLowerCase()) {
                case "chrome":
                    System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
                    driver = new ChromeDriver();
                    break;
                case "firefox":
                    System.setProperty("webdriver.gecko.driver", "/opt/homebrew/bin/geckodriver");
                    FirefoxOptions options = new FirefoxOptions();
                    options.setBinary("/Applications/Firefox.app/Contents/MacOS/firefox");
                    driver = new FirefoxDriver(options);
                    break;
                case "safari":
                    driver = new SafariDriver();
                    break;
                default:
                    throw new IllegalArgumentException("Invalid browser specified");
            }
        }
    }

    public void testLoginAndLaunchDashboard_Positive() {
        System.out.println("==========Executing testLoginAndLaunchDashboard_Positive");
        driver.get("https://www.browserstack.com/users/sign_in");  // Step to login
        System.out.println("==========Print 1");
        // Add a wait for the login form to be visible
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user_email_login")));
        System.out.println("==========Print 2");
        // Example login steps, modify based on actual form fields
        driver.findElement(By.id("user_email_login")).sendKeys(WEBSITE_USERNAME);
        driver.findElement(By.id("user_password")).sendKeys(WEBSITE_PASSWORD);
        WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(By.name("commit")));
            signInButton.click();
        System.out.println("==========Print 3");
        // Wait for the "Live" link after successful login
        //WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));  // Replaced Duration with long value
        wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText("Live")));
        WebElement liveLink = wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Live")));
        System.out.println("==========Print 4");
        liveLink.click();  // Launch the Live dashboard
        System.out.println("==========Print 5");
        // Assert that the Live dashboard page title contains "Live Dashboard"
        assert driver.getTitle().contains("Live Dashboard");
        System.out.println("==========Print 6");
    }

    public void testLoginWithInvalidCredentials_Negative() {
        System.out.println("==========Executing testLoginWithInvalidCredentials_Negative");
    
        // Add the session cookie to bypass CAPTCHA
        Cookie cookie = new Cookie("session_id", "d275ead14b047980e65d5382fda46aad"); // Replace with actual session ID
        driver.manage().addCookie(cookie);
    
        // Navigate directly to the dashboard after adding the cookie
        driver.get("https://www.browserstack.com/dashboard");
        System.out.println("==========Session cookie added and navigated to dashboard");
    
        // Now navigate to the login page (without CAPTCHA blocking it)
        driver.get("https://www.browserstack.com/users/sign_in");
        System.out.println("==========Navigated to login page");
    
        // Add a wait for the login form to be visible
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user_email_login")));
        System.out.println("==========Login form visible");
    
        // Attempt login with invalid credentials
        driver.findElement(By.id("user_email_login")).sendKeys("invalid-email@example.com");
        driver.findElement(By.id("user_password")).sendKeys("wrong-password");
        WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(By.name("commit")));
        signInButton.click();
        System.out.println("==========Invalid credentials entered and login attempted");
    
        // Wait for the error message to appear
        WebElement alertText = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".bs-alert-text")));
        System.out.println("==========Error message visible");
    
        // Validate error message text
        String errorMessage = alertText.getText();
        assert errorMessage.contains("Invalid login credentials") :
                "Expected error message not found. Actual message: " + errorMessage;
        System.out.println("==========Test completed successfully with error message: " + errorMessage);
    }
    

    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    public static void main(String[] args) {
        LocalMultiBrowserTest test = new LocalMultiBrowserTest();
        
        String browser = "firefox";  // Change as needed
        boolean runOnGrid = true;   // Change to true if using Selenium Grid
        
        // Check if command-line arguments are provided
        System.out.println("==========Received Browser Parameter from Args: " + args[0]);
        System.out.println("==========Running on Grid from Args: " + args[1]);
        if (args.length > 0) {
        browser = args[0];  // First argument is the browser
        }
        if (args.length > 1) {
        runOnGrid = Boolean.parseBoolean(args[1]);  // Second argument is runOnGrid
        }

        try {
            test.setup(browser, runOnGrid);
            test.testLoginWithInvalidCredentials_Negative();
            test.testLoginAndLaunchDashboard_Positive();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            test.teardown();
        }
    }
}