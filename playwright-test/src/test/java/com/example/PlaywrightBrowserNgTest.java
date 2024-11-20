package com.example;

import com.microsoft.playwright.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.nio.file.Paths;

public class PlaywrightBrowserNgTest {
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeMethod
    @Parameters({"browserType"})
    public void setUp(String browserType) {
        playwright = Playwright.create();
        
        // Configure browser launch options
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
            .setHeadless(false);

        // Launch browser based on parameter
        switch (browserType.toLowerCase()) {
            case "chromium":
                browser = playwright.chromium().launch(launchOptions);
                break;
            case "firefox":
                browser = playwright.firefox().launch(launchOptions);
                break;
            case "webkit":
                browser = playwright.webkit().launch(launchOptions);
                break;
            default:
                throw new IllegalArgumentException("Invalid browser type specified");
        }

        // Create new context and page
        context = browser.newContext();
        page = context.newPage();
    }

    @Test
    public void addItemToCart() {
        page.navigate("https://bstackdemo.com/");
        
        // Using more reliable locators
        Locator iPhone11AddToCart = page.locator("div[data-sku='iPhone11-device-info.png'] >> div.shelf-item__buy-btn");
        iPhone11AddToCart.click();
        
        // Verify cart is updated
        page.waitForSelector(".float-cart__content");
        System.out.println("iPhone 11 added to cart.");
    }

    @Test
    public void proceedToCheckout() {
        // Wait for cart to be visible and verify
        Locator cartContent = page.locator(".float-cart__content");
        Assert.assertTrue(cartContent.isVisible());
        
        // Click checkout using more precise locator
        page.locator(".float-cart__content >> text=Checkout").click();
        System.out.println("Proceeded to checkout.");
    }

    @Test
    public void loginAndPlaceOrder() {
        page.navigate("https://bstackdemo.com/signin?checkout=true");

        // Using Playwright's built-in select handling
        page.selectOption("#username", "demouser");
        page.selectOption("#password", "testingisfun99");
        
        // Click login button
        page.click("#login-btn");
        
        // Wait for successful login
        page.waitForNavigation();
    }

    @Test
    public void fillShippingAddress() {
        // Fill form using Playwright's fill method which handles clearing and typing
        page.fill("#firstNameInput", "John");
        page.fill("#lastNameInput", "Doe");
        page.fill("#addressInput", "123 Main St");
        page.fill("#stateInput", "CA");
        page.fill("#postalCodeInput", "12345");

        // Click order button and wait for response
        page.click("#order-btn");
        
        // Wait for order confirmation
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