package com.aozora.fileserv.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aozora.fileserv.exception.FileServiceExceptionHandler;
import com.aozora.fileserv.model.WordFreqPair;

public class FreqWords {

 //private static Map<String, Integer> freqResult;
	 
	private static final Logger logger = LoggerFactory.getLogger(FreqWords.class);
	 //count string frequencies in a list
	 static Map<String, Integer> count(List<String> wordList){
	        int n = wordList.size();
	        Map<String, Integer> countMap= new HashMap<>();

	        for (String s : wordList) {
	            if(countMap.containsKey(s)){
	                Integer c = countMap.get(s);
	                countMap.put(s, ++c);
	            }
	            else {
	                countMap.put(s, 1);
	            }
	        }
	        return countMap;
	 }
	 
	 static Map<String, Integer> getFrequencies(String fileName, String directoryPath) throws IOException, URISyntaxException{
	        Map<String, Integer> result = new HashMap<>();
	        Pattern pattern = Pattern.compile(FileContentServI.DELIMITER);
	        
	           List<String> lines = FileUtils.getLinesInFile(fileName,directoryPath);
	           for(String line: lines){
	               System.out.println("counting frequencies for the following line: "+line);
	               
	               Map<String, Integer> result1= count(Arrays.asList(line.split(FileContentServI.DELIMITER)));

	               for(Map.Entry<String, Integer> entry: result1.entrySet()){
	                   String key =entry.getKey();
	                  Integer val= result.get(key);
	                  if(val == null){
	                      val = 0;

	                  }
	                  result.put(key, val+entry.getValue());
	               }
	           }	           
	            return result;
	        
	 }
	 
	 static TreeMap<Integer,Set<String>> getWordSetByFrequency(String filename, String directoryPath) throws IOException, URISyntaxException{
		
	        TreeMap<Integer, Set<String>> wordFreqResult = new TreeMap<Integer, Set<String>>(Collections.reverseOrder());
	        
	        Map<String, Integer> freqResult = getFrequencies(filename, directoryPath);
	        
	        	        
	        for( String each: freqResult.keySet()) {
	            Set<String> wordList = null;
	            Integer freq = freqResult.get(each);
	            wordList = wordFreqResult.get(freq);
	            if (wordList == null || wordList.size() == 0) {
	                wordList = new HashSet<>();
	            }
	            if(!each.isEmpty()) {
	            	wordList.add(each.trim());
	            }
	           if(wordList.size() > 0) {
	        	   wordFreqResult.put(freq, wordList);
	           }
	            
	        }
	        wordFreqResult.entrySet().forEach(w->System.out.println(""+w.getKey()+""+w.getValue()));
	        return wordFreqResult;
	 }

