package com.sfoust.snmpparser.enums;

public enum ObjectIdentifierEnums implements PatternEnumsInterface {
	ROOT("^([a-zA-Z]+)\\s+OBJECT IDENTIFIER ::= \\{ ([a-zA-Z]+) ([0-9]+) \\}($|\\s+)"),
	OBJECT("^([a-zA-Z0-9]+)\\s+OBJECT-TYPE($|\\s+)"),
	SEQUENCE("^([a-zA-Z0-9]+)\\s+::= SEQUENCE($|.+)");
	
	private String pattern;
	
	private ObjectIdentifierEnums() {
		this.pattern = this.name();
	}
	
	private ObjectIdentifierEnums(String pattern) {
		this.pattern = pattern;
	}
	
	public String getPattern() {
		return this.pattern;
	}
}
