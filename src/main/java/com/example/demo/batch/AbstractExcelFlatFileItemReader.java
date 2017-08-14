package com.example.demo.batch;

import java.io.IOException;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public abstract class AbstractExcelFlatFileItemReader<T> extends AbstractItemCountingItemStreamItemReader<T>
    implements ResourceAwareItemReaderItemStream<T> {

  private class CellToStringMapper implements Function<Cell, String> {

    @Override
    public String apply(Cell cell) {

      cell.setCellType(CellType.STRING);
      return cell.getStringCellValue();
    }
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractExcelFlatFileItemReader.class);

  private Resource resource;

  private LineMapper<T> lineMapper;

  private boolean strict = true;

  private int skipRows;

  private boolean noInput;

  private int lineCount = 0;

  protected Workbook workbook;

  private Iterator<Sheet> sheetIterator;

  private Iterator<Row> rowIterator;

  private Sheet currentSheet;

  private Row currentRow;

  public AbstractExcelFlatFileItemReader() {

    setName(ClassUtils.getShortName(AbstractExcelFlatFileItemReader.class));
  }

  public void setLineMapper(LineMapper<T> lineMapper) {

    this.lineMapper = lineMapper;
  }

  public void setStrict(boolean strict) {

    this.strict = strict;
  }

  public void setSkipRows(int skipRows) {

    this.skipRows = skipRows;
  }

  protected abstract void createWorkbook(Resource resource) throws IOException;

  @Override
  public void setResource(Resource resource) {

    this.resource = resource;
  }

  @Override
  protected T doRead() throws Exception {

    if (noInput || !ensureInput()) {
      return null;
    }
    String line = readLine();
    if (line == null) {
      return null;
    }
    else {
      try {
        return lineMapper.mapLine(line, lineCount);
      }
      catch (Exception ex) {
        if (strict) {
          throw new FlatFileParseException("Parsing error at excel line: " + lineCount + " in resource=["
                                               + resource.getDescription() + "], input=[" + line + "]", ex, line, lineCount);
        } else {
          LOGGER.warn("Could not parse line: {}", line);
          return doRead();
        }
      }
    }
  }

  @Override
  protected void doOpen() throws Exception {

    Assert.notNull(resource, "Input resource must be set");
    noInput = true;
    if (!resource.exists()) {
      if (strict) {
        throw new IllegalStateException("Input resource must exist (reader is in 'strict' mode): " + resource);
      }
      LOGGER.warn("Input resource does not exist " + resource.getDescription());
      return;
    }

    if (!resource.isReadable()) {
      if (strict) {
        throw new IllegalStateException("Input resource must be readable (reader is in 'strict' mode): "
                                            + resource);
      }
      LOGGER.warn("Input resource is not readable " + resource.getDescription());
      return;
    }

    createWorkbook(resource);
    sheetIterator = workbook.sheetIterator();
    if (nextSheet()) {
      nextRow();
      for (int i = 0; i < skipRows; i++) {
        nextRow();
      }
    }
    noInput = false;
  }

  @Override
  protected void doClose() throws Exception {

    lineCount = 0;
    currentRow = null;
    currentSheet = null;
    rowIterator = null;
    sheetIterator = null;
    if (workbook != null) {
      workbook.close();
    }
  }

  private boolean nextSheet() {
    if (sheetIterator.hasNext()) {
      currentSheet = sheetIterator.next();
      rowIterator = currentSheet.rowIterator();
      return true;
    }

    return false;
  }

  private boolean nextRow() {

    if (rowIterator.hasNext()) {
      currentRow = rowIterator.next();
      return true;
    }
    return false;
  }

  private boolean ensureInput() {

    if (currentRow != null) {
      return true;
    }
    if (nextRow()) {
      return true;
    }
    if (nextSheet()) {
      return ensureInput();
    }
    return false;
  }

  private String readLine() {

    lineCount++;
    Iterable<Cell> iterable = () -> currentRow.cellIterator();
    String line = StreamSupport.stream(iterable.spliterator(), false)
                        .map(new CellToStringMapper()).filter(s -> !s.isEmpty()).collect(Collectors.joining(","));
    currentRow = null;
    return line;
  }

}
