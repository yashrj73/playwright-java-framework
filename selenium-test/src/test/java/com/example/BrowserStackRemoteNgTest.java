package com.example;
import org.testng.annotations.*;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.support.ui.*;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.testng.Assert;

public class BrowserStackRemoteNgTest {
    public WebDriver driver;
    public WebDriverWait wait;
    private static final Duration TIMEOUT = Duration.ofSeconds(20);
    private static final Duration POLLING_INTERVAL = Duration.ofMillis(500);

    private static final String USERNAME = System.getenv("BROWSERSTACK_USERNAME");
    private static final String ACCESS_KEY = System.getenv("BROWSERSTACK_ACCESS_KEY");
    private static final String URL = "https://" + USERNAME + ":" + ACCESS_KEY + "@hub-cloud.browserstack.com/wd/hub";

    @BeforeMethod // Changed from @BeforeTest to ensure fresh session for each test
    @Parameters({"browser", "browser_version", "os", "os_version"})
    public void setUp(String browser, String browser_version, String os, String os_version) throws Exception {
        System.out.println("\n========== TEST SETUP STARTED ==========");
        
        MutableCapabilities capabilities = new MutableCapabilities();
        capabilities.setCapability("browserName", browser);
        capabilities.setCapability("browserVersion", browser_version);

        Map<String, Object> browserstackOptions = new HashMap<>();
        browserstackOptions.put("os", os);
        browserstackOptions.put("osVersion", os_version);
        browserstackOptions.put("build", "Test-NG-maven based run");
        browserstackOptions.put("name", "BrowserStackRemoteNgTest");
        browserstackOptions.put("consoleLogs", "info");
        browserstackOptions.put("networkLogs", "true");
        browserstackOptions.put("seleniumVersion", "4.11.0");
        capabilities.setCapability("bstack:options", browserstackOptions);

        driver = new RemoteWebDriver(new URL(URL), capabilities);
        wait = new WebDriverWait(driver, TIMEOUT);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        System.out.println("========== TEST SETUP COMPLETED ==========\n");
    }

    @Test(priority = 1)
public void addItemToCart() {
    try {
        driver.get("https://bstackdemo.com/");
        
        // Wait for product to be clickable and click
        wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//div[@data-sku='iPhone11-device-info.png']//div[contains(@class, 'shelf-item__buy-btn')]")))
            .click();
            
        // Wait for cart to be visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.className("float-cart__content")));
            
    } catch (Exception e) {
        System.err.println("ERROR in addItemToCart test: " + e.getMessage());
        throw e;
    }
}

@Test(priority = 2, dependsOnMethods = "addItemToCart")
public void proceedToCheckout() {
    try {
        // Wait for the cart tray content to become visible
        Assert.assertTrue(driver.findElement(By.className("float-cart__content")).isDisplayed());

        // Refined XPath for locating the "Checkout" button
        WebElement checkoutButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//*[@id=\\\"__next\\\"]/div/div/div[2]/div[2]/div[2]/div/div[3]")));

        // Click the "Checkout" button
        checkoutButton.click();

    } catch (Exception e) {
        System.err.println("ERROR in proceedToCheckout test: " + e.getMessage());
        throw e;
    }
}


    @Test(priority = 3, dependsOnMethods = "proceedToCheckout")
    public void loginAndPlaceOrder() {
        try {
            // Ensure we're on the login page
            wait.until(ExpectedConditions.urlContains("/signin"));
            
            // Handle username dropdown
            WebElement usernameDropdown = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));
            new Select(usernameDropdown).selectByVisibleText("demouser");

            // Handle password dropdown
            WebElement passwordDropdown = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("password")));
            new Select(passwordDropdown).selectByVisibleText("testingisfun99");

            // Click login
            wait.until(ExpectedConditions.elementToBeClickable(By.id("login-btn"))).click();
            
        } catch (Exception e) {
            System.err.println("ERROR in loginAndPlaceOrder test: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 4, dependsOnMethods = "loginAndPlaceOrder")
    public void fillShippingAddress() {
        try {
            // Wait for form to be present
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("firstNameInput")))
                .sendKeys("John");
                
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("lastNameInput")))
                .sendKeys("Doe");
                
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("addressInput")))
                .sendKeys("123 Main St");
                
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("stateInput")))
                .sendKeys("CA");
                
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("postalCodeInput")))
                .sendKeys("12345");

            // Submit order
            wait.until(ExpectedConditions.elementToBeClickable(By.id("order-btn")))
                .click();
                
        } catch (Exception e) {
            System.err.println("ERROR in fillShippingAddress test: " + e.getMessage());
            throw e;
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}