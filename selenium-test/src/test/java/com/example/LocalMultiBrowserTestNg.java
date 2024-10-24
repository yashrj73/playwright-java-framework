import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Optional;

import java.net.MalformedURLException;
import java.net.URL;

public class LocalMultiBrowserTestNg {
    private WebDriver driver;
    private static final String WEBSITE_USERNAME = System.getenv("BROWSERSTACK_WEBSITE_USERNAME");
    private static final String WEBSITE_PASSWORD = System.getenv("BROWSERSTACK_WEBSITE_PASSWORD");

    @BeforeMethod
    @Parameters({"browser", "runOnGrid"})
    @SuppressWarnings("deprecation")
    public void setup(@Optional("chrome") String browser, @Optional("false") String runOnGrid) throws MalformedURLException {
        System.out.println("==========Received Browser Parameter1: " + browser);
        System.out.println("==========Running on Grid1: " + runOnGrid);

        boolean isGrid = Boolean.parseBoolean(runOnGrid);
        if (isGrid) {
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

    @Test
    public void testLoginAndLaunchDashboard_Positive() {
        System.out.println("==========Executing testLoginAndLaunchDashboard_Positive");
        
        driver.get("https://www.browserstack.com/users/sign_in");
        System.out.println("==========Print 1");

        // Wait for login form to be visible
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user_email_login")));
        System.out.println("==========Print 2");

        // Login steps
        driver.findElement(By.id("user_email_login")).sendKeys(WEBSITE_USERNAME);
        driver.findElement(By.id("user_password")).sendKeys(WEBSITE_PASSWORD);
        WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(By.name("commit")));
        signInButton.click();
        System.out.println("==========Print 3");

        // Wait for the "Live" link after successful login
        wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText("Live")));
        WebElement liveLink = wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Live")));
        System.out.println("==========Print 4");
        
        liveLink.click(); // Launch the Live dashboard
        System.out.println("==========Print 5");

        // Assert the title of the Live Dashboard page
        //String pageTitle = driver.getTitle();
        //Assert.assertTrue(pageTitle.contains("Live Dashboard"), "Page title does not contain 'Live Dashboard'");
        assert driver.getTitle().contains("Live Dashboard");
        System.out.println("==========Print 6");
    }
    @Test
    public void testLoginWithInvalidCredentials_Negative() {
        driver.get("https://www.browserstack.com/users/sign_in");  // Navigate to login page
        
        // Attempt login with invalid credentials
        driver.findElement(By.id("user_email_login")).sendKeys("invalid-email@example.com");
        driver.findElement(By.id("user_password")).sendKeys("wrong-password");
        driver.findElement(By.name("commit")).click();

        // Wait for the error message to appear
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));  // Replaced Duration with long value
        WebElement alertText = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".bs-alert-text")));

        // Validate error message text
        assert alertText.getText().contains("Invalid login credentials");
    }

    @AfterMethod
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
