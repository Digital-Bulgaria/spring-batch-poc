package com.example.demo.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobLauncherController {

  @Autowired
  JobLauncher jobLauncher;

  @Autowired
  @Qualifier("importPredictionsJob")
  Job job;

  @GetMapping("/jobLauncher")
  public long handle() throws Exception{

    JobExecution jobExecution = jobLauncher.run(job, new JobParameters());

    return jobExecution.getId();
  }

}
