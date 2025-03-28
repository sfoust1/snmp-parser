package com.sfoust.snmpparser.controllers;

public class Pair <F, S>{
	public F first;
	public S second;
	
	public Pair() {}
	
	public Pair(F first, S second) {
		this.first = first;
		this.second = second;
	}
	
	public boolean isEmpty() {
		return this.first == null || this.second == null;
	}

	@Override
	public String toString() {
		return String.format("[ first=%s, second=%s ]", this.first, this.second);
	}
}
