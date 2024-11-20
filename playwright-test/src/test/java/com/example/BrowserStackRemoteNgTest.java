package com.example;

import com.microsoft.playwright.*;
import org.testng.annotations.*;

public class BrowserStackRemoteNgTest {
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeClass
    public void setUp() {
        // Create Playwright instance
        playwright = Playwright.create();

        // Set up browser options for BrowserStack
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
            .setHeadless(false);

        // Launch browser - using Chromium by default
        browser = playwright.chromium().launch(launchOptions);
        
        // Create a new browser context
        context = browser.newContext();
        
        // Create a new page
        page = context.newPage();
        
        System.out.println("Playwright setup completed successfully.");
    }

    @Test(priority = 1)
    public void addItemToCart() {
        try {
            // Navigate to the website
            page.navigate("https://bstackdemo.com/");
            
            // Find and click the Add to Cart button for iPhone 11
            page.locator("//p[text()='iPhone 11']/ancestor::div[@class='shelf-item']//button")
                .click();
                
            System.out.println("iPhone 11 added to cart.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(priority = 2)
    public void proceedToCheckout() {
        try {
            // Click the checkout button in the cart
            page.locator(".buy-btn").click();
            System.out.println("Proceeded to checkout.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(priority = 3)
    public void loginAndPlaceOrder() {
        try {
            page.navigate("https://bstackdemo.com/signin?checkout=true");
            
            // Select username from dropdown
            page.selectOption("#username", "demouser");
            
            // Select password from dropdown
            page.selectOption("#password", "testingisfun99");
            
            // Click login button
            page.click("#login-btn");
            
            System.out.println("Logged in successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(priority = 4)
    public void fillShippingAddress() {
        try {
            // Fill in shipping details
            page.fill("#firstNameInput", "John");
            page.fill("#lastNameInput", "Doe");
            page.fill("#addressInput", "123 Main St");
            page.fill("#stateInput", "CA");
            page.fill("#postalCodeInput", "12345");
            
            // Click confirm order button
            page.click("#order-btn");
            
            System.out.println("Shipping address filled and order confirmed.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public void tearDown() {
        if (page != null) {
            page.close();
        }
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
        BrowserStackRemoteNgTest test = new BrowserStackRemoteNgTest();
        try {
            test.setUp();
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