package com.example;

import com.microsoft.playwright.*;
import org.testng.Assert;
import org.testng.annotations.*;

public class LocalGridPlaywrightTest {
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeMethod
    @Parameters({"browserType", "useGrid"})
    public void setUp(String browserType, boolean useGrid) {
        playwright = Playwright.create();
        
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
            .setHeadless(false);

        if (useGrid) {
            // For Playwright grid execution, use connection to playwright grid
            String gridUrl = "ws://localhost:4444/playwright";
            
            switch (browserType.toLowerCase()) {
                case "chromium":
                    browser = playwright.chromium().connect(gridUrl);
                    break;
                case "firefox":
                    browser = playwright.firefox().connect(gridUrl);
                    break;
                case "webkit":
                    browser = playwright.webkit().connect(gridUrl);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid browser type: " + browserType);
            }
        } else {
            // Local execution
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
                    throw new IllegalArgumentException("Invalid browser type: " + browserType);
            }
        }
        
        context = browser.newContext();
        page = context.newPage();
    }

    @Test
    public void addItemToCart() {
        page.navigate("https://bstackdemo.com/");
        
        Locator addToCartButton = page.locator("div[data-sku='iPhone11-device-info.png'] >> .shelf-item__buy-btn");
        addToCartButton.click();
        
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
        
        page.selectOption("#username", "demouser");
        page.selectOption("#password", "testingisfun99");
        
        page.click("#login-btn");
        page.waitForNavigation();
    }

    @Test
    public void fillShippingAddress() {
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

    public static void main(String[] args) {
        LocalGridPlaywrightTest test = new LocalGridPlaywrightTest();
        String browserType = args.length > 0 ? args[0] : "chromium";
        boolean useGrid = args.length > 1 ? Boolean.parseBoolean(args[1]) : false;
        
        try {
            test.setUp(browserType, useGrid);
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