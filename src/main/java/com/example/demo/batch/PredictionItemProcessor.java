package com.example.demo.batch;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("predictionItemProcessor")
public class PredictionItemProcessor implements ItemProcessor<PredictionItem, PredictionEntity> {

  private static final Logger LOGGER = LoggerFactory.getLogger(PredictionItemProcessor.class);


  @Override
  public PredictionEntity process(PredictionItem item) throws Exception {

    LOGGER.info("Processing prediction item for nan {}", item.getNan());

    //TODO
    PredictionEntity entity = new PredictionEntity();

    entity.setNan(item.getNan());
    entity.setValidFromAsDate(new Date());
    entity.setValidToAsDate(new Date());

    return entity;
  }
}
