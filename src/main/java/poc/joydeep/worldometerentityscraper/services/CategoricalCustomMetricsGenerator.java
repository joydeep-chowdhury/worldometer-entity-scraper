package poc.joydeep.worldometerentityscraper.services;

import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import poc.joydeep.worldometerentityscraper.models.GaugeModel;
import poc.joydeep.worldometerentityscraper.repositories.CategoricalDataRepository;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class CategoricalCustomMetricsGenerator implements CustomMetricsGenerator{
    private static final Logger logger = LoggerFactory.getLogger(CategoricalCustomMetricsGenerator.class);

    private final MeterRegistry meterRegistry;
    private final CategoricalDataRepository categoricalDataRepository;
    private final Map<String, GaugeModel> gaugeModelMap = new LinkedHashMap<>();

    public CategoricalCustomMetricsGenerator(MeterRegistry meterRegistry, CategoricalDataRepository categoricalDataRepository) {
        this.meterRegistry = meterRegistry;
        this.categoricalDataRepository = categoricalDataRepository;
    }

    @Override
    public void generate() {
        logger.info("Generating metrics started");
        categoricalDataRepository.findAll().forEach(categoricalData -> {
            categoricalData.getCounters().forEach(counter -> {
                String gaugeId = categoricalData.getCategoryName().toLowerCase().trim().replaceAll(" ","")+counter.getCounterItem().toLowerCase().trim().replaceAll(" ","");

                if(gaugeModelMap.containsKey(gaugeId)){
                    gaugeModelMap.get(gaugeId).update(Long.parseLong(counter.getCounterNumber().replaceAll("[^\\d]", "")));
                    logger.info("Updating gauge. Category: {} Category Item: {} value: {}",categoricalData.getCategoryName(),counter.getCounterItem(),counter.getCounterNumber());
                }
                else{
                    GaugeModel gaugeModel = new GaugeModel(gaugeId,"categorical_custom_metrics",counter.getCounterItem(),categoricalData.getCategoryName(),"worldometer-entity-scrapper",meterRegistry);
                    gaugeModel.update(Long.parseLong(counter.getCounterNumber().replaceAll("[^\\d]", "")));
                    gaugeModelMap.put(gaugeId,gaugeModel);
                    logger.info("Creating gauge. Category: {} Category Item: {} value: {}",categoricalData.getCategoryName(),counter.getCounterItem(),counter.getCounterNumber());

                }

            });
        });
        logger.info("Generating metrics ended");
    }
}
