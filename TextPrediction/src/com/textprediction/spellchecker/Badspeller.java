package com.textprediction.spellchecker;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class Badspeller {

	private static int MISTAKES = 4;
	private static boolean DEBUG = false;
	private static int BADWORDS = 2;
	private static Random gen = new Random(System.currentTimeMillis());
	private static ArrayList<String> vowels;
	
	/**
	 * @param args[0] Debug mode (uses a different dictionary)
	 * @param args[1] The number of words to mess up per line of the file
	 * @param args[2] The number of random mistakes to make per word
	 */
	public static void main(String[] args) {
		if(args.length > 0) {
			DEBUG = Boolean.parseBoolean(args[0]);
			if(args.length > 1) {
				BADWORDS = Integer.parseInt(args[1]);
				if(args.length > 2) {
					MISTAKES = Integer.parseInt(args[1]);
				}
			}
		}
		vowels = new ArrayList<String>();
		vowels.add("a");
		vowels.add("e");
		vowels.add("o");
		vowels.add("i");
		vowels.add("u");
		
		if(DEBUG) {
			generateWords("/home/user/workspace/spellchecker/src/words.txt");
		} else {
			generateWords("/usr/share/dict/words");
		}
	}
	
	public static void generateWords(String fileName) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(fileName))));
			String line;
			while ((line = br.readLine()) != null) {
				if(line.length() > 0) {
					for(int i=0; i<BADWORDS; i++) {
						misspellWord(line);
					}
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("File not found: "+fileName);
		} catch (IOException e) {
			System.err.println("IOError: "+e.getMessage());
		} catch (NullPointerException e) {
			System.err.println("File error: "+e.getMessage());
		}
	}
	
	public static void misspellWord(String word) {
		word = changeVowel(word); //make a vowel change first
		for(int i = 0; i < MISTAKES-1; i++) {
			word = addMistake(word);
		}
		System.out.println(word);
	}
	
	public static String addMistake(String word) {
		switch(gen.nextInt(2)) {
			case 0:
				word = changeCapitalization(word);
				break;
			case 1:
				word = addRepeatedLetters(word);
				break;
		}
		return word;
	}
	
	public static String changeVowel(String word) {
		int numOfVowels = countVowels(word);
		if(numOfVowels > 0) {
			int toChange = gen.nextInt(numOfVowels);
			int vowel = gen.nextInt(vowels.size());
			
			int count = 0;
			for(int i = 0; i < word.length()-1; i++) {
				for(int j = 0; j < vowels.size()-1; j++) {
					if((vowels.get(j)).equals(String.valueOf(word.charAt(i)).toLowerCase())) { //if it's a vowel
						if(count == toChange) {
							word = Trie.replaceLetter(word, i, vowels.get(vowel));
						} else {
							count++;
						}
					}
				}
			}
		}
		return word;
	}
	
	private static int countVowels(String word) {
		int count = 0;
		for(int i = 0; i < word.length(); i++) {
			for(int j = 0; j < vowels.size(); j++) {
				if(vowels.get(j).equals(String.valueOf(word.charAt(i)).toLowerCase())) { //if it's a vowel
					count++;
				}
			}
		}
		return count;
	}

	public static String changeCapitalization(String word) {
		int pos = gen.nextInt(word.length());
		int len = gen.nextInt(word.length()-pos+1);
		if(pos == 0) {
			word = word.substring(pos, pos+len).toUpperCase() + word.substring(len);
		} else if(pos + len == word.length()-1) {
			word = word.substring(0, pos) + word.substring(pos).toUpperCase();
		} else {
			word = word.substring(0, pos) + word.substring(pos, pos+len).toUpperCase() + word.substring(pos+len);
		}
		return word;		
	}
	
	public static String addRepeatedLetters(String word) {
		int pos = gen.nextInt(word.length());
		if(pos == 0) {
			word = word.charAt(0) + word;
		} else if(pos == word.length()-1) { //repeat last letter
			word = word + word.charAt(pos);
		} else {
			word = word.substring(0, pos+1) + word.charAt(pos) + word.substring(pos+1, word.length());
		}
		return word;
		
	}
	
}
