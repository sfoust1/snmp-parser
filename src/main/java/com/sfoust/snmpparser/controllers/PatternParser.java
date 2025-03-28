package com.sfoust.snmpparser.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sfoust.snmpparser.enums.PatternEnumsInterface;

public class PatternParser {
	private static final Logger LOGGER = LogManager.getLogger(PatternParser.class);
	
	Map<PatternEnumsInterface, Pattern> patternMap;
	
	public PatternParser(PatternEnumsInterface[] patterns) {
		patternMap = new HashMap<>();
		
		for (PatternEnumsInterface pattern : patterns) {
			LOGGER.debug("Adding pattern: " + pattern.getPattern());
			patternMap.put(pattern, Pattern.compile(pattern.getPattern()));
		}
	}
	
	public Pair<PatternEnumsInterface, Matcher> getMatcher(String line) {
		Pair<PatternEnumsInterface, Matcher> match = new Pair<>();
		LOGGER.debug("Trying line: " + line);

		int commentIndex;
		// Check for and remove comment
		if ((commentIndex = line.indexOf("--")) >= 0) {
			line = line.substring(0, commentIndex);
		}
		
		Matcher matcher;
		
		for(Map.Entry<PatternEnumsInterface, Pattern> entry : patternMap.entrySet()) {
			LOGGER.debug("Trying pattern: " + entry.getValue().pattern());
			matcher = entry.getValue().matcher(line);
			
			if (matcher.matches()) {
				LOGGER.debug("Found a match! Pattern=" + entry.getKey().name());
				match.first = entry.getKey();
				match.second = matcher;
				break;
			}
		}
		
		return match;
	}
}
