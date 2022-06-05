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
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class FileStorageException extends RuntimeException {



	public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }

}
