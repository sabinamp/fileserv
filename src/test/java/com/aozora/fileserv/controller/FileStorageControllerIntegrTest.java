/**
 * 
 */
package com.aozora.fileserv.controller;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;


import java.net.URI;
import java.util.Set;

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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.aozora.fileserv.FileservApplication;
import com.aozora.fileserv.config.FileServiceProperties;
import com.aozora.fileserv.service.FileStorageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.test.web.client.match.MockRestRequestMatchers;

/**
 * @author SabinaM
 * test the controller using a MockMvc object without an embedded server 
 */
@SpringBootTest(classes = {
		FileservApplication.class, FileServiceProperties.class},
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations="classpath:application.properties")
@AutoConfigureMockMvc
public class FileStorageControllerIntegrTest {
	 
	 	private static String pattern ;	    
	 	private static String patternSyntax;
	 	private static String dataDirectoryPath;
		
		  private static final String LOCAL_HOST = "http://localhost:"; 
	 	  @LocalServerPort
	 	  private int randomServerPort;
		  private static RestTemplate template = new RestTemplate(); 
		  private static MockRestServiceServer mockServer;
		 
	 	
 
	 
	 @InjectMocks
	 private FileStorageService fileStorageService;
	 
	 @Autowired
	 private MockMvc mockMvc;
	 
	 @BeforeAll
	 public static void init() {
	        mockServer = MockRestServiceServer.createServer(template);
	        pattern ="^[a-zA-Z0-9._ -]+\\.(doc|pdf|csv|txt)$";
		    patternSyntax = "regex";
		    dataDirectoryPath = "C:/workspace_sts4-14/fileserv/bin/main/static/data/";		
	 }
	
	 
	 @DisplayName("Integration test - GetAllFilesThatMatchRegex")
	 @Test
	 void givenPatternWhenGetAllFilesThatMatchRegex_thenOK() throws Exception {
		    final String baseUrl = LOCAL_HOST + randomServerPort + "/files/matchedfiles?syntax=regex";
	        URI uri = new URI(baseUrl);
	        
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode patternAndDirectoryJsonNode = mapper.createObjectNode();
			patternAndDirectoryJsonNode.put("pattern", pattern);
			patternAndDirectoryJsonNode.put("directory", dataDirectoryPath);
			
			 mockServer.expect(ExpectedCount.once(), requestTo(uri))
			  .andExpect(method(HttpMethod.POST))
			  .andExpect(queryParam("syntax", patternSyntax))
			 // .andExpect(content().json(mapper.writeValueAsString(patternAndDirectoryJsonNode)))			  
			  .andRespond(withStatus(HttpStatus.OK) ) ;
			 
			 HttpHeaders headers = new HttpHeaders();
			  headers.setContentType(MediaType.APPLICATION_JSON);
			  Set<String> response = template.postForObject(uri, patternAndDirectoryJsonNode, Set.class);
			  //assertTrue(response.contains("words.txt")); 
			  mockServer.verify();
			 			
	 }
	 
	 @DisplayName("Integration test - given null Pattern and null directory GetAllFilesThatMatchRegex - Request body: {}")
	 @Test
	 void givenNullPatternNullDirectoryWhenGetAllFilesThatMatchRegex_thenBadRequest() throws JsonProcessingException, Exception {
		 MultiValueMap<String, String> reqParams = new LinkedMultiValueMap<>();
			reqParams.add("syntax", "regex");
			
			
			
			
		
	 }
}
