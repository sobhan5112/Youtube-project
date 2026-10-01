package demo.wrappers;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Wrappers {

    private WebDriver driver;
    private WebDriverWait wait;

    public Wrappers(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void navigateTo(String url) {
        driver.get(url);
    }

    public void click(By locator) {
        WebElement element = wait.until(
                ExpectedConditions.elementToBeClickable(locator)
        );
        element.click();
    }

    public void clickElement(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));

        try {
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].click();", element
            );
        }
    }

    public void enterText(By locator, String text) {
        WebElement element = wait.until(
                ExpectedConditions.visibilityOfElementLocated(locator)
        );
        element.clear();
        element.sendKeys(text);
    }

    public void pressEnter(By locator) {
        WebElement element = wait.until(
                ExpectedConditions.visibilityOfElementLocated(locator)
        );
        element.sendKeys(Keys.ENTER);
    }

    public void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                element
        );
    }

    public void scrollHorizontal(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollLeft = arguments[0].scrollWidth;",
                element
        );
    }

    public void scrollDown() {
        ((JavascriptExecutor) driver).executeScript(
                "window.scrollBy(0, 700);"
        );
    }

    public void scrollToBottom() {
        ((JavascriptExecutor) driver).executeScript(
                "window.scrollTo(0, document.body.scrollHeight);"
        );
    }

    public String getText(By locator) {
        try {
            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(locator)
            ).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public String getElementText(WebElement element) {
        try {
            return element.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public String getAttribute(
            WebElement element,
            String attribute
    ) {
        try {
            String value = element.getAttribute(attribute);
            return value == null ? "" : value.trim();
        } catch (Exception e) {
            return "";
        }
    }

    public List<WebElement> getElements(By locator) {
        try {
            wait.until(
                    ExpectedConditions.presenceOfElementLocated(locator)
            );
            return driver.findElements(locator);
        } catch (Exception e) {
            return List.of();
        }
    }

    public WebElement getElement(By locator) {
        return wait.until(
                ExpectedConditions.presenceOfElementLocated(locator)
        );
    }

    public boolean isDisplayed(By locator) {
        try {
            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(locator)
            ).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public void waitForPageLoad() {
        wait.until(driver ->
                ((JavascriptExecutor) driver)
                        .executeScript("return document.readyState")
                        .equals("complete")
        );
    }

    public void waitForSearchResults() {
        try {
            wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.cssSelector("ytd-video-renderer")
                    )
            );
        } catch (TimeoutException e) {
            wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.cssSelector("ytd-rich-item-renderer")
                    )
            );
        }
    }

    public int extractNumber(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        String number = text.replaceAll("[^0-9]", "");

        if (number.isEmpty()) {
            return 0;
        }

        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public long extractViews(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        text = text.toLowerCase().replace(",", "").trim();

        try {
            if (text.contains("crore") || text.contains("cr")) {
                String number =
                        text.replaceAll("[^0-9.]", "");

                return (long) (
                        Double.parseDouble(number) * 10_000_000
                );
            }

            if (text.contains("m")) {
                String number =
                        text.replaceAll("[^0-9.]", "");

                return (long) (
                        Double.parseDouble(number) * 1_000_000
                );
            }

            if (text.contains("k")) {
                String number =
                        text.replaceAll("[^0-9.]", "");

                return (long) (
                        Double.parseDouble(number) * 1_000
                );
            }

            String number =
                    text.replaceAll("[^0-9]", "");

            return number.isEmpty()
                    ? 0
                    : Long.parseLong(number);

        } catch (Exception e) {
            return 0;
        }
    }

    public void searchYouTube(String searchText) {
        enterText(
                By.name("search_query"),
                searchText
        );

        pressEnter(By.name("search_query"));

        waitForSearchResults();
    }

    public WebElement findLatestNewsCard(int index) {
        List<WebElement> cards = getElements(
                By.cssSelector(
                        "ytd-rich-item-renderer"
                )
        );

        if (index < cards.size()) {
            return cards.get(index);
        }

        return null;
    }

    public String getBodyText(WebElement card) {
        try {
            List<WebElement> elements =
                    card.findElements(
                            By.cssSelector(
                                    "#description"
                            )
                    );

            if (!elements.isEmpty()) {
                return elements.get(0).getText().trim();
            }

            return card.getText().trim();

        } catch (Exception e) {
            return "";
        }
    }

    public long getLikes(WebElement card) {
        try {
            String text = card.getText();

            String[] lines = text.split("\\n");

            for (String line : lines) {
                if (line.toLowerCase().contains("like")) {
                    return extractViews(line);
                }
            }

        } catch (Exception e) {
            return 0;
        }

        return 0;
    }
}
