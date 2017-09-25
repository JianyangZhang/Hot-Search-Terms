package com.textprediction.spellchecker;

public class Trie {
	private DictionaryNode root;
	
	public Trie() {
		root = new DictionaryNode();
	}
	
	public void insert(String toAdd) {
		insert(root, toAdd, 0);
	}
	
	/**
	 * Insert a String into the Trie data structure.
	 * Inserts multiple strings for a single one containing vowels.
	 * Example: "best" will also insert:
	 * 	bost, bast, bist, bust, which will all point to "best" as the correct spelled word
	 * If "bust" is added later, it will overwrite the correct spelling of "best" to be "bust" 
	 * without the need to add extra nodes
	 * 
	 * @param toAdd the String to add to the Trie data structure
	 */
	private static void insert(DictionaryNode loc, String toAdd, int letterPos) {
		if(toAdd != null && toAdd.length() > 0) { //not a null or empty string
			if(letterPos == toAdd.length()) { //last character, set word
				loc.setWord(toAdd);
			} else {
				DictionaryNode currNode = loc, found, newlyCreated;
				char charToFind = toAdd.charAt(letterPos);
				if((found = currNode.findChildNode(charToFind)) == null) { //if the child node based on this current char doesn't exist, create one
					newlyCreated = new DictionaryNode(charToFind, currNode);
					currNode.getChildren().add(newlyCreated);
					insert(newlyCreated, toAdd, letterPos+1); //recursively continue creating trie
				} else { //if the node already did exist
					insert(found, toAdd, letterPos+1); //keep recursing
				}
			}
		}
	}
	
	/**
	 * Check to see if a String is in the Trie
	 * @param toFind the String to look for in the Trie (Case Sensitive)
	 * @return true if the String exists in the Trie
	 * @return false if the String is not marked as a word or does not exist
	 */
	public String find(String toFind) {
		String found;
		
		found = simpleFind(root, toFind, 0);
		if(found != null) {
			return found;
		} else {
			return regexFind(root, toRegex(toFind), 0);
		}
	}
	
	/**
	 * Recursively ooks for the exact match in the trie
	 * @param node
	 * @param toFind
	 * @param pos
	 * @return
	 */
	private static String simpleFind(DictionaryNode node, String toFind, int pos) {
		if((node.getWord() != null) && (node.getWord().matches(toFind))) return node.getWord(); //jump out as soon as we find a match
		if(node.getChildren() == null) return null;
		
		for(int i = pos; i < toFind.length(); i++) {
			for(int j = 0; j < node.getChildren().size(); j++) {
				if(node.getChildren().get(j).getData() == toFind.charAt(i)) {
					return simpleFind(node.getChildren().get(j), toFind, pos+1);
				}
			}
		}
		return null;
	}
	
	/**
	 * Recursively follows all possible paths given a RegEx statement
	 * Valid RegEx includes:
	 * 1. + for repeated characters 
	 * 2. [aeiou] for vowels
	 * 3. normal characters
	 * @param node
	 * @param toMatch
	 * @param pos
	 * @return
	 */
	private static String regexFind(DictionaryNode node, String toMatch, int pos) {
		String found;
		char tmpChar;
		
		if((node.getWord() != null) && (node.getWord().matches(toMatch))) return node.getWord(); //jump out as soon as we find a match
		if(node.getChildren() == null) return null;
		for(int i = pos; i < toMatch.length(); i++) {
			//System.out.print("Looking for: "+toMatch+". Looking at Position: "+i+". On Node: "+node.data+". Word stored: "+node.word+". Path is: "+node.parents()+". Children are:");
			//for(int j = 0; j < node.children.size(); j++) {
			//	System.out.print(" "+node.children.get(j).data);
			//}
			//System.out.print("\n");
			if(toMatch.charAt(i) == '[') { //a little trickery for vowels
				for(int j = 0; j < node.getChildren().size(); j++) {
					tmpChar = node.getChildren().get(j).getData();
					if(tmpChar == 'a' || tmpChar == 'e' || tmpChar == 'i' || tmpChar == 'o' || tmpChar == 'u') {
						found = regexFind(node.getChildren().get(j), toMatch, pos+7); //advance past the regex
						if(found != null) return found;
					}
				}
			} else if(toMatch.charAt(i) == '+') { //a little trickery for repeated characters
				for(int j = 0; j < node.getChildren().size(); j++) {
					if(node.getData() == node.getChildren().get(j).getData()) { //try repeated characters
						found = regexFind(node.getChildren().get(j), toMatch, pos);
						if(found != null) return found;
					}
				}
			} else { //just a normal character
				for(int j = 0; j < node.getChildren().size(); j++) {
					if(node.getChildren().get(j).getData() == toMatch.charAt(i)) {
						found = regexFind(node.getChildren().get(j), toMatch, pos+1);
						if(found != null) return found;
					}
				}
			}
		}
		return null;
	}
	
