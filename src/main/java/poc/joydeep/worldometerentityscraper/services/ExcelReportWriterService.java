package poc.joydeep.worldometerentityscraper.services;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import poc.joydeep.worldometerentityscraper.configurations.ReportConfiguration;
import poc.joydeep.worldometerentityscraper.models.CategoricalData;
import poc.joydeep.worldometerentityscraper.models.Counter;
import poc.joydeep.worldometerentityscraper.repositories.CategoricalDataRepository;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
public class ExcelReportWriterService implements ReportWriterService {
    private static final Logger logger = LoggerFactory.getLogger(ExcelReportWriterService.class);

    private final ReportConfiguration reportConfiguration;
    private final CategoricalDataRepository categoricalDataRepository;

    public ExcelReportWriterService(ReportConfiguration reportConfiguration, CategoricalDataRepository categoricalDataRepository) {
        this.reportConfiguration = reportConfiguration;
        this.categoricalDataRepository = categoricalDataRepository;
    }


    @Override
    public void write() {
        try {
            logger.info("Generating report ");
            String fileName = CategoricalData.class.getSimpleName() + LocalDateTime.now() + ".xlsx";
            String filePath = reportConfiguration.getFilePath()+fileName;
            HSSFWorkbook workbook = new HSSFWorkbook();

            Font headerCellFont = workbook.createFont();
            headerCellFont.setFontHeightInPoints((short)10);
            headerCellFont.setFontName("Arial");
            headerCellFont.setColor(IndexedColors.BLUE.getIndex());
            headerCellFont.setItalic(true);
            headerCellFont.setBold(true);

            Font contentCellFont = workbook.createFont();
            contentCellFont.setFontHeightInPoints((short)8);
            contentCellFont.setFontName("Arial");
            contentCellFont.setItalic(true);

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerCellFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.index);
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle contentCellStyle = workbook.createCellStyle();
            contentCellStyle.setFont(contentCellFont);
            contentCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
            contentCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            categoricalDataRepository.findAll().forEach(categoricalData -> {
                HSSFSheet sheet = workbook.createSheet(categoricalData.getCategoryName());
                sheet.setColumnWidth(0, 25 * 256);
                sheet.setColumnWidth(1, 25 * 256);
                HSSFRow rowhead = sheet.createRow((short) 0);
                Cell counterItemCell = rowhead.createCell(0);
                Cell counterNumberCell = rowhead.createCell(1);
                counterItemCell.setCellStyle(headerCellStyle);
                counterNumberCell.setCellStyle(headerCellStyle);
                counterItemCell.setCellValue("Counter Item");
                counterNumberCell.setCellValue("Counter Number");

                List<Counter> counters = categoricalData.getCounters();
                for(int i=0;i<counters.size();i++){
                    HSSFRow row = sheet.createRow((short) i+1);
                    Cell itemCell = row.createCell(0);
                    Cell valueCell = row.createCell(1);
                    itemCell.setCellStyle(contentCellStyle);
                    valueCell.setCellStyle(contentCellStyle);
                    itemCell.setCellValue(counters.get(i).getCounterItem());
                    valueCell.setCellValue(counters.get(i).getCounterNumber());
                }
            });

            FileOutputStream fileOut = new FileOutputStream(filePath);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();
            logger.info("Generated report {} successfully",fileName);
        }
        catch (Exception exception){
            logger.error("Issue in generating report ",exception);
        }
    }

    @Override
    public ResponseEntity<Resource> download(HttpServletRequest request) {
        File folder = new File(reportConfiguration.getFilePath());
        File[] listOfFiles = folder.listFiles();
        Arrays.sort(listOfFiles, Comparator.comparingLong(File::lastModified)
                                           .reversed());
        File latestFile = listOfFiles[0];
        Path path = Paths.get(latestFile.getAbsolutePath());
        try {
            Resource resource = new UrlResource(path.toUri());
            String contentType = request.getServletContext()
                                        .getMimeType(resource.getFile()
                                                             .getAbsolutePath());

            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                                 .contentType(MediaType.parseMediaType(contentType))
                                 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                                 .body(resource);
        } catch (MalformedURLException e) {
            logger.error("Download failed", e);
            throw new RuntimeException("Download failed" + e.getMessage());
        } catch (IOException ioException) {
            logger.error("Download failed", ioException.getMessage());
            throw new RuntimeException("Download failed" + ioException.getMessage());
        }
    }
}
