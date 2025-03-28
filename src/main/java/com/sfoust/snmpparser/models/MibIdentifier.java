package com.sfoust.snmpparser.models;

import com.sfoust.snmpparser.enums.MibType;

public class MibIdentifier {
	private MibType type;
	private String name;
	private String referenceName;
	private String index;
	
	public MibIdentifier() {
		this.type = MibType.UNKNOWN;
	}
	public MibIdentifier(MibType type, String name, String referenceName, String index) {
		this.type = type;
		this.name = name;
		this.referenceName = referenceName;
		this.index = index;
	}

	public String getIndex() {
		return index;
	}
	
	public void setIndex(String index) {
		this.index = index;
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
}
