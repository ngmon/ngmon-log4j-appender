package com.mycompany.log4jproject;

import org.apache.log4j.lf5.LogLevel;

import java.util.Map;
import org.apache.log4j.Level;

public interface Log4JSON {

	public void log(String type, Level level, Map<String, Object> payload);

	public void log(String type, Level level, Object ... variables);

}
