import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.*;
import java.net.URL;

public class BrowserStackRemoteTestNg {
    public WebDriver driver;
    private static final String USERNAME = System.getenv("BROWSERSTACK_USERNAME");
    private static final String ACCESS_KEY = System.getenv("BROWSERSTACK_ACCESS_KEY");
    private static final String WEBSITE_USERNAME = System.getenv("BROWSERSTACK_WEBSITE_USERNAME");
    private static final String WEBSITE_PASSWORD = System.getenv("BROWSERSTACK_WEBSITE_PASSWORD");
    private static final String URL = "https://" + USERNAME + ":" + ACCESS_KEY + "@hub-cloud.browserstack.com/wd/hub";

    @BeforeMethod
    @Parameters({"browser", "browser_version", "os", "os_version"})
    public void setUp(String browser, String browser_version, String os, String os_version) throws Exception {
        System.out.println("Setting up test environment...");
        System.out.println("Test Type: BrowserStack");
        System.out.println("Browser: " + browser);
        System.out.println("Browser Version: " + browser_version);
        System.out.println("OS: " + os);
        System.out.println("OS Version: " + os_version);

        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("browser", browser);
        caps.setCapability("browser_version", browser_version);
        caps.setCapability("os", os);
        caps.setCapability("os_version", os_version);
        caps.setCapability("build", "Build 1");
        caps.setCapability("name", "Sample Test");
        caps.setCapability("consoleLogs", "info");
        driver = new RemoteWebDriver(new URL(URL), caps);
        System.out.println("WebDriver initialized successfully.");
    }

    // Positive Test Cases
    @Test(description = "Verify successful login to BrowserStack")
    public void testSuccessfulLogin() {
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
        Assert.assertTrue(dashboardHeader.isDisplayed(), "Dashboard is not displayed after login");
        System.out.println("Login successful. Dashboard is displayed.");
    }

    @Test(description = "Verify navigation to Live Dashboard")
    public void testLiveDashboardAccess() {
        System.out.println("Executing test: Verify navigation to Live Dashboard");
        // First login
        testSuccessfulLogin();

        // Navigate to Live Dashboard
        WebElement liveDashboardLink = driver.findElement(By.xpath("//a[contains(@href, '/live')]"));
        liveDashboardLink.click();

        // Verify Live Dashboard elements
        WebElement newSessionButton = driver.findElement(By.xpath("//button[contains(text(), 'New Session')]"));
        Assert.assertTrue(newSessionButton.isDisplayed(), "New Session button is not visible");
        System.out.println("Navigated to Live Dashboard successfully.");
    }

    @Test(description = "Verify browser launch in Live Dashboard")
    public void testBrowserLaunch() {
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
        Assert.assertTrue(browserWindow.isDisplayed(), "Browser session window is not displayed");
        System.out.println("Browser session launched successfully.");
    }

    // Negative Test Cases
    @Test(description = "Verify login failure with invalid credentials")
    public void testLoginWithInvalidCredentials() {
        System.out.println("Executing test: Verify login failure with invalid credentials");
        driver.get("https://www.browserstack.com/users/sign_in");

        WebElement emailField = driver.findElement(By.id("user_email_login"));
        WebElement passwordField = driver.findElement(By.id("user_password"));
        WebElement loginButton = driver.findElement(By.id("user_submit"));

        emailField.sendKeys("invalid@email.com");
        passwordField.sendKeys("invalidpassword");
        loginButton.click();

        WebElement errorMessage = driver.findElement(By.className("error-msg"));
        Assert.assertTrue(errorMessage.isDisplayed(), "Error message not displayed for invalid credentials");
        System.out.println("Error message displayed for invalid credentials.");
    }

    @Test(description = "Verify login failure with empty credentials")
    public void testLoginWithEmptyCredentials() {
        System.out.println("Executing test: Verify login failure with empty credentials");
        driver.get("https://www.browserstack.com/users/sign_in");

        WebElement loginButton = driver.findElement(By.id("user_submit"));
        loginButton.click();

        WebElement errorMessage = driver.findElement(By.className("error-msg"));
        Assert.assertTrue(errorMessage.isDisplayed(), "Error message not displayed for empty credentials");
        System.out.println("Error message displayed for empty credentials.");
    }

    @Test(description = "Verify session timeout handling")
    public void testSessionTimeout() {
        System.out.println("Executing test: Verify session timeout handling");
        // First login successfully
        testSuccessfulLogin();

        // Clear cookies to simulate session timeout
        driver.manage().deleteAllCookies();

        // Try to access Live Dashboard
        driver.get("https://live.browserstack.com");

        // Verify redirect to login page
        WebElement loginForm = driver.findElement(By.id("sign-in-form"));
        Assert.assertTrue(loginForm.isDisplayed(), "Not redirected to login page after session timeout");
        System.out.println("Redirected to login page after session timeout.");
    }

    @AfterMethod
    public void tearDown() {
        System.out.println("Tearing down the test environment...");
        if (driver != null) {
            driver.quit();
            System.out.println("WebDriver closed successfully.");
        }
    }
}
