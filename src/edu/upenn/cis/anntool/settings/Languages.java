package edu.upenn.cis.anntool.settings;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;

public class Languages {
	private static final String languageFileLocation = "LanguageSettings.txt";

	/*
	 * public static enum LANGUAGE {
	 * 
	 * English("en", "US", "UTF8", "Default", "word"), Hindi("hi", "IN", "UTF8",
	 * "Default", "character"), Japanese("ja", "JP", "EUC_JP",
	 * "Arial Unicode MS", "character"), Tamil("ta", "IN", "UTF8", "Latha",
	 * "character"), Urdu("ur", "IN", "UTF8", "Nafees Web Naskh", "character");
	 * 
	 * //Urdu("ur", "IN", "UTF8", // "Nafees Naskh v2.01", "character"),
	 * 
	 * 
	 * }
	 */

	private Language[] languages;

	public Languages() {
		Properties languageProperties = new Properties();
		try {
			FileInputStream is = new FileInputStream(languageFileLocation);
			languageProperties.load(is);
			is.close();
			languages = new Language[languageProperties.size()];
			Enumeration<String> keys = (Enumeration<String>) languageProperties
					.propertyNames();
			for (int i = 0; i < languageProperties.size(); i++) {
				String name = keys.nextElement();
				String[] values = languageProperties.getProperty(name).split(
						",");
				if (values.length == 5) {
					languages[i] = new Language(name, values[0], values[1],
							values[2], values[3], values[4]);
				} else {
					loadDefaults();
				}
			}

		} catch (Exception e) {
			loadDefaults();
		}
	}

	private void loadDefaults() {
		languages = new Language[] {
				new Language("English", "en", "US", "UTF8", "Default", "word"),
				new Language("Hindi", "hi", "IN", "UTF8", "Default",
						"character") };
	}

	public Language[] values() {
		return languages;
	}
	
	public Language valueOf(String name){
		for (int i = 0; i<languages.length;i++){
			if(languages[i].toString().equals(name)){
				return languages[i];
			}
		}
		throw new IllegalArgumentException();
	}

}
