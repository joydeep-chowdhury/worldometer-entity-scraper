package poc.joydeep.worldometerentityscraper.models;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.concurrent.atomic.AtomicLong;

public class GaugeModel {

    private final AtomicLong metric = new AtomicLong(0L);
    private final Gauge gauge;

    public GaugeModel(String gaugeId, String metricsName, String metricDescription, String categoryName, String appName, MeterRegistry meterRegistry){
        gauge = Gauge.builder(metricsName, metric::get)
                .description(metricDescription)
                .tag("description", metricDescription)
                .tag("category", categoryName)
                .tag("app_name", appName)
                .tag("gaugeId", gaugeId)
                .register(meterRegistry);
        gauge.measure();
    }

    public void update(Long value){
        metric.set(value);
        gauge.measure();
    }
}
