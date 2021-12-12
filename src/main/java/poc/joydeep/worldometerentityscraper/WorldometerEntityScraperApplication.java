package poc.joydeep.worldometerentityscraper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import poc.joydeep.worldometerentityscraper.models.CategoricalData;
import poc.joydeep.worldometerentityscraper.repositories.CategoricalDataRepository;
import poc.joydeep.worldometerentityscraper.services.ReportWriterService;
import poc.joydeep.worldometerentityscraper.services.ScraperService;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class WorldometerEntityScraperApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorldometerEntityScraperApplication.class, args);
    }

}
