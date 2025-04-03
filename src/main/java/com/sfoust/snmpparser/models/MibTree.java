package com.sfoust.snmpparser.models;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MibTree {
	
	private static final Logger LOGGER = LogManager.getLogger(MibTree.class);
	private MibIdentifier mibData;
	private Map<Integer, MibTree> children = new HashMap<>();
	// <mibName, mibOid>
	private Map<String, String> mibMap = new HashMap<>();
	private Map<String, MibSequence> mibSequences = new HashMap<>();
	
	public MibTree(String parentName, String parentFullOid) {
		// Create mib data that isn't meant as a full mib, just used to track what the parent is called
		mibData = new MibIdentifier(parentName, parentFullOid);
		mibMap.put(parentName, parentFullOid);
	}
	
	private MibTree() {}
	private MibTree(MibIdentifier mibData) {
		this.mibData = mibData;
	}
	
	public MibIdentifier getMibData() {
		return mibData;
	}
	
	public Map<Integer, MibTree> getChildren() {
		return children;
	}
	
	public void addMibSequence(MibSequence sequence) {
		mibSequences.put(sequence.getName(), sequence);
	}
	
	public MibSequence getSequence(String sequenceName) {
		return mibSequences.get(sequenceName);
	}
	
	public String getOidByMibName(String parentName) {
		return mibMap.get(parentName);
	}
	
	public void addChildTree(MibIdentifier child) {
		String parentName = child.getReferenceName();
		if (!mibMap.containsKey(parentName))
			throw new IllegalArgumentException("We do not have MIB \"" + parentName + "\" in our map of known mibs" );
		
		Integer childOid = Integer.valueOf(child.getIdentifierNumber());
		
		MibTree tree;
		String treeOid;
		if (parentName.equals(this.mibData.getName())) {
			tree = this;
			treeOid = this.mibData.getIdentifierNumber();
		} else {
			treeOid = getOidByMibName(parentName);
			tree = getChildTreeByOid(treeOid); 
		}
		if (tree.children.containsKey(childOid)) {
			String existingName = tree.children.get(childOid).getMibData().getName();
			throw new IllegalArgumentException("Multiple of the same OID defined. "
					+ existingName + " and " + child.getName() + " already exists at " + treeOid);
		} else {
			tree.children.put(childOid, new MibTree(child));
			mibMap.put(child.getName(), treeOid + "." + child.getIdentifierNumber());
		}
	}
	
	public MibTree getChildTreeByName(String mibName) {
		String mibOid = getOidByMibName(mibName);
		return getChildTreeByOid(mibOid);
	}
	
	public MibTree getChildTreeByOid(String fullOidString) {
		String oidString = fullOidString.replace(this.mibData.getIdentifierNumber() + ".", "");
		Integer[] oids = Arrays.stream(oidString.split("\\.")).map(String::trim).map(Integer::valueOf).toArray(Integer[]::new);
		
		MibTree returnTree = null;
		if (!children.containsKey(oids[0])) return null;
		
		Map<Integer, MibTree> currentChildren = children;
		
		for(int oid: oids) {
			// We do not contain the OID
			if ((returnTree = currentChildren.get(oid)) == null) {
				LOGGER.error("We failed to find OID: " + oidString);
				break;
			}
			currentChildren = returnTree.children;
		}
		
		return returnTree;
	}
}
