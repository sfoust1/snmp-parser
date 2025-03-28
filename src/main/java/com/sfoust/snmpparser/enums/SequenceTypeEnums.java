package com.sfoust.snmpparser.enums;

public enum SequenceTypeEnums implements PatternEnumsInterface {
	INTEGER("\\s*([a-zA-Z0-9]+)\\s+INTEGER,?($|\\s+)");
	
	private String pattern;
	
	private SequenceTypeEnums() {
		this.pattern = this.name();
	}
	
	private SequenceTypeEnums(String pattern) {
		this.pattern = pattern;
	}
	
	public String getPattern() {
		return this.pattern;
	}
}