	private static String toRegex(String word) {
		boolean repeatFlag = false;
		//turn repeated characters into ".+" where . is the character
		for(int i = 0; i < word.length(); i++) {
			if((i < word.length()-1) && (word.charAt(i) == word.charAt(i+1))) {
				repeatFlag = true;
				word = replaceLetter(word, i+1, "");
				i = i-1; //recheck the position for another repeat
			} else if (repeatFlag) {
				word = replaceLetter(word, i, word.charAt(i)+"+");
				repeatFlag = false;
				i++;
			}
		}
		//turn vowels into "[aeiou]"
		for(int i = (word.length()-1); i >= 0; i--) {
			if(isVowel(word.charAt(i))) {
				word = replaceLetter(word, i, "[aeiou]");
			}
		}
		return word;
	}
	
	private static boolean isVowel(char toCheck) {
		if(toCheck == 'a' || toCheck == 'e' || toCheck == 'i' || toCheck == 'o' || toCheck == 'u') {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * This is a small helper function to replacing letters in a word.
	 * It is also used to delete letters by sending in an empty string in the place of a character in toAdd.
	 * @param word The word
	 * @param place The position (0-word.length()) to replace in the word
	 * @param toAdd The character to put in that place, or an empty string to delete a character
	 * @return the modified word
	 */
	public static String replaceLetter(String word, int place, String toAdd) {
		if(word.length() == 1) {
			if(place == 0) {
				return toAdd; //if we're replacing the first letter of a 1 letter word, just return the new letter
			}
		} else if(place == 0) {
			word = toAdd + word.substring(1); //replace the first character
		} else if(place == word.length()-1) {
			word = word.substring(0,word.length()-1) + toAdd; //replace the last character
		} else {
			word = word.substring(0, place) + toAdd + word.substring(place+1, word.length()); //replace a character in the middle of the word
		}
		return word;
	}
	

	public String toString() {
		return traverseTrieNBC(this.root);
	}
	
	/**
	 * Traverses the Trie childen before node
	 * @param start
	 */
	private static String traverseTrieCBN(DictionaryNode start) {
		String toReturn = "";
		if(start != null) {
			for(int i = 0; i < start.getChildren().size(); i++) {
				toReturn += traverseTrieCBN(start.getChildren().get(i));
			}
			if(start.getWord() != null) toReturn += start.getWord()+"\n";
		}
		return toReturn;
	}
	
	/**
	 * Traverses the Trie node before children
	 * @param start
	 */
	private static String traverseTrieNBC(DictionaryNode start) {
		String toReturn = "";
		if(start != null) {
			if(start.getWord() != null) toReturn += start.getWord()+"\n";
			for(int i = 0; i < start.getChildren().size(); i++) {
				toReturn += traverseTrieCBN(start.getChildren().get(i));
			}
		}
		return toReturn;
	}
	
}
