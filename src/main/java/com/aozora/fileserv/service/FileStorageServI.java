package com.aozora.fileserv.service;

import java.util.Set;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.aozora.fileserv.exception.FileStorageException;

public interface FileStorageServI {
	
		//public String save(MultipartFile file);
		
		public Resource getResourceInUploads(String filename) throws FileStorageException;
		public Resource getResourceInData(String filename) throws FileStorageException;
		public void deleteAllUploads();
		
		Set<Resource> getAllThatMatchRegex(String pattern,String patternSyntax, String directory);
		Set<String> getAllFileNamesThatMatchRegex(String pattern,String patternSyntax, String directory);
		public String getUploadDir();
}
