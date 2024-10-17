import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.*;
import org.testng.annotations.Optional;
import java.net.URL;

public class RemoteDriverTest {
    private WebDriver driver;

    @BeforeMethod
    @Parameters("browser")
    public void setup(@Optional("chrome") String browser) throws Exception {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setBrowserName(browser.toLowerCase());

        driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), caps);
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