package com.sfoust.snmpparser.controllers;

import java.util.regex.Matcher;

import com.sfoust.snmpparser.enums.ObjectIdentifierEnums;
import com.sfoust.snmpparser.enums.ObjectTypeEnums;
import com.sfoust.snmpparser.enums.PatternEnumsInterface;
import com.sfoust.snmpparser.enums.SequenceTypeEnums;

public class PatternFactory {
	private PatternParser objectIdentifiers;
	private PatternParser objectTypes;
	private PatternParser sequenceTypes;
	
	public PatternFactory() {
		objectIdentifiers = new PatternParser(ObjectIdentifierEnums.values());
		objectTypes = new PatternParser(ObjectTypeEnums.values());
		sequenceTypes = new PatternParser(SequenceTypeEnums.values());
	}
	
	public Pair<PatternEnumsInterface, Matcher> getObjectIdentifierData(String line) {
		return objectIdentifiers.getMatcher(line);
	}
	
	public Pair<PatternEnumsInterface, Matcher> getObjectTypeData(String line) {
		return objectTypes.getMatcher(line);
	}
	
	public Pair<PatternEnumsInterface, Matcher> getSequenceTypeData(String line) {
		return sequenceTypes.getMatcher(line);
	}
}
