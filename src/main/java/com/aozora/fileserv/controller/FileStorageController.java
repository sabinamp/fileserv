/**
 * 
 */
package com.aozora.fileserv.controller;

import java.util.HashSet;
import java.util.Set;

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
	private Set<Resource> filesInDirectory = new HashSet<>();
	private Set<String> mFiles = new HashSet<>();
	
	
	private FileStorageServI fileStorageService;


	/**
	 * 
	 */
	public FileStorageController(FileStorageServI storageService) {
		fileStorageService=storageService;
	}

	
	//POST http://localhost:8082/matchedfiles?syntax="regex"
	//pattern as plain text in request body
	@PostMapping(value = "/matchedfiles", consumes = MediaType.APPLICATION_JSON_VALUE,produces = {MediaType.APPLICATION_JSON_VALUE})
	ResponseEntity<Set<String>> getAllFilesThatMatchRegex(@RequestBody String jsonPatternAndDirectory,
	@RequestParam(required = false, defaultValue="regex") String syntax){
		ObjectMapper objectMapper = new ObjectMapper();		
		JsonNode jsonNode = null;
		try {
			jsonNode = objectMapper.readTree(jsonPatternAndDirectory);
			String directory= jsonNode.get("directory").asText();
			 String pattern = jsonNode.get("pattern").asText();
			 if (!(syntax.equalsIgnoreCase("glob") || syntax.equalsIgnoreCase("regex"))) {			
				  return new ResponseEntity<Set<String>>(mFiles, HttpStatus.BAD_REQUEST);
			 }
			 filesInDirectory = fileStorageService.getAllThatMatchRegex(pattern, syntax, directory);
			 filesInDirectory.forEach( e -> mFiles.add(e.getFilename()));
			 if( mFiles == null || mFiles.size() == 0) {			
				return new ResponseEntity<Set<String>>(mFiles, HttpStatus.NOT_FOUND);			
			}else {
				   return new ResponseEntity<Set<String>>(mFiles, HttpStatus.OK);
			}
		} catch (JsonMappingException e1) {		
			e1.printStackTrace();
			
		} catch (JsonProcessingException e1) {			
			e1.printStackTrace();
		}
		 return new ResponseEntity<Set<String>>(mFiles, HttpStatus.OK);		 
		
	}
	
	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> getFile(@PathVariable String filename) {
	    Resource file = fileStorageService.getResource(filename);
	    return ResponseEntity.ok()
	        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
	        .body(file);
	}
}
