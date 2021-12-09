package poc.joydeep.worldometerentityscraper.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import poc.joydeep.worldometerentityscraper.models.CategoricalData;

public interface CategoricalDataRepository extends PagingAndSortingRepository<CategoricalData, String> {
}
