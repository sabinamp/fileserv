package com.aozora.fileserv.model;

import java.util.Objects;

public class WordFreqPair {

	 private String word;
	 private int frequency;

	/**
	 * 
	 */
	public WordFreqPair(String word, int frequency) {
		  this.word = word;
	      this.frequency = frequency;
	}
	
	public String getWord() {
        return word;
    }

    @Override
    public String toString() {
        return "WordFreqPair{" + word + 
                ", frequency=" + frequency +
                '}';
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordFreqPair that = (WordFreqPair) o;
        return /*frequency == that.frequency &&*/ word.equals(that.word);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word/*, frequency*/);
    }

}
