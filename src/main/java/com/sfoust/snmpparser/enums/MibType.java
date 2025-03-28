package com.sfoust.snmpparser.enums;

public enum MibType {
	UNKNOWN, // Before we try and figure out it's type
	ROOT, // Just an object identifier
	TABLE, // Will be a sequence of
	ATTRIBUTE_ENTRY, // Contains INDEX
	ATTRIBUTE_INDEX, // References an entry
	SEQUENCE, // Starts with ::= SEQUENCE in it
	INTEGER,
	STRING
}
