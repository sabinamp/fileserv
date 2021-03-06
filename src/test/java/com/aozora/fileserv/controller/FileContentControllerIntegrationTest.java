package com.aozora.fileserv.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.NoSuchFileException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.aozora.fileserv.FileservApplication;
import com.aozora.fileserv.config.FileServiceProperties;
import com.aozora.fileserv.model.WordFreqPair;
import com.aozora.fileserv.service.FileContentService;
import com.aozora.fileserv.service.FileStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author SabinaM
 * test the controller using a MockMvc object without an embedded server 
 */
@SpringBootTest(classes = {
		FileservApplication.class},
		webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(locations="classpath:application.properties")
@AutoConfigureMockMvc
public class FileContentControllerIntegrationTest {

	private static String fileName;
	
	private static String directoryPath;

	private static TreeMap<Integer, Set<WordFreqPair>> expectedFreq;
	 
	 @TestConfiguration static class FileContentServiceTestContextConfiguration {
		  
		  @Bean 
		  public FileContentService employeeService() {
			  return new  FileContentService(); 
		 }
	}
		 
	 
	
	 @Autowired
	 private FileContentService fileContentService;
	 
	 @Autowired
	 private MockMvc mockMvc;
	 
	 @BeforeAll
	 public static void init() {
	               
		    fileName = "words.txt";
		    directoryPath = "C:/workspace_sts4-14/fileserv/bin/main/static/uploads/";		
			
			 expectedFreq = new TreeMap<Integer,Set<WordFreqPair>>();
			 Set<WordFreqPair> freq1= new HashSet<>();
				freq1.add(new WordFreqPair("branch", 1));	
				expectedFreq.put(1, freq1);
				
				Set<WordFreqPair> freq2= new HashSet<>();
				freq2.add(new WordFreqPair("coding", 2));
				freq2.add(new WordFreqPair("I", 2));
				expectedFreq.put(2, freq2);
				
				Set<WordFreqPair> freq3= new HashSet<>();
				freq3.add(new WordFreqPair("house", 7));		
				expectedFreq.put(7, freq3);
				
				Set<WordFreqPair> freq4= new HashSet<>();
				freq4.add(new WordFreqPair("home", 14));		
				expectedFreq.put(14, freq4);
				
				Set<WordFreqPair> freq5= new HashSet<>();
				freq5.add(new WordFreqPair("love", 15));		
				expectedFreq.put(15, freq5);
				
				//[Christmas, gardening, garden]
				Set<WordFreqPair> freq6= new HashSet<>();
				freq6.add(new WordFreqPair("Christmas", 9));	
				freq6.add(new WordFreqPair("gardening", 9));
				freq6.add(new WordFreqPair("garden", 9));
				expectedFreq.put(9, freq6);
				
	 }
	 
	 @Test
	 void givenFileNameDirectoryPath_WhenGetFirstNFreqWords_thenOK() throws Exception {
			
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode filenameAndDirectoryJsonNode = mapper.createObjectNode();
			filenameAndDirectoryJsonNode.put("fileName", "words.txt");
			filenameAndDirectoryJsonNode.put("directory", directoryPath);
			
			MultiValueMap<String, String> reqParams = new LinkedMultiValueMap<>();
			reqParams.add("n", "10");
			
			MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/content/freqwords")							
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.params(reqParams).content(filenameAndDirectoryJsonNode.toPrettyString())	
				    .accept(MediaType.TEXT_PLAIN_VALUE))
				    .andExpect(status().isOk())	
				    .andExpect(jsonPath("$.size()").value(expectedFreq.size()))	
				    //.andExpect(jsonPath("$[0]", contains(" \"the frequency: 15\": \"[love]" ))
				    .andReturn();
			   	 
			 assertNotNull(result.getResponse().getContentAsString().contains("gardening"));
		}
	 
	   @Test
	   void givenNoFileNameNoDirectoryPath_whenGetFirstNFreqWords_then400() throws Exception {
						
			MultiValueMap<String, String> reqParams = new LinkedMultiValueMap<>();
			reqParams.add("n", "10");
			
			MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/content/freqwords")							
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					 .params(reqParams).content("{}")	
				    .accept(MediaType.TEXT_PLAIN_VALUE))
				    .andExpect(status().isBadRequest())				   			   			    
				    .andReturn();
			
			
			 assertNotNull(result.getResponse());
			 assertTrue(result.getResponse().getContentAsString().contains("Bad request"));
			
		}
	
	   @Test
		void givenFileNameDirectoryPath_WhenGetFirstNFreqWordsNMissing_thenOK() throws Exception {
						
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode filenameAndDirectoryJsonNode = mapper.createObjectNode();
			filenameAndDirectoryJsonNode.put("fileName", "words.txt");
			filenameAndDirectoryJsonNode.put("directory", directoryPath);
			
			
			MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/content/freqwords")							
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(filenameAndDirectoryJsonNode.toPrettyString())	
				    .accept(MediaType.TEXT_PLAIN_VALUE))
				    .andExpect(status().isBadRequest())				    
				    .andReturn();
			
			
			 assertNotNull(result.getResponse());
		}
	   
	   
	    @Test
		void givenFileAndDirectory_whenGetLongestWordsPerLine_thenOK() throws Exception {
									
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode filenameAndDirectoryJsonNode = mapper.createObjectNode();
			filenameAndDirectoryJsonNode.put("fileName", "words.txt");
			filenameAndDirectoryJsonNode.put("directory", directoryPath);
			
			MvcResult result2 = mockMvc.perform(MockMvcRequestBuilders.post("/content/longestwords")							
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(mapper.writeValueAsString(filenameAndDirectoryJsonNode))	
				    .accept(MediaType.APPLICATION_JSON_VALUE))
				    .andExpect(status().isOk())				    	
				    .andReturn();
			
			 
			 assertNotNull(result2.getResponse());
			 assertTrue(result2.getResponse().getContentAsString().contains("gardening"));
		}
}
