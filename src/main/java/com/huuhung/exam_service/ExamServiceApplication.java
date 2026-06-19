package com.huuhung.exam_service;

import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableAsync
public class ExamServiceApplication {

	public static void main(String[] args) {
            
                TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		SpringApplication.run(ExamServiceApplication.class, args);
	}

}