	  /*
	   * @param filename - file name
	   * @param directoryPath - path of the directory were the file given is located
	   * 
	   * For a given input text file return for each line of the file the longest 2 words.
	   * In case there are more than 2 words with the same length on a line, filter on
	   * the most frequent 2 words from the whole document, these shall be returned.
	   */
	  static Map<Integer, Set<String>> getLongestWords(String fileName, String directoryPath) throws IOException, URISyntaxException{
		  
		  Map<String, Integer> freqResult = getFrequencies(fileName, directoryPath);
	        
	        //line number and the 2 longest words or frequent words
	        Map<Integer, Set<String>> resultLines = new HashMap<>();

	        
	        Pattern pattern = Pattern.compile(FileContentServI.DELIMITER);
	        try {
	            List<String> lines = FileUtils.getLinesInFile(fileName, directoryPath);
	            int currentLineNb=0;
	            for(String line: lines){
	               if(line.length()!= 0) {
	            	   	             
	                currentLineNb++;
	                Map<String, Integer> freqLineResult= count(Arrays.asList(line.split(FileContentServI.DELIMITER)));
	                TreeMap<Integer, TreeSet<String>> longestWords = getLongestWordsInLine(freqLineResult);
	                if(longestWords != null) {
	                	Set<String> longest2WordsSetPerLine = handleMoreThan2StringsSameLength(longestWords, freqResult);
	                	if(longest2WordsSetPerLine.size() >0) {
	                		resultLines.put(currentLineNb,longest2WordsSetPerLine);
	                		System.out.println("current line number: "+currentLineNb +". Added the words: "+longest2WordsSetPerLine);
	                		logger.debug("current line number: "+currentLineNb +". Added the words: "+longest2WordsSetPerLine);
	                	}else {
	                		logger.debug("current line number: "+currentLineNb + "- empty line");
	                		System.out.println("current line number: "+currentLineNb + "- empty line");
	                	}
	                	
	                	
	                }
	               }else {
	            	   currentLineNb++;
	               }	               	                
	            }

	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return resultLines;
	    }
	    
	  // get a map of the word length and the corresponding words of the same length in a set
	  static TreeMap<Integer, TreeSet<String>> getLongestWordsInLine(Map<String, Integer> freqLineResult){
	        TreeMap<Integer, TreeSet<String>> lengthWordList = new TreeMap<>();
	        
	        //map of string, length
	        TreeMap<String, Integer> wordsMap = new TreeMap<>();
	        freqLineResult.keySet().forEach( entry-> wordsMap.put( entry, entry.length()));
	        for( String each: wordsMap.keySet()){
	            TreeSet<String> sameLengthWords= null;
	            Integer sameLength = wordsMap.get(each);
	            sameLengthWords = lengthWordList.get(sameLength);
	            if (sameLengthWords == null || sameLengthWords.size() <= 0) {
	                sameLengthWords = new TreeSet<>();
	            }
	            sameLengthWords.add(each);

	            lengthWordList.put(sameLength,sameLengthWords );
	        }
	        return lengthWordList;
	    }

	    /*
	     * @param n - number of frequency entries in the returned map
	     * @param filename - file name
	     * @param directoryPath - path of the directory were the file given is located
	     * 
	     * @return the most frequent n words, the first n entries in a TreeMap of <integer frequency, set of strings>
	     * in reverse order, which is the reverse of the natural ordering of the objects
	     *	    
	     */
	  static TreeMap<Integer, Set<WordFreqPair>> getFirstNFreq(int n, String filename, String directoryPath){
		  Map<Integer, Set<String>> freqMap = null;
		  TreeMap<Integer, Set<WordFreqPair>> result = new TreeMap<>(Collections.reverseOrder());
		try {
			freqMap = getWordSetByFrequency(filename, directoryPath);
						

			int counter=0;
			for(Entry<Integer, Set<String>> each: freqMap.entrySet() ) {
				Set<WordFreqPair> wordFreqPairs= new HashSet<>();
				Set<String> currentWordSet = each.getValue();
				Integer currentFrequency =  each.getKey();
				
				for( String word: currentWordSet ) {
					wordFreqPairs.add(new WordFreqPair(word,currentFrequency));					
					counter++;
					if(counter == n) {
						break;
					}
				}
				result.put(currentFrequency, wordFreqPairs);
				if(counter == n) {
					break;
				}
							
			}					
			 
		} catch (IOException e) {			
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	        
	       //the frequencies in reverse natural order			
	        return result;

	   }
	    
	  /**
	   *  if there are  more than 2 words of the same length on a line
	   *  filter by frequency of the string
	   * @param longestWordsMap for a given line
	   * @param freqResult for the whole file
	   * 
	   * @return longest 2 words or if there are  more than 2 words of the same length on a line
	   *  2 words filtered by string frequency in the whole file
	   */	  
	  static Set<String> handleMoreThan2StringsSameLength(TreeMap<Integer, TreeSet<String>> longestWordsMap,
			 Map<String, Integer> freqResult){
		   Set<String> result= new HashSet<>();
		   List<WordFreqPair> lineWords = new ArrayList<>();
	       
		    
	        NavigableSet<Integer> descendingKeySet =  longestWordsMap.descendingKeySet();	 
	        int keySetSize = descendingKeySet.size();
	      //the given longestWordMap size should be greater than 0 as the line should not be empty
	        if(keySetSize == 0) {
	        	throw new IllegalArgumentException("the given map should not be empty");
	        }
	       
	        Integer[] descendingKeyArray= new Integer[keySetSize];
	        if( keySetSize == 1) {
            	logger.debug("all words have the same length");
            	TreeSet<String> wordSet= longestWordsMap.get( descendingKeySet.first());
            	   int c=0;
            	   for(String each:  wordSet) {
            		   	result.add(each);
            	    	c++;
            	    	if(c == 2) {
            	    		break;
            	    	}
            	    };
            	  
            	return result;
            }
	        if(keySetSize > 1) {
	        	        	
		        int index=0;
		        for(Integer each : descendingKeySet) {
		        	descendingKeyArray[index]=each;
		        	index++;
		        }
		    
	           Integer greatestLength = descendingKeyArray[0];
	           Integer secondGreatestLength= descendingKeyArray[1];
	           System.out.println(" greatestLength: "+greatestLength +" secondGreatestLength: "+secondGreatestLength);
	           logger.debug("greatestLength: "+greatestLength +" secondGreatestLength: "+secondGreatestLength);
	           
	           Set<String> wList = longestWordsMap.get(greatestLength);
	     	    int wordsOfSameLengthNb= (wList != null)?  wList.size() : 0;
	   	        logger.debug(" wordsOfSameLengthNb: "+ wordsOfSameLengthNb);
	   	     if( wordsOfSameLengthNb > 2){
		            // if there are  more than 2 words of the same length on a line, get frequency of the string
		            for( String e: wList){
		                lineWords.add(new WordFreqPair(e, freqResult.get(e)));
		            }
		            Collections.sort(lineWords, new Comparator<WordFreqPair>() {
		                @Override
		                public int compare(WordFreqPair o1, WordFreqPair o2) {
		                    return o1.getFrequency() - ((WordFreqPair) o2).getFrequency();
		                }
		            });
		            String currentWord = lineWords.get(wordsOfSameLengthNb - 1).getWord();
		            String nextWord = lineWords.get(wordsOfSameLengthNb - 2).getWord();
		            System.out.println("added the word: "+currentWord);
		            result.add(currentWord);
		            result.add( nextWord);
		            System.out.println("added the word: "+currentWord);
		        }else if (wordsOfSameLengthNb == 2){
		            //add the longest 2 words in the current line
		            result.addAll(wList);
		        }else if(wordsOfSameLengthNb == 1){
		            result.addAll(wList);
		            if(secondGreatestLength != null) {
		            	 TreeSet<String> lastw = longestWordsMap.get(secondGreatestLength);
		 	            if(lastw != null) {
		 	            	result.add(lastw.first());
		 	            }
		            }
		           
			        	            

		        }
	           
	        }        	        
	      
	       
	        return result;
	    }


}
