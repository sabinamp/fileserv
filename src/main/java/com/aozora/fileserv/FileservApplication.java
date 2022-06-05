package com.aozora.fileserv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.aozora.fileserv.config.FileServiceProperties;

@SpringBootApplication
@EnableConfigurationProperties({ FileServiceProperties.class }) 
public class FileservApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileservApplication.class, args);
	}

}
