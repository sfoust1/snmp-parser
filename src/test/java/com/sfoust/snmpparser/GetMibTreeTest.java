package com.sfoust.snmpparser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.sfoust.snmpparser.enums.MibType;
import com.sfoust.snmpparser.models.MibIdentifier;
import com.sfoust.snmpparser.models.MibObject;
import com.sfoust.snmpparser.models.MibSequence;
import com.sfoust.snmpparser.models.MibTree;

@SpringJUnitConfig
@TestPropertySource("classpath:application.properties")
public class GetMibTreeTest {
	String parentMibName = "enterprises";
	String parentOid = "1.3.6.1.4.1";
	
	@Value("${files.table}")
	public String testTableFile;

	public String assertMibEquals(MibIdentifier mib, String name, MibType type,
			String identifierNumber, String referenceName) {
		assertEquals(name, mib.getName());
		assertEquals(type, mib.getType());
		assertEquals(identifierNumber, mib.getIdentifierNumber());
		assertEquals(referenceName, mib.getReferenceName());
		
		return name;
	}
	
	public MibIdentifier assertMibEquals(MibTree rootMibTree, String referenceName, String name, MibType type,
			String identifierNumber) {
		String parentOid = rootMibTree.getOidByMibName(referenceName);
		assertNotNull(parentOid);
		
		String expectedOid = parentOid + "." + identifierNumber;
		assertEquals(expectedOid, rootMibTree.getOidByMibName(name));
		
		MibTree testTree = rootMibTree.getChildTreeByOid(expectedOid);
		assertNotNull(testTree);
		
		MibIdentifier mib = testTree.getMibData();
		assertNotNull(mib);
		
		assertEquals(name, mib.getName());
		assertEquals(type, mib.getType());
		assertEquals(identifierNumber, mib.getIdentifierNumber());
		assertEquals(referenceName, mib.getReferenceName());
		
		return mib;
	}
	
	@Test void testGetParsedRoot() {
		MibParser mibParser = new MibParser();
		
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("testProxy   OBJECT IDENTIFIER ::= { testRoot 20 }\n");
		sb.append("\n");
		
		InputStream inputStream = new ByteArrayInputStream(sb.toString().getBytes());
		
		MibTree mibTree = mibParser.getMibTree(inputStream, "testRoot", parentOid);
		assertMibEquals(mibTree, "testRoot", "testProxy", MibType.ROOT, "20");
	}
	
	private static String getTable() {
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
		
		return sb.toString();
	}
	
	private static String getParsedEntry() {
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
		
		return sb.toString();
	}
	
	private static String getParsedObject() {
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
		
		return sb.toString();
	}
	
	static Stream<Arguments> parsedObjectProvider() {
		return Stream.of(
		// objectDefinition, parentName, objectName, expectedIdentifierNumber
		//     expectedSyntax, expectedMaxAccess, expectedStatus, expectedIndexName, MibType
		Arguments.of(getTable(), "testProxy1", "testProxy1AttributeTable", "1",
				"TestProxy1AttributeEntry", "not-accessible", "current", null, MibType.TABLE),
		Arguments.of(getParsedEntry(), "testProxy1AttributeTable", "testProxy1AttributeEntry", "1",
				"TestProxy1AttributeEntry", "not-accessible", "current", "testProxy1AttributeIdx", MibType.ATTRIBUTE_ENTRY),
		Arguments.of(getParsedObject(), "testProxy1AttributeEntry", "testProxy1AttributeIdx", "1",
				"INTEGER", "read-only", "current", null, MibType.INTEGER)
				);
	}
	@ParameterizedTest
	@MethodSource("parsedObjectProvider")
	void testObjects(
			String objectDefinition, String parentName, String objectName, String expectedIdentifierNumber,
			String expectedSyntax, String expectedMaxAccess, String expectedStatus,
			String expectedIndexName, MibType expectedType) {
		MibParser mibParser = new MibParser();
		InputStream inputStream = new ByteArrayInputStream(objectDefinition.toString().getBytes());
		
		MibTree mibTree = mibParser.getMibTree(inputStream, parentName, parentOid);
		MibObject mib = (MibObject) assertMibEquals(mibTree, parentName, objectName, expectedType, expectedIdentifierNumber);
		assertEquals(expectedSyntax, mib.getSyntax());
		assertEquals(expectedMaxAccess, mib.getMaxAccess());
		assertEquals(expectedStatus, mib.getStatus());
		assertEquals(expectedIndexName, mib.getEntryIndexName());
	}
	
	@Test void testGetParsedSyntaxArrayObject() {
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
		
		MibParser mibParser = new MibParser();
		InputStream inputStream = new ByteArrayInputStream(sb.toString().getBytes());
		
		MibTree mibTree = mibParser.getMibTree(inputStream, "testProxy1AttributeEntry", parentOid);
		MibObject mib = (MibObject) assertMibEquals(mibTree, "testProxy1AttributeEntry", "performAction", MibType.INTEGER, "54");
		assertEquals("INTEGER", mib.getSyntax());
		assertEquals("read-write", mib.getMaxAccess());
		assertEquals("current", mib.getStatus());
		assertEquals(null, mib.getEntryIndexName());
		
		assertEquals(2, mib.getValidValues().size());
		assertEquals("1", mib.getValidValues().get(0));
		assertEquals("2", mib.getValidValues().get(1));
	}
	
	@Test void testGetParsedSequence() {
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
		MibTree mibTree = mibParser.getMibTree(inputStream, "performAction", parentOid);
		
		MibSequence sequence = mibTree.getSequence("TestProxy1AttributeEntry");
		
		assertNotNull(sequence);
		assertEquals("TestProxy1AttributeEntry", sequence.getName());
		assertEquals(1, sequence.getMibObjects().size());
		assertEquals("performAction", sequence.getMibObjects().get(0));
	}
	
	@Test void testSingleTableFile() {
		MibParser mibParser = new MibParser();
		MibTree mibTree = mibParser.getMibTree(testTableFile, parentMibName, parentOid);
		
		assertEquals(parentMibName, mibTree.getMibData().getName());
		
		MibIdentifier mibParent = assertMibEquals(mibTree, parentMibName, "testRoot", MibType.ROOT, "100");
		mibParent = assertMibEquals(mibTree, mibParent.getName(), "testProxy", MibType.ROOT, "20");
		mibParent = assertMibEquals(mibTree, mibParent.getName(), "testProxy1", MibType.ROOT, "1");
		mibParent = assertMibEquals(mibTree, mibParent.getName(), "testProxy1AttributeTable", MibType.TABLE, "1");
		mibParent = assertMibEquals(mibTree, mibParent.getName(), "testProxy1AttributeEntry", MibType.ATTRIBUTE_ENTRY, "1");
		assertMibEquals(mibTree, mibParent.getName(), "performAction", MibType.INTEGER, "54");
		assertMibEquals(mibTree, mibParent.getName(), "testProxy1AttributeIdx", MibType.INTEGER, "1");
		
		MibSequence sequence = mibTree.getSequence("TestProxy1AttributeEntry");
		assertEquals(1, sequence.getMibObjects().size());
		assertEquals("performAction", sequence.getMibObjects().get(0));

	}
}
