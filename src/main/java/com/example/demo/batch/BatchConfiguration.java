package com.example.demo.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

  @Autowired
  public JobBuilderFactory jobBuilderFactory;

  @Autowired
  public StepBuilderFactory stepBuilderFactory;

  @Autowired
  @Qualifier("predictionItemWriter")
  private ItemWriter<PredictionEntity> itemWriter;

  @Autowired
  @Qualifier("predictionItemProcessor")
  private ItemProcessor<PredictionItem, PredictionEntity> itemProcessor;

  @Autowired
  private JobRepository jobRepository;

  @Bean
  public ItemReader<PredictionItem> reader() {

    FlatFileItemReader<PredictionItem> reader = new FlatFileItemReader<PredictionItem>();
    reader.setResource(new ClassPathResource("predictions.csv"));
    reader.setLineMapper(new DefaultLineMapper<PredictionItem>() {{

      setLineTokenizer(new DelimitedLineTokenizer() {{
        setNames(new String[] { "calendarWeek", "year", "nan"});
      }});

      setFieldSetMapper(new BeanWrapperFieldSetMapper<PredictionItem>() {{
        setTargetType(PredictionItem.class);
      }});
    }});
    return reader;

  }

  @Bean
  @Qualifier("importPredictionsJob")
  public Job importPredictionsJob() {
    return jobBuilderFactory.get("importPredictionsJob")
                            .incrementer(new RunIdIncrementer())
                            .flow(step1())
                            .end()
                            .build();
  }

  private Step step1() {
    return stepBuilderFactory.get("step1")
        .<PredictionItem, PredictionEntity> chunk(10)
        .reader(reader())
        .processor(itemProcessor)
        .writer(itemWriter)
        .build();
  }
  // end::jobstep[]

  @Bean
  public JobLauncher jobLauncher() throws Exception
  {
    //@see
    //http://docs.spring.io/spring-batch/4.0.x/reference/html/job.html#configuringJobLauncher

    SimpleJobLauncher jobLauncher = new SimpleJobLauncher();

    jobLauncher.setJobRepository(jobRepository);
    jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
    jobLauncher.afterPropertiesSet();

    return jobLauncher;
  }
}