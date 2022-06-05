/**
 * 
 */
package com.aozora.fileserv.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

/**
 * @author SabinaM
 *
 */
@ResponseStatus( value = HttpStatus.INTERNAL_SERVER_ERROR)
public class FileContentServiceException extends Exception{
	
	private String message;
	
	public FileContentServiceException() {
		
	}
		
	public FileContentServiceException(String message) {
		this.message=message;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
	
}
