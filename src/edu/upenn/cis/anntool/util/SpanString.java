package edu.upenn.cis.anntool.util;

public class SpanString extends Span {

	private String token;

	public SpanString(int start, int end, String text) {
		super(start, end);
		token = text.substring(start, end);
	}

	public String getToken() {
		return token;
	}

}
