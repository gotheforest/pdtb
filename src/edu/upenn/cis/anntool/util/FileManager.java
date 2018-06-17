package edu.upenn.cis.anntool.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.upenn.cis.anntool.settings.Constants;
import edu.upenn.cis.anntool.settings.Language;

public class FileManager {

	private File rawRoot;
	private File annRoot;
	private File tipsterRoot;
	private File outputCommentRoot;
	private List<File> adjudicationRoots = new ArrayList<File>();
	private List<File> commentRoots = new ArrayList<File>();
	//private File comment1Root;
	//private File comment2Root;
	private String sec;
	private String fil;
	private Vector<Section> rawSectionsList = new Vector<Section>();
	private String rawText;
	private TipsterParser tipsterParser1;
	private TipsterParser tipsterParser2;
	private File annSec;
	private File annFil;
	private File commentSec;
	private File commentFil;
	
	
	
	private String title;
	private List<Relation> relationList;
	private List<Relation> adjudicationList;
	private int secIndex = -1;
	private int filIndex = -1;
	private Map<String, Map<String, TipsterFileDocNos>> ptbTipsterMap = new HashMap<String, Map<String, TipsterFileDocNos>>();
	private AdjudicationGroupManager adjudicationGroupManager;

	private Tokenizer tokenizer;
	private SpanString[] tokens;
	private IndexSpanMap ism;
	private Language lang;

	BufferedReader in = null;
	
	private static final Pattern ptbDuplicatesPattern = Pattern
			.compile("(.*)/(.*) == (.*)/(.*)");
	private static final Pattern ptbTipsterPattern = Pattern
			.compile("(.*?)/(.*?) WSJ(\\d*-\\d*) (\\+ WSJ(\\d*-\\d*) )?1989/wsj9_00(\\d)");

