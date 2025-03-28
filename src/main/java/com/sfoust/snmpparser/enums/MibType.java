package com.sfoust.snmpparser.enums;

public enum MibType {
	UNKNOWN, // Before we try and figure out it's type
	Root, // Just an object identifier
	Table, // Will be a sequence of
	AttributeEntry, // Contains INDEX
	AttributeIndex, // References an entry
	Sequence, // Starts with ::= SEQUENCE in it
	Integer,
	String
}
