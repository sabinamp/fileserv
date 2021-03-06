/**
 * 
 */
package com.aozora.fileserv.controller;

import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.aozora.fileserv.model.WordFreqPair;
import com.aozora.fileserv.payload.UploadFileResponse;
import com.aozora.fileserv.service.FileContentServI;
import com.aozora.fileserv.service.FileStorageServI;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


/**
 * @author SabinaM
 *
 */
@RestController
@RequestMapping("/content")
public class FileContentController {
	
	@Autowired
	private FileContentServI fileContentService;

	//@Autowired
	//private FileStorageServI fileStorageService;
	
	private static final Logger logger = LoggerFactory.getLogger(FileContentController.class);
	
	private UploadFileResponse uploadResponse;
	private TreeMap<Integer, Set<WordFreqPair>> words;
	
	public FileContentController() {
		super();
		
	}


		
	@PostMapping(value = "/freqwords", consumes={MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.TEXT_PLAIN_VALUE})
	public ResponseEntity<String> getFirstNFreqWords(@RequestParam(required=true) int n, @RequestBody String jsonFileNameAndDir) {	
		ObjectMapper objectMapper = new ObjectMapper();		
		JsonNode jsonFileNode = null;
		List<String> frequenciesW = new ArrayList<>();
		ResponseEntity<String> response = null;
		try {
			jsonFileNode = objectMapper.readTree(jsonFileNameAndDir);
			if(jsonFileNode ==null) {
				 return ResponseEntity.badRequest().body("Bad request. No filename and directory JSON");
			}
			JsonNode directoryNode =  jsonFileNode.get("directory");			
			JsonNode fileNode = jsonFileNode.get("fileName");
			 if(fileNode != null  && directoryNode != null & n!=0) {
				    String fileName = fileNode.asText();			
					String directoryPath = directoryNode.asText();
					
					words = fileContentService.getFirstNFreqWords(fileName,directoryPath, n);
					
					if(words != null && words.size() > 0) {
						MultiValueMap<String,String> headers= new HttpHeaders();
						headers.set("eTag", Long.valueOf(fileName.length()+directoryPath.length())+"111");
						
						ObjectMapper objectMapper2 = new ObjectMapper();						
						ArrayNode frequenciesWArrayNode=objectMapper2.createArrayNode();
						
						words.entrySet().forEach(each-> {
							ObjectNode eachNode = objectMapper2.createObjectNode() ;
							Set<WordFreqPair> pairs= each.getValue();
							Set<String> sameFreqWords= new HashSet<>();
							pairs.forEach(p-> sameFreqWords.add(p.getWord()));
							eachNode.put("the frequency: "+each.getKey(), ""+sameFreqWords);
							frequenciesWArrayNode.add(eachNode);
						});
						String frequenciesWJSON = objectMapper2.writerWithDefaultPrettyPrinter().writeValueAsString(frequenciesWArrayNode);
						response = new ResponseEntity<String>(frequenciesWJSON, headers,HttpStatus.OK);
										
					}else if(words == null || words.size()==0){
						response = ResponseEntity.noContent().build();
					}
			 }else {
				 response = ResponseEntity.badRequest().body("Bad request. Incorrect JSON");
			 }
			

		} catch (JsonMappingException e) {			
			e.printStackTrace();
			 logger.debug(" JsonProcessingException caught"+e.getMessage());
			 response = ResponseEntity.badRequest().body("Bad request. Incorrect JSON");
		} catch (JsonProcessingException e) {
			 logger.debug(" JsonProcessingException caught"+e.getMessage());
			e.printStackTrace();
			response = ResponseEntity.badRequest().body("Bad request. Incorrect JSON");
		} catch (NoSuchFileException e) {
			 logger.debug(" NoSuchFileException caught"+e.getMessage());
			e.printStackTrace();
			response = ResponseEntity.badRequest().body("Bad request. Incorrect file name or Incorrect directory path");
		}
		
	 				
		return response;
	}
	
	
	@PostMapping( value = "/longestwords", consumes={MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<String> getLongestFreqWords(@RequestBody String jsonFileNameAndDir) {
		ObjectMapper objectMapper = new ObjectMapper();		
		JsonNode jsonFileNode = null;

		ResponseEntity<String> response = null;
	
		try {
			jsonFileNode = objectMapper.readTree(jsonFileNameAndDir);
			String fileName = jsonFileNode.get("fileName").asText();
			String directoryPath = jsonFileNode.get("directory").asText();
									
			Map<Integer, Set<String>> lwords = fileContentService.getLongestWords(fileName, directoryPath);
			
					
			if(lwords != null && lwords.size() != 0 && !lwords.isEmpty()) {
				MultiValueMap<String,String> headers= new HttpHeaders();
				headers.set("eTag", Long.valueOf(fileName.length() + directoryPath.length())+"101");
				
				ObjectMapper objectMapper3 = new ObjectMapper();			
				
				ArrayNode longestWArrayNode=objectMapper3.createArrayNode();
				lwords.entrySet().forEach(each-> {
					ObjectNode eachNode = objectMapper3.createObjectNode() ;
					Set<String> longestSet = each.getValue();
					eachNode.put("the current line number: "+each.getKey(), ""+longestSet);
					longestSet.forEach( w -> eachNode.put("length "+w.length(), w));
					longestWArrayNode.add(eachNode);
				});
				String longestWJSON = objectMapper3.writerWithDefaultPrettyPrinter().writeValueAsString(longestWArrayNode);
				response = new ResponseEntity<String>(longestWJSON, headers,HttpStatus.OK);
								
			}
		} catch (JsonMappingException e) {			
			e.printStackTrace();
			response = ResponseEntity.badRequest().body("Bad request.Incorrect JSON");
		} catch (JsonProcessingException e) {
			
			e.printStackTrace();
			response = ResponseEntity.badRequest().body("Bad request. Incorrect JSON");
		} catch (NoSuchFileException e) {
			
			e.printStackTrace();			
			response = ResponseEntity.badRequest().body("Bad request. Wrong file name or wrong directory path");
		}	
		
		return response;	
		
	}
}
