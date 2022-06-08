/**
 * 
 */
package com.aozora.fileserv.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.aozora.fileserv.payload.UploadFileResponse;
import com.aozora.fileserv.service.FileContentServI;
import com.aozora.fileserv.service.FileStorageServI;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * @author SabinaM
 *
 */
@RestController
@RequestMapping("/content")
public class FileContentController {
	
	@Autowired
	private FileContentServI fileContentService;

	@Autowired
	private FileStorageServI fileStorageService;
	
	private static final Logger logger = LoggerFactory.getLogger(FileContentController.class);
	
	private UploadFileResponse uploadResponse;
	private TreeMap<Integer, Set<String>> words;
	
	public FileContentController() {
		super();
		
	}


	@PostMapping( value = "upload/freqwords", consumes = MediaType.TEXT_PLAIN_VALUE, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<String> getFirstNFreqWords(@RequestParam("file") MultipartFile file, @RequestParam(required=true) int n) {		
		 String fileName = fileStorageService.save(file);

	        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
	                .path("/downloads/")
	                .path(fileName)
	                .toUriString();

	      uploadResponse=  new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
	  
		ResponseEntity<String> response = null;
		StringBuilder builder = new StringBuilder();
		String directoryPath = fileStorageService.getUploadDir();
		words = fileContentService.getFirstNFreqWords(uploadResponse.getFileName(),directoryPath, n);
		words.entrySet().forEach( s -> builder.append(s.getValue()+" has the frequency: "+s.getKey() +" \n"));		

		
		String output = builder.toString();
		if(output != null && !output.isEmpty()) {
			response = new ResponseEntity<String>(output, HttpStatus.OK);
		}
	 				
		return response;
	}
	
	@PostMapping( value = "/freqwords", consumes={MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<List<String>> getFirstNFreqWords(@RequestParam(required=true) int n,
			@RequestBody String jsonFileName) {	
		ObjectMapper objectMapper = new ObjectMapper();		
		JsonNode jsonFileNode = null;
		List<String> frequenciesW = new ArrayList<>();
		ResponseEntity<List<String>> response = null;
		try {
			jsonFileNode = objectMapper.readTree(jsonFileName);
			String fileName = jsonFileNode.get("fileName").asText();
			
			String directoryPath = fileStorageService.getUploadDir();
			
			words = fileContentService.getFirstNFreqWords(fileName,directoryPath, n);
			words.entrySet().forEach( 
					w -> frequenciesW.add( ""+w.getValue()+" have the frequency "+w.getKey())
							);		

			if(!frequenciesW.isEmpty()) {
				MultiValueMap<String,String> headers= new HttpHeaders();
				headers.set("eTag", Long.valueOf(fileName.length()+directoryPath.length())+"111");
				response = new ResponseEntity<List<String>>(frequenciesW, headers,HttpStatus.OK);
								
			}
		} catch (JsonMappingException e) {			
			e.printStackTrace();
			 logger.debug(" JsonProcessingException caught"+e.getMessage());
			 response = new ResponseEntity<List<String>>(frequenciesW, HttpStatus.BAD_REQUEST);
		} catch (JsonProcessingException e) {
			 logger.debug(" JsonProcessingException caught"+e.getMessage());
			e.printStackTrace();
			response = new ResponseEntity<List<String>>(frequenciesW, HttpStatus.BAD_REQUEST);
		}
		
	 				
		return response;
	}
	
	
	@PostMapping( value = "/longestwords", consumes={MediaType.ALL_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<List<String>> getLongestFreqWords(@RequestBody String jsonFileName) {
		ObjectMapper objectMapper = new ObjectMapper();		
		JsonNode jsonFileNode = null;

		ResponseEntity<List<String>> response = null;
		List<String> longestW = new ArrayList<>();
		try {
			jsonFileNode = objectMapper.readTree(jsonFileName);
			String fileName = jsonFileNode.get("fileName").asText();
			
			String directoryPath = fileStorageService.getUploadDir();
			
			Map<Integer, Set<String>> lwords = fileContentService.getLongestWords(fileName, directoryPath);
			if(lwords != null && lwords.size() != 0) {
				lwords.entrySet().forEach( 
						w -> longestW.add( "the current line number: "+w.getKey()+". The 2 longest words are: "
				+w.getValue()) );
			}
					
			if(!longestW.isEmpty()) {
				MultiValueMap<String,String> headers= new HttpHeaders();
				headers.set("eTag", Long.valueOf(fileName.length() + directoryPath.length())+"101");
				response = new ResponseEntity<List<String>>(longestW, headers,HttpStatus.OK);
								
			}
		} catch (JsonMappingException e) {
			e.printStackTrace();
			response = new ResponseEntity<List<String>>(longestW, HttpStatus.BAD_REQUEST);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			response = new ResponseEntity<List<String>>(longestW, HttpStatus.BAD_REQUEST);
		}	
		
		return response;	
		
	}
}
