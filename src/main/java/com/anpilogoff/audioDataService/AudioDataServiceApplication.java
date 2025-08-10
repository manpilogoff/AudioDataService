package com.anpilogoff.audioDataService;

import com.anpilogoff.audioDataService.config.SearchProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class AudioDataServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AudioDataServiceApplication.class, args);
	}

}
