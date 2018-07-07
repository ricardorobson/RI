package com.ri.wse.utils;

public class No {
	String nome;
	private int numDoc;
	private int freq;
	private No next;
	private byte freqByte;
	private byte numDocByte;
	 
	public No(String nome, byte numDoc, byte freq){
		this.nome = nome;
		this.numDocByte = numDoc;
		this.freqByte = freq;
		this.next = null;
	}
	public No(int num, int freq){
		this.nome = null;
		this.numDoc = num;
		this.freq = freq;
		next = null;
	}
	public No(String nome, int num, int freq){
		this.nome = nome;
		this.numDoc = num;
		this.freq = freq;
		next = null;
	}
	public No(byte num, byte freq){
		this.freqByte = freq;
		this.numDocByte = num;
		next = null;
	}
	
	public String getNumDoc() {
		return this.numDoc+"";
	}

	public int getFreq() {
		return this.freq;
	}

	public No getNext() {
		return this.next;
	}
	
	public byte getFreqByte(){
		return this.freqByte;
	}
	
	public byte getNumDocByte(){
		return this.numDocByte;
	}
	
	public boolean hasNext(){
		if(next!=null)return true;
		return false;
	}
	
	public void setNext(No n){
		if(this.next==null) this.next = n;
		else this.next.setNext(n);
	}
	
	public No getLast(){
		if(this.next==null){
			return this;
		}
		else return	this.next.getLast();

	}
}
