/**
 * 
 */
package com.aozora.fileserv.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author SabinaM
 *
 */
class FreqWordsTest {
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
	void givenFileWhenReadingLinesInFile_ThenCorrect() {
		
	}
	@Test
	public void testFileNameIsEmptyString_GetLongestWords() {
	 
	    assertThrows(NoSuchFileException.class, () -> {
	    	FreqWords.getLongestWords("", "");
     	   
	    });	   
	}

}
