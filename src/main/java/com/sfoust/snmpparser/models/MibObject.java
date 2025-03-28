package com.sfoust.snmpparser.models;

import java.util.ArrayList;
import java.util.List;

import com.sfoust.snmpparser.enums.MibType;

public class MibObject extends MibIdentifier {
	private String syntax;
	private String maxAccess;
	private String status;
	private String description;
	private String entryIndexName;
	private List<String> validValues = new ArrayList<>();
	
	public MibObject() {}
	public MibObject(MibType type, String identifierName, String identifierReference, String identifierNumber) {
		super(type, identifierName, identifierReference, identifierNumber);
	}

	public String getSyntax() {
		return syntax;
	}

	public void setSyntax(String syntax) {
		this.syntax = syntax;
	}

	public String getMaxAccess() {
		return maxAccess;
	}

	public void setMaxAccess(String max_access) {
		this.maxAccess = max_access;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public List<String> getValidValues() {
		return validValues;
	}
	
	public void addValidValue(String value) {
		validValues.add(value);
	}
	
	public String getEntryIndexName() {
		return entryIndexName;
	}
	
	public void setEntryIndexName(String entryIndexName) {
		this.entryIndexName = entryIndexName;
	}
}
