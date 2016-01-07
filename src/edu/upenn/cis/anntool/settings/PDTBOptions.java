package edu.upenn.cis.anntool.settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Stack;
import java.util.regex.Pattern;

import edu.upenn.cis.anntool.lexyacc.Options;
import edu.upenn.cis.anntool.util.OptFeat;
import edu.upenn.cis.anntool.util.PDTBOptionsAccum;

/**
 * Makes the options in Options.properties available for PDTBFeatures to read in
 * 
 * @author geraud
 */
public final class PDTBOptions {

	private static String[] conns;
	private static String[] semanticClassesTree;
	private static String[] semanticClassesTreeLong;
	private static OptFeat connSourceFeature;
	private static OptFeat connTypeFeature;
	private static OptFeat connPolarityFeature;
	private static OptFeat connDeterminancyFeature;
	private static OptFeat argSourceFeature;
	private static OptFeat argTypeFeature;
	private static OptFeat argPolarityFeature;
	private static OptFeat argDeterminancyFeature;

	public PDTBOptions(String properties) {
		File f = new File(properties);
		if (!f.exists()) {
			try {
				InputStream in = getClass().getResourceAsStream(properties);
				OutputStream out = new FileOutputStream(f);
				byte[] b = new byte[in.available()];
				in.read(b);
				out.write(b);
				out.flush();
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			InputStream inFile = new FileInputStream(f);
			BufferedReader r = new BufferedReader(new InputStreamReader(inFile));
			Options fScanner = new Options(r);
			PDTBOptionsAccum opts = fScanner.yylex();

			conns = (String[]) opts.getConns().toArray(new String[0]);

			LinkedList<String> classList = opts.getClasses();
			classList.addFirst("");
			semanticClassesTree = (String[]) classList.toArray(new String[0]);
			semanticClassesTreeLong = createSClassesLong(semanticClassesTree);

			connSourceFeature = opts.getConnSource();
			connTypeFeature = opts.getConnType();
			connPolarityFeature = opts.getConnPolarity();
			connDeterminancyFeature = opts.getConnDet();
			argSourceFeature = opts.getArgSource();
			argTypeFeature = opts.getArgType();
			argPolarityFeature = opts.getArgPolarity();
			argDeterminancyFeature = opts.getArgDet();

			r.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String[] createSClassesLong(String[] sclasses) {
		String[] sclassesLong = new String[sclasses.length];
		sclassesLong[0] = "";
		Stack<String> stack = new Stack<String>();
		for (int i = 1; i < sclasses.length; i++) {
			String sclass = sclasses[i];
			String[] split = sclass.split(Pattern.quote("."));
			if (split.length > stack.size()) {
				stack.push(split[split.length - 1]);
			} else if (split.length < stack.size()) {
				int pops = stack.size() - split.length;
				for (int j = 0; j <= pops; j++) {
					stack.pop();
				}
				stack.push(split[split.length - 1]);
			} else {
				stack.pop();
				stack.push(split[split.length - 1]);
			}
			sclassesLong[i] = "";
			for (int j = 0; j < stack.size(); j++) {
				sclassesLong[i] += stack.get(j);
				if (j < stack.size() - 1) {
					sclassesLong[i] += ".";
				}
			}

		}
		return sclassesLong;
	}

	public String[] getConns() {
		return conns;
	}

	public String[] getSemanticClassesTreeLong() {
		return semanticClassesTreeLong;
	}

	public String[] getSemanticClassesTree() {
		return semanticClassesTree;
	}

	public OptFeat getConnSourceFeature() {
		return connSourceFeature;
	}

	public OptFeat getConnTypeFeature() {
		return connTypeFeature;
	}

	public OptFeat getConnPolarityFeature() {
		return connPolarityFeature;
	}

	public OptFeat getConnDeterminancyFeature() {
		return connDeterminancyFeature;
	}

	public OptFeat getArgSourceFeature() {
		return argSourceFeature;
	}

	public OptFeat getArgTypeFeature() {
		return argTypeFeature;
	}

	public OptFeat getArgPolarityFeature() {
		return argPolarityFeature;
	}

	public OptFeat getArgDeterminancyFeature() {
		return argDeterminancyFeature;
	}

}
