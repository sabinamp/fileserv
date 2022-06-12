/**
 * 
 */
package com.aozora.fileserv.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aozora.fileserv.model.WordFreqPair;

/**
 * @author SabinaM
 *
 */
@ExtendWith(MockitoExtension.class)
public class FileContentServiceTest {
	 @InjectMocks
	 private FileContentService fileContentService;
	
	private String fileName;
	private String directoryPath;
	private Map<Integer, Set<String>> longestWordsPerLine = new TreeMap<Integer,Set<String>>();
	Map<Integer, Set<WordFreqPair>> freqWords = new TreeMap<Integer,Set<WordFreqPair>>();
	/**
	 * [
    "the current line number: 1. The 2 longest words are: [PathMatcher, implementation]",
    "the current line number: 4. The 2 longest words are: [PathMatcher, interface]"
]
	 */
	public FileContentServiceTest() {
		// TODO Auto-generated constructor stub
	}
	
	@BeforeEach
	void setUp() throws Exception {
			fileName= "PathMatcher.txt";
			directoryPath="C:/workspace_sts4-14/fileserv/bin/main/static/uploads/";
			setUpTestData();
			
	}
	
	private void setUpTestData() {
		/*
		 * greatestLength: 14 secondGreatestLength: 11
		 * current line number: 1. Added the words: [PathMatcher, implementation] 
		 * greatestLength: 11 secondGreatestLength: 9 
		 * current line number: 4. Added the words: [PathMatcher, interface] current
		 * line number: 5- empty line
		 */
		
		Set<String> longestW1= new HashSet<>();
		longestW1.add("PathMatcher");
		longestW1.add("implementation");
		longestWordsPerLine.put(1, longestW1);
		//line 2 and 3 are empty lines
		Set<String> longestW2= new HashSet<>();
		longestW2.add("PathMatcher");
		longestW2.add("interface");
		longestWordsPerLine.put(4, longestW2);
		
		/*
		 * [ "[branch] have the frequency 1", "[coding, I] have the frequency 2",
		 * "[house] have the frequency 7",
		 * "[Christmas, gardening, garden] have the frequency 9",
		 * "[home] have the frequency 14", "[love] have the frequency 15" ]
		 */
		Set<WordFreqPair> freq1= new HashSet<>();
		freq1.add(new WordFreqPair("branch", 1));	
		freqWords.put(1, freq1);
		
		Set<WordFreqPair> freq2= new HashSet<>();
		freq2.add(new WordFreqPair("coding", 2));
		freq2.add(new WordFreqPair("I", 2));
		freqWords.put(2, freq2);
		
		Set<WordFreqPair> freq3= new HashSet<>();
		freq3.add(new WordFreqPair("house", 7));		
		freqWords.put(7, freq3);
		
		Set<WordFreqPair> freq4= new HashSet<>();
		freq4.add(new WordFreqPair("home", 14));		
		freqWords.put(14, freq4);
		
		Set<WordFreqPair> freq5= new HashSet<>();
		freq5.add(new WordFreqPair("love", 15));		
		freqWords.put(15, freq5);
		
		//[Christmas, gardening, garden]
		Set<WordFreqPair> freq6= new HashSet<>();
		freq6.add(new WordFreqPair("Christmas", 9));	
		freq6.add(new WordFreqPair("gardening", 9));
		freq6.add(new WordFreqPair("garden", 9));
		freqWords.put(9, freq6);
		
	}

	@Test
	void givenFileWhenGetFirstNFreqWords_ThenCorrect() {

		
		Map<Integer, Set<WordFreqPair>> result;
		try {
			result = fileContentService.getFirstNFreqWords("words.txt", directoryPath,10);
		
		assertTrue(freqWords.size() == result.size());
		assertNotEquals(0, result.size(), "The result cannot be 0");
		assertEquals(freqWords.get(15), result.get(15));
		assertNotEquals(0, result.get(15).size(), "The result cannot be 0");
		assertEquals(freqWords.get(14), result.get(14));
		assertNotEquals(0, result.get(14).size(), "The result cannot be 0");
		assertEquals(freqWords.get(1).size(), result.get(1).size());
		assertNotEquals(0, result.get(1).size(), "The result cannot be 0");
		assertEquals(freqWords.get(7).size(), result.get(7).size());
		assertNotEquals(0, result.get(7).size(), "The result cannot be 0");
		assertEquals(freqWords.get(9).size(), result.get(9).size());
		} catch (NoSuchFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	 

	@Test
	void givenFileWhenGettingLongestWordsPerLine_ThenCorrect() {
		Map<Integer, Set<String>> result;
		try {
			result = fileContentService.getLongestWords(fileName, directoryPath);
		
		assertTrue(longestWordsPerLine.size()== result.size());
		assertNotEquals(0, result.size(), "The result cannot be 0");
		assertEquals(longestWordsPerLine.toString(), result.toString());
		assertTrue(longestWordsPerLine.get(4).size()== result.get(4).size());
		} catch (NoSuchFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
