package poc.joydeep.worldometerentityscraper.services;

import java.util.List;

public interface ReportWriterService<M> {

    public void write(List<M> data);

}
