/**
 * 
 */
package com.aozora.fileserv.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.aozora.fileserv.config.FileServiceProperties;
import com.aozora.fileserv.exception.FileStorageException;

/**
 * @author SabinaM
 *
 */
@Service
public class FileStorageService implements FileStorageServI {
	@Autowired
	private FileServiceProperties serviceProperties;

	/**
	 * 
	 */
	public FileStorageService() {
		
	}

	
	public String getUploadDir() {
		return serviceProperties.getUploadDir();
	}


	@Override
	public String save(MultipartFile file) {
		Path fileStorageLocation =Paths.get(this.serviceProperties.getUploadDir());
		// Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        Path targetLocation = fileStorageLocation.resolve(fileName);
		try {
		      Files.copy(file.getInputStream(), targetLocation,StandardCopyOption.REPLACE_EXISTING);
		 } catch (IOException e) {
			 throw new FileStorageException("Could not save file " + fileName + ". Please try again!"+ e.getMessage());
		   
		    }
		return file.getName();
	}

	
	@Override
	public Resource getResourceInUploads(String filename) throws FileStorageException {
		Path fileStorageLocation =Paths.get(this.serviceProperties.getUploadDir());
		 try {
		      Path filePath = fileStorageLocation.resolve(filename).normalize();
		      Resource resource = new UrlResource(filePath.toUri());
		      if (resource.exists() || resource.isReadable()) {
		        return resource;
		      } else {
		        throw new FileStorageException("Could not read the file!");
		      }
		    } catch (MalformedURLException e) {
		      throw new FileStorageException("Error: " + e.getMessage());
		    }
	}
	

	@Override
	public void deleteAllUploads() {
		Path fileStorageLocation =Paths.get(this.serviceProperties.getUploadDir());
		FileSystemUtils.deleteRecursively(fileStorageLocation.toFile());
		
	}

	//get all files that match a pattern in a given directory 	
	//if directory is null, default directory-the data folder in static resources
	@Override
	public Set<Resource> getAllThatMatchRegex(String pattern, String patternSyntax, String directoryName){
			    Set<Resource> fileList = new HashSet<>();
				String directoryN= directoryName;
			    if(null==directoryName || directoryName.isEmpty()) {
			    	directoryN = serviceProperties.getDIRECTORY_PATH();
			    }
				Path directory = Paths.get(directoryN).toAbsolutePath().normalize();
							 
		        // get the path matcher from the file system
		        FileSystem fs = FileSystems.getDefault();
		        if (patternSyntax.equalsIgnoreCase("glob") || patternSyntax.equalsIgnoreCase("regex")) {
		        	 //specifies glob or regex syntax and the pattern to be matched
			        //When the syntax is "regex", the pattern component is a regular expression as
			        //defined by the java.util.regex.Pattern class.
			        final PathMatcher matcher = fs.getPathMatcher(patternSyntax +":"+ pattern);
			        System.out.println(patternSyntax + ":" + pattern);
			        
			        FileVisitor<Path> matcherVisitor = new SimpleFileVisitor<>() {
			            @Override
			            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException{
			                	               
			                if (matcher.matches(file.getFileName())) {
			                    System.out.println(String.format("Found matched file: '%s'.%n", file));
			                    fileList.add(new UrlResource(file.toUri()));	                    
			                }
			                return FileVisitResult.CONTINUE;
			            }
			        };		        
					try {
						Files.walkFileTree(directory, matcherVisitor);
					} catch (IOException e) {
						e.printStackTrace();
					}				
		        }else {
		            
		             throw new UnsupportedOperationException("Syntax '" + patternSyntax + "' not recognized");
		        }
		       return fileList;
		}

	@Override
	public Resource getResourceInData(String filename) throws FileStorageException {
		Path fileStorageLoc =Paths.get(this.serviceProperties.getDIRECTORY_PATH());
		 try {
		      Path filePath = fileStorageLoc.resolve(filename).normalize();
		      Resource resource = new UrlResource(filePath.toUri());
		      if (resource.exists() || resource.isReadable()) {
		        return resource;
		      } else {
		        throw new FileStorageException("Could not read the file!");
		      }
		    } catch (MalformedURLException e) {
		      throw new FileStorageException("Error: " + e.getMessage());
		    }
	
	}

}
