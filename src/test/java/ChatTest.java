import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.bonigarcia.wdm.WebDriverManager;
import utils.AutoConfig;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ChatTest {
    public static AutoConfig config = new AutoConfig(
        "/Users/shrish/src/resources/chromedriver-mac-arm64",
        "https://elm-qa-chat.theburnsway.ca/",
        "leps2@theburnsway.ca",
        "***********",
        "Jim",
        "Hello I am an automated advocate.",
        "What a coincidence, I am also an automated guest...",
        "Addiction / Substance Use"
    );

    private static ArrayList<WebDriver> users;
    private static ChromeOptions chromeOptions;
    private static final Logger log = LoggerFactory.getLogger(ChatTest.class);

    private static final int GUEST = 0;
    private static final int ADVOCATE = 1;

    /**
     * Delay execution for X seconds
     * @param seconds time to delay execution for
     */
    public static void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException ignored) {}
    }

    private static WebElement clickElementByLocator(int user, By locator, int sleepSeconds) {
        int maxRetries = 3;
        WebDriver driver = users.get(user);

        for (int i=0; i<maxRetries; i++) {
            try {
                WebDriverWait userWait = new WebDriverWait(driver, Duration.ofSeconds(20));
                WebElement element = userWait.until(ExpectedConditions.elementToBeClickable(locator));
                element.click();
                sleep(sleepSeconds);
                return element;
            } catch (Exception e) {
                log.error("e: ", e);
                sleep(1);
            }
        }
        throw new RuntimeException("Unable to click element :(");
    }

    /**
     * Find element by id and click
     * - adjust sleep time after clicking
     * @param user web driver index
     * @param elementID HTML element id field
     * @param sleepSeconds seconds to sleep after clicking
     * @return element instance
     */
    public static WebElement clickElement(int user, String elementID, int sleepSeconds) {
        return clickElementByLocator(user, By.id(elementID), sleepSeconds);
    }

    /**
     * Find element by id and click
     * @param user web driver index
     * @param elementID HTML element id field
     * @return element instance
     */
    public static WebElement clickElement(int user, String elementID) {
        return clickElement(user, elementID, 1);
    }

    /**
     * Find element by text and click
     * @param user web driver index
     * @param query text to search for
     */
    public static void clickElementByText(int user, String query) {
        String xpathQuery = String.format("//*[text()='%s']", query);
        System.out.println("xpath for advocate locator "+xpathQuery);
        clickElementByLocator(user, By.xpath(xpathQuery), 1);
    }

    private static void setupChromeOptions() {
        System.setProperty("webdriver.chrome.drive", config.getChromeDriverPath());
        chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--remote-allow-origins=*");

        Map<String, Object> prefs = new HashMap<>();
        // 1 to allow, 2 to block
        prefs.put("profile.default_content_setting_values.notifications", 1);
        chromeOptions.setExperimentalOption("prefs", prefs);
    }

    private static void createDriver() {
    	WebDriverManager.chromedriver().setup();
    	
        WebDriver user = new ChromeDriver(chromeOptions);
        users.add(user);

        // Move windows to other monitor
        // user.manage().window().setPosition(new Point(-3000, 0));

        user.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        user.get(config.getUrl());
    }

    @BeforeEach
    public void setupUsers() {
        setupChromeOptions();

        users = new ArrayList<>();
        createDriver();  // create guest
        createDriver();  // create advocate

        sleep(5);
    }

    @AfterEach
    public void tearDownUsers() {
        for(WebDriver user : users) {
            user.quit();
        }
        users.clear();
    }

    private void advocateLogin() {
        clickElement(ADVOCATE, "agree");
        clickElement(ADVOCATE, "agree");

        clickElement(ADVOCATE, "settings");
        clickElement(ADVOCATE, "advocate-login");

        WebElement emailInput = users.get(ADVOCATE).findElement(By.id("email")).findElement(By.xpath(".//input[1]"));
        emailInput.sendKeys(config.getAdvocateEmail());

        WebElement passwordInput = users.get(ADVOCATE).findElement(By.id("password")).findElement(By.xpath(".//input[1]"));
        passwordInput.sendKeys(config.getAdvocatePassword());
        passwordInput.sendKeys(Keys.ENTER);

        sleep(4);

        clickElement(ADVOCATE, "toggle-online");
    }

    private void guestStartChat() {
        clickElement(GUEST,"agree");
        clickElement(GUEST,"agree");

        users.get(GUEST).navigate().refresh();

        clickElementByText(GUEST, config.getAdvocateName());
        clickElement(GUEST,"chat-now");
        clickElement(GUEST,"agree");
    }

    private void guestAndAdvocateChat() {
        WebElement chatBoxAdvocate = clickElement(ADVOCATE, "sendbox");
        chatBoxAdvocate.sendKeys(config.getAdvocateMessage());
        chatBoxAdvocate.sendKeys(Keys.ENTER);

        // FIXME: guest should probably wait for advocate's message instead :P
        WebElement chatBoxGuest = clickElement(GUEST, "sendbox");
        chatBoxGuest.sendKeys(config.getGuestMessage());
        chatBoxGuest.sendKeys(Keys.ENTER);

        sleep(3);

        chatBoxAdvocate.sendKeys(config.getAdvocateMessage());
        chatBoxAdvocate.sendKeys(Keys.ENTER);

        sleep(3);
        
             
    }
    
    

    private void guestEndChat() {
        clickElement(GUEST,"end-chat");
        clickElement(GUEST,"agree");

        sleep(3);

        clickElement(GUEST,"thumbs-up", 3);
        clickElement(GUEST,"skip");
    }
    
    
    private void advocatePostChatSurvey() {
        clickElement(ADVOCATE,"ok");

        // PCS Part 1
        clickElement(ADVOCATE,"chatDone");
        clickElement(ADVOCATE,"next");

        // PCS Part 2
        clickElement(ADVOCATE,"no");
        clickElement(ADVOCATE,"next");

        // PCS Part 3
        clickElement(ADVOCATE,"chat-topics");
        clickElementByText(ADVOCATE, config.getChatEndReason());
        clickElement(ADVOCATE,"done");
        clickElement(ADVOCATE,"training-checkbox", 0);
        clickElement(ADVOCATE,"submit");
    }

    @Test
    public void baseCaseChat() {
        advocateLogin();
        guestStartChat();
        guestAndAdvocateChat();
        guestEndChat();
        advocatePostChatSurvey();
    }
}
