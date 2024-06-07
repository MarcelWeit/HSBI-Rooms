package com.example.application.services;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RaumViewIT {

    private WebDriver driver;

    @BeforeEach
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.get("http://localhost:8080/login");

        new WebDriverWait(driver, Duration.ofSeconds(30), Duration.ofSeconds(1))
                .until(d -> d.getTitle().startsWith("Login"));

        driver.findElement(By.id("input-vaadin-text-field-6")).sendKeys("admin@gmail.com");
        driver.findElement(By.id("input-vaadin-password-field-7")).sendKeys("admin");
        driver.findElement(By.xpath("//vaadin-button[contains(.,'Einloggen')]")).click();

        new WebDriverWait(driver, Duration.ofSeconds(30), Duration.ofSeconds(1))
                .until(d -> d.getTitle().startsWith("Startseite"));
    }

    @Test
    public void testButtons() {
        driver.get("http://localhost:8080/raumverwaltung");
        new WebDriverWait(driver, Duration.ofSeconds(30), Duration.ofSeconds(1))
                .until(d -> d.getTitle().startsWith("Räume verwalten"));

        var addRoomButton = driver.findElement(By.xpath("//vaadin-button[contains(.,'Raum hinzufügen')]"));

        assertEquals("Raum hinzufügen", addRoomButton.getText());
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
