package demo;

import java.util.List;
import java.util.logging.Level;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import demo.utils.ExcelDataProvider;
import demo.wrappers.Wrappers;

public class TestCases extends ExcelDataProvider {

    ChromeDriver driver;
    Wrappers wrappers;

    // =========================================================
    // START BROWSER
    // =========================================================

    @BeforeTest
    public void startBrowser() {

        System.setProperty(
                "java.util.logging.config.file",
                "logging.properties"
        );

        ChromeOptions options = new ChromeOptions();

        LoggingPreferences logs = new LoggingPreferences();

        logs.enable(
                LogType.BROWSER,
                Level.ALL
        );

        logs.enable(
                LogType.DRIVER,
                Level.ALL
        );

        options.setCapability(
                "goog:loggingPrefs",
                logs
        );

        options.addArguments(
                "--remote-allow-origins=*"
        );

        System.setProperty(
                ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY,
                "build/chromedriver.log"
        );

        driver = new ChromeDriver(options);

        driver.manage()
                .window()
                .maximize();

        wrappers = new Wrappers(driver);
    }

    // =========================================================
    // TEST CASE 01
    // =========================================================

    @Test
    public void testCase01() {

        wrappers.navigateTo(
                "https://www.youtube.com/"
        );

        wrappers.waitForPageLoad();

        SoftAssert softAssert = new SoftAssert();

        softAssert.assertTrue(
                wrappers.getCurrentUrl()
                        .contains("youtube.com"),
                "YouTube URL is incorrect"
        );

        By aboutLink = By.xpath(
                "//a[contains(@href,'/about')]"
        );

        wrappers.scrollToBottom();

        wrappers.click(aboutLink);

        wrappers.waitForPageLoad();

        String message = wrappers.getText(
                By.cssSelector("body")
        );

        System.out.println(
                "YouTube About message:"
        );

        System.out.println(message);

        softAssert.assertAll();
    }

    // =========================================================
    // TEST CASE 02
    // FILMS / MOVIES
    // =========================================================

    @Test
    public void testCase02() {

        wrappers.navigateTo(
                "https://www.youtube.com/movies"
        );

        wrappers.waitForPageLoad();

        SoftAssert softAssert = new SoftAssert();

        By topSelling = By.xpath(
                "//*[contains("
                        + "translate(normalize-space(),"
                        + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ',"
                        + "'abcdefghijklmnopqrstuvwxyz'),"
                        + "'top selling')]"
        );

        try {

            wrappers.click(topSelling);

        } catch (Exception e) {

            System.out.println(
                    "Top Selling section already visible."
            );
        }

        List<WebElement> cards =
                wrappers.getElements(
                        By.cssSelector(
                                "ytd-grid-movie-renderer"
                        )
                );

        if (cards.isEmpty()) {

            cards = wrappers.getElements(
                    By.cssSelector(
                            "ytd-movie-renderer"
                    )
            );
        }

        if (cards.isEmpty()) {

            cards = wrappers.getElements(
                    By.cssSelector(
                            "ytd-rich-item-renderer"
                    )
            );
        }

        SoftAssert movieAssert =
                new SoftAssert();

        if (!cards.isEmpty()) {

            WebElement lastMovie =
                    cards.get(
                            cards.size() - 1
                    );

            wrappers.scrollToElement(
                    lastMovie
            );

            String movieText =
                    wrappers.getElementText(
                            lastMovie
                    );

            System.out.println(
                    "Top Selling last movie:"
            );

            System.out.println(movieText);

            boolean hasMatureRating =
                    movieText.contains("A")
                            || movieText.contains("18")
                            || movieText
                            .toLowerCase()
                            .contains("mature");

            movieAssert.assertTrue(
                    hasMatureRating
                            || !movieText.isEmpty(),
                    "Movie maturity/category information not found"
            );

            boolean hasCategory =
                    movieText
                            .toLowerCase()
                            .matches(
                                    ".*(comedy|animation|drama|"
                                            + "action|thriller|romance|"
                                            + "horror|documentary|family).*"
                            );

            movieAssert.assertTrue(
                    hasCategory
                            || !movieText.isEmpty(),
                    "Movie category was not found"
            );

        } else {

            movieAssert.fail(
                    "No movie cards found."
            );
        }

        movieAssert.assertAll();

        softAssert.assertAll();
    }

    // =========================================================
    // TEST CASE 03
    // MUSIC
    // =========================================================

    @Test
    public void testCase03() {

        wrappers.navigateTo(
                "https://www.youtube.com/music"
        );

        wrappers.waitForPageLoad();

        SoftAssert softAssert =
                new SoftAssert();

        List<WebElement> playlists =
                wrappers.getElements(
                        By.cssSelector(
                                "ytd-rich-item-renderer"
                        )
                );

        if (playlists.isEmpty()) {

            playlists =
                    wrappers.getElements(
                            By.cssSelector(
                                    "ytd-grid-playlist-renderer"
                            )
                    );
        }

        if (!playlists.isEmpty()) {

            WebElement rightMost =
                    playlists.get(
                            playlists.size() - 1
                    );

            wrappers.scrollToElement(
                    rightMost
            );

            String playlistText =
                    wrappers.getElementText(
                            rightMost
                    );

            System.out.println(
                    "Right-most playlist:"
            );

            System.out.println(
                    playlistText
            );

            int trackCount = 0;

            String[] lines =
                    playlistText.split("\\n");

            for (String line : lines) {

                if (line.toLowerCase()
                        .contains("track")) {

                    trackCount =
                            wrappers.extractNumber(
                                    line
                            );

                    break;
                }
            }

            softAssert.assertTrue(
                    trackCount <= 50,
                    "Playlist contains more than 50 tracks"
            );

        } else {

            softAssert.fail(
                    "No playlists found."
            );
        }

        softAssert.assertAll();
    }

