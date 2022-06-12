/**
 * 
 */
package com.aozora.fileserv.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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
	private  Map<String, Integer> freqResult;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		//line 1: I, love, coding, I, love, coding, branch, love, home, Christmas, garden, gardening, house, home, love
		//line 2: love, home, Christmas, garden, gardening, house, home
		//line 3: love, home, Christmas, garden, gardening, house,
		//line 4: love, home, Christmas, garden, gardening
		//line 5: love, home, Christmas, garden, gardening, house, home, love
		//line 6: love, home, Christmas, garden, gardening,
		//line 7: love, home, Christmas, garden, gardening, house,
		//line 8: love, home, Christmas, garden, gardening, house, home, love
		//line 9: love, home, Christmas, garden, gardening, house, home, love
		fileName= "words.txt";
		directoryPath="C:/workspace_sts4-14/fileserv/bin/main/static/uploads/";
		freqResult = new HashMap<>();
		/*
		 * [ { "the frequency: 15": "[love]" }, { "the frequency: 14": "[home]" }, {
		 * "the frequency: 9": "[Christmas, gardening, garden]" }, { "the frequency: 7":
		 * "[house]" }, { "the frequency: 2": "[coding, I]" }, { "the frequency: 1":
		 * "[branch]" } ]
		 */
		freqResult.put("love", 15);
		freqResult.put("home", 14);
		freqResult.put("Christmas", 9);
		freqResult.put("gardening", 9);
		freqResult.put("garden", 9);
		freqResult.put("house", 7);
		freqResult.put("coding", 2);
		freqResult.put("I", 2);
		freqResult.put("branch", 1);
	}


	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void givenLineWhenGettingLongestWordsInLine_ThenCorrect() {
		//line 2: love, home, Christmas, garden, gardening, house, home
		Map<String, Integer> freqLineResult = new HashMap<>();
		freqLineResult.put("love", 1);
		freqLineResult.put("home", 2);
		freqLineResult.put("Christmas", 1);
		freqLineResult.put("garden", 1);
		freqLineResult.put("gardening", 1);
		freqLineResult.put("house", 1);
		freqLineResult.put("home", 1);
		//current line number: 2 -greatestLength: 9 secondGreatestLength: 6
		//longest words -length 9 - Christmas, gardening
		TreeMap<Integer, TreeSet<String>> expected = new TreeMap<Integer, TreeSet<String>>();
		TreeSet<String> wordS1 = new TreeSet<>();
		wordS1.add("Christmas");
		wordS1.add("gardening");
		expected.put(9, wordS1);
		TreeMap<Integer, TreeSet<String>> actual= FreqWords.getLongestWordsInLine(freqLineResult);
		
		assertEquals(expected.get(9), actual.get(9));
		assertTrue(actual.get(9).size() == 2);
		assertTrue(actual.containsKey(9));
		assertEquals(wordS1, actual.get(9));
		
	}
	@Test
	public void testFileNameIsEmptyString_GetLongestWords() {
	 
	    assertThrows(NoSuchFileException.class, () -> {
	    	FreqWords.getLongestWords("", "");
     	   
	    });	   
	}
	
	@Test
	public void givenLine2Map_testHandleMoreThan2StringsSameLength() {
		TreeMap<Integer, TreeSet<String>> longestWordsMap= new TreeMap<>();
		//current line number: 2 -greatestLength: 9 secondGreatestLength: 6
		//current line: "love, home, Christmas, garden, gardening, house, home"
		TreeSet<String> wordS1 = new TreeSet<>();
		wordS1.add("Christmas");
		wordS1.add("gardening");
		longestWordsMap.put(9, wordS1);
		
		Set<String> expected = wordS1;
		
		Set<String> actual = FreqWords.handleMoreThan2StringsSameLength(longestWordsMap, freqResult);
     	assertEquals(expected.size(), actual.size());
     	assertTrue(actual.contains("gardening"));
		
	}
	
	@Test
	public void givenEmptyMap_testHandleMoreThan2StringsSameLength() {
		TreeMap<Integer, TreeSet<String>> longestWordsMap= new TreeMap<>();
		//current line number: 2 -greatestLength: 9 secondGreatestLength: 6
		//current line: "love, home, Christmas, garden, gardening, house, home"
		TreeSet<String> wordS1 = new TreeSet<>();
		
				
		Set<String> expected = wordS1;
		assertThrows(IllegalArgumentException.class, () -> {
			Set<String> actual = FreqWords.handleMoreThan2StringsSameLength(longestWordsMap, freqResult);
     	   
	    });	   
		
	}

}
