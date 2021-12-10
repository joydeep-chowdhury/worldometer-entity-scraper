package poc.joydeep.worldometerentityscraper.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import poc.joydeep.worldometerentityscraper.configurations.SeleniumConfiguration;
import poc.joydeep.worldometerentityscraper.configurations.WorldometerBusinessConfiguration;
import poc.joydeep.worldometerentityscraper.models.CategoricalData;
import poc.joydeep.worldometerentityscraper.models.Counter;
import poc.joydeep.worldometerentityscraper.repositories.CategoricalDataRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class WorldometerScrapperService implements ScraperService {
    private final WorldometerBusinessConfiguration worldometerBusinessConfiguration;
    private final SeleniumConfiguration seleniumConfiguration;
    private final CategoricalDataRepository categoricalDataRepository;
    private WebDriver webDriver;

    public WorldometerScrapperService(WorldometerBusinessConfiguration worldometerBusinessConfiguration, SeleniumConfiguration seleniumConfiguration,
            CategoricalDataRepository categoricalDataRepository) {
        this.worldometerBusinessConfiguration = worldometerBusinessConfiguration;
        this.seleniumConfiguration = seleniumConfiguration;
        this.categoricalDataRepository = categoricalDataRepository;

        System.setProperty(seleniumConfiguration.getWebDriverType(), seleniumConfiguration.getWebDriverPath());

    }

    @Override
    public void navigate() {
        initializeBrowser();
        webDriver.navigate()
                 .to(worldometerBusinessConfiguration.getRootUrl());
        WebDriverWait wait = new WebDriverWait(webDriver, 20);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText("Current World Population")));
        Document pageContents = Jsoup.parse(webDriver.getPageSource());
        Element counterDiv = pageContents.selectFirst("div[class=counterdiv]");
        Elements elements = counterDiv.select(
                "div[class=counter-title-top],div[class=counter-group],div[class=counter-header],div[class=counter-title],div[class=counter-header last-line],div[class=counter-group counter-group-double]");
        Map<String, List<Element>> contentsMap = new LinkedHashMap<>();
        List<Integer> titleElementsIndexList = new ArrayList<>();
        for (int elementIndex = 0; elementIndex < elements.size(); elementIndex++) {
            Element currentElement = elements.get(elementIndex);
            if (currentElement.hasAttr("class") && currentElement.attr("class")
                                                                 .startsWith("counter-title")) {
                titleElementsIndexList.add(elementIndex);
            }
        }
        System.out.println(titleElementsIndexList);
        for (int titleElementIndex = 0; titleElementIndex < titleElementsIndexList.size(); titleElementIndex++) {
            if (titleElementIndex != (titleElementsIndexList.size() - 1)) {
                int thisElementIndex = titleElementsIndexList.get(titleElementIndex);
                int nextElementIndex = titleElementsIndexList.get(titleElementIndex + 1);
                List<Element> contentsList = new ArrayList<>();
                for (int i = thisElementIndex + 1; i < nextElementIndex; i++) {
                    contentsList.add(elements.get(i));
                }
                System.out.println(elements.get(thisElementIndex)
                                           .text()
                        + " --- " + contentsList.size());
                contentsMap.put(elements.get(thisElementIndex)
                                        .text(),
                        contentsList);
            } else {
                int thisElementIndex = titleElementsIndexList.get(titleElementIndex);
                List<Element> contentsList = new ArrayList<>();
                for (int i = thisElementIndex + 1; i < elements.size(); i++) {
                    contentsList.add(elements.get(i));
                }
                System.out.println(elements.get(thisElementIndex)
                                           .text()
                        + " --- " + contentsList.size());
                contentsMap.put(elements.get(thisElementIndex)
                                        .text(),
                        contentsList);
            }
        }

        for (Map.Entry<String, List<Element>> entrySet : contentsMap.entrySet()) {
            CategoricalData categoricalData = new CategoricalData();
            categoricalData.setCategoryName(entrySet.getKey());
            categoricalData.set_id(entrySet.getKey());
            List<Counter> counters = new ArrayList<>();
            entrySet.getValue()
                    .stream()
                    .forEach(elem -> {
                        String itemText = "";
                        if (elem.select("span[class=counter-item]")
                                .first() != null) {
                            itemText = elem.selectFirst("span[class=counter-item]")
                                           .text();
                        } else if (elem.select("span[class=counter-item-double]")
                                       .first() != null) {
                            itemText = elem.selectFirst("span[class=counter-item-double]")
                                           .text();
                        }
                        Counter counter = new Counter(itemText, elem.selectFirst("span[class=counter-number]")
                                                                    .text());
                        counters.add(counter);

                    });
            categoricalData.setCounters(counters);
            System.out.println(categoricalData);
            categoricalDataRepository.save(categoricalData);
            webDriver.quit();
        }

    }

    private void initializeBrowser() {
        webDriver = new ChromeDriver();
        webDriver.manage()
                 .deleteAllCookies();
        webDriver.manage()
                 .window()
                 .maximize();
        webDriver.manage()
                 .timeouts()
                 .pageLoadTimeout(seleniumConfiguration.getWebDriverPageLoadTimeout(), TimeUnit.SECONDS);
    }
}
