package poc.joydeep.worldometerentityscraper.models;

public class Counter {
    private String counterItem;
    private String counterNumber;

    public Counter(String counterItem, String counterNumber) {
        this.counterItem = counterItem;
        this.counterNumber = counterNumber;
    }

    public String getCounterItem() {
        return counterItem;
    }

    public void setCounterItem(String counterItem) {
        this.counterItem = counterItem;
    }

    public String getCounterNumber() {
        return counterNumber;
    }

    public void setCounterNumber(String counterNumber) {
        this.counterNumber = counterNumber;
    }

    @Override
    public String toString() {
        return "Counter{" + "counterItem='" + counterItem + '\'' + ", counterNumber='" + counterNumber + '\'' + '}';
    }
}
