package com.example.demo.batch;

import java.io.IOException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.Resource;

public class XLSXFlatFileItemReader extends AbstractExcelFlatFileItemReader {

  @Override
  protected void createWorkbook(Resource resource) throws IOException {
    workbook = new XSSFWorkbook(resource.getInputStream());
  }
}
