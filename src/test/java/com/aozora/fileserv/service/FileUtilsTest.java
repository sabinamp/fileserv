/**
 * 
 */
package com.aozora.fileserv.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.MalformedInputException;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

/**
 * @author SabinaM
 *
 */
class FileUtilsTest {
	private String fileName;
	private String directoryPath;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		fileName= "words.txt";
		directoryPath="C:/workspace_sts4-14/fileserv/bin/main/static/uploads/";
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void givenFileWhenReadingLinesInFile_ThenCorrect() throws IOException, URISyntaxException {
		String str = "love, home, Christmas, garden, gardening, house, home";

	    List<String> allLines= FileUtils.getLinesInFile(fileName, directoryPath);
	    String read= allLines.get(1);
	    assertEquals(str, read);
	}
	
	@Test
	void givenFileWhenReadingLinesInFile_ThenIncorrect() {
		String str = "love, home, Christmas, garden, gardening, house, home";
		
	     assertThrows(NoSuchFileException.class, new Executable() {
            
            @Override
            public void execute() throws Throwable {
            	List<String> allLines= FileUtils.getLinesInFile("notexistingFile.txt", directoryPath);
         	   
            }
        });
	}
	
	@Test
	public void testFileNameIsEmptyString() {
	 
	    assertThrows(AccessDeniedException.class, () -> {
	    	List<String> allLines= FileUtils.getLinesInFile("", directoryPath);
     	   
	    });	   
	}
	
	@Test
	public void testFileIsNotinUTF8() {
	 
	    assertThrows(MalformedInputException.class, () -> {
	    	List<String> allLines= FileUtils.getLinesInFile("StoreAddressList.pdf", directoryPath);
     	   
	    });
	   
	}
	
	

}
