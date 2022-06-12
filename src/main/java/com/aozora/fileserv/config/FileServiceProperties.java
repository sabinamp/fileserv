package com.aozora.fileserv.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@ConfigurationProperties(prefix="fileservice.static")
@Configuration
@Primary
public class FileServiceProperties {
	private String DIRECTORY_PATH;
	private String uploadDir;

	

	public String getDIRECTORY_PATH() {
		return DIRECTORY_PATH;
	}

	public void setDIRECTORY_PATH(String dIRECTORY_PATH) {
		DIRECTORY_PATH = dIRECTORY_PATH;
	}
	
	public String getUploadDir() {
	        return uploadDir;
	}

	public void setUploadDir(String uploadDir) {
	        this.uploadDir = uploadDir;
	    }

}
