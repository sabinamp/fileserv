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

/**
 * @author SabinaM
 *
 */
@RestController
@RequestMapping("/files")
public class FileStorageController {
	private Set<Resource> filesInDirectory = new HashSet<>();
	private Set<String> mFiles = new HashSet<>();
	
	@Autowired
	private FileStorageServI fileStorageService;


	/**
	 * 
	 */
	public FileStorageController() {
		// TODO Auto-generated constructor stub
	}

	
	//POST http://localhost:8082/matchedfiles?syntax="regex"
	//pattern as plain text in request body
	@PostMapping(value = "/matchedfiles", consumes = MediaType.ALL_VALUE,produces = {MediaType.APPLICATION_JSON_VALUE})
	ResponseEntity<Set<Resource>> getAllFilesThatMatchRegex(@RequestBody String pattern,
	@RequestParam(required = false, defaultValue="regex") String syntax){
		 
		if (syntax.equalsIgnoreCase("glob") || syntax.equalsIgnoreCase("regex")) {			
			filesInDirectory = fileStorageService.getAllThatMatchRegex(pattern, syntax, null);
			if( filesInDirectory == null || filesInDirectory.size() == 0) {			
				return new ResponseEntity<Set<Resource>>(filesInDirectory, HttpStatus.NOT_FOUND);			
			}else {
			   return new ResponseEntity<Set<Resource>>(filesInDirectory, HttpStatus.OK);
			}
		}else {
			  return new ResponseEntity<Set<Resource>>(filesInDirectory, HttpStatus.BAD_REQUEST);
			 }
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
