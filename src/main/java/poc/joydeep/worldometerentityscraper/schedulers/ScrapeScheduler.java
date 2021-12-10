package poc.joydeep.worldometerentityscraper.schedulers;

import com.google.common.util.concurrent.AtomicDouble;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import poc.joydeep.worldometerentityscraper.models.GaugeModel;
import poc.joydeep.worldometerentityscraper.repositories.CategoricalDataRepository;
import poc.joydeep.worldometerentityscraper.services.ScraperService;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ScrapeScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ScrapeScheduler.class);
    
    private final ScraperService scraperService;
    private final MeterRegistry meterRegistry;
    private final CategoricalDataRepository categoricalDataRepository;
    private final Map<String, GaugeModel> gaugeModelMap = new LinkedHashMap<>();

    public ScrapeScheduler(ScraperService scraperService, MeterRegistry meterRegistry, CategoricalDataRepository categoricalDataRepository) {
        this.scraperService = scraperService;
        this.meterRegistry = meterRegistry;
        this.categoricalDataRepository = categoricalDataRepository;
    }

    @Scheduled(cron="0 0/1 * * * *")
    public void scrapeCategoricalData()
    {
        scraperService.navigate();
        generateCustomMetrics();
    }

    private void generateCustomMetrics(){
        categoricalDataRepository.findAll().forEach(categoricalData -> {
            categoricalData.getCounters().forEach(counter -> {
                String gaugeId = categoricalData.getCategoryName().toLowerCase().trim().replaceAll(" ","")+counter.getCounterItem().toLowerCase().trim().replaceAll(" ","");
                logger.info("Checking gauges for gauge id {}",gaugeId);
                
                if(gaugeModelMap.containsKey(gaugeId)){
                    logger.info("Gauge with id {} found",gaugeId);
                    gaugeModelMap.get(gaugeId).update(Long.parseLong(counter.getCounterNumber().replaceAll("[^\\d]", "")));
                }
                else{
                    logger.info("Creating gauge with id {} ",gaugeId);
                    GaugeModel gaugeModel = new GaugeModel(gaugeId,"categorical_custom_metrics",counter.getCounterItem(),categoricalData.getCategoryName(),"worldometer-entity-scrapper",meterRegistry);
                    gaugeModel.update(Long.parseLong(counter.getCounterNumber().replaceAll("[^\\d]", "")));
                    gaugeModelMap.put(gaugeId,gaugeModel);
                }

            });
        });
    }

}
