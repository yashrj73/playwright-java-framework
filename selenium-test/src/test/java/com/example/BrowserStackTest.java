package com.example;

   import org.openqa.selenium.WebDriver;
   import org.openqa.selenium.chrome.ChromeDriver;
   import org.testng.annotations.AfterMethod;
   import org.testng.annotations.BeforeMethod;
   import org.testng.annotations.Test;

   public class BrowserStackTest {
       private WebDriver driver;

       @BeforeMethod
       public void setup() {
           System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
           driver = new ChromeDriver();
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
