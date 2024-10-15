import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.Optional;

public class LocalMultiBrowserTest {
    private WebDriver driver;

    @BeforeMethod
    @Parameters("browser")
    public void setup(@Optional("chrome") String browser) {
        System.out.println("==========Received Browser Parameter: " + browser);
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

    @Test
    public void testBrowserStackTitle() {
        driver.get("https://www.browserstack.com/");
        assert driver.getTitle().contains("BrowserStack");
    }

    @AfterMethod
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}