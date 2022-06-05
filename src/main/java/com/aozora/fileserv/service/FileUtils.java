package com.aozora.fileserv.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtils {

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
    public static Path getPath(String fileName)  {
        try {
            return Paths.get(FileUtils.class.getResource(fileName).toURI());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static List<String> getLinesInFile(String filePath) throws IOException, URISyntaxException {
    	Path fPath=null;
    	try {    		
    		fPath = Paths.get(filePath);
    	}catch(NullPointerException e) {
    		e.printStackTrace();
    		throw new IllegalArgumentException(e);
    	}
    	
        return Files.lines(fPath, StandardCharsets.UTF_8).collect(Collectors.toList());
    }

}
