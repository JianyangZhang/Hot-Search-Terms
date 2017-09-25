package com.textprediction.spellchecker;

import java.io.*;
import java.util.*;

/**
 * A custom Spell Checker program which uses a Trie as the data structure
 * 
 * The executable builds it's dictionary by reading in a defined dictionary file,
 * or "/usr/share/dict/words" as default, and then prompts the user to type a word.
 * Based on the input word, 3 rules are checked to see if a match is found or a
 * suggestion can be made. If no word is found, then NO SUGGESTION is printed.
 * 
 * The current rules this software checks for are:
 * 1. ImPROper cAPITalIZAioN. The entire dictionary is treated as lower case and all
 *    inputs are suggested as lowercase.
 * 2. Misplacad vuwals. If the input word is not found, the vowels A,E,I,O,U are
 *    substituted until a suggestion is found. Through a brute force method, all
 *    vowel combinations are attempted. For example, in the input "tost", "test",
 *    "tast", "tist", "tust" are all attempted words. While "toast" tries "toest",
 *    "toust", "teost", "teust", etc...
 * 3. Reeeepeeeated Letttters. Any repeated letters are removed and the word is
 *    checked again.
 *    
 * Any combination of these rules is acceptable. Capitalizations can occur on the same
 * section of the string as repeated letters or incorrect vowels.
 * 
 */
public class Spellchecker {
	private static final char PROMPT = '>'; 
	private static Trie dict = new Trie();
	private static Scanner input = new Scanner(System.in);
	private static boolean DEBUG = false;
	
	/**
	 * The command line program which prompts a user for words, read in using new lines
	 * @param args[0] Debug Mode (true or false)
	 */
	public static void main(String[] args) {
		if(args.length > 0) DEBUG = Boolean.parseBoolean(args[0]);
		String word, found;
		
		if(DEBUG) {
			buildDictionary("/home/user/workspace/spellchecker/src/words.txt");
			printDictionary();
		} else {
			buildDictionary("/usr/share/dict/words");
		}
	
		try {
			while(true) {
				word = getWord().toLowerCase();
				found = dict.find(word);
				if(DEBUG) System.out.print("Trying: "+word+". Got: ");
				if(found != null) {
					System.out.println(found);
				} else {
					System.out.println("NO SUGGESTION");
				}
			}
		} catch(Exception e) {
			//ignore fatals, when terminating loop
		}
	}

	/**
	 * Prompt and get the next word from the user
	 * @return the word entered by the user
	 */
	private static String getWord() {
		System.out.print(PROMPT+"");
		return input.next();
	}
	
	/**
	 * Builds a dictionary file into a Trie
	 * @param fileName the dictionary file
	 */
	private static void buildDictionary(String fileName) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(fileName))));
			String line;
			while ((line = br.readLine()) != null) {
				dict.insert(line.toLowerCase());
				//System.out.println("Added to dictionary: "+line);
			}
		} catch (Exception e) { //File not found, rebuild dictionary with default dictionary file
			System.err.println(e.getMessage());
			//System.err.println("File not found: "+fileName);
			//System.err.println("Using standard system dictionary: /usr/share/dict/words");
			//buildDictionary("/usr/share/dict/words");
		}
	}
	
	private static void printDictionary() {
		System.out.println(dict.toString());
	}
	
}
