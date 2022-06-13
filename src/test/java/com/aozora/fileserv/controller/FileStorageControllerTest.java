/**
 * 
 */
package com.aozora.fileserv.controller;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.JsonContentAssert;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.aozora.fileserv.config.FileServiceProperties;
import com.aozora.fileserv.service.FileStorageService;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author SabinaM
 *
 */

@WebMvcTest(FileStorageController.class)
public class FileStorageControllerTest {
	 private static String fileName;
	 private static String dataDirectoryPath;
	 private static Set<String> expectedFilenames;
	 private static Set<Resource> expectedResources;
		
	 @Autowired
	 private MockMvc mockMvc;
	 
	 private static String pattern;
	 private static String patternSyntax;	
	 
	 @MockBean
	 private static FileStorageService fileStorageService;
	 
	 @BeforeAll
	 private static void setUpTestData() {
		    pattern ="^[a-zA-Z0-9._ -]+\\.(doc|pdf|csv|txt)$";
		    patternSyntax = "regex";
		
		    expectedFilenames = new HashSet<>();
			expectedFilenames.add("words.txt");
			expectedFilenames.add("StoreAddressList.pdf");
			expectedFilenames.add("8-Reasons-AStartupOverACorporateJob.pdf");
			expectedFilenames.add("PathMatcher.txt");
			expectedFilenames.add("50-contacts.csv" );
			
			fileName = "PathMatcher.txt";			
			dataDirectoryPath = "C:/workspace_sts4-14/fileserv/bin/main/static/data/";	
			
			expectedResources = new HashSet<>();
			Path fileStorageLocation =Paths.get(dataDirectoryPath);
			
			Path pathMatcherFilePath = fileStorageLocation.resolve(fileName).normalize();
			Path wordsFilePath = fileStorageLocation.resolve("words.txt").normalize();
			Path contactsFilePath = fileStorageLocation.resolve("50-contacts.csv").normalize();
			Path reasonsFilePath = fileStorageLocation.resolve("8-Reasons-AStartupOverACorporateJob.pdf").normalize();
			Path addressListFilePath = fileStorageLocation.resolve("StoreAddressList.pdf").normalize();
		      try {
				Resource resourcePathMatcher = new UrlResource(pathMatcherFilePath.toUri());
				Resource resourceWords = new UrlResource(wordsFilePath.toUri());
				Resource contactsResource= new UrlResource(contactsFilePath.toUri());
				Resource reasonsResource = new UrlResource(reasonsFilePath.toUri());
			
				Resource addressListResource= new UrlResource(addressListFilePath.toUri());
				expectedResources.add(resourcePathMatcher);
				expectedResources.add(resourceWords);
				expectedResources.add(contactsResource);
				expectedResources.add(reasonsResource);
				expectedResources.add(addressListResource);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}  
		    
		 
	 }
	 
	 
	 @Test
	 void givenPatternWhenGetAllFilesThatMatchRegex_then200() throws Exception {
		 when(fileStorageService.getAllThatMatchRegex(pattern, patternSyntax,
				  dataDirectoryPath)) .thenReturn(expectedResources);
				 
		 when(fileStorageService.getAllFileNamesThatMatchRegex(pattern, patternSyntax, dataDirectoryPath))
			 	.thenReturn(expectedFilenames);
		
		MultiValueMap<String, String> reqParams = new LinkedMultiValueMap<>();
		reqParams.add("syntax", "regex");
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode patternAndDirectoryJsonNode = mapper.createObjectNode();
		patternAndDirectoryJsonNode.put("pattern", "^[a-zA-Z0-9._ -]+\\.(doc|pdf|csv|txt)$");
		patternAndDirectoryJsonNode.put("directory", dataDirectoryPath);
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/files/matchedfiles")							
					.contentType(MediaType.APPLICATION_JSON)
					 .params(reqParams).content(patternAndDirectoryJsonNode.toString())	
				    .accept(MediaType.APPLICATION_JSON))
				    .andExpect(status().isOk())	
				    .andExpect(jsonPath("$.size()").value(expectedFilenames.size()))
				    .andExpect(jsonPath("$[*]", hasItem("words.txt")))				    
				    .andReturn();
			 		
		 verify(fileStorageService, times(1)).getAllFileNamesThatMatchRegex(pattern, patternSyntax, dataDirectoryPath);
		 assertNotNull(result.getResponse().getContentAsString());
		 assertTrue(result.getResponse().getErrorMessage()==null);		
			 
	 }
	 
	 
	 @Test
	 void givenNoPatternWhenGetAllFilesThatMatchRegex_then400() throws Exception {
		 MultiValueMap<String, String> reqParams = new LinkedMultiValueMap<>();
			reqParams.add("syntax", "regex");
			 when(fileStorageService.getAllThatMatchRegex(pattern, patternSyntax,
					  dataDirectoryPath)) .thenReturn(expectedResources);
					 
			 when(fileStorageService.getAllFileNamesThatMatchRegex(pattern, patternSyntax, dataDirectoryPath))
				 	.thenReturn(expectedFilenames);
				
		mockMvc.perform(MockMvcRequestBuilders.post("/files/matchedfiles")	
				.contentType(MediaType.APPLICATION_JSON)
				.params(reqParams).content("{}")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());			 		
		 
	 }
	 
	 @Test
	 void givenPatternWhenGetAllFilesThatMatchRegex_thenReturnNoContent() throws Exception {
		 MultiValueMap<String, String> reqParams = new LinkedMultiValueMap<>();
			reqParams.add("syntax", "regex");
		 when(fileStorageService.getAllFileNamesThatMatchRegex(pattern, patternSyntax, dataDirectoryPath))
		 	.thenReturn(Collections.EMPTY_SET);		
		 		
				
		mockMvc.perform(MockMvcRequestBuilders.post("/files/matchedfiles")	
				.contentType(MediaType.APPLICATION_JSON)
				.param("syntax", "regex").content("{}")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
			 		
		 
	 }
		
	 
	 
		
	  @Test
	  public void givenGlobPatternAndGlobSyntax_ThenCorrect() throws Exception {
			 
		  	when(fileStorageService.getAllFileNamesThatMatchRegex("*.{doc,csv,pdf,txt}", "glob", dataDirectoryPath))
		 	.thenReturn(expectedFilenames);
		  
		    MultiValueMap<String, String> reqParams = new LinkedMultiValueMap<>();
			reqParams.add("syntax", "glob");
			
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode patternAndDirectoryJsonNode2 = mapper.createObjectNode();
			patternAndDirectoryJsonNode2.put("pattern", "*.{doc,csv,pdf,txt}");
			patternAndDirectoryJsonNode2.put("directory", dataDirectoryPath);
			
			MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/files/matchedfiles")							
					.contentType(MediaType.APPLICATION_JSON)
						.params(reqParams).content(patternAndDirectoryJsonNode2.toString())	
					    .accept(MediaType.APPLICATION_JSON))
					    .andExpect(status().isOk())	
					    .andExpect(jsonPath("$.size()").value(expectedFilenames.size()))
					    .andExpect(jsonPath("$[*]", hasItem("words.txt")))				    
					    .andReturn();
				 		
			 verify(fileStorageService, times(1)).getAllFileNamesThatMatchRegex( "*.{doc,csv,pdf,txt}", "glob", dataDirectoryPath);
			 assertNotNull(result.getResponse().getContentAsString());
			 assertTrue(result.getResponse().getErrorMessage()==null);
		  
	  }
		 

}
