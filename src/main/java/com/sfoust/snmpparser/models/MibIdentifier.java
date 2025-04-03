package com.sfoust.snmpparser.models;

import com.sfoust.snmpparser.enums.MibType;

public class MibIdentifier {
	private MibType type;
	private String name;
	private String referenceName;
	private String identifierNumber;
	
	public MibIdentifier() {
		this.type = MibType.UNKNOWN;
	}
	
	public MibIdentifier(String name, String identifierNumber) {
		this.name = name;
		this.identifierNumber = identifierNumber;
	};
	public MibIdentifier(MibType type, String name, String referenceName, String identifierNumber) {
		this.type = type;
		this.name = name;
		this.referenceName = referenceName;
		this.identifierNumber = identifierNumber;
	}

	public String getIdentifierNumber() {
		return identifierNumber;
	}
	
	public void setIdentifierNumber(String identifierNumber) {
		this.identifierNumber = identifierNumber;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setType(MibType type) {
		this.type = type;
	}
	
	public MibType getType() {
		return type;
	}

	public String getReferenceName() {
		return referenceName;
	}

	public void setReferenceName(String referenceName) {
		this.referenceName = referenceName;
	}
	
	@Override
	public boolean equals(Object obj) {
		MibIdentifier theirs = (MibIdentifier) obj;
		return this.name.equals(theirs.name)
				&& this.type == theirs.type
				&& this.identifierNumber == theirs.identifierNumber
				&& this.referenceName.equals(theirs.referenceName);
	}
}
