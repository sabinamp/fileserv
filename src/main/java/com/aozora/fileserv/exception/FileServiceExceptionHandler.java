/**
 * 
 */
package com.aozora.fileserv.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author SabinaM
 *
 */
public class FileServiceExceptionHandler {

	/**
	 * 
	 */
	public FileServiceExceptionHandler() {
		// TODO Auto-generated constructor stub
	}
	
	@ExceptionHandler(value = FileContentServiceException.class)
	@ResponseStatus( value = HttpStatus.INTERNAL_SERVER_ERROR)
	public Map<String, String> handleFileServiceErrors(FileContentServiceException ex){
		 Map<String, String> errors = new HashMap<>();
		  String errorMessage = ex.getMessage();
	        errors.put("message", errorMessage);
	        String localizedMessage = ex.getLocalizedMessage();
	        errors.put("localizedMessage", localizedMessage);
		    return errors;
	}
	
	@ExceptionHandler(value = FileStorageException.class)
	@ResponseStatus( value = HttpStatus.NOT_FOUND)
	public Map<String, String> handleFileStorageServiceErrors(FileStorageException ex){
		 Map<String, String> errors = new HashMap<>();
		  String errorMessage = ex.getMessage();
	        errors.put("message", errorMessage);
	        String localizedMessage = ex.getLocalizedMessage();
	        errors.put("localizedMessage", localizedMessage);
		    return errors;
	}
}
