/**
 * 
 */
package com.aozora.fileserv.service;

import java.nio.file.NoSuchFileException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.aozora.fileserv.model.WordFreqPair;

/**
 * @author SabinaM
 *
 */
public interface FileContentServI {
	static final String DELIMITER = "\\s*,\\s*|,\\s+|\\s+\\s*|\\.\\s*|\\?\\s*|\\!\\s*|\\:\\s*";
	
	TreeMap<Integer, Set<WordFreqPair>> getFirstNFreqWords(String filename, String directoryPath, int n) throws NoSuchFileException;
	
	Map<Integer, Set<String>> getLongestWords(String filename, String directoryPath) throws NoSuchFileException;
}
