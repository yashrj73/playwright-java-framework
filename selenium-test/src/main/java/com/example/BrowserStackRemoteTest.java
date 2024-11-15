package com.example;
import org.testng.annotations.Test;  
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.support.ui.Select;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.testng.Assert;
public class BrowserStackRemoteTest {
    public WebDriver driver;

    private static final String USERNAME = System.getenv("BROWSERSTACK_USERNAME");
    private static final String ACCESS_KEY = System.getenv("BROWSERSTACK_ACCESS_KEY");
    private static final String URL = "https://" + USERNAME + ":" + ACCESS_KEY + "@hub-cloud.browserstack.com/wd/hub";

    @SuppressWarnings("deprecation")
    public void setUp(String browser, String browser_version, String os, String os_version) throws Exception {
        System.out.println("Setting up test environment...");
        MutableCapabilities capabilities = new MutableCapabilities();
        capabilities.setCapability("browserName", browser);
        capabilities.setCapability("browserVersion", browser_version);

        Map<String, Object> browserstackOptions = new HashMap<>();
        browserstackOptions.put("os", os);
        browserstackOptions.put("osVersion", os_version);
        browserstackOptions.put("build", "Local Driver based run");
        browserstackOptions.put("name", "BrowserStackRemoteTest");
        browserstackOptions.put("consoleLogs", "info");
        capabilities.setCapability("bstack:options", browserstackOptions);

        driver = new RemoteWebDriver(new URL(URL), capabilities);
        System.out.println("WebDriver initialized successfully.");
    }

    @Test
    public void addItemToCart() {
        try {
            driver.get("https://bstackdemo.com/");
            WebElement iPhone11AddToCart = driver.findElement(By.xpath("//div[@data-sku='iPhone11-device-info.png']//div[contains(@class, 'shelf-item__buy-btn')]"));
            iPhone11AddToCart.click();
            System.out.println("iPhone 11 added to cart.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void proceedToCheckout() {
        try {
            Assert.assertTrue(driver.findElement(By.className("float-cart__content")).isDisplayed());
            WebElement checkoutButton = driver.findElement(By.xpath("//*[@id=\\\"__next\\\"]/div/div/div[2]/div[2]/div[2]/div/div[3]"));
            checkoutButton.click();
            System.out.println("Proceeded to checkout.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loginAndPlaceOrder() {
        try {
            driver.get("https://bstackdemo.com/signin?checkout=true");
            
            WebElement usernameDropdown = driver.findElement(By.id("username"));
            Select selectUsername = new Select(usernameDropdown);
            selectUsername.selectByVisibleText("demouser");
            
            WebElement passwordDropdown = driver.findElement(By.id("password"));
            Select selectPassword = new Select(passwordDropdown);
            selectPassword.selectByVisibleText("testingisfun99");

            WebElement loginButton = driver.findElement(By.id("login-btn"));
            loginButton.click();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void fillShippingAddress() {
        try {
            WebElement firstNameField = driver.findElement(By.id("firstNameInput"));
            firstNameField.sendKeys("John");
        
            WebElement lastNameField = driver.findElement(By.id("lastNameInput"));
            lastNameField.sendKeys("Doe");
        
            WebElement addressField = driver.findElement(By.id("addressInput"));
            addressField.sendKeys("123 Main St");
        
            WebElement stateField = driver.findElement(By.id("stateInput"));
            stateField.sendKeys("CA");
        
            WebElement postalCodeField = driver.findElement(By.id("postalCodeInput"));
            postalCodeField.sendKeys("12345");

            WebElement confirmOrderButton = driver.findElement(By.id("order-btn"));
            confirmOrderButton.click();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }


    public static void main(String[] args) {
        BrowserStackRemoteTest test = new BrowserStackRemoteTest();
        try {
            test.setUp("chrome", "latest", "Windows", "10");

            // Run the test sequence
            test.addItemToCart();
            test.proceedToCheckout();
            test.loginAndPlaceOrder();
            test.fillShippingAddress();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            test.tearDown();
        }
    }
}