	public FileManager(String rawRoot, String annRoot, String tipsterRoot,
			String outputCommentRoot, List<String> adjudicationRoots, List<String> commentRoots, Language lang) {
		System.out.println("RAW: " + rawRoot);
		System.out.println("ANN: " + annRoot);
		System.out.println("COM: " + outputCommentRoot);
		
		this.rawRoot = new File(rawRoot.trim());
		this.annRoot = new File(annRoot.trim());
		if (outputCommentRoot != null && !outputCommentRoot.equals("")) {
			this.outputCommentRoot = new File(outputCommentRoot.trim());
		} else {
			this.outputCommentRoot = null;
		}
		for (String adjudicationRoot : adjudicationRoots) {
			this.adjudicationRoots.add(new File(adjudicationRoot));
		}
		for (String commentRoot : commentRoots) {
			this.commentRoots.add(new File(commentRoot));
		}
		this.lang = lang;
		this.tokenizer = new Tokenizer(lang);
		File[] rawSections = this.rawRoot.listFiles();
		for (int i = 0; i < rawSections.length; i++) {
			if (rawSections[i].isDirectory()) {
				File[] rawFiles = rawSections[i].listFiles();
				// If we include empty sections, it screws things up
				if (rawFiles.length > 0) {
					Section section = new Section(rawSections[i].getName());
					for (int j = 0; j < rawFiles.length; j++) {
						if (rawFiles[j].isFile()) {
							section.add(rawFiles[j].getName());
						}
					}
					Collections.sort(section);
					rawSectionsList.add(section);
				}
			}
		}
		Collections.sort(rawSectionsList);

		// Create map of ptb files -> tipster files
		this.tipsterRoot = new File(tipsterRoot);
		try {
			File ptbTipsterFile;

			// Normal tipster file
			ptbTipsterFile = new File(this.tipsterRoot,
					Constants.ptbTipsterFilename);
			if (ptbTipsterFile.exists()) {
				in = new BufferedReader(new FileReader(
						ptbTipsterFile));
				for (String next = in.readLine(); next != null; next = in
						.readLine()) {
					Matcher matcher = ptbTipsterPattern.matcher(next);
					if (matcher.matches()) {
						String ptbSection = matcher.group(1);
						String ptbFile = matcher.group(2);
						String docNo1 = matcher.group(3) + ".";
						String docNo2 = null;
						String tipsterFile = "w9_" + matcher.group(6);
						if (!ptbTipsterMap.containsKey(ptbSection)) {
							ptbTipsterMap.put(ptbSection,
									new HashMap<String, TipsterFileDocNos>());
						}
						ptbTipsterMap.get(ptbSection).put(
								ptbFile,
								new TipsterFileDocNos(tipsterFile, docNo1,
										docNo2));
					}
				}
				in.close();
			}

			// Dual tipster file
			ptbTipsterFile = new File(this.tipsterRoot,
					Constants.ptbDualTipsterFilename);
			if (ptbTipsterFile.exists()) {
				in = new BufferedReader(new FileReader(
						ptbTipsterFile));
				for (String next = in.readLine(); next != null; next = in
						.readLine()) {
					Matcher matcher = ptbTipsterPattern.matcher(next);
					if (matcher.matches()) {
						String ptbSection = matcher.group(1);
						String ptbFile = matcher.group(2);
						String docNo1 = matcher.group(3) + ".";
						String docNo2 = matcher.group(5) + ".";
						String tipsterFile = "w9_" + matcher.group(6);
						if (!ptbTipsterMap.containsKey(ptbSection)) {
							ptbTipsterMap.put(ptbSection,
									new HashMap<String, TipsterFileDocNos>());
						}
						ptbTipsterMap.get(ptbSection).put(
								ptbFile,
								new TipsterFileDocNos(tipsterFile, docNo1,
										docNo2));
					}
				}
				in.close();
			}

			// Duplicates tipster file
			ptbTipsterFile = new File(this.tipsterRoot,
					Constants.ptbDuplicatesFilename);
			if (ptbTipsterFile.exists()) {
				in = new BufferedReader(new FileReader(
						ptbTipsterFile));
				for (String next = in.readLine(); next != null; next = in
						.readLine()) {
					Matcher matcher = ptbDuplicatesPattern.matcher(next);
					if (matcher.matches()) {
						String ptbSection = matcher.group(1);
						String ptbFile = matcher.group(2);
						String duplicateSection = matcher.group(3);
						String duplicateFile = matcher.group(4);
						if (ptbTipsterMap.containsKey(duplicateSection)
								&& ptbTipsterMap.get(duplicateSection)
										.containsKey(duplicateFile)) {
							TipsterFileDocNos tfdn = ptbTipsterMap.get(
									duplicateSection).get(duplicateFile);
							String docNo1 = tfdn.getDocNo1();
							String docNo2 = tfdn.getDocNo2();
							String tipsterFile = tfdn.getFile();
							if (!ptbTipsterMap.containsKey(ptbSection)) {
								ptbTipsterMap
										.put(ptbSection,
												new HashMap<String, TipsterFileDocNos>());
							}
							ptbTipsterMap.get(ptbSection).put(
									ptbFile,
									new TipsterFileDocNos(tipsterFile, docNo1,
											docNo2));
						}

					}
				}
				in.close();
			}

			// Create map of adjudication groups
			adjudicationGroupManager = new AdjudicationGroupManager(annRoot);

		} catch (IOException e) {
			// TODO: Should do a warning if tipster file is given but invalid
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public AdjudicationGroupManager getAdjudicationGroupManager() {
		return adjudicationGroupManager;
	}

	public Language getLanguage() {
		return lang;
	}

	public Vector<Section> getRawSectionsList() {
		return rawSectionsList;
	}

	public String getAnnRoot() {
		return annRoot.getAbsolutePath();
	}

	public Section getSection(int i) {
		return rawSectionsList.get(i);
	}

	public int getSecIndex() {
		return secIndex;
	}

	public String getSec() {
		return sec;
	}

	public int getFilIndex() {
		return filIndex;
	}

	public String getFil() {
		return fil;
	}

	public void setSecFile(int i, int j) {
		secIndex = i;
		filIndex = j;
		sec = rawSectionsList.get(i).getSec();
		fil = rawSectionsList.get(i).get(j);

		// set rawtext
		rawText = "";
		try {
			in = new BufferedReader(new InputStreamReader(
					new BufferedInputStream(new FileInputStream(new File(
							new File(rawRoot, sec), fil))), lang.getCharset()));
			for (String next = in.readLine(); next != null; next = in
					.readLine()) {
				rawText += next + "\n";
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// set tokens
		tokens = tokenizer.tokenize(rawText);
		ism = new IndexSpanMap(tokens, lang, rawText);

		// set title
		title = sec + File.separator + fil;

		relationList = new ArrayList<Relation>();
		adjudicationList = new ArrayList<Relation>();

		try {

			// set adjudicationmanager node
			adjudicationGroupManager.setSecFile(sec, fil);

			Properties commentMap = new Properties();
			/* Load comments */
			if (outputCommentRoot != null) {
				commentSec = new File(outputCommentRoot, sec);
				commentFil = new File(commentSec, fil);

				if (commentFil.exists()) {
					FileInputStream in = new FileInputStream(commentFil);
					commentMap.load(in);
					in.close();
				}
			}
			
			/* Load ann file into the relation list */
			annSec = new File(annRoot, sec);
			annFil = new File(annSec, fil);
			if (annFil.exists()) {
				in = new BufferedReader(new InputStreamReader(
						new BufferedInputStream(new FileInputStream(annFil)),
						"UTF8"));
				for (String next = in.readLine(); next != null; next = in
						.readLine()) {
					Relation relation = new Relation(this, next, commentMap);
					
					if (!relationList.contains(relation)) {
						relationList.add(relation);
					} else {
						System.err.println("ERROR: Duplicate Relation: " + fil + " "
								+ next);
					}
				}
				in.close();
			}
			
			int index = 0;
			/* Load adjudication files into a flat list */
			for (File adjudicationRoot : adjudicationRoots) {
				File adjudicationFile = new File(new File(adjudicationRoot, sec), fil);
				File currentCommentRoot = commentRoots.get(index);
				commentSec = new File(currentCommentRoot, sec);
				commentFil = new File(commentSec, fil);
				Properties currentCommentMap = new Properties();
				if (commentFil.exists()) {
					FileInputStream in = new FileInputStream(commentFil);
					currentCommentMap.load(in);
					in.close();
				}				
				if (adjudicationFile.exists()) {
					in = new BufferedReader(
							new InputStreamReader(new BufferedInputStream(
									new FileInputStream(adjudicationFile)),
									"UTF8"));
					for (String next = in.readLine(); next != null; next = in
							.readLine()) {
						adjudicationList.add(new Relation(this, next,
								currentCommentMap, adjudicationRoot.getPath()));

					}
					in.close();
				}
				index++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// set tipster data
		if (ptbTipsterMap.containsKey(sec)
				&& ptbTipsterMap.get(sec).containsKey(fil)) {
			TipsterFileDocNos tipsterFileDocNo = ptbTipsterMap.get(sec)
					.get(fil);
			tipsterParser1 = TipsterParser.TipsterParserFactory(new File(
					tipsterRoot, tipsterFileDocNo.getFile()), tipsterFileDocNo
					.getDocNo1());
			tipsterParser2 = TipsterParser.TipsterParserFactory(new File(
					tipsterRoot, tipsterFileDocNo.getFile()), tipsterFileDocNo
					.getDocNo2());
		} else {
			tipsterParser1 = null;
			tipsterParser2 = null;
		}

	}

	public void insertAdjudicationRelations() {
		/* We don't want to auto-adjudicate if we have already */
		boolean firstTime = relationList.isEmpty();

		/* Remove ghosts and clear all sublists */
		for (ListIterator<Relation> i = relationList.listIterator(); i
				.hasNext();) {
			Relation relation = i.next();
			if (relation.isGhost()) {
				i.remove();
			} else {
				relation.clearAdjudications();
			}
		}

		/* Insert the sublists according to group number or similarity */
		for (Relation adjudication : adjudicationList) {
			Relation parent = null;
			if (adjudication.getGroupNumber() != -1) {
				for (Relation relation : relationList) {
					if (relation.getGroupNumber() == adjudication
							.getGroupNumber()) {
						parent = relation;
						break;
					}
				}
			}
			if (parent == null && adjudication.getGroupNumber() == -1) {
				for (Relation relation : relationList) {
					if (relation.isSimilar(adjudication)) {
						parent = relation;
						break;
					}
				}
			}
			if (parent == null) {
				parent = new Relation(adjudication);
				relationList.add(parent);
				System.out.println("parent before adding adju: " + parent.isGhost());
			}
			parent.addAdjudication(adjudication);
			System.out.println("parent after adding adju: " + parent.isGhost());
			
		}

		/*
		 * Automatically adjudicate annotations that are equivalent and copy
		 * these to the output
		 */
		boolean anyNew = false;
		/*for (Relation relation : relationList) {
			if (relation.isGhost() && firstTime) {
				boolean areEquivalent = true;
				for (Relation adjudication : relation.getAdjudications()) {
					areEquivalent &= adjudication.getSubGroupNumber() == 1;
				}
				if (areEquivalent) {
					relation.copyFrom(relation.getAdjudications().get(0));
					anyNew = true;
				}
			}
		}*/

		// TODO: uncollapse after deletes

		/* Reset group numbers */
		int i = 1;
		for (Relation relation : relationList) {
			relation.setGroupNumber(i);
			i++;
		}
		Collections.sort(relationList);
		
		if (anyNew) {
			save();
		}
	}

	public SpanString[] tokenize(String s) {
		return tokenizer.tokenize(s);
	}

	public IndexSpanMap getIndexSpanMap() {
		return ism;
	}

	public SpanString[] getTokens() {
		return tokens;
	}

	public String getRawText() {
		return rawText;
	}

	public TipsterParser getTipsterData1() {
		return tipsterParser1;
	}

	public TipsterParser getTipsterData2() {
		return tipsterParser2;
	}

	public String getTitle() {
		return title;
	}

	public List<Relation> getRelationList() {
		return relationList;
	}

	public void save() {
		try {
			annSec.mkdirs();
			BufferedWriter annOut = new BufferedWriter(new OutputStreamWriter(
					new BufferedOutputStream(new FileOutputStream(annFil)),
					"UTF8"));
			Properties commentMap = new Properties();
			for (Iterator<Relation> i = relationList.iterator(); i.hasNext();) {
				Relation relation = i.next();
				if (!relation.isGhost()) {
					annOut.write(relation.getTextLine());
					annOut.newLine();
					String comment = relation.getComment();
					if (comment != null) {
						commentMap.setProperty(relation.getRelationID(),
								comment);
					}
				}
			}
			annOut.close();

			
			//write to output comment root if comment root is set
			File outputCommentSec = null;
			File outputCommentFil = null;
			
			//System.out.println("OCR:" + 	outputCommentRoot.getAbsolutePath() + "XXXXX");		
			
			if (outputCommentRoot != null) {	
				outputCommentSec = new File(outputCommentRoot, sec);
				outputCommentFil = new File(outputCommentSec, fil);
				if (commentMap.isEmpty()) {
					//commentFil.delete();
					outputCommentFil.delete();
				}	 else {			
					System.out.println("WRITING COMMENTS");
					//commentSec.mkdirs();
					outputCommentSec.mkdirs();				
					//FileOutputStream commentOut = new FileOutputStream(commentFil);
					FileOutputStream commentOut = new FileOutputStream(outputCommentFil);
					commentMap.store(commentOut, null);
					commentOut.close();
				}
			}


			if (relationList.size() == 0) {  //no relations left
				if (annFil.exists()) {
					annFil.delete();
				}
				if (annSec.listFiles().length == 0) {
					annSec.delete();
				}
			}			

			if (relationList.size() == 0) {  //no relations left
				if (annFil.exists()) {
					annFil.delete();
				}
				
				if (annSec.exists()) {					
					if (annSec.listFiles().length == 0) {
						annSec.delete();
					}
				}
				
				if (outputCommentFil != null) {
					if (outputCommentFil.exists()) {
						outputCommentFil.delete();
					}
				}

				if (outputCommentSec != null) {
					if (outputCommentSec.exists()) {
						if (outputCommentSec.listFiles().length == 0) {
							outputCommentSec.delete();
						}
					}
				}
			}
			
			adjudicationGroupManager.save(relationList);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public class Section extends Vector<String> implements Comparable<Section> {

		private static final long serialVersionUID = 1L;
		private String sec;

		public Section(String sec) {
			this.sec = sec;
		}

		public String getSec() {
			return sec;
		}

		@Override
		public String toString() {
			return sec;
		}

		// @Override
		public int compareTo(Section o) {
			return sec.compareTo(o.getSec());
		}

	}

}

