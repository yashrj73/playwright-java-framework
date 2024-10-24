import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginAndHomePageTest extends BaseTest {
    // Use environment variables to store sensitive information
    private static final String WEBSITE_USERNAME = System.getenv("$BROWSERSTACK_WEBSITE_USERNAME");
    private static final String WEBSITE_PASSWORD = System.getenv("$BROWSERSTACK_WEBSITE_PASSWORD");

    // Positive Test Case: Successful login and navigation to dashboard
    @Test
    public void testSuccessfulLoginAndDashboardAccess() {
        // Navigate to BrowserStack login page
        driver.get("https://www.browserstack.com/users/sign_in");
        
        // Locate and input valid login credentials using environment variables
        driver.findElement(By.id("user_email_login")).sendKeys(WEBSITE_USERNAME);
        driver.findElement(By.id("user_password")).sendKeys(WEBSITE_PASSWORD);
        
        // Click the Sign In button
        driver.findElement(By.name("commit")).click();
        
        // Assert that login is successful by checking the dashboard page title
        Assert.assertTrue(driver.getTitle().contains("Dashboard"), "Login successful and dashboard loaded");
    }

    // Negative Test Case: Invalid login credentials
    @Test
    public void testInvalidLogin() {
        // Navigate to BrowserStack login page
        driver.get("https://www.browserstack.com/users/sign_in");
        
        // Locate and input invalid login credentials
        driver.findElement(By.id("user_email_login")).sendKeys("invalid_username");
        driver.findElement(By.id("user_password")).sendKeys("invalid_password");
        
        // Click the Sign In button
        driver.findElement(By.name("commit")).click();
        
        // Verify that the error message is displayed for invalid login
        Assert.assertTrue(driver.findElement(By.cssSelector(".error-msg")).isDisplayed(), "Error message displayed for invalid login");
    }
    
    // Test case to verify BrowserStack homepage title
    @Test
    public void testHomePageTitle() {
        // Navigate to BrowserStack homepage
        driver.get("https://www.browserstack.com/");
        
        // Assert that the homepage title contains "BrowserStack"
        Assert.assertTrue(driver.getTitle().contains("BrowserStack"), "Home page loaded successfully");
    }

    // Test case to navigate to the Live dashboard after login
    @Test(dependsOnMethods = "testSuccessfulLoginAndDashboardAccess")
    public void testLiveDashboardAccess() {
        // After logging in, navigate to the Live dashboard
        driver.get("https://live.browserstack.com/dashboard");
        
        // Check that the Live dashboard page is loaded
        Assert.assertTrue(driver.getTitle().contains("Live Dashboard"), "Live dashboard page loaded");
    }
}
