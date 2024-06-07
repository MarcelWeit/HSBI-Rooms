package com.example.application.services;

import com.example.application.repository.RaumRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static java.time.Duration.ofSeconds;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RaumViewIT {

    private WebDriver driver;

    @Autowired
    private RaumRepository raumRepository;

    @BeforeEach
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.get("http://localhost:8080/login");

        new WebDriverWait(driver, ofSeconds(30), ofSeconds(1))
                .until(d -> d.getTitle().startsWith("Login"));

        driver.findElement(By.id("input-vaadin-text-field-6")).sendKeys("admin@gmail.com");
        driver.findElement(By.id("input-vaadin-password-field-7")).sendKeys("admin");
        driver.findElement(By.xpath("//vaadin-button[contains(.,'Einloggen')]")).click();

        new WebDriverWait(driver, ofSeconds(30), ofSeconds(1))
                .until(d -> d.getTitle().startsWith("Startseite"));
    }

    //    @Test
    //    @Order(1)
    //    public void buttons() {
    //        driver.get("http://localhost:8080/raumverwaltung");
    //        new WebDriverWait(driver, ofSeconds(30), ofSeconds(1))
    //                .until(d -> d.getTitle().startsWith("Räume verwalten"));
    //
    //        var addRoomButton = driver.findElement(By.xpath("//vaadin-button[contains(.,'Raum hinzufügen')]"));
    //        var editRoomButton = driver.findElement(By.xpath("//vaadin-button[contains(.,'Raum bearbeiten')]"));
    //        var deleteRoomButton = driver.findElement(By.xpath("//vaadin-button[contains(.,'Raum löschen')]"));
    //        var bookRoomButton = driver.findElement(By.xpath("//vaadin-button[contains(.,'Raum buchen')]"));
    //        var showBookingsButton = driver.findElement(By.xpath("//vaadin-button[contains(.,'Buchungen anzeigen')]"));
    //        var kwButton = driver.findElement(By.xpath("//vaadin-button[contains(.,'KW Verfügbarkeit')]"));
    //
    //        assertEquals("Raum hinzufügen", addRoomButton.getText());
    //        assertEquals("Raum bearbeiten", editRoomButton.getText());
    //        assertEquals("Raum löschen", deleteRoomButton.getText());
    //        assertEquals("Raum buchen", bookRoomButton.getText());
    //        assertEquals("Buchungen anzeigen", showBookingsButton.getText());
    //        assertEquals("KW Verfügbarkeit", kwButton.getText());
    //    }

    @Test
    @Order(2)
    public void addRoom() {
        driver.get("http://localhost:8080/raumverwaltung");
        new WebDriverWait(driver, ofSeconds(30), ofSeconds(1))
                .until(d -> d.getTitle().startsWith("Räume verwalten"));

        var addRoomButton = driver.findElement(By.xpath("//vaadin-button[contains(.,'Raum hinzufügen')]"));
        addRoomButton.click();

        new WebDriverWait(driver, ofSeconds(30), ofSeconds(1))
                .until(d -> d.findElement(By.xpath("//vaadin-button[contains(.,'Speichern')]")));

        var refNr = "C337";
        var fb = "Wirtschaft";
        var pos = "Fachbereich Wirtschaft Etage 3";
        var typ = "Hörsaal";

        driver.findElement(By.id("input-vaadin-text-field-47")).sendKeys(refNr);
        driver.findElement(By.id("input-vaadin-combo-box-49")).sendKeys(fb);
        driver.findElement(By.id("input-vaadin-text-field-50")).sendKeys(pos);
        driver.findElement(By.id("input-vaadin-combo-box-52")).sendKeys(typ);

        driver.findElement(By.xpath("//vaadin-button[contains(.,'Speichern')]")).click();

        var xPathStart = "//vaadin-grid-cell-content[contains(.,'";
        var xPathEnd = "')]";

        new WebDriverWait(driver, ofSeconds(30), ofSeconds(1))
                .until(visibilityOfElementLocated(By.xpath(xPathStart + refNr + xPathEnd)));

        var refNrElement = driver.findElement(By.xpath(xPathStart + refNr + xPathEnd));
        var fbElement = driver.findElement(By.xpath(xPathStart + fb + xPathEnd));
        var typElement = driver.findElement(By.xpath(xPathStart + typ + xPathEnd));
        var posElement = driver.findElement(By.xpath(xPathStart + pos + xPathEnd));

        assertEquals(refNr, refNrElement.getText());
        assertEquals(fb, fbElement.getText());
        assertEquals(typ, typElement.getText());
        assertEquals(pos, posElement.getText());
    }

    @Test
    @Order(3)
    public void editRoom() {
        driver.get("http://localhost:8080/raumverwaltung");
        new WebDriverWait(driver, ofSeconds(30), ofSeconds(1))
                .until(d -> d.getTitle().startsWith("Räume verwalten"));

        var refNr = "C337";
        var newPos = "Fachbereich Wirtschaft Etage 4";

        driver.findElement(By.xpath("//vaadin-grid-cell-content[contains(.,'" + refNr + "')]")).click();
        driver.findElement(By.xpath("//vaadin-button[contains(.,'Raum bearbeiten')]")).click();

        new WebDriverWait(driver, ofSeconds(30), ofSeconds(1))
                .until(visibilityOfElementLocated(By.id("position-textfield")));
        driver.findElement(By.id("position-textfield")).sendKeys(Keys.CONTROL + "a");
        driver.findElement(By.id("position-textfield")).sendKeys(Keys.DELETE);
        // Irgendwie sendet er die ersten zwei Zeichen nicht - keine ahnung warum
        driver.findElement(By.id("position-textfield")).sendKeys("..Fachbereich Wirtschaft Etage 4");

        driver.findElement(By.xpath("//vaadin-button[contains(.,'Speichern')]")).click();

        var xPathStart = "//vaadin-grid-cell-content[contains(.,'";
        var xPathEnd = "')]";

        new WebDriverWait(driver, ofSeconds(30), ofSeconds(1))
                .until(visibilityOfElementLocated(By.xpath(xPathStart + refNr + xPathEnd)));
        new WebDriverWait(driver, ofSeconds(30), ofSeconds(1))
                .until(visibilityOfElementLocated(By.xpath(xPathStart + newPos + xPathEnd)));

        var refNrElement = driver.findElement(By.xpath(xPathStart + refNr + xPathEnd));
        var posElement = driver.findElement(By.xpath(xPathStart + newPos + xPathEnd));

        assertEquals(refNr, refNrElement.getText());
        assertEquals(newPos, posElement.getText());
    }

    @Test
    @Order(4)
    public void delete() {
        driver.get("http://localhost:8080/raumverwaltung");
        new WebDriverWait(driver, ofSeconds(30), ofSeconds(1))
                .until(d -> d.getTitle().startsWith("Räume verwalten"));

        var refNr = "C337";

        driver.findElement(By.xpath("//vaadin-grid-cell-content[contains(.,'" + refNr + "')]")).click();
        driver.findElement(By.xpath("//vaadin-button[contains(.,'Raum löschen')]")).click();

        new WebDriverWait(driver, ofSeconds(30), ofSeconds(1))
                .until(visibilityOfElementLocated(By.xpath("//vaadin-button[contains(.,'Löschen')]")));
        driver.findElement(By.xpath("//vaadin-button[contains(.,'Löschen')]")).click();

        new WebDriverWait(driver, ofSeconds(30), ofSeconds(1))
                .until(d -> !d.getPageSource().contains(refNr));
        assertEquals(Optional.empty(), raumRepository.findByRefNr(refNr));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
