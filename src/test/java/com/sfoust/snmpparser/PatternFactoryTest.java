package com.sfoust.snmpparser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Matcher;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.sfoust.snmpparser.controllers.Pair;
import com.sfoust.snmpparser.controllers.PatternFactory;
import com.sfoust.snmpparser.enums.ObjectIdentifierEnums;
import com.sfoust.snmpparser.enums.ObjectTypeEnums;
import com.sfoust.snmpparser.enums.PatternEnumsInterface;
import com.sfoust.snmpparser.enums.SequenceTypeEnums;

public class PatternFactoryTest {
	@Test
	public void testRootFound() {
		PatternFactory factory = new PatternFactory();
		Pair<PatternEnumsInterface, Matcher> match = factory.getObjectIdentifierData("testRoot    OBJECT IDENTIFIER ::= { enterprises 100 }");
		
		assertNotNull(match);
		assertFalse(match.isEmpty());
		
		assertEquals(ObjectIdentifierEnums.ROOT, match.first);
		assertEquals("testRoot", match.second.group(1));
		assertEquals("enterprises", match.second.group(2));
		assertEquals("100", match.second.group(3));
	}
	
	@Test
	public void testObjectFound() {
		PatternFactory factory = new PatternFactory();
		Pair<PatternEnumsInterface, Matcher> match = factory.getObjectIdentifierData("testProxy1AttributeTable OBJECT-TYPE");
		
		assertNotNull(match);
		assertFalse(match.isEmpty());
		
		assertEquals(ObjectIdentifierEnums.OBJECT, match.first);
		assertEquals("testProxy1AttributeTable", match.second.group(1));
	}
	
	@Test
	public void testSequenceFound() {
		PatternFactory factory = new PatternFactory();
		Pair<PatternEnumsInterface, Matcher> match = factory.getObjectIdentifierData("TestProxy1AttributeEntry ::= SEQUENCE {");
		
		assertNotNull(match);
		assertFalse(match.isEmpty());
		
		assertEquals(ObjectIdentifierEnums.SEQUENCE, match.first);
		assertEquals("TestProxy1AttributeEntry", match.second.group(1));
	}
	
	@Test
	public void testObjectIdentifierNotFound() {
		PatternFactory factory = new PatternFactory();
		Pair<PatternEnumsInterface, Matcher> match = factory.getObjectIdentifierData("SYNTAX INTEGER");
		assertNotNull(match);
		assertTrue(match.isEmpty());
	}
	
