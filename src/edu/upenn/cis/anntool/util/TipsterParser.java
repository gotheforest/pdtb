package edu.upenn.cis.anntool.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TipsterParser {

	// TODO: fix file 0037 (dual tip)
	// TODO: handle case where there is no tipster info associated (hindi
	// corpus)

	private static final String docnoPattern1 = "<DOCNO>\\s*";
	private static final String docnoPattern2 = "\\s*</DOCNO>";
	private static final String nodesPattern = "<.*?>\\s*.*?\\s*</.*?>";
	private static final Pattern nodePattern = Pattern
			.compile("<(.*?)>\\s*(.*?)\\s*</.*?>");
	private static final String txtPattern = "<TXT>\\s*(.*?)\\s*</TXT>";

	List<Pair> tipsterMap = new ArrayList<Pair>();

	public static TipsterParser TipsterParserFactory(File tipsterFile,
			String docno) {
		if (docno == null || docno.equals("")) {
			return null;
		} else {
			return new TipsterParser(tipsterFile, docno);
		}
	}

	private TipsterParser(File tipsterFile, String docno) {

		String docPattern = "<DOC>\\s*" + docnoPattern1 + Pattern.quote(docno)
				+ docnoPattern2 + "\\s*" + "((" + nodesPattern + "\\s*)*?)"
				+ txtPattern + "\\s*</DOC>";

		Pattern pattern = Pattern.compile(docPattern, Pattern.DOTALL);

		StringBuffer sb = new StringBuffer();
		try {
			BufferedReader in = new BufferedReader(new FileReader(tipsterFile));
			String str;
			while ((str = in.readLine()) != null) {
				sb.append(str);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		tipsterMap.add(new Pair("DOCNO", docno));
		Matcher matcher1 = pattern.matcher(sb);
		if (matcher1.find()) {
			String nodes = matcher1.group(1);
			Matcher matcher2 = nodePattern.matcher(nodes);
			while (matcher2.find()) {
				tipsterMap.add(new Pair(matcher2.group(1), matcher2.group(2)));
			}
			tipsterMap.add(new Pair("TXT", matcher1.group(3)));
		}
	}

	public String getTipsterMap() {
		String ret = "";
		for (ListIterator<Pair> i = tipsterMap.listIterator(); i.hasNext();) {
			Pair next = i.next();
			if (i.nextIndex() < tipsterMap.size()) {
				ret += next;
			}
			if (i.nextIndex() < tipsterMap.size() - 1) {
				ret += "\n";
			}
		}
		return ret;
	}

	public String getTxt() {
		for (ListIterator<Pair> i = tipsterMap.listIterator(); i.hasNext();) {
			Pair next = i.next();
			if (next.key.equals("TXT")) {
				return next.value;
			}
		}
		return "";
	}

	private static final Pattern breakPattern = Pattern
			.compile(
					"</s>\\s*</p>\\s*<p>\\s*<s>\\s*---\\s*</s>\\s*</p>\\s*<p>\\s*<s>\\s*(.*?)\\s*</s>",
					Pattern.DOTALL);

	public List<String> getTextAfterArticleBreaks() {
		String txt = removeAnnotations(getTxt());
		Matcher matcher = breakPattern.matcher(txt);
		List<String> breaks = new ArrayList<String>();
		while (matcher.find()) {
			breaks.add(matcher.group(1));
		}
		return breaks;
	}

	private static final Pattern lastLinePattern = Pattern.compile(
			".*<s>\\s*(.*?)</s>\\s*</p>\\s*", Pattern.DOTALL);

	public String getLastLineOfText() {
		String txt = removeAnnotations(getTxt());
		Matcher matcher = lastLinePattern.matcher(txt);
		if (matcher.matches()) {
			return matcher.group(1);
		}
		return "";
	}

	private static String removeAnnotations(String txt) {
		return txt.replaceAll("\\s*<s>\\s*@.*?</s>\\s*", "");
	}

	// public String getDocNo() {
	// return docno;
	// }
	//
	// public String getDd1() {
	// return dd1;
	// }
	//
	// public String getAn() {
	// return an;
	// }
	//
	// public String getHl() {
	// return hl;
	// }
	//
	// public String getDd2() {
	// return dd2;
	// }
	//
	// public String getSo() {
	// return so;
	// }
	//
	// public String getCo() {
	// return co;
	// }
	//
	// public String getIn() {
	// return in;
	// }
	//
	// public String getGV() {
	// return gv;
	// }
	//
	// public String getDateline() {
	// return dateline;
	// }
	//

	//
	// public static final int NumberOfAttributes = 10;
	//
	// public String[] getAllAttribtues() {
	// return new String[] { "DOCNO : " + docno, "DD : " + dd1, "AN : " + an,
	// "HL : " + hl, "DD : " + dd2, "SO : " + so, "CO : " + co,
	// "IN : " + in, "GV : " + gv, "DATELINE : " + dateline,
	// "TXT : " + txt };
	// }

}
