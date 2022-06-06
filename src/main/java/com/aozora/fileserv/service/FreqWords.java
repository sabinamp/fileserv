package com.aozora.fileserv.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import com.aozora.fileserv.model.WordFreqPair;

public class FreqWords {

 private static Map<String, Integer> freqResult;
	 
	 
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
	 
	 static Map<String, Integer> getFrequencies(String fileName) throws IOException, URISyntaxException{
	        Map<String, Integer> result = new HashMap<>();
	        Pattern pattern = Pattern.compile(FileContentServI.DELIMITER);
	        try {
	            List<String> lines = FileUtils.getLinesInFile(fileName);
	           for(String line: lines){
	               System.out.println("counting frequencies for the line: "+line);
	               System.out.println("number of words in the line: "+Arrays.asList(line.split(FileContentServI.DELIMITER)).size());
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
	        } catch (IOException e) {
	            e.printStackTrace();
	            throw new IOException();
	        }
	 }
	 
	 static Map<Integer,Set<String>> getWordSetByFrequency(String filename) throws IOException, URISyntaxException{
		 //ClassPathResource fileResource = new ClassPathResource(filename);
	        TreeMap<Integer, Set<String>> wordFreqResult = new TreeMap<Integer, Set<String>>();
	        if(freqResult == null) {
	        	freqResult = getFrequencies(filename);
	        }
	        	        
	        for( String each: freqResult.keySet()) {
	            Set<String> wordList = null;
	            Integer freq = freqResult.get(each);
	            wordList = wordFreqResult.get(freq);
	            if (wordList == null || wordList.size() == 0) {
	                wordList = new HashSet<>();
	            }
	            wordList.add(each);
	            wordFreqResult.put(freq, wordList);
	        }
	        return wordFreqResult;
	 }

	 /*
	   * For a given input text file return for each line of the file the longest 2 words.
	   * In case there are more than 2 words with the same length on a line, filter on
	   * the most frequent 2 words from the whole document, these shall be returned.
	   */
	  static Map<Integer, Set<String>> getLongestWords(String fileName) throws IOException, URISyntaxException{
		  if(freqResult == null) {
	        	freqResult = getFrequencies(fileName);
	        }
	        //line number and the 2 longest words or frequent words
	        Map<Integer, Set<String>> resultLines = new HashMap<>();

	        Set<String> longest2WordsSetPerLine= null;
	        Pattern pattern = Pattern.compile(FileContentServI.DELIMITER);
	        try {
	            List<String> lines = FileUtils.getLinesInFile(fileName);
	            int currentLineNb=0;
	            for(String line: lines){
	               
	                currentLineNb++;
	                Map<String, Integer> freqLineResult= count(Arrays.asList(line.split(FileContentServI.DELIMITER)));
	                TreeMap<Integer, TreeSet<String>> longestWords = getLongestWordsInLine(freqLineResult);

	                longest2WordsSetPerLine = handleMoreThan2StringsSameLength(longestWords);
	                resultLines.put(currentLineNb,longest2WordsSetPerLine);
	                System.out.println("current line number: "+currentLineNb +". Added the words: "+longest2WordsSetPerLine);
	            }

	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return resultLines;
	    }
	    
	  // get a map of the word length and the corresponding words of the same length in a set
	  private static TreeMap<Integer, TreeSet<String>> getLongestWordsInLine(Map<String, Integer> freqResult){
	        TreeMap<Integer, TreeSet<String>> lengthWordList = new TreeMap<>();

	        TreeMap<String, Integer> wordsMap = new TreeMap<>();
	        freqResult.keySet().forEach( entry-> wordsMap.put( entry, entry.length()));
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
	     * returns the first n entries in a TreeMap of frequency and set of strings
	     * @input n -number of frequency entries in the returned map
	     */
	  static TreeMap<Integer, Set<String>> getFirstNFreq(Map<Integer, Set<String>> freqMap, int n){
	        TreeMap<Integer, Set<String>> result = new TreeMap<>();
	        for( int i= 0; i < n; i++){
	            freqMap.entrySet()
	                    .forEach( entry-> result.put(entry.getKey(), entry.getValue()));
	        }
	       //the frequencies in natural order
	        return result;

	   }
	    
	  // if there are  more than 2 words of the same length on a line
	  //filter by frequency of the string
	  private static Set<String> handleMoreThan2StringsSameLength(TreeMap<Integer, TreeSet<String>> longestWordsMap){
	        Set<String> result= new HashSet<>();
	       
	        List<WordFreqPair> lineWords = new ArrayList<>();
	        Set<String> wList = longestWordsMap.lastEntry().getValue();
	        int wordsOfSameLengthNb= (wList != null)?  wList.size() : 0;
	        System.out.println(" wordsOfSameLengthNb: "+ wordsOfSameLengthNb);
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
	            longestWordsMap.pollLastEntry();
	            TreeSet<String> lastw = longestWordsMap.lastEntry().getValue();
	            result.add(lastw.first());

	        }
	        return result;
	    }


}
