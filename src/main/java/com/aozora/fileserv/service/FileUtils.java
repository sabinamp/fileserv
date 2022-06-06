package com.aozora.fileserv.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aozora.fileserv.controller.FileContentController;
import com.aozora.fileserv.exception.FileStorageException;

public class FileUtils {
	private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
	
	public FileUtils() {
		
	}
	
	public static BufferedReader getReader(String fileName) {
        BufferedReader reader = null;
        InputStream fileInputStream = FileUtils.class.getResourceAsStream(fileName);
        if(fileInputStream != null){
            reader = new BufferedReader(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8));
        }

        return reader;
    }
	/*
	 * public static Path getPath(String fileName) { try { return
	 * Paths.get(FileUtils.class.getResource(fileName).toURI()); } catch
	 * (URISyntaxException e) { throw new IllegalArgumentException(e); } }
	 */

    public static List<String> getLinesInFile(String fileName, String directoryPath) throws IOException, URISyntaxException {
    	logger.debug("entering "+FileUtils.class.getName()+"the method: getLinesInFile ");
    	Path fPath=null;
    	try {    		
    		fPath = Paths.get(directoryPath).resolve(fileName);
    		System.out.println("FileUtils fPath is: "+fPath);
    		logger.debug("fPath:" +fPath);
    	}catch(NullPointerException e) {
    		e.printStackTrace();
    		throw new NoSuchFileException(e.getMessage());
    	}
    	logger.debug("exiting "+FileUtils.class.getName()+"the method: getLinesInFile ");
        return Files.lines(fPath, StandardCharsets.UTF_8).collect(Collectors.toList());
    	//return Files.readAllLines(fPath);
    }

}
