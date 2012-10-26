package com.mycompany.log4jproject;

import org.apache.log4j.lf5.LogLevel;

import java.util.Map;

public interface Log4JSON {

	public void log(String type, LogLevel level, Map<String, Object> payload);

	public void log(String type, LogLevel level, Object ... variables);

}
