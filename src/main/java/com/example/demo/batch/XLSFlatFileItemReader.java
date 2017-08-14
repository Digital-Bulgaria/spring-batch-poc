package com.example.demo.batch;

import java.io.IOException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.core.io.Resource;

public class XLSFlatFileItemReader extends AbstractExcelFlatFileItemReader {

  @Override
  protected void createWorkbook(Resource resource) throws IOException {
    workbook = new HSSFWorkbook(resource.getInputStream());
  }
}
