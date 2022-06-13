package com.aozora.fileserv.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.NoSuchFileException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import com.aozora.fileserv.model.WordFreqPair;

@Service
public class FileContentService implements FileContentServI {

	public FileContentService() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public TreeMap<Integer, Set<WordFreqPair>> getFirstNFreqWords(String filename, String directoryPath, int n) throws NoSuchFileException {
		if(filename == null || filename.length() <1 || directoryPath == null || directoryPath.length() < 1) {
			throw new NoSuchFileException("the filename or directoryPath is null");
		}	
		 TreeMap<Integer, Set<WordFreqPair>> result = FreqWords.getFirstNFreq(n, filename,directoryPath);
		 return result;
	}

	@Override
	public Map<Integer, Set<String>> getLongestWords(String filename, String directoryPath) throws NoSuchFileException {
		Map<Integer, Set<String>> result = null;
		if(filename == null ||filename.length() <5 || directoryPath == null || directoryPath.length() < 8) {
			throw new NoSuchFileException("the filename or directoryPath is null");
		}
		try {
			result= FreqWords.getLongestWords(filename, directoryPath);
		} catch (IOException e) {		
			e.printStackTrace();
		} catch (URISyntaxException e) {			
			e.printStackTrace();
			throw new NoSuchFileException("the file name or directoryPath is wrong");
		}
		return result;
	}

}
