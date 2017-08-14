package com.example.demo.batch;

import java.util.List;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("predictionItemWriter")
public class PredictionItemWriter implements ItemWriter<PredictionEntity> {

  @Autowired
  private PredictionRepository predictionRepo;

  @Override
  public void write(List<? extends PredictionEntity> items) throws Exception {
    predictionRepo.saveAll(items);
  }
}
