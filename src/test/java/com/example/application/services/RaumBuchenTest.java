package com.example.application.services;

import com.example.application.data.enums.Zeitslot;
import com.example.application.repository.BuchungRepository;
import com.example.application.repository.RaumRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Optional;

import static java.time.Duration.ofSeconds;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RaumBuchenTest {

    private WebDriver driver;

    @Autowired
    private RaumRepository raumRepository;
    @Autowired
    private BuchungRepository buchungRepository;

    @BeforeEach
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.get("http://localhost:8080/login");
        driver.manage().window().maximize();
        Actions actions = new Actions(driver);
        actions.keyDown(Keys.CONTROL).sendKeys(Keys.SUBTRACT).keyUp(Keys.CONTROL).perform();

        new WebDriverWait(driver, ofSeconds(30), ofSeconds(1))
                .until(d -> d.getTitle().startsWith("Login"));

        driver.findElement(By.id("input-vaadin-text-field-6")).sendKeys("admin@gmail.com");
        driver.findElement(By.id("input-vaadin-password-field-7")).sendKeys("admin");
        driver.findElement(By.xpath("//vaadin-button[contains(.,'Einloggen')]")).click();

        new WebDriverWait(driver, ofSeconds(30), ofSeconds(1))
                .until(d -> d.getTitle().startsWith("Startseite"));
    }

    @Test
    @Order(1)
    public void bookRoom(){
        driver.get("http://localhost:8080/raumverwaltung");
        new WebDriverWait(driver, ofSeconds(30), ofSeconds(1))
                .until(d -> d.getTitle().startsWith("Räume verwalten"));

        driver.findElement(By.xpath("//vaadin-grid-cell-content[contains(.,'" + "A1" + "')]")).click();
        driver.findElement(By.xpath("//vaadin-button[contains(.,'Raum buchen')]")).click();
        new WebDriverWait(driver, ofSeconds(30), ofSeconds(1))
                .until(visibilityOfElementLocated(By.id("datepicker-startdate")));

        driver.findElement(By.id("datepicker-startdate")).sendKeys("13.6.2024");
        driver.findElement(By.id("datepicker-startdate")).sendKeys(Keys.ENTER);
        driver.findElement(By.id("combobox-veranstaltung")).sendKeys("Software Engineering");
        driver.findElement(By.id("combobox-dozent")).sendKeys("Küster, Jochen");
        driver.findElement(By.id("combobox-zeitslot")).sendKeys("08:00 - 09:30");
        driver.findElement(By.id("combobox-zeitslot")).sendKeys(Keys.ENTER);

        assertNotEquals(Optional.empty(), buchungRepository.findByDateAndRoomAndZeitslot(LocalDate.of(2024, 6, 13), raumRepository.findByRefNr("A1").get(), Zeitslot.EINS));
    }

    @Test
    @Order(2)
    public void deleteBuchung(){
        driver.get("http://localhost:8080/raumverwaltung");
        new WebDriverWait(driver, ofSeconds(30), ofSeconds(1))
                .until(d -> d.getTitle().startsWith("Räume verwalten"));

        driver.findElement(By.xpath("//vaadin-grid-cell-content[contains(.,'" + "A1" + "')]")).click();
        driver.findElement(By.xpath("//vaadin-button[contains(.,'Buchungen anzeigen')]")).click();

        new WebDriverWait(driver, ofSeconds(30), ofSeconds(1))
                .until(visibilityOfElementLocated(By.id("button-deletebooking")));

        driver.findElement(By.xpath("//vaadin-grid-cell-content[contains(.,'" + "A1" + "')]")).click();
        driver.findElement(By.id("button-deletebooking")).click();

        new WebDriverWait(driver, ofSeconds(30), ofSeconds(1))
                .until(visibilityOfElementLocated(By.xpath("//vaadin-button[contains(.,'Löschen')]")));
        driver.findElement(By.xpath("//vaadin-button[contains(.,'Löschen')]")).click();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
