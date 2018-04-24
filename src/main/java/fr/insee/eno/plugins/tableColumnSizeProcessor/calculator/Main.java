package fr.insee.eno.plugins.tableColumnSizeProcessor.calculator;

import java.util.HashMap;
import java.util.Map;

public class Main {

	public static void main(String[] args) {

		Map<String, String> argsMap = new HashMap<>();
		for (String arg : args) {
			if (arg.contains("=")) {
				argsMap.put(arg.substring(0, arg.indexOf('=')), arg.substring(arg.indexOf('=') + 1));
			}
		}
		try {
			FakeApp.run(argsMap.get("inFileName"), argsMap.get("outFileName"), argsMap.get("xmlConfFile"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}