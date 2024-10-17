import org.openqa.selenium.WebDriver;
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
import org.testng.annotations.Optional;

import java.net.MalformedURLException;
import java.net.URL;

public class LocalMultiBrowserTest {
    private WebDriver driver;

    @BeforeMethod
    @Parameters({"browser", "runOnGrid"})
    public void setup(@Optional("chrome") String browser, @Optional("false") String runOnGrid) throws MalformedURLException {
        System.out.println("==========Received Browser Parameter: " + browser);
        System.out.println("==========Running on Grid: " + runOnGrid);

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