	static Stream<Arguments> objectTypeTestProvider() {
		// No spaces, spaces, then tabs
		return Stream.of(
				Arguments.of("SYNTAX TestProxy1AttributeEntry", ObjectTypeEnums.SYNTAX_ONLY, "TestProxy1AttributeEntry"),
				Arguments.of("      SYNTAX TestProxy1AttributeEntry", ObjectTypeEnums.SYNTAX_ONLY, "TestProxy1AttributeEntry"),
				Arguments.of("		SYNTAX TestProxy1AttributeEntry", ObjectTypeEnums.SYNTAX_ONLY, "TestProxy1AttributeEntry"),
				Arguments.of("MAX-ACCESS not-accessible", ObjectTypeEnums.MAX_ACCESS, "not-accessible"),
				Arguments.of("      MAX-ACCESS not-accessible", ObjectTypeEnums.MAX_ACCESS, "not-accessible"),
				Arguments.of("		MAX-ACCESS not-accessible", ObjectTypeEnums.MAX_ACCESS, "not-accessible"),
				Arguments.of("SYNTAX SEQUENCE OF TestProxy1AttributeEntry", ObjectTypeEnums.SYNTAX_SEQUENCE, "TestProxy1AttributeEntry"),
				Arguments.of("      SYNTAX SEQUENCE OF TestProxy1AttributeEntry", ObjectTypeEnums.SYNTAX_SEQUENCE, "TestProxy1AttributeEntry"),
				Arguments.of("		SYNTAX SEQUENCE OF TestProxy1AttributeEntry", ObjectTypeEnums.SYNTAX_SEQUENCE, "TestProxy1AttributeEntry"),
				Arguments.of("STATUS current", ObjectTypeEnums.STATUS, "current"),
				Arguments.of("      STATUS current", ObjectTypeEnums.STATUS, "current"),
				Arguments.of("		STATUS current", ObjectTypeEnums.STATUS, "current"),
				Arguments.of("INDEX { testProxy1AttributeIdx }", ObjectTypeEnums.INDEX, "testProxy1AttributeIdx"),
				Arguments.of("      INDEX { testProxy1AttributeIdx }", ObjectTypeEnums.INDEX, "testProxy1AttributeIdx"),
				Arguments.of("		INDEX { testProxy1AttributeIdx }", ObjectTypeEnums.INDEX, "testProxy1AttributeIdx"),
				Arguments.of("false(2)", ObjectTypeEnums.VALID_VALUE, "2"),
				Arguments.of("      false(2)", ObjectTypeEnums.VALID_VALUE, "2"),
				Arguments.of("		false(2)", ObjectTypeEnums.VALID_VALUE, "2"),
				Arguments.of("true(1),", ObjectTypeEnums.VALID_VALUE, "1"),
				Arguments.of("      true(1),", ObjectTypeEnums.VALID_VALUE, "1"),
				Arguments.of("		true(1),", ObjectTypeEnums.VALID_VALUE, "1"),
				Arguments.of("SYNTAX INTEGER {", ObjectTypeEnums.SYNTAX_ARRAY, "INTEGER"),
				Arguments.of("      SYNTAX INTEGER {", ObjectTypeEnums.SYNTAX_ARRAY, "INTEGER"),
				Arguments.of("		SYNTAX INTEGER {", ObjectTypeEnums.SYNTAX_ARRAY, "INTEGER")
				);
	}
	@ParameterizedTest
	@MethodSource("objectTypeTestProvider")
	public void testSyntaxOnlyFound(String line, ObjectTypeEnums expectedEnum, String expectedName) {
		PatternFactory factory = new PatternFactory();
		Pair<PatternEnumsInterface, Matcher> match = factory.getObjectTypeData(line);
		
		assertNotNull(match);
		assertFalse(match.isEmpty());
		
		assertEquals(expectedEnum, match.first);
		assertEquals(expectedName, match.second.group(1));
	}
	
	@Test
	public void testLinkedMoFound() {
		String line = "::= { testProxy1AttributeEntry 1 }";
		ObjectTypeEnums expectedEnum = ObjectTypeEnums.LINKED_MO;
		String expectedReferenceName = "testProxy1AttributeEntry";
		String expectedIndex = "1";
		
		PatternFactory factory = new PatternFactory();
		Pair<PatternEnumsInterface, Matcher> match = factory.getObjectTypeData(line);
		
		assertNotNull(match);
		assertFalse(match.isEmpty());
		
		assertEquals(expectedEnum, match.first);
		assertEquals(expectedReferenceName, match.second.group(1));
		assertEquals(expectedIndex, match.second.group(2));
	}
	
	@Test
	public void testObjectTypeNotFound() {
		PatternFactory factory = new PatternFactory();
		Pair<PatternEnumsInterface, Matcher> match = factory.getObjectTypeData("TestProxy1AttributeEntry ::= SEQUENCE");
		assertNotNull(match);
		assertTrue(match.isEmpty());
	}
	
	static Stream<Arguments> sequenceTypeTestProvider() {
		return Stream.of(
				// Found
				Arguments.of("performAction INTEGER", SequenceTypeEnums.INTEGER, "performAction"),
				Arguments.of("performAction INTEGER,", SequenceTypeEnums.INTEGER, "performAction")
				);
	}
	@ParameterizedTest
	@MethodSource("sequenceTypeTestProvider")
	public void testSequenceInteger(String line, SequenceTypeEnums expectedEnum, String expectedMibReference) {
		PatternFactory factory = new PatternFactory();
		Pair<PatternEnumsInterface, Matcher> match = factory.getSequenceTypeData(line);
		
		assertNotNull(match);
		assertFalse(match.isEmpty());
		
		assertEquals(expectedEnum, match.first);
		assertEquals(expectedMibReference, match.second.group(1));
	}
}
