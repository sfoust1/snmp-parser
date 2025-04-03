package com.sfoust.snmpparser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sfoust.snmpparser.controllers.Pair;
import com.sfoust.snmpparser.controllers.PatternFactory;
import com.sfoust.snmpparser.enums.MibType;
import com.sfoust.snmpparser.enums.ObjectIdentifierEnums;
import com.sfoust.snmpparser.enums.ObjectTypeEnums;
import com.sfoust.snmpparser.enums.PatternEnumsInterface;
import com.sfoust.snmpparser.models.MibIdentifier;
import com.sfoust.snmpparser.models.MibObject;
import com.sfoust.snmpparser.models.MibSequence;
import com.sfoust.snmpparser.models.MibTree;

public class MibParser {
	private static final Logger LOGGER = LogManager.getLogger(MibParser.class);
	
	PatternFactory factory;
	
	public MibParser () {
		factory = new PatternFactory();
	}
	
	public MibTree getMibTree(InputStream is, String parentMibName, String parentFullOid) {
		InputStreamReader in = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(in);
		
		return getMibTree(parentMibName, parentFullOid, br);
	}
	
	public MibTree getMibTree(String filePath, String parentMibName, String parentFullOid) {
    	MibTree mibTree = null;
    	
    	try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			mibTree = getMibTree(parentMibName, parentFullOid, br);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    	
    	return mibTree;
	}
	
	/**
	 * 
	 * @param parentMib The mib that all of the files mibs belong to, like enterprises 1.3.6.1.4.1
	 * @param filePath
	 * @return
	 */
	public MibTree getMibTree(String parentMibName, String parentFullOid, BufferedReader br) {
		MibTree mibTree = new MibTree(parentMibName, parentFullOid);
    	
    	String line = "-1";
    	Integer count = 0;
        try {
            while ((line = br.readLine()) != null) {
            	count++;
            	
            	Pair<PatternEnumsInterface, Matcher> match = factory.getObjectIdentifierData(line);
            	
            	// Could be some type we don't care about like DESCIPRTION or could also be a continuation of
            	// a string for said description, or perhaps sub-objects for an identifier we don't care about.
            	if (match.isEmpty()) continue;

            	ObjectIdentifierEnums patternEnum = (ObjectIdentifierEnums) match.first;
            	Matcher matcher = match.second;
            	
            	switch (patternEnum) {
            	case ROOT:
            		String name = matcher.group(1);
            		String referenceName = matcher.group(2);
            		String index = matcher.group(3);
            		mibTree.addChildTree(new MibIdentifier(MibType.ROOT, name, referenceName, index));
            		break;
            	case SEQUENCE:
            		MibSequence parsedSequence = getParsedSequenceType(br, count);
            		parsedSequence.setName(matcher.group(1));
            		mibTree.addMibSequence(parsedSequence);
            		break;
            	case OBJECT:
            		MibObject parsedObject = getParsedObjectType(br, count);
            		parsedObject.setName(matcher.group(1));
            		mibTree.addChildTree(parsedObject);
            		break;
            	}
            }
        }
        catch (NumberFormatException e) {
        	LOGGER.error("Failed to parse number on line number " + String.valueOf(count) + " using line: " + line);
        	e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
		
		return mibTree;
	}
	
    
    public MibSequence getParsedSequenceType(BufferedReader br, Integer currentCount) {
    	MibSequence mib = new MibSequence();
    	try {
    		String line = br.readLine();
			do {
				currentCount++;
				Pair<PatternEnumsInterface, Matcher> match = factory.getSequenceTypeData(line);
				// It will be empty if } is on a newline with no value in it
				if (!match.isEmpty()) {
					mib.addMibObject(match.second.group(1));
				}
			} while ((line = br.readLine()) != null && !line.contains("}"));
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return mib;
    }
    
    public MibObject getParsedObjectType(BufferedReader br, Integer currentCount) {
    	MibObject mib = new MibObject();
    	ObjectTypeEnums currentPattern = ObjectTypeEnums.STATUS;
    	
    	String line;
    	try {
			while(currentPattern != ObjectTypeEnums.LINKED_MO
					&& (line = br.readLine()) != null) {
				currentCount++;
				Pair<PatternEnumsInterface, Matcher> match = factory.getObjectTypeData(line);
				
				// This can be description strings, comments, empty newlines, or fields we don't care about
				if (match.isEmpty()) continue;
				
				currentPattern = (ObjectTypeEnums) match.first;
				Matcher matcher = match.second;
				
				String syntax;
				switch(currentPattern) {
				case SYNTAX_ONLY:
					// This can be an entry, integer, or string
					syntax = matcher.group(1);
					mib.setSyntax(syntax);
					
					try {
						// This will catch a string or integer.
						mib.setType(MibType.valueOf(syntax));
					} catch (IllegalArgumentException e) {
						mib.setType(MibType.ATTRIBUTE_ENTRY);
					}
					
					break;
				case SYNTAX_ARRAY:
					// Same as SYNTAX_ONLY except that it has a list of valid values
					syntax = matcher.group(1);
					mib.setSyntax(syntax);
					try {
						mib.setType(MibType.valueOf(syntax));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
					
					// Read value lines until we hit a }. That line may or may not include a value.
					line = br.readLine();
					do {
						currentCount++;
						Pair<PatternEnumsInterface, Matcher> valueMatch = factory.getObjectTypeData(line);
						// It will be empty if } is on a newline with no value in it
						if (!valueMatch.isEmpty()) {
							mib.addValidValue(valueMatch.second.group(1));
						}
						
						if (line.contains("}")) break;
					} while ((line = br.readLine()) != null);
					break;
				case SYNTAX_SEQUENCE:
					// This value will be a table entry
					mib.setSyntax(matcher.group(1));
					mib.setType(MibType.TABLE);
					break;
				case MAX_ACCESS:
					mib.setMaxAccess(matcher.group(1));
					break;
				case STATUS:
					mib.setStatus(matcher.group(1));
					break;
				case INDEX:
					mib.setEntryIndexName(matcher.group(1));
					mib.setType(MibType.ATTRIBUTE_ENTRY);
					break;
				case LINKED_MO:
					// This will be the last one
					mib.setReferenceName(matcher.group(1));
					mib.setIdentifierNumber(matcher.group(2));
					break;
				default:
					LOGGER.debug("Treating line " + String.valueOf(currentCount) +
							" as one we don't care about. Contents are: " + line);
					break;
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return mib;
    }
}
