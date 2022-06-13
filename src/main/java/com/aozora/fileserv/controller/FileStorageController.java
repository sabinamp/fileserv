/**
 * 
 */
package com.aozora.fileserv.controller;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.aozora.fileserv.payload.UploadFileResponse;
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
@RequestMapping("/files")
public class FileStorageController {
	
	private Set<String> filesInDirectory = new HashSet<>();	
	private FileStorageServI fileStorageService;
	private static final Logger logger = LoggerFactory.getLogger(FileStorageController.class);
	

	/**
	 * 
	 */
	public FileStorageController(FileStorageServI storageService) {
		fileStorageService=storageService;
	}

	
	//POST http://localhost:8082/fileservice/files/matchedfiles?syntax="regex"
	@PostMapping(value = "/matchedfiles", consumes = MediaType.APPLICATION_JSON_VALUE,produces = {MediaType.APPLICATION_JSON_VALUE})
	ResponseEntity<Set<String>> getAllFilesThatMatchRegex(@RequestBody String jsonPatternAndDirectory,
	@RequestParam(required = false, defaultValue="regex") String syntax){
		
		ObjectMapper objectMapper = new ObjectMapper();		
		JsonNode jsonNode = null;
		try {
			jsonNode = objectMapper.readTree(jsonPatternAndDirectory);
			if(jsonNode==null) {
				 return new ResponseEntity<Set<String>>(HttpStatus.BAD_REQUEST);
			}
			 JsonNode directoryNode =  jsonNode.get("directory");			
			 JsonNode patternNode = jsonNode.get("pattern");
			 if(patternNode != null && directoryNode != null) {
				 String pattern = patternNode.asText();
				 String directory=directoryNode.asText();	
				 filesInDirectory = fileStorageService.getAllFileNamesThatMatchRegex(pattern, syntax, directory);
					 
				 if( filesInDirectory == null || filesInDirectory.size() == 0) {			
					return new ResponseEntity<Set<String>>(HttpStatus.NOT_FOUND);			
				}else {
					   return new ResponseEntity<Set<String>>(filesInDirectory, HttpStatus.OK);
				}				 
			 }else {
				 return new ResponseEntity<Set<String>>(HttpStatus.BAD_REQUEST);
			 }			 
			
		} catch (JsonMappingException e1) {		
			e1.printStackTrace();
			return new ResponseEntity<Set<String>>(HttpStatus.BAD_REQUEST);
		} catch (JsonProcessingException e1) {			
			e1.printStackTrace();
			return new ResponseEntity<Set<String>>(HttpStatus.BAD_REQUEST);
		}		 
		
	}
	
	@GetMapping("/{staticFolder}")	
	public ResponseEntity<Resource> getFile(@RequestParam(required=true) String file, @PathVariable String staticFolder) {
		Resource fileResource = null;
		if(staticFolder=="data") {
			 fileResource = fileStorageService.getResourceInData(file);
		}else if( staticFolder=="uploads") {
			fileResource = fileStorageService.getResourceInUploads(file);
		}
	   
	    return ResponseEntity.ok()
	        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file + "\"")
	        .body(fileResource);
	}
	

}
