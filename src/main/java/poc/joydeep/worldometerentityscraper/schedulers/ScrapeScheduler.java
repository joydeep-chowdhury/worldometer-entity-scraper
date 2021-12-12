package poc.joydeep.worldometerentityscraper.schedulers;

import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import poc.joydeep.worldometerentityscraper.repositories.CategoricalDataRepository;
import poc.joydeep.worldometerentityscraper.services.CustomMetricsGenerator;
import poc.joydeep.worldometerentityscraper.services.ReportWriterService;
import poc.joydeep.worldometerentityscraper.services.ScraperService;

@Service
public class ScrapeScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ScrapeScheduler.class);

    private final ScraperService scraperService;
    private final CustomMetricsGenerator customMetricsGenerator;
    private final ReportWriterService reportWriterService;

    public ScrapeScheduler(ScraperService scraperService, MeterRegistry meterRegistry, CategoricalDataRepository categoricalDataRepository,
                           CustomMetricsGenerator customMetricsGenerator, ReportWriterService reportWriterService) {
        this.scraperService = scraperService;
        this.customMetricsGenerator = customMetricsGenerator;
        this.reportWriterService = reportWriterService;
    }

    @Scheduled(cron = "0 0/30 * * * *")
    public void scrapeCategoricalData() {
        scraperService.scrape();
        customMetricsGenerator.generate();

    }

    @Scheduled(cron = "0 0/60 * * * *")
    public void generateCategoricalReport(){
        reportWriterService.write();
    }

}
