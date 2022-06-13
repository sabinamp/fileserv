/**
 * 
 */
package com.aozora.fileserv.controller;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.aozora.fileserv.FileservApplication;
import com.aozora.fileserv.config.FileServiceProperties;
import com.aozora.fileserv.service.FileStorageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author SabinaM
 * test the controller using a MockMvc object without an embedded server 
 */
@SpringBootTest(classes = {
		FileservApplication.class, FileServiceProperties.class},
		webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(locations="classpath:application.properties")
@AutoConfigureMockMvc
public class FileStorageControllerIntegrationTest {

	
	 
	 private static String pattern ;	    
	 private static String patternSyntax;
	 private static String dataDirectoryPath;
	
		
		  @TestConfiguration static class FileStorageServiceTestContextConfiguration {
		  
		  @Bean public FileStorageService employeeService() { 
			  return new FileStorageService(); 
			 }
		  }
		 
	 
	 //To check the Service class, we need to have an instance of the Service class created
	 //and available as a @Bean so that we can @Autowire it in our test class
	 @Autowired
	 private FileStorageService fileStorageService;
	 
	 @Autowired
	 private MockMvc mockMvc;
	 
	 @BeforeAll
	 public static void init() {
	        
	        pattern ="^[a-zA-Z0-9._ -]+\\.(doc|pdf|csv|txt)$";
		    patternSyntax = "regex";
		    dataDirectoryPath = "C:/workspace_sts4-14/fileserv/bin/main/static/data/";		
	 }
	
	 
	 @DisplayName("Integration test - GetAllFilesThatMatchRegex")
	 @Test
	 void givenPatternWhenGetAllFilesThatMatchRegex_thenOK() throws Exception {
		    MultiValueMap<String, String> reqParams = new LinkedMultiValueMap<>();
			reqParams.add("syntax", "regex");
			
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode patternAndDirectoryJsonNode = mapper.createObjectNode();
			patternAndDirectoryJsonNode.put("pattern", "^[a-zA-Z0-9._ -]+\\.(doc|pdf|csv|txt)$");
			patternAndDirectoryJsonNode.put("directory", dataDirectoryPath);
			MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/files/matchedfiles")								
						.contentType(MediaType.APPLICATION_JSON)
						.param("syntax", "regex")
						.content(mapper.writeValueAsString(patternAndDirectoryJsonNode))	
					    .accept(MediaType.APPLICATION_JSON))
					    .andExpect(status().isOk())						    
					    .andExpect(jsonPath("$[*]", hasItem("words.txt")))					   
					    .andReturn();
				 					
			 assertNotNull(result.getResponse().getContentAsString());
			 			
	 }
	 
	 @DisplayName("Integration test - given null Pattern and null directory GetAllFilesThatMatchRegex - Request body: {}")
	 @Test
	 void givenNullPatternNullDirectoryWhenGetAllFilesThatMatchRegex_thenBadRequest() throws JsonProcessingException, Exception {
		 MultiValueMap<String, String> reqParams = new LinkedMultiValueMap<>();
			reqParams.add("syntax", "regex");
			
			
			MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/files/matchedfiles")								
						.contentType(MediaType.APPLICATION_JSON)
						.param("syntax", "regex")
						.content("{}"))						  
					    .andExpect(status().isBadRequest())					    
					 	.andReturn();
				 					
			 assertTrue(result.getResponse().getContentAsString().isEmpty());
			
		
	 }
}
