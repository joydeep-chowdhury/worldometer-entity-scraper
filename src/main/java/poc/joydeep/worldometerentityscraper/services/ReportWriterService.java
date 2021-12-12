package poc.joydeep.worldometerentityscraper.services;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface ReportWriterService {

    public void write();
    public ResponseEntity<Resource> download(HttpServletRequest request);

}
