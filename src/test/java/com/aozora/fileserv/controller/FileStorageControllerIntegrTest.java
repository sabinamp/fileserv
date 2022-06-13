/**
 * 
 */
package com.aozora.fileserv.controller;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;


import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.aozora.fileserv.FileservApplication;
import com.aozora.fileserv.config.FileServiceProperties;
import com.aozora.fileserv.service.FileStorageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author SabinaM
 * test the controller using an embedded server 
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
	 	private static String expectedFilenames;
		
		  private static final String LOCAL_HOST = "http://localhost:"; 
		  
	 	  @LocalServerPort
	 	  private int randomServerPort;
		  private static RestTemplate template = new RestTemplate(); 
		  private static MockRestServiceServer mockServer;
		 
	 @InjectMocks
	 private FileStorageService fileStorageService;
	 
	 @Autowired
	 private MockMvc mockMvc;
	 
	 @BeforeEach
	 public void init() {
	        mockServer = MockRestServiceServer.createServer(template);
	        pattern ="^[a-zA-Z0-9._ -]+\\.(doc|pdf|csv|txt)$";
		    patternSyntax = "regex";
		    dataDirectoryPath = "C:/workspace_sts4-14/fileserv/bin/main/static/data/";		
		    expectedFilenames ="[\r\n"
			 		+ "    \"StoreAddressList.pdf\",\r\n"
			 		+ "    \"words.txt\",\r\n"
			 		+ "    \"8-Reasons-AStartupOverACorporateJob.pdf\",\r\n"
			 		+ "    \"AppPropsExamples.txt\",\r\n"
			 		+ "    \"PathMatcher.txt\",\r\n"
			 		+ "    \"50-contacts.csv\"\r\n"
			 		+ "]";
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
			  .andExpect(content().json(mapper.writeValueAsString(patternAndDirectoryJsonNode)))		 			  
			  .andRespond(withStatus(HttpStatus.OK)
					  .contentType(MediaType.APPLICATION_JSON)    	 
	          .body(expectedFilenames));
			 
			 
			  HttpHeaders headers = new HttpHeaders();
			  headers.setContentType(MediaType.APPLICATION_JSON);
			  HttpEntity<String> request = new HttpEntity<String>(patternAndDirectoryJsonNode.toPrettyString(), headers);
			 
			  ResponseEntity<String[]> response = template.postForEntity(uri, request, String[].class);
			  assertNotNull(response);
			  assertTrue(response.getStatusCode().is2xxSuccessful());
			  assertTrue(response.hasBody());
			  mockServer.verify();
			 			
	 }
	 
	 @DisplayName("Integration test -Given Correct Pattern and Directory GetAllFilesThatMatchRegex -patternSyntax-unknown ")
	 @Test
	 void givenPatternDirectoryWhenGetAllFilesThatMatchRegex_WrongRegexSyntax_thenBadRequest() throws JsonProcessingException, Exception {
		
		  final String baseUrl = LOCAL_HOST + randomServerPort + "/files/matchedfiles?syntax=unknown";
	      URI uri = new URI(baseUrl);
	      
	        ObjectMapper mapper = new ObjectMapper();
			ObjectNode patternAndDirectoryJsonNode = mapper.createObjectNode();
			patternAndDirectoryJsonNode.put("pattern", pattern);
			patternAndDirectoryJsonNode.put("directory", dataDirectoryPath);
			
	        mockServer.expect(ExpectedCount.once(), requestTo(uri))
			  .andExpect(method(HttpMethod.POST))			  
			  .andExpect(queryParam("syntax", "unknown"))	
			  .andExpect(content().json(mapper.writeValueAsString(patternAndDirectoryJsonNode)))
			  .andRespond(withStatus(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)    	 
	          );
			
	          HttpHeaders headers = new HttpHeaders();
			  headers.setContentType(MediaType.APPLICATION_JSON);
			  HttpEntity<String> request = new HttpEntity<String>(patternAndDirectoryJsonNode.toPrettyString(), headers);
			
			
			  Assertions.assertThrows(HttpClientErrorException.class,()->{
				  ResponseEntity<String[]> response  = template.postForEntity(uri, request, String[].class);
				});
								
			
			  mockServer.verify();			
		
	 }
	 
	 @DisplayName("Integration test - givenPatternNullDirectoryWhenGetAllFilesThatMatchRegex_thenBadRequest ")
	 @Test
	 void givenPatternNullDirectoryWhenGetAllFilesThatMatchRegex_thenBadRequest() throws JsonProcessingException, Exception {
		
		  final String baseUrl = LOCAL_HOST + randomServerPort + "/files/matchedfiles?syntax=regex";
	      URI uri = new URI(baseUrl);
	      
	        ObjectMapper mapper = new ObjectMapper();
			ObjectNode patternAndDirectoryJsonNode = mapper.createObjectNode();
			patternAndDirectoryJsonNode.put("pattern", pattern);
			patternAndDirectoryJsonNode.put("directory", "");
			
	        mockServer.expect(ExpectedCount.once(), requestTo(uri))
			  .andExpect(method(HttpMethod.POST))			  
			  .andExpect(queryParam("syntax", "regex"))	
			  .andExpect(content().json(mapper.writeValueAsString(patternAndDirectoryJsonNode)))
			  .andRespond(withStatus(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)    	 
	          );
			
	          HttpHeaders headers = new HttpHeaders();
			  headers.setContentType(MediaType.APPLICATION_JSON);
			  HttpEntity<String> request = new HttpEntity<String>(patternAndDirectoryJsonNode.toPrettyString(), headers);
			
			
			  Assertions.assertThrows(HttpClientErrorException.class,()->{
				  ResponseEntity<String[]> response  = template.postForEntity(uri, request, String[].class);
				});
								
			
			  mockServer.verify();			
		
	 }
	 
	 @DisplayName("Integration test - given null Pattern and null directory GetAllFilesThatMatchRegex - Request body: {}")
	 @Test
	 void  givenNullPatternNullDirectoryWhenGetAllFilesThatMatchRegexGlobSyntax_thenBadRequest() throws JsonProcessingException, Exception {
		
		  final String baseUrl = LOCAL_HOST + randomServerPort + "/files/matchedfiles?syntax=glob";
	        URI uri = new URI(baseUrl);
	        
	        mockServer.expect(ExpectedCount.once(), requestTo(uri))
			  .andExpect(method(HttpMethod.POST))
			  .andExpect(queryParam("syntax", "glob"))			  	 			  
			  .andRespond(withStatus(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)    	 
	          );
			
	          HttpHeaders headers = new HttpHeaders();
			  headers.setContentType(MediaType.APPLICATION_JSON);
			  HttpEntity<String> request = new HttpEntity<String>("{}", headers);
		
			  Assertions.assertThrows(HttpClientErrorException.class,()->{
				  ResponseEntity<String[]> response  = template.postForEntity(uri, request, String[].class);
				});
			
			
			  mockServer.verify();
			
		
	 }
}
