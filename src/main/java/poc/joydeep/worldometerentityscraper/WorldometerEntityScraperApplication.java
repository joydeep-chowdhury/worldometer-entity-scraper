package poc.joydeep.worldometerentityscraper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import poc.joydeep.worldometerentityscraper.models.CategoricalData;
import poc.joydeep.worldometerentityscraper.repositories.CategoricalDataRepository;
import poc.joydeep.worldometerentityscraper.services.ReportWriterService;
import poc.joydeep.worldometerentityscraper.services.ScraperService;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class WorldometerEntityScraperApplication {
    @Autowired
    private ReportWriterService<CategoricalData> reportWriterService;

    @Autowired
    private CategoricalDataRepository categoricalDataRepository;

    public static void main(String[] args) {
        SpringApplication.run(WorldometerEntityScraperApplication.class, args);
    }

    @PostConstruct
    public void postInit(){
        System.out.println("Hello");
        List<CategoricalData> data = new ArrayList<>();
        categoricalDataRepository.findAll().forEach(data::add);
        reportWriterService.write(data);
    }

}