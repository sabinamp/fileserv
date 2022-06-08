/**
 * 
 */
package com.aozora.fileserv.exception;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.aozora.fileserv.controller.FileContentController;
import com.aozora.fileserv.payload.ResponseMessage;

/**
 * @author SabinaM
 *
 */
public class FileServiceExceptionHandler {
	private static final Logger log = LoggerFactory.getLogger(FileServiceExceptionHandler.class);
	/**
	 * 
	 */
	public FileServiceExceptionHandler() {
		// TODO Auto-generated constructor stub
	}
	
	@ExceptionHandler(value = FileContentServiceException.class)
	@ResponseStatus( value = HttpStatus.INTERNAL_SERVER_ERROR)
	public Map<String, String> handleFileServiceErrors(FileContentServiceException ex){
		log.error("received error ({}) with message: {}", ex.getCause(), ex.getMessage());
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
		log.error("received error ({}) with message: {}", ex.getCause(), ex.getMessage());
		 Map<String, String> errors = new HashMap<>();
		  String errorMessage = ex.getMessage();
	        errors.put("message", errorMessage);
	        String localizedMessage = ex.getLocalizedMessage();
	        errors.put("localizedMessage", localizedMessage);
		    return errors;
	}
	
	@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<ResponseMessage> handleMaxSizeException(MaxUploadSizeExceededException ex) {
		log.error("received error ({}) with message: {}", ex.getCause(), ex.getMessage());
	    return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage("File too large!"));
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(PatternSyntaxException.class)
	public Map<String, String> handleValidationExceptions(PatternSyntaxException ex) {
		log.error("received error ({}) with message: {}", ex.getCause(), ex.getMessage());
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
		log.error("received error ({}) with message: {}", ex.getCause(), ex.getMessage());
	    Map<String, String> errors = new HashMap<>();
	    String errorMessage = ex.getMessage();
        errors.put("message", errorMessage);
        String cause = ex.getCause().getMessage(); 
        errors.put("causeMessage", cause);
	    return errors;
	}
	
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(IOException.class)
	public Map<String, String> handleIOExceptions(IOException ex) {
		log.error("received error ({}) with message: {}", ex.getCause(), ex.getMessage());
	    Map<String, String> errors = new HashMap<>();
	    String errorMessage = ex.getMessage();
        errors.put("message", errorMessage);
        String localizedMessage = ex.getLocalizedMessage();
        errors.put("localizedMessage", localizedMessage);
       
	    return errors;
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(NoSuchFileException.class)
	public Map<String, String> handleNoSuchFileExceptions(NoSuchFileException ex) {
		log.error("received error ({}) with message: {}", ex.getCause(), ex.getMessage());
	    Map<String, String> errors = new HashMap<>();
	    String errorMessage = ex.getMessage();
        errors.put("message", errorMessage);
        String reasonMessage = ex.getReason();
        errors.put("reasonMessage", reasonMessage);
       
	    return errors;
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(URISyntaxException.class)
	public Map<String, String> handleValidationExceptions(URISyntaxException ex) {
		log.error("received error ({}) with message: {}", ex.getInput(), ex.getMessage());
		 Map<String, String> errors = new HashMap<>();
		    String errorMessage = "input: "+ex.getInput()+ ex.getMessage();
	        errors.put("message", errorMessage);
	        String reasonMessage = ex.getReason();
	        errors.put("reasonMessage", reasonMessage);
	    return errors;
	}
	
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(PatternSyntaxException.class)
	public Map<String, String> handlePatternValidationExceptions(PatternSyntaxException ex) {
		log.error("received error ({}) with message: {}", "Pattern: "+ ex.getPattern(), ex.getMessage());
	    Map<String, String> errors = new HashMap<>();
	    String errorMessage = ex.getMessage();
        errors.put("message", errorMessage);
        String errorDescription = ex.getDescription();
        errors.put(errorDescription, errorDescription);
	    return errors;
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(InvalidPathException.class)
	public Map<String, String> handleFilePathRelatedExceptions(InvalidPathException ex) {
		log.error("received error ({}) with message: {}", ex.getCause(), ex.getMessage());
	    Map<String, String> errors = new HashMap<>();
	    String errorMessage = ex.getMessage();
        errors.put("message", errorMessage);
        String reason = ex.getReason(); 
        errors.put("reasonMessage", reason);
	    return errors;
	}
	
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MalformedURLException.class)
	public Map<String, String> handleFileRelatedExceptions(MalformedURLException ex) {
		log.error("received error ({}) with message: {}", ex.getCause(), ex.getMessage());
	    Map<String, String> errors = new HashMap<>();
	    String errorMessage = ex.getMessage();
        errors.put("errorMessage", errorMessage);
        String localizedMessage = ex.getLocalizedMessage();
        errors.put("localizedMessage", localizedMessage);
        errors.put("cause",  ex.getCause().getMessage());
	    return errors;
	}
}
