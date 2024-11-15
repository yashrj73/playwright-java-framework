package com.example;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.Cookie;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class LocalGridBrowserNgTest {

    private WebDriver driver;
    private static final String USERNAME = System.getenv("BROWSERSTACK_USERNAME");
    private static final String ACCESS_KEY = System.getenv("BROWSERSTACK_ACCESS_KEY");
    private static final String URL = "https://" + USERNAME + ":" + ACCESS_KEY + "@hub-cloud.browserstack.com/wd/hub";
    private static final String WEBSITE_USERNAME = System.getenv("BROWSERSTACK_WEBSITE_USERNAME");
    private static final String WEBSITE_PASSWORD = System.getenv("BROWSERSTACK_WEBSITE_PASSWORD");

    @BeforeMethod
    @Parameters({"browser", "runOnGrid"})
    public void setup(String browser, boolean runOnGrid) throws MalformedURLException {
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
            driver = new RemoteWebDriver(new URL(gridUrl), capabilities);
        } else {
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

    @Test
    public void addItemToCart() {
        driver.get("https://bstackdemo.com/");
        WebElement iPhone11AddToCart = driver.findElement(By.xpath("//div[@data-sku='iPhone11-device-info.png']//div[contains(@class, 'shelf-item__buy-btn')]"));
        iPhone11AddToCart.click();
        System.out.println("iPhone 11 added to cart.");
    }

    @Test
    public void proceedToCheckout() {
        Assert.assertTrue(driver.findElement(By.className("float-cart__content")).isDisplayed());
        WebElement checkoutButton = driver.findElement(By.xpath("//*[@id=\\\"__next\\\"]/div/div/div[2]/div[2]/div[2]/div/div[3]"));
        checkoutButton.click();
        System.out.println("Proceeded to checkout.");
    }

    @Test
    public void loginAndPlaceOrder() {
        driver.get("https://bstackdemo.com/signin?checkout=true");

        WebElement usernameDropdown = driver.findElement(By.id("username"));
        Select selectUsername = new Select(usernameDropdown);
        selectUsername.selectByVisibleText("demouser");

        WebElement passwordDropdown = driver.findElement(By.id("password"));
        Select selectPassword = new Select(passwordDropdown);
        selectPassword.selectByVisibleText("testingisfun99");

        WebElement loginButton = driver.findElement(By.id("login-btn"));
        loginButton.click();
    }

    @Test
    public void fillShippingAddress() {
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
    }

    @AfterMethod
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
