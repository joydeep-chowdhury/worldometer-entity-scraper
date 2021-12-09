package poc.joydeep.worldometerentityscraper.services;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;
import poc.joydeep.worldometerentityscraper.configurations.ReportConfiguration;
import poc.joydeep.worldometerentityscraper.models.CategoricalData;
import poc.joydeep.worldometerentityscraper.models.Counter;

import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExcelReportWriterService implements ReportWriterService<CategoricalData> {

    private final ReportConfiguration reportConfiguration;

    public ExcelReportWriterService(ReportConfiguration reportConfiguration) {
        this.reportConfiguration = reportConfiguration;
    }


    @Override
    public void write(List<CategoricalData> data) {
        try {
            String fileName = CategoricalData.class.getSimpleName() + LocalDateTime.now() + ".xlsx";
            String filePath = reportConfiguration.getFilePath()+fileName;
            HSSFWorkbook workbook = new HSSFWorkbook();
            data.forEach(categoricalData -> {
                HSSFSheet sheet = workbook.createSheet(categoricalData.getCategoryName());
                HSSFRow rowhead = sheet.createRow((short) 0);
                rowhead.createCell(0).setCellValue("Counter Item");
                rowhead.createCell(1).setCellValue("Counter Number");

                List<Counter> counters = categoricalData.getCounters();
                for(int i=0;i<counters.size();i++){
                    HSSFRow row = sheet.createRow((short) i+1);
                    row.createCell(0).setCellValue(counters.get(i).getCounterItem());
                    row.createCell(1).setCellValue(counters.get(i).getCounterNumber());
                }
            });

            FileOutputStream fileOut = new FileOutputStream(filePath);
            workbook.write(fileOut);
//closing the Stream
            fileOut.close();
//closing the workbook
            workbook.close();
//prints the message on the console
            System.out.println("Excel file has been generated successfully.");
        }
        catch (Exception exception){
            exception.printStackTrace();
        }
    }
}
