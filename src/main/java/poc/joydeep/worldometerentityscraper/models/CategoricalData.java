package poc.joydeep.worldometerentityscraper.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "worldometer_categorical_document")
public class CategoricalData {

    @Id
    private String _id;
    private String categoryName;
    private List<Counter> counters;

    public CategoricalData() {
    }

    public CategoricalData(String categoryName, List<Counter> counters) {
        this.categoryName = categoryName;
        this.counters = counters;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<Counter> getCounters() {
        return counters;
    }

    public void setCounters(List<Counter> counters) {
        this.counters = counters;
    }

    @Override
    public String toString() {
        return "CategoricalData{" + "categoryName='" + categoryName + '\'' + ", counters=" + counters + '}';
    }
}
