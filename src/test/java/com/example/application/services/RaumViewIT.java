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
        var editRoomButton = driver.findElement(By.xpath("//vaadin-button[contains(.,'Raum bearbeiten')]"));
        var deleteRoomButton = driver.findElement(By.xpath("//vaadin-button[contains(.,'Raum löschen')]"));
        var bookRoomButton = driver.findElement(By.xpath("//vaadin-button[contains(.,'Raum buchen')]"));
        var showBookingsButton = driver.findElement(By.xpath("//vaadin-button[contains(.,'Buchungen anzeigen')]"));
        var kwButton = driver.findElement(By.xpath("//vaadin-button[contains(.,'KW Verfügbarkeit')]"));

        assertEquals("Raum hinzufügen", addRoomButton.getText());
        assertEquals("Raum bearbeiten", editRoomButton.getText());
        assertEquals("Raum löschen", deleteRoomButton.getText());
        assertEquals("Raum buchen", bookRoomButton.getText());
        assertEquals("Buchungen anzeigen", showBookingsButton.getText());
        assertEquals("KW Verfügbarkeit", kwButton.getText());
    }

    @Test
    public void addRoom(){
        driver.get("http://localhost:8080/raumverwaltung");
        new WebDriverWait(driver, Duration.ofSeconds(30), Duration.ofSeconds(1))
                .until(d -> d.getTitle().startsWith("Räume verwalten"));

        var addRoomButton = driver.findElement(By.xpath("//vaadin-button[contains(.,'Raum hinzufügen')]"));
        addRoomButton.click();

        new WebDriverWait(driver, Duration.ofSeconds(30), Duration.ofSeconds(1))
                .until(d -> d.findElement(By.xpath("//vaadin-button[contains(.,'Speichern')]")));

        driver.findElement(By.id("input-vaadin-text-field-47")).sendKeys("C337");
        driver.findElement(By.id("input-vaadin-combo-box-49")).sendKeys("Wirtschaft");
        driver.findElement(By.id("input-vaadin-text-field-50")).sendKeys("Fachbereich Wirtschaft Etage 1");
        driver.findElement(By.id("input-vaadin-combo-box-52")).sendKeys("Hörsaal");
//        driver.findElement(By.id("input-vaadin-integer-field-53")).sendKeys("100");
        driver.findElement(By.id("input-vaadin-multi-select-combo-box-55")).sendKeys("Beamer");

        driver.findElement(By.xpath("//vaadin-button[contains(.,'Speichern')]")).click();

    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
