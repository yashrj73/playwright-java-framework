package com.example;

import com.microsoft.playwright.*;
import org.testng.Assert;
import org.testng.annotations.*;

public class BrowserStackPlaywrightTest {
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    private static final String USERNAME = System.getenv("BROWSERSTACK_USERNAME");
    private static final String ACCESS_KEY = System.getenv("BROWSERSTACK_ACCESS_KEY");

    @BeforeMethod
    @Parameters({"browser", "browserVersion", "os", "osVersion"})
    public void setUp(String browser, String browserVersion, String os, String osVersion) {
        playwright = Playwright.create();
        
        // Configure BrowserStack capabilities
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
            .setChannel("msedge")  // or chrome/firefox
            .setHeadless(false);
            
        // Set BrowserStack specific capabilities
        launchOptions.setEnv(new String[] {
            "BROWSERSTACK_USERNAME=" + USERNAME,
            "BROWSERSTACK_ACCESS_KEY=" + ACCESS_KEY,
            "BROWSERSTACK_BROWSER=" + browser,
            "BROWSERSTACK_BROWSER_VERSION=" + browserVersion,
            "BROWSERSTACK_OS=" + os,
            "BROWSERSTACK_OS_VERSION=" + osVersion,
            "BROWSERSTACK_BUILD_NAME=Playwright Tests",
            "BROWSERSTACK_PROJECT_NAME=Example Project"
        });

        // Connect to BrowserStack
        browser = playwright.chromium().connect("wss://cdp.browserstack.com/playwright?caps=" + 
            String.format("browserName=%s&browserVersion=%s&os=%s&osVersion=%s",
                browser, browserVersion, os, osVersion));
                
        context = browser.newContext();
        page = context.newPage();
    }

    @Test
    public void addItemToCart() {
        page.navigate("https://bstackdemo.com/");
        
        // Using Playwright's more reliable locators
        Locator addToCartButton = page.locator("div[data-sku='iPhone11-device-info.png'] >> .shelf-item__buy-btn");
        addToCartButton.click();
        
        // Verify cart update
        page.waitForSelector(".float-cart__content");
        System.out.println("iPhone 11 added to cart.");
    }

    @Test
    public void proceedToCheckout() {
        Locator cartContent = page.locator(".float-cart__content");
        Assert.assertTrue(cartContent.isVisible());
        
        page.locator(".float-cart__content >> text=Checkout").click();
        System.out.println("Proceeded to checkout.");
    }

    @Test
    public void loginAndPlaceOrder() {
        page.navigate("https://bstackdemo.com/signin?checkout=true");
        
        // Handle dropdowns with Playwright's selectOption
        page.selectOption("#username", "demouser");
        page.selectOption("#password", "testingisfun99");
        
        page.click("#login-btn");
        page.waitForNavigation();
    }

    @Test
    public void fillShippingAddress() {
        // Fill form fields using Playwright's fill method
        page.fill("#firstNameInput", "John");
        page.fill("#lastNameInput", "Doe");
        page.fill("#addressInput", "123 Main St");
        page.fill("#stateInput", "CA");
        page.fill("#postalCodeInput", "12345");
        
        page.click("#order-btn");
        page.waitForSelector(".order-confirmation", new Page.WaitForSelectorOptions().setTimeout(5000));
    }

    @AfterMethod
    public void tearDown() {
        if (context != null) {
            context.close();
        }
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}