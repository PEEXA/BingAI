package me.axorom;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.v85.browser.Browser;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.interactions.Actions;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class BingSimulation {
    private static int messageNumber = 0;
    private static JavascriptExecutor js;
    public static WebDriver driver;


    public static void initialize(String browser) throws InterruptedException {
        if (!browser.isEmpty())
            System.setProperty("webdriver.edge.driver", browser);
        EdgeOptions options = new EdgeOptions();
        options.addArguments("userAgent=\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36 Edg/118.0.2088.61\"");
        options.addArguments("ignore-certificate-errors");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        WebDriver driver;
        if (!browser.isEmpty())
            driver = new EdgeDriver(options);
        else
            driver = new ChromeDriver();
        driver.get("https://www.bing.com/search?q=Bing+AI&showconv=1&FORM=undexpand");
        Thread.sleep(4000);
        driver.findElement(By.id("bnp_btn_accept")).click();
        Thread.sleep(100);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String script = "document.querySelector('cib-serp').shadowRoot.querySelector('#cib-conversation-main').shadowRoot.querySelector('cib-welcome-container').shadowRoot.querySelector('cib-tone-selector').shadowRoot.querySelectorAll('button')[2].click()";
        js.executeScript(script);
    }



    public static String sendText(String text) throws InterruptedException {
        return sendTexts(Collections.singletonList(text));
    }

    public static String sendTexts(List<String> texts) throws InterruptedException {
        String answer = "";
        for (int i = 0; i < texts.size(); i++) {
            focusInput();
            sendBingMessage(texts.get(i));
            waitForSend();
            String script = "return document.querySelector('cib-serp')?.shadowRoot.querySelector('#cib-conversation-main')?.shadowRoot.querySelectorAll('cib-chat-turn')[" + messageNumber + "]?.shadowRoot.querySelectorAll('cib-message-group')[1]?.shadowRoot.querySelectorAll('cib-message')[document.querySelector('cib-serp')?.shadowRoot.querySelector('#cib-conversation-main')?.shadowRoot.querySelectorAll('cib-chat-turn')[document.querySelector('cib-serp')?.shadowRoot.querySelector('#cib-conversation-main')?.shadowRoot.querySelectorAll('cib-chat-turn').length-1]?.shadowRoot.querySelectorAll('cib-message-group')[1]?.shadowRoot.querySelectorAll('cib-message').length-1]?.shadowRoot.querySelector('cib-shared').querySelector('.content').querySelector('.ac-textBlock').innerText";
            if (i == texts.size() - 1) {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                String result = null;
                String resultprev = null;
                while (result == null || !Objects.equals(resultprev, result)) {
                    resultprev = result;
                    try {
                        result = (String) js.executeScript(script);
                    } catch (Exception e) {
                        System.out.println("exception:    " + e.getMessage());
                    }
                    Thread.sleep(10000);
                }
                answer = result;
            }
            stopAnswer();
            messageNumber += 1;
        }
        String script = "document.querySelector('cib-serp').shadowRoot.querySelector('#cib-action-bar-main').shadowRoot.querySelector('.button-compose').click()";
        js.executeScript(script);
        messageNumber = 1;
        return answer;
    }

    private static void sendBingMessage(String message) throws InterruptedException {
        Thread.sleep(100);
        Actions actions = new Actions(driver);
        actions.sendKeys(message).perform();
        Thread.sleep(2343);
        actions.sendKeys(Keys.ENTER).perform();
    }

    private static void stopAnswer() throws InterruptedException {
        String scriptStopAnswer = "document.querySelector('cib-serp').shadowRoot.querySelector('#cib-action-bar-main').shadowRoot.querySelector('cib-typing-indicator').shadowRoot.querySelector('button').click()";
        String scriptWaiting = "return document.querySelector('cib-serp').shadowRoot.querySelector('#cib-action-bar-main').shadowRoot.querySelector('cib-typing-indicator').getAttribute('aria-hidden')";
        Thread.sleep(1337);
        js.executeScript(scriptStopAnswer);
        while (js.executeScript(scriptWaiting).equals("false")) {
            Thread.sleep(100);
        }
    }

    private static void waitForSend() throws InterruptedException {
        Thread.sleep(1003);
        int i = 0;
        String script = "return Array.from(document.querySelector('cib-serp').shadowRoot.querySelector('#cib-conversation-main').shadowRoot.querySelectorAll('cib-chat-turn')[" + messageNumber + "]?.shadowRoot.querySelectorAll('cib-message-group')[1]?.shadowRoot.querySelectorAll('cib-message')).some(d => d.getAttribute('type') == 'text')";
        Boolean result = false;
        while (!result) {
            try {
                result = (Boolean) js.executeScript(script);
            } catch (Exception ignored) {}
            Thread.sleep(50);
        }
        Thread.sleep(1020);
    }

    private static void focusInput() throws InterruptedException {
        Thread.sleep(100);
        String scriptWriting = "document.querySelector('cib-serp').shadowRoot.querySelector('#cib-action-bar-main').shadowRoot.querySelector('cib-text-input').shadowRoot.querySelector('.text-area').focus()";
        js.executeScript(scriptWriting);
    }
}
