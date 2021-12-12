package poc.joydeep.worldometerentityscraper.controllers;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import poc.joydeep.worldometerentityscraper.services.ReportWriterService;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportWriterService reportWriterService;

    public ReportController(ReportWriterService reportWriterService) {
        this.reportWriterService = reportWriterService;
    }

    @GetMapping("/latest")
    public ResponseEntity<Resource> getLatestReport(HttpServletRequest request){
       return reportWriterService.download(request);
    }

}
