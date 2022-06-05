/**
 * 
 */
package com.aozora.fileserv.exception;

import java.nio.file.InvalidPathException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.aozora.fileserv.payload.ResponseMessage;

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
	
	@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<ResponseMessage> handleMaxSizeException(MaxUploadSizeExceededException exc) {
		
	    return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage("File too large!"));
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(PatternSyntaxException.class)
	public Map<String, String> handleValidationExceptions(PatternSyntaxException ex) {
	    Map<String, String> errors = new HashMap<>();
	    String errorMessage = ex.getMessage();
        errors.put("message", errorMessage);
        String errorDescription = ex.getDescription();
        errors.put(errorDescription, errorDescription);
	    return errors;
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(InvalidPathException.class)
	public Map<String, String> handleFileRelatedExceptions(InvalidPathException ex) {
	    Map<String, String> errors = new HashMap<>();
	    String errorMessage = ex.getMessage();
        errors.put("message", errorMessage);
        String cause = ex.getCause().getMessage(); 
        errors.put("causeMessage", cause);
	    return errors;
	}
	
}
