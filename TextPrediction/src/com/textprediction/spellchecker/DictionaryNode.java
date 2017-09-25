package com.textprediction.spellchecker;

import java.util.LinkedList;

/**
 * A Custom Trie Node
 * Modified 
 */
public class DictionaryNode {
	private char data; //The character on this node
	private LinkedList<DictionaryNode> children; //The list of children Nodes
	private DictionaryNode parent;
	private String word;
	
	public DictionaryNode() {
		this.children = new LinkedList<DictionaryNode>();
		this.parent = null;
	}
	
	public DictionaryNode(char data, DictionaryNode parent) {
		this.data = data;
		this.children = new LinkedList<DictionaryNode>();
		this.parent = parent;
	}
	
	/**
	 * Checks to see if any of this Node's children match the char
	 * @param data the char to match
	 * @return the child Node or null if none found
	 */
	public DictionaryNode findChildNode(char data) {
		if(this.children != null) {
			for(int i = 0; i < this.children.size(); i++) {
				DictionaryNode child = this.children.get(i);
				if(child.data == data) {
					return child;
				}
			}
		}
		return null;
	}
	
	/**
	 * A simple way of seeing a list of characters that this node calls children
	 * @return the String to print
	 */
	public String toString() {
		String toReturn;
		if(this.children.size() > 0) {
			toReturn = "Node "+this.data+" has children:";
			for(int i = 0; i < this.children.size(); i++) {
				char child = this.children.get(i).data;
				toReturn += " "+child;
			}
			if(this.word != null) toReturn += " and gives word: "+this.word;
			toReturn += "\n";
			for(int i = 0; i < this.children.size(); i++) {
				toReturn += this.children.get(i).toString();
			}
		} else {
			toReturn = "Node "+this.data+" has no children.";
			if(this.word != null) toReturn += " and gives word: "+this.word;
			toReturn += "\n";
		}
		return toReturn;
	}
	
	public String parents() {
		String toReturn = "";
		DictionaryNode looking = this;
		while(looking != null) {
			toReturn = looking.data + " " + toReturn;
			looking = looking.parent;
		}
		return toReturn;
	}
	
	public LinkedList<DictionaryNode> getChildren() {
		return this.children;
	}
	
	public char getData() {
		return this.data;
	}
	
	public String getWord() {
		return this.word;
	}
	
	public void setWord(String word) {
		this.word = word;
	}
	
	public void setData(char data) {
		this.data = data;
	}
	
}