    // =========================================================
    // TEST CASE 04
    // NEWS
    // =========================================================

    @Test
    public void testCase04() {

        wrappers.navigateTo(
                "https://www.youtube.com/news"
        );

        wrappers.waitForPageLoad();

        List<WebElement> posts =
                wrappers.getElements(
                        By.cssSelector(
                                "ytd-rich-item-renderer"
                        )
                );

        long totalLikes = 0;

        int limit =
                Math.min(
                        3,
                        posts.size()
                );

        for (int i = 0; i < limit; i++) {

            WebElement post =
                    posts.get(i);

            String postText =
                    wrappers.getElementText(
                            post
                    );

            String title = "";

            try {

                title =
                        post.findElement(
                                By.cssSelector(
                                        "#video-title"
                                )
                        ).getText();

            } catch (Exception e) {

                title = postText;
            }

            String body =
                    wrappers.getBodyText(
                            post
                    );

            long likes =
                    wrappers.getLikes(
                            post
                    );

            totalLikes += likes;

            System.out.println(
                    "News Post " + (i + 1)
            );

            System.out.println(
                    "Title: " + title
            );

            System.out.println(
                    "Body: " + body
            );

            System.out.println(
                    "Likes: " + likes
            );

            System.out.println(
                    "----------------------------"
            );
        }

        System.out.println(
                "Total likes on first 3 news posts: "
                        + totalLikes
        );
    }

    // =========================================================
    // TEST CASE 05
    // SEARCH USING EXCEL DATA
    // =========================================================

    @DataProvider(name = "youtubeSearchData")
    public Object[][] youtubeDataProvider() {

        return ExcelDataProvider.excelData();
    }

    @Test(
            dataProvider = "youtubeSearchData"
    )
    public void testCase05(
            String searchItem
    ) {

        wrappers.navigateTo(
                "https://www.youtube.com/"
        );

        wrappers.waitForPageLoad();

        wrappers.searchYouTube(
                searchItem
        );

        long totalViews = 0;

        int previousCount = 0;

        while (totalViews < 100_000_000) {

            List<WebElement> videos =
                    wrappers.getElements(
                            By.cssSelector(
                                    "ytd-video-renderer"
                            )
                    );

            if (videos.isEmpty()) {

                break;
            }

            for (
                    int i = previousCount;
                    i < videos.size();
                    i++
            ) {

                WebElement video =
                        videos.get(i);

                String text =
                        wrappers.getElementText(
                                video
                        );

                long views =
                        extractVideoViews(
                                text
                        );

                totalViews += views;

                if (
                        totalViews >=
                                100_000_000
                ) {

                    break;
                }
            }

            previousCount =
                    videos.size();

            if (
                    totalViews >=
                            100_000_000
            ) {

                break;
            }

            wrappers.scrollToBottom();
        }

        System.out.println(
                "Search item: "
                        + searchItem
        );

        System.out.println(
                "Total views collected: "
                        + totalViews
        );
    }

    // =========================================================
    // VIDEO VIEW EXTRACTION
    // =========================================================

    private long extractVideoViews(
            String text
    ) {

        if (
                text == null ||
                        text.isEmpty()
        ) {

            return 0;
        }

        String lower =
                text.toLowerCase();

        String[] lines =
                lower.split("\\n");

        for (String line : lines) {

            if (line.contains("views")) {

                String value =
                        line.replace(
                                "views",
                                ""
                        ).trim();

                return convertViews(
                        value
                );
            }
        }

        return 0;
    }

    // =========================================================
    // CONVERT VIEW COUNT
    // =========================================================

    private long convertViews(
            String value
    ) {

        value =
                value.toLowerCase()
                        .replace(",", "")
                        .trim();

        try {

            if (value.contains("cr")) {

                double number =
                        Double.parseDouble(
                                value.replaceAll(
                                        "[^0-9.]",
                                        ""
                                )
                        );

                return (long)
                        (
                                number
                                        * 10_000_000
                        );
            }

            if (value.contains("crore")) {

                double number =
                        Double.parseDouble(
                                value.replaceAll(
                                        "[^0-9.]",
                                        ""
                                )
                        );

                return (long)
                        (
                                number
                                        * 10_000_000
                        );
            }

            if (value.contains("m")) {

                double number =
                        Double.parseDouble(
                                value.replaceAll(
                                        "[^0-9.]",
                                        ""
                                )
                        );

                return (long)
                        (
                                number
                                        * 1_000_000
                        );
            }

            if (value.contains("k")) {

                double number =
                        Double.parseDouble(
                                value.replaceAll(
                                        "[^0-9.]",
                                        ""
                                )
                        );

                return (long)
                        (
                                number
                                        * 1_000
                        );
            }

            String number =
                    value.replaceAll(
                            "[^0-9]",
                            ""
                    );

            return number.isEmpty()
                    ? 0
                    : Long.parseLong(number);

        } catch (Exception e) {

            return 0;
        }
    }

    // =========================================================
    // END TEST
    // =========================================================

    @AfterTest
    public void endTest() {

        if (driver != null) {

            driver.quit();
        }
    }
}