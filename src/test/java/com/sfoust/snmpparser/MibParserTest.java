package com.sfoust.snmpparser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.sfoust.snmpparser.enums.MibType;
import com.sfoust.snmpparser.models.MibIdentifier;
import com.sfoust.snmpparser.models.MibObject;
import com.sfoust.snmpparser.models.MibSequence;

public class MibParserTest {
	@Test
	public void testGetParsedSequence() {
		MibParser mibParser = new MibParser();
		
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("TestProxy1AttributeEntry ::= SEQUENCE {\n");
		sb.append("        -- We are saying this value that is addressed through the testProxy table means we are to perform some action\n");
		sb.append("        -- And this value is an integer type. Sometimes, this can be multiple values with commas and newlines\n");
		sb.append("        performAction INTEGER\n");
		sb.append("    }\n");
		sb.append("\n");
		
		InputStream inputStream = new ByteArrayInputStream(sb.toString().getBytes());
		
		Map<String, MibIdentifier> mibs = mibParser.getMibObjects(inputStream);
		
		assertNotNull(mibs);
		assertFalse(mibs.isEmpty());
		assertEquals(mibs.size(), 1);
		
		assertTrue(mibs.containsKey("TestProxy1AttributeEntry"));
		
		MibIdentifier mib = mibs.get("TestProxy1AttributeEntry");
		assertEquals(MibType.SEQUENCE, mib.getType());
		assertInstanceOf(MibSequence.class, mib);
		
		MibSequence sequence = (MibSequence) mib;
		assertEquals("TestProxy1AttributeEntry", sequence.getName());
		assertEquals(sequence.getMibObjects().size(), 1);
		assertEquals(sequence.getMibObjects().get(0), "performAction");
	}
	
	@Test void testGetParsedRoot() {
		MibParser mibParser = new MibParser();
		
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("testProxy   OBJECT IDENTIFIER ::= { testRoot 20 }\n");
		sb.append("\n");
		
		InputStream inputStream = new ByteArrayInputStream(sb.toString().getBytes());
		
		Map<String, MibIdentifier> mibs = mibParser.getMibObjects(inputStream);
		
		assertNotNull(mibs);
		assertFalse(mibs.isEmpty());
		assertEquals(mibs.size(), 1);
		
		assertTrue(mibs.containsKey("testProxy"));
		
		MibIdentifier mib = mibs.get("testProxy");
		assertEquals(MibType.ROOT, mib.getType());
		assertEquals("testProxy", mib.getName());
		assertEquals("testRoot", mib.getReferenceName());
		assertEquals("20", mib.getIndex());
	}
	
	@Test void testGetParsedTableObject() {
		MibParser mibParser = new MibParser();
		
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("testProxy1AttributeTable OBJECT-TYPE\n");
		sb.append("-- Array of that type\n");
		sb.append("    SYNTAX SEQUENCE OF TestProxy1AttributeEntry\n");
		sb.append("    MAX-ACCESS not-accessible\n");
		sb.append("    STATUS current\n");
		sb.append("    DESCRIPTION\n");
		sb.append("        \"Some multi line comment describing\n");
		sb.append("        what this table is\"\n");
		sb.append("    ::= { testProxy1 1 }\n");
		sb.append("\n");
		
		InputStream inputStream = new ByteArrayInputStream(sb.toString().getBytes());
		
		Map<String, MibIdentifier> mibs = mibParser.getMibObjects(inputStream);
		
		assertNotNull(mibs);
		assertFalse(mibs.isEmpty());
		assertEquals(mibs.size(), 1);
		
		assertTrue(mibs.containsKey("testProxy1AttributeTable"));
		
		MibIdentifier mib = mibs.get("testProxy1AttributeTable");
		assertEquals(MibType.TABLE, mib.getType());
		assertInstanceOf(MibObject.class, mib);
		
		MibObject sequence = (MibObject) mib;
		assertEquals("testProxy1AttributeTable", sequence.getName());
		assertEquals("TestProxy1AttributeEntry", sequence.getSyntax());
		assertEquals("not-accessible", sequence.getMaxAccess());
		assertEquals("current", sequence.getStatus());
		assertEquals("testProxy1", sequence.getReferenceName());
		assertEquals("1", sequence.getIndex());
	}
	
	@Test void testGetParsedEntryObject() {
		MibParser mibParser = new MibParser();
		
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("testProxy1AttributeEntry OBJECT-TYPE\n");
		sb.append("    SYNTAX TestProxy1AttributeEntry\n");
		sb.append("    MAX-ACCESS not-accessible\n");
		sb.append("    STATUS current\n");
		sb.append("    DESCRIPTION\n");
		sb.append("        \"Describes what an entry at this location in the table looks like\"\n");
		sb.append("-- An index is a way to identify how to find an entry in a table.\n");
		sb.append("-- It can be a single identifier (<OID>.1), or it can be a nested set of identifiers (<OID>.1.3.5).\n");
		sb.append("-- In this case, the testProxy1Idx has no children identifiers, testProxy1Idx IS the object\n");
		sb.append("    INDEX { testProxy1AttributeIdx }\n");
		sb.append("-- These always point to 1....\n");
		sb.append("    ::= { testProxy1AttributeTable 1 }\n");
		sb.append("\n");
		
		InputStream inputStream = new ByteArrayInputStream(sb.toString().getBytes());
		
		Map<String, MibIdentifier> mibs = mibParser.getMibObjects(inputStream);
		
		assertNotNull(mibs);
		assertFalse(mibs.isEmpty());
		assertEquals(mibs.size(), 1);
		
		assertTrue(mibs.containsKey("testProxy1AttributeEntry"));
		
		MibIdentifier mib = mibs.get("testProxy1AttributeEntry");
		assertEquals(MibType.ATTRIBUTE_ENTRY, mib.getType());
		assertInstanceOf(MibObject.class, mib);
		
		MibObject sequence = (MibObject) mib;
		assertEquals("testProxy1AttributeEntry", sequence.getName());
		assertEquals("TestProxy1AttributeEntry", sequence.getSyntax());
		assertEquals("not-accessible", sequence.getMaxAccess());
		assertEquals("current", sequence.getStatus());
		assertEquals("testProxy1AttributeTable", sequence.getReferenceName());
		assertEquals("testProxy1AttributeIdx", sequence.getEntryIndexName());
		assertEquals("1", sequence.getIndex());
	}
	
	@Test void testGetParsedObject() {
		MibParser mibParser = new MibParser();
		
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("testProxy1AttributeIdx OBJECT-TYPE\n");
		sb.append("    SYNTAX INTEGER\n");
		sb.append("    MAX-ACCESS read-only\n");
		sb.append("    STATUS current\n");
		sb.append("    DESCRIPTION\n");
		sb.append("        \"Index in the table. Allows for multiple entries of the testProxy1AttributeEntry.\n");
		sb.append("Goes onto the end of an OID, but might not be done by me, might be done after creating the whole table.\"\n");
		sb.append("    ::= { testProxy1AttributeEntry 1 }\n");
		sb.append("\n");
		
		InputStream inputStream = new ByteArrayInputStream(sb.toString().getBytes());
		
		Map<String, MibIdentifier> mibs = mibParser.getMibObjects(inputStream);
		
		assertNotNull(mibs);
		assertFalse(mibs.isEmpty());
		assertEquals(mibs.size(), 1);
		
		assertTrue(mibs.containsKey("testProxy1AttributeIdx"));
		
		MibIdentifier mib = mibs.get("testProxy1AttributeIdx");
		assertEquals(MibType.INTEGER, mib.getType());
		assertInstanceOf(MibObject.class, mib);
		
		MibObject sequence = (MibObject) mib;
		assertEquals("testProxy1AttributeIdx", sequence.getName());
		assertEquals("INTEGER", sequence.getSyntax());
		assertEquals("read-only", sequence.getMaxAccess());
		assertEquals("current", sequence.getStatus());
		assertEquals("testProxy1AttributeEntry", sequence.getReferenceName());
		assertEquals("1", sequence.getIndex());
	}
	
	@Test void testGetParsedSyntaxArrayObject() {
		MibParser mibParser = new MibParser();
		
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("performAction OBJECT-TYPE\n");
		sb.append("    SYNTAX INTEGER {\n");
		sb.append("    true(1),\n");
		sb.append("    false(2) }\n");
		sb.append("    MAX-ACCESS read-write\n");
		sb.append("    STATUS current\n");
		sb.append("    DESCRIPTION\n");
		sb.append("        \"The INTEGER array is essentially a named enum. True/false is there, but it can be any string and the number represents it.\"\n");
		sb.append("-- Notice we aren't pointing to the index. The index is every object within INDEX defined entry. This is just a single value\n");
		sb.append("    ::= { testProxy1AttributeEntry 54 }\n");
		sb.append("\n");
		
		InputStream inputStream = new ByteArrayInputStream(sb.toString().getBytes());
		
		Map<String, MibIdentifier> mibs = mibParser.getMibObjects(inputStream);
		
		assertNotNull(mibs);
		assertFalse(mibs.isEmpty());
		assertEquals(mibs.size(), 1);
		
		assertTrue(mibs.containsKey("performAction"));
		
		MibIdentifier mib = mibs.get("performAction");
		assertEquals(MibType.INTEGER, mib.getType());
		assertInstanceOf(MibObject.class, mib);
		
		MibObject sequence = (MibObject) mib;
		assertEquals("performAction", sequence.getName());
		assertEquals("INTEGER", sequence.getSyntax());
		assertEquals("read-write", sequence.getMaxAccess());
		assertEquals("current", sequence.getStatus());
		assertEquals("testProxy1AttributeEntry", sequence.getReferenceName());
		assertEquals("54", sequence.getIndex());
		
		assertEquals(2, sequence.getValidValues().size());
		assertEquals("1", sequence.getValidValues().get(0));
		assertEquals("2", sequence.getValidValues().get(1));
	}
}
