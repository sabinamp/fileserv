/**
 * 
 */
package com.aozora.fileserv.service;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author SabinaM
 *
 */
public interface FileContentServI {
	static final String DELIMITER = "\\s*,\\s*|,\\s+|\\s+|\\.\\s*|\\?\\s*|\\!\\s*";
	
	TreeMap<Integer, Set<String>> getFirstNFreqWords(String filename, String directoryPath, int n);
	
	Map<Integer, Set<String>> getLongestWords(String filename, String directoryPath);
}
