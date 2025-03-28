package com.sfoust.snmpparser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.sfoust.snmpparser.controllers.Pair;
import com.sfoust.snmpparser.controllers.PatternFactory;
import com.sfoust.snmpparser.enums.MibType;
import com.sfoust.snmpparser.enums.ObjectTypeEnums;
import com.sfoust.snmpparser.enums.PatternEnumsInterface;
import com.sfoust.snmpparser.models.MibIdentifier;
import com.sfoust.snmpparser.models.MibSequence;

public class MibParserTest {
	static Stream<Arguments> objectTypeTestProvider() {
		return Stream.of(
				// Found
				Arguments.of("SYNTAX TestProxy1AttributeEntry", ObjectTypeEnums.SYNTAX_ONLY, "TestProxy1AttributeEntry"),
				Arguments.of("MAX-ACCESS not-accessible", ObjectTypeEnums.MAX_ACCESS, "not-accessible"),
				Arguments.of("SYNTAX SEQUENCE OF TestProxy1AttributeEntry", ObjectTypeEnums.SYNTAX_SEQUENCE, "TestProxy1AttributeEntry"),
				Arguments.of("STATUS current", ObjectTypeEnums.STATUS, "current"),
				Arguments.of("INDEX { testProxy1AttributeIdx }", ObjectTypeEnums.INDEX, "testProxy1AttributeIdx"),
				Arguments.of("::= { testProxy1AttributeEntry 1 }", ObjectTypeEnums.LINKED_MO, "1"),
				Arguments.of("false(2)", ObjectTypeEnums.VALID_VALUE, "2"),
				Arguments.of("true(1),", ObjectTypeEnums.VALID_VALUE, "1"),
				Arguments.of("SYNTAX INTEGER {", ObjectTypeEnums.SYNTAX_ARRAY, "INTEGER")
				);
	}
	@ParameterizedTest
	@MethodSource("objectTypeTestProvider")
	public void testSyntaxOnlyFound(String line, ObjectTypeEnums expectedEnum, String expectedName) {
		PatternFactory factory = new PatternFactory();
		Pair<PatternEnumsInterface, Matcher> match = factory.getObjectTypeData(line);
		
		assertNotNull(match);
		assertFalse(match.isEmpty());
		
		assertEquals(match.first, expectedEnum);
		assertEquals(match.second.group(1), expectedName);
	}
	
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
		assertEquals(mib.getType(), MibType.Sequence);
		assertInstanceOf(MibSequence.class, mib);
		
		MibSequence sequence = (MibSequence) mib;
		assertEquals(sequence.getMibObjects().size(), 1);
		assertEquals(sequence.getMibObjects().get(0), "performAction");
	}
}
