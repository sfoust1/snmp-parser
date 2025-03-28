package com.sfoust.snmpparser.models;

import java.util.ArrayList;
import java.util.List;

import com.sfoust.snmpparser.enums.MibType;

public class MibSequence extends MibIdentifier {
	private List<String> mibObjects = new ArrayList<>();
	
	public MibSequence() {
		this.setType(MibType.SEQUENCE);
	}
	
	public List<String> getMibObjects() {
		return mibObjects;
	}
	
	public void addMibObject(String mibObject) {
		mibObjects.add(mibObject);
	}
}
