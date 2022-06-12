package com.aozora.fileserv.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;

@EnableConfigurationProperties(value = FileServiceProperties.class)
@TestPropertySource("classpath:application.properties")
public class FileServicePropertiesTest {

	@Autowired
	FileServiceProperties serviceProps;

}
