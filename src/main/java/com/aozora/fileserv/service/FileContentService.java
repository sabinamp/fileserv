package com.aozora.fileserv.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

@Service
public class FileContentService implements FileContentServI {

	public FileContentService() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public TreeMap<Integer, Set<String>> getFirstNFreqWords(String filename, int n) {
		 Map<Integer, Set<String>> res = null;
		try {			 
			res = FreqWords.getWordSetByFrequency(filename);
		} catch (IOException e) {			
			e.printStackTrace();
		} catch (URISyntaxException e) {			
			e.printStackTrace();
		}
		 TreeMap<Integer, Set<String>> result = FreqWords.getFirstNFreq(res, n);
		 return result;
	}

	@Override
	public Map<Integer, Set<String>> getLongestWords(String filename) {
		Map<Integer, Set<String>> result = null;
		try {
			result= FreqWords.getLongestWords(filename);
		} catch (IOException e) {		
			e.printStackTrace();
		} catch (URISyntaxException e) {			
			e.printStackTrace();
		}
		return result;
	}

}
