package edu.upenn.cis.anntool.settings;

public class Language {

	private final String name;
	private final String language;
	private final String country;
	private final String charset;
	private final String font;
	private final String breaktype;

	public Language(String name, String language, String country,
			String charset, String font, String breaktype) {
		this.name = name;
		this.language = language;
		this.country = country;
		this.charset = charset;
		this.font = font;
		this.breaktype = breaktype;
	}

	public String toString() {
		return name;
	}

	public String getLanguage() {
		return language;
	}

	public String getCountry() {
		return country;
	}

	public String getCharset() {
		return charset;
	}

	public String getFont() {
		return font;
	}

	public String getBreaktype() {
		return breaktype;
	}

}
