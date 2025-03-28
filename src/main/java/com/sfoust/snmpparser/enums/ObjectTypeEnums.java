package com.sfoust.snmpparser.enums;

public enum ObjectTypeEnums implements PatternEnumsInterface {
	SYNTAX_ONLY("\\s*SYNTAX\\s+([a-zA-Z0-9]+)($|\\s+)"),
	SYNTAX_SEQUENCE("\\s*SYNTAX SEQUENCE OF\\s+([a-zA-Z0-9]+)($|\\s+)"),
	SYNTAX_ARRAY("\\s*SYNTAX\\s+([a-zA-Z0-9]+)\\s+\\{($|\\s+)"),
	MAX_ACCESS("\\s*MAX-ACCESS\\s+([a-zA-Z0-9\\-]+)($|\\s+)"),
	STATUS("\\s*STATUS\\s+([a-zA-Z0-9]+)($|\\s+)"),
	INDEX("\\s*INDEX\\s+\\{\\s+([a-zA-Z0-9]+)\\s+\\}($|\\s+)"),
	LINKED_MO("\\s*::= \\{\\s+([a-zA-Z0-9]+)\\s+([0-9])\\s+\\}($|\\s+)"),
	VALID_VALUE(".+\\((\\w+)[\\),]+($|\\s+)");
	
	private String pattern;
	
	private ObjectTypeEnums() {
		this.pattern = this.name();
	}
	
	private ObjectTypeEnums(String pattern) {
		this.pattern = pattern;
	}
	
	public String getPattern() {
		return this.pattern;
	}
}
