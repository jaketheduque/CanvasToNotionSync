package me.jaketheduque.util;

import me.jaketheduque.data.Class;
import me.jaketheduque.data.UpcomingEvent;
import me.jaketheduque.main.CanvasToNotionSync;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public record MMLInterface(ChromeDriver driver, Class mmlClass) {

    public List<UpcomingEvent> getUpcomingHomework() {
        // Configure a FluentWait object
        FluentWait<ChromeDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class);

        driver.get(CanvasToNotionSync.PROPERTIES_FILE.getString("mml_url"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("mainButton")));

        // Login to MML
        driver.findElement(By.id("username")).sendKeys(CanvasToNotionSync.PROPERTIES_FILE.getString("mml_username"));
        driver.findElement(By.id("password")).sendKeys(CanvasToNotionSync.PROPERTIES_FILE.getString("mml_password"));
        driver.findElement(By.id("mainButton")).click();

        // Click on first class available
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(CanvasToNotionSync.PROPERTIES_FILE.getString("mml_class_element_id"))));
        driver.findElement(By.id(CanvasToNotionSync.PROPERTIES_FILE.getString("mml_class_element_id"))).click();

        // Swap to the iframe containing the assignments
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("contentFrame")));
        driver.switchTo().frame(driver.findElement(By.id("contentFrame")));

        // Get list of all upcoming assignments
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("all-upcoming-panel")));
        List<WebElement> assignments = driver.findElement(By.id("all-upcoming-panel")).findElements(By.xpath("*"));

        List<UpcomingEvent> temp = new ArrayList<>();

        // Create an UpcomingEvent object for every assignment found
        for (WebElement e : assignments) {
            String id = e.findElement(By.className("assignment-title")).findElement(By.xpath("..")).getAttribute("id");
            String title = e.findElement(By.className("assignment-title")).getText();
            String dueDateString = e.findElement(By.className("accMetadata-00-02")).getText().substring(4);

            // Convert text date to date object
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d");
            try {
                int currentYear = cal.get(Calendar.YEAR);
                cal.setTime(dateFormat.parse(dueDateString));
                cal.set(Calendar.YEAR, currentYear);
            } catch (ParseException ex) {
                throw new RuntimeException(ex);
            }

            temp.add(new UpcomingEvent(id, title, cal.getTime(), mmlClass));
        }

        return temp;
    }
}
