/*
 * NewTokenizer.java
 *
 * Created on September 13, 2008, 8:03 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.upenn.cis.anntool.util;

import java.text.BreakIterator;
import java.util.LinkedList;
import java.util.Locale;

import edu.upenn.cis.anntool.settings.Language;

/**
 * 
 * @author alanlee
 */
public class Tokenizer {

	private Language lang;

	/** Creates a new instance of Tokenizer */
	public Tokenizer(Language lang) {
		this.lang = lang;
	}

	public SpanString[] tokenize(String s) {

		LinkedList<SpanString> spList = new LinkedList<SpanString>();
		BreakIterator biter;

		if (lang.getBreaktype().equalsIgnoreCase("word")) {
			biter = BreakIterator.getWordInstance(new Locale(
					lang.getLanguage(), lang.getCountry()));
		} else if (lang.getBreaktype().equalsIgnoreCase("character")) {
			biter = BreakIterator.getCharacterInstance(new Locale(lang
					.getLanguage(), lang.getCountry()));
		} else {
			biter = BreakIterator.getCharacterInstance(new Locale(lang
					.getLanguage(), lang.getCountry()));
		}

		biter.setText(s);
		int index = 0;

		while (biter.next() != BreakIterator.DONE) {
			spList.add(new SpanString(index, biter.current(), s));
			// System.out.println("TOKENIZE " + index + " " + biter.current());
			index = biter.current();
		}

		// System.out.println (boundary);

		return spList.toArray(new SpanString[0]);
	}
}
