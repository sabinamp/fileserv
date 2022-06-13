/**
 * 
 */
package com.aozora.fileserv.controller;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.aozora.fileserv.model.WordFreqPair;
import com.aozora.fileserv.service.FileContentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author SabinaM
 *
 */
@WebMvcTest(FileStorageController.class)
public class FileContentControllerTest {
	 @Autowired
	 private MockMvc mockMvc;
	 
	 @MockBean
	 private static FileContentService fileContentService;
	 
	 private static String fileName;
	 private static String directoryPath;
	 
	 private Map<Integer, Set<String>> longestWordsPerLine = new TreeMap<Integer,Set<String>>();
	 Map<Integer, Set<WordFreqPair>> freqWords = new TreeMap<Integer,Set<WordFreqPair>>();

	 @BeforeAll
	 private static void setUpTestData() {
		 fileName= "words.txt";
		 directoryPath="C:/workspace_sts4-14/fileserv/bin/main/static/uploads/";
	 }

	@Test
	void testGetFirstNFreqWords(String filename, String directoryPath, int n) throws Exception {
		/*
		 * [ { "the frequency: 15": "[love]" }, { "the frequency: 14": "[home]" }, {
		 * "the frequency: 9": "[Christmas, gardening, garden]" }, { "the frequency: 7":
		 * "[house]" }, { "the frequency: 2": "[coding, I]" }, { "the frequency: 1":
		 * "[branch]" } ]
		 */
		TreeMap<Integer, Set<WordFreqPair>> expectedFreq = new TreeMap<Integer,Set<WordFreqPair>>();;
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
		
		when(fileContentService.getFirstNFreqWords(filename, directoryPath, 10)).thenReturn(expectedFreq);
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode patternAndDirectoryJsonNode = mapper.createObjectNode();
		patternAndDirectoryJsonNode.put("filename", filename);
		patternAndDirectoryJsonNode.put("directory", directoryPath);
		
		MultiValueMap<String, String> reqParams = new LinkedMultiValueMap<>();
		reqParams.add("n", "10");
		//
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/content/freqwords")							
				.contentType(MediaType.APPLICATION_JSON)
				 .params(reqParams).content(patternAndDirectoryJsonNode.toString())	
			    .accept(MediaType.APPLICATION_JSON))
			    .andExpect(status().isOk())	
			    .andExpect(jsonPath("$.size()").value(expectedFreq.size()))			   			    
			    .andReturn();
		
		 verify(fileContentService, times(1)).getFirstNFreqWords(filename, directoryPath, 10);
		 assertNotNull(result.getResponse());
	}
}
