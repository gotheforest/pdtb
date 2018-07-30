package edu.upenn.cis.anntool.util;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import edu.upenn.cis.anntool.gui.AnnComponent;
import edu.upenn.cis.anntool.settings.Constants.LABELS;
import edu.upenn.cis.anntool.settings.Constants.RELTYPELABELS;

public class Relation implements Comparable<Relation>, Transferable {

	public final static String sep = "|";
	private String[] originalVals;
	private FileManager fileManager;
	private String comment = "";
	private String root;
	private boolean isGhost = false;
	private boolean isRejected = false;
	private List<Relation> adjudicationRelations = new ArrayList<Relation>();
	private int subGroupNumber = 1;
	private int groupNumber = -1;

	/**
	 * Used to create a new default Relation
	 * 
	 * @param rawText
	 */
	public Relation(FileManager fileManager) {
		this.fileManager = fileManager;
		originalVals = new String[LABELS.values().length];
		for (int i = 0; i < originalVals.length; i++) {
			originalVals[i] = "";
		}		
	}

	/**
	 * Used by the connective search to create a relation based on the
	 * Connective Spans
	 * 
	 * @param rawText
	 * @param connSpan
	 */
	public Relation(FileManager fileManager, Span connSpan) {
		this.fileManager = fileManager;
		originalVals = new String[LABELS.values().length];
		for (int i = 0; i < originalVals.length; i++) {
			originalVals[i] = "";
		}
		originalVals[LABELS.connSpanList.ordinal()] = connSpan.toString();
		originalVals[LABELS.rel.ordinal()] = RELTYPELABELS.Explicit.toString();
	}

	/**
	 * Used to create a relation from a line in an annotation file
	 * 
	 * @param rawText
	 * @param line
	 * @param commentMap
	 */
	public Relation(FileManager fileManager, String line, Properties commentMap) {
		this.fileManager = fileManager;
		if (fileManager == null) {
			this.root = "";
		} else {
			this.root = fileManager.getAnnRoot();
		}
		originalVals = line.split(Pattern.quote(sep), LABELS.values().length);
		if (commentMap.containsKey(getRelationID())) {
			comment = commentMap.getProperty(getRelationID());
		}
		if (fileManager != null) {
			groupNumber = fileManager.getAdjudicationGroupManager()
					.getGroupNumber(this);
		}
		if (originalVals[LABELS.adjudicationReason.ordinal()].equals("Rejected")) {
			isRejected = true;
		}
	}

	/**
	 * Used to create a relation from a line in an adjudication file
	 * 
	 * @param rawText
	 * @param line
	 * @param commentMap
	 * @param root
	 */
	public Relation(FileManager fileManager, String line,
			Properties commentMap, String root) {
		this.fileManager = fileManager;
		this.root = root;
		originalVals = line.split(Pattern.quote(sep), LABELS.values().length);
		if (commentMap.containsKey(getRelationID())) {
			comment = commentMap.getProperty(getRelationID());
		}
		groupNumber = fileManager.getAdjudicationGroupManager().getGroupNumber(
				this);
	}

	/**
	 * Used to create a ghost copy
	 */
	public Relation(Relation relation) {
		this.isGhost = true;
		this.fileManager = relation.fileManager;
		this.originalVals = new String[LABELS.values().length];
		for (int i = 0; i < originalVals.length; i++) {
			originalVals[i] = "";
		}
		this.originalVals[LABELS.rel.ordinal()] = relation.originalVals[LABELS.rel
				.ordinal()];
		this.originalVals[LABELS.connSpanList.ordinal()] = relation.originalVals[LABELS.connSpanList
				.ordinal()];
		this.originalVals[LABELS.arg2SpanList.ordinal()] = relation.originalVals[LABELS.arg2SpanList
				.ordinal()];
		this.originalVals[LABELS.arg2AttrSpanList.ordinal()] = relation.originalVals[LABELS.arg2AttrSpanList
		                                                         				.ordinal()];	
		
		this.originalVals[LABELS.adjudicationReason.ordinal()] = relation.originalVals[LABELS.adjudicationReason.ordinal()];
		this.originalVals[LABELS.adjudicationDisagreement.ordinal()] = relation.originalVals[LABELS.adjudicationDisagreement.ordinal()];		
		
		this.originalVals[LABELS.propBankRole.ordinal()] = relation.originalVals[LABELS.propBankRole.ordinal()];
		this.originalVals[LABELS.propBankVerb.ordinal()] = relation.originalVals[LABELS.propBankVerb.ordinal()];
		this.originalVals[LABELS.identifier.ordinal()] = relation.originalVals[LABELS.identifier.ordinal()];
		this.originalVals[LABELS.identifierType.ordinal()] = relation.originalVals[LABELS.identifierType.ordinal()];		
		
		subGroupNumber = 0;
		groupNumber = relation.groupNumber;
	}

	public void copyFrom(Relation relation) {
		this.isGhost = false;
		this.subGroupNumber = 1;
		for (int i = 0; i < relation.originalVals.length; i++) {
			this.originalVals[i] = relation.originalVals[i];
		}
		this.root = fileManager.getAnnRoot();
	}

	public void setGroupNumber(int i) {
		groupNumber = i;
		for (Relation adjudication : adjudicationRelations) {
			adjudication.groupNumber = i;
		}
	}

	public boolean isGhost() {
		return isGhost;
	}
	
	public String getRoot() {
		return root;
	}

	public List<Relation> getAdjudications() {
		return adjudicationRelations;
	}

	public void clearAdjudications() {
		adjudicationRelations.clear();
	}

	public void addAdjudication(Relation adjudication) {
		if (isEquivalent(adjudication)) {
			adjudication.subGroupNumber = 1;
		} else {
			boolean matchFound = false;
			for (Relation relation : adjudicationRelations) {
				if (adjudication.isEquivalent(relation)) {
					adjudication.subGroupNumber = relation.subGroupNumber;
					matchFound = true;
					break;
				}
			}
			if (!matchFound) {
				if (adjudicationRelations.isEmpty() && isGhost) {
					adjudication.subGroupNumber = 1;
				} else if (adjudicationRelations.isEmpty()) {
					adjudication.subGroupNumber = 2;
				} else {
					adjudication.subGroupNumber = adjudicationRelations
							.get(adjudicationRelations.size() - 1).subGroupNumber + 1;
				}
			}
		}
		adjudicationRelations.add(adjudication);
	}

	public int getSubGroupNumber() {
		return subGroupNumber;
	}

	public void setComponents(AnnComponent[] components, AnnComponent commentPane) {
		for (int i = 0; i < components.length; i++) {
			components[i].setAnnValue(originalVals[i]);
		}
		commentPane.setAnnValue(comment);
	}
	
	public void setPBComponents() {
		if (adjudicationRelations.size() > 0) { //don't do this for new relations with no children
			originalVals[LABELS.propBankRole.ordinal()] = ((Relation) adjudicationRelations.get(0)).getPropBankRole();
			originalVals[LABELS.propBankVerb.ordinal()] = ((Relation) adjudicationRelations.get(0)).getPropBankVerb();
		}
	}

	/**
	 * Checks if every part of a relation is equal to every part of another
	 */
	public boolean isEquivalent(AnnComponent[] components,
			AnnComponent commentPane) {
		for (int i = 0; i < components.length; i++) {
			if ((components[i].getJComponent().isEnabled() || !originalVals[i]
					.equals(""))
					&& !components[i].getAnnValue().equals(originalVals[i])) {
				return false;
			}
		}
		if (!commentPane.getAnnValue().equals(comment)) {
			return false;
		}
		return true;
	}

	/**
	 * Checks if all annotations within the same group are completely equal
	 * 
	 * @return
	 */
	public boolean areAdjudicationsEquivalent() {
		for (Relation relation : adjudicationRelations) {
			if (!isEquivalent(relation)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if every part of a relation is equal to every part of another
	 */
	public boolean isEquivalent(Relation other) {
		for (int i = 0; i < LABELS.values().length; i++) {
			if (!originalVals[i].equals(other.originalVals[i])) {
				return false;
			}
		}
		return true;
	}

	public void setNewVals(AnnComponent[] components, AnnComponent commentPane) {
		for (int i = 0; i < components.length; i++) {
			if (components[i].getJComponent().isEnabled()) {
				originalVals[i] = components[i].getAnnValue();
			} else {
				originalVals[i] = "";
			}
		}	
		
		originalVals[LABELS.adjudicationReason.ordinal()] = components[LABELS.adjudicationReason.ordinal()].getAnnValue();
		originalVals[LABELS.adjudicationDisagreement.ordinal()] = components[LABELS.adjudicationDisagreement.ordinal()].getAnnValue();
		originalVals[LABELS.propBankRole.ordinal()] = components[LABELS.propBankRole.ordinal()].getAnnValue();
		originalVals[LABELS.propBankVerb.ordinal()] = components[LABELS.propBankVerb.ordinal()].getAnnValue();
		originalVals[LABELS.identifier.ordinal()] = components[LABELS.identifier.ordinal()].getAnnValue();
		originalVals[LABELS.identifierType.ordinal()] = components[LABELS.identifierType.ordinal()].getAnnValue();
		
		setIdentifiers();
		
		isRejected = false;
		comment = commentPane.getAnnValue();
		isGhost = false;
		root = fileManager.getAnnRoot();
	}
	
	public void setAcceptVals(AnnComponent[] components, AnnComponent commentPane) {
		setNewVals(components, commentPane);
		originalVals[LABELS.adjudicationReason.ordinal()] = "Full Agreement";
		//originalVals[LABELS.consensus.ordinal()] =  "true";
	}
	
	public void setReject(AnnComponent[] components, AnnComponent commentPane) {
		originalVals[LABELS.adjudicationDisagreement.ordinal()] = "";
		originalVals[LABELS.adjudicationReason.ordinal()] = "Rejected";		
		//originalVals[LABELS.consensus.ordinal()] = "";
		
		//commentPane.setAnnValue("");
		comment = commentPane.getAnnValue();
		isGhost = false;
		isRejected = true;
		root = fileManager.getAnnRoot();
	}
	
	public void undoReject(AnnComponent[] components, AnnComponent commentPane) {
		originalVals[LABELS.adjudicationReason.ordinal()] = "";
		isRejected = false;
	}
	
	public boolean isRejected() {
		return isRejected;
	}
	
	public void setNoVals(AnnComponent[] components, AnnComponent commentPane) {
		String originalRel = originalVals[LABELS.rel.ordinal()];
		String originalConnSpanList = originalVals[LABELS.rel.connSpanList.ordinal()];
		String originalArg2SpanList = originalVals[LABELS.arg2SpanList.ordinal()];
		
		for (int i = 0; i < components.length; i++) {
			originalVals[i] = "";
		}
		
		originalVals[LABELS.rel.ordinal()] = originalRel; //"NoRel";
		originalVals[LABELS.adjudicationReason.ordinal()] = "Rejected";
		originalVals[LABELS.connSpanList.ordinal()] = originalConnSpanList;
		originalVals[LABELS.arg2SpanList.ordinal()] = ((Relation) adjudicationRelations.get(0)).originalVals[LABELS.arg2SpanList.ordinal()];
		setPBComponents();
		originalVals[LABELS.identifier.ordinal()] = components[LABELS.identifier.ordinal()].getAnnValue();
		originalVals[LABELS.identifierType.ordinal()] = components[LABELS.identifierType.ordinal()].getAnnValue();		
		//commentPane.setAnnValue("");
		comment = commentPane.getAnnValue();
		isGhost = false;
		isRejected = true;
		root = fileManager.getAnnRoot();
	}
	
	public String getAdjuDiff() {
		return originalVals[LABELS.adjudicationDisagreement.ordinal()];
	}
	
	public String getPropBankRole() {
		return originalVals[LABELS.propBankRole.ordinal()];
	}
	
	public String getPropBankVerb() {
		return originalVals[LABELS.propBankVerb.ordinal()];
	}	
	


	public String getTextLine() {
		setIdentifiers();
		
		String ret = "";
		for (int i = 0; i < originalVals.length; i++) {
			ret += originalVals[i];
			if (i < originalVals.length - 1) {
				ret += sep;
			}
		}
		return ret;
	}

	public String getComment() {
		if (!comment.equals("")) {
			return comment;
		}
		return null;
	}

	public String getRelationID() {
		return originalVals[LABELS.rel.ordinal()] + sep
				+ originalVals[LABELS.connSpanList.ordinal()] + sep
				+ originalVals[LABELS.arg1SpanList.ordinal()] + sep
				+ originalVals[LABELS.arg2SpanList.ordinal()];
	}

	public boolean isComplete() {
		boolean isComplete = true;
		String relType = originalVals[LABELS.rel.ordinal()];
		RELTYPELABELS relTypeLabel;
		try {
			relTypeLabel = RELTYPELABELS.valueOf(relType);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		switch (relTypeLabel) {
		case Explicit:
		case AltLex:
		case AltLexC:
			isComplete &= !originalVals[LABELS.connSpanList.ordinal()]
					.equals("");
			isComplete &= !originalVals[LABELS.connAttrSrc.ordinal()]
					.equals("");
			isComplete &= !originalVals[LABELS.connAttrType.ordinal()]
					.equals("");
			isComplete &= !originalVals[LABELS.connAttrPol.ordinal()]
					.equals("");
			isComplete &= !originalVals[LABELS.connAttrDet.ordinal()]
					.equals("");
			isComplete &= !originalVals[LABELS.arg1SpanList.ordinal()]
					.equals("");
			isComplete &= !originalVals[LABELS.arg1AttrSrc.ordinal()]
					.equals("");
			isComplete &= !originalVals[LABELS.arg1AttrType.ordinal()]
					.equals("");
			isComplete &= !originalVals[LABELS.arg1AttrPol.ordinal()]
					.equals("");
			isComplete &= !originalVals[LABELS.arg1AttrDet.ordinal()]
					.equals("");
			isComplete &= !originalVals[LABELS.arg2SpanList.ordinal()]
					.equals("");
			isComplete &= !originalVals[LABELS.arg2AttrSrc.ordinal()]
					.equals("");
			isComplete &= !originalVals[LABELS.arg2AttrType.ordinal()]
					.equals("");
			isComplete &= !originalVals[LABELS.arg2AttrPol.ordinal()]
					.equals("");
			isComplete &= !originalVals[LABELS.arg2AttrDet.ordinal()]
					.equals("");
			isComplete &= !originalVals[LABELS.sClass1A.ordinal()].equals("");
			break;
		case Implicit:
			isComplete &= !originalVals[LABELS.connAttrSrc.ordinal()]
					.equals("");
			isComplete &= !originalVals[LABELS.connAttrType.ordinal()]
					.equals("");
			isComplete &= !originalVals[LABELS.connAttrPol.ordinal()]
					.equals("");
			isComplete &= !originalVals[LABELS.connAttrDet.ordinal()]
					.equals("");
			isComplete &= !originalVals[LABELS.arg1SpanList.ordinal()]
					.equals("");
			isComplete &= !originalVals[LABELS.arg1AttrSrc.ordinal()]
					.equals("");
			isComplete &= !originalVals[LABELS.arg1AttrType.ordinal()]
					.equals("");
			isComplete &= !originalVals[LABELS.arg1AttrPol.ordinal()]
					.equals("");
			isComplete &= !originalVals[LABELS.arg1AttrDet.ordinal()]
					.equals("");
			isComplete &= !originalVals[LABELS.arg2SpanList.ordinal()]
					.equals("");
			isComplete &= !originalVals[LABELS.arg2AttrSrc.ordinal()]
					.equals("");
			isComplete &= !originalVals[LABELS.arg2AttrType.ordinal()]
					.equals("");
			isComplete &= !originalVals[LABELS.arg2AttrPol.ordinal()]
					.equals("");
			isComplete &= !originalVals[LABELS.arg2AttrDet.ordinal()]
					.equals("");
			isComplete &= !originalVals[LABELS.conn1.ordinal()].equals("");
			isComplete &= !originalVals[LABELS.sClass1A.ordinal()].equals("");
			// Slight simplification of logic 2011-10-17
			isComplete &= originalVals[LABELS.conn2.ordinal()].equals("")
					|| !originalVals[LABELS.sClass2A.ordinal()].equals("");
			break;
		case EntRel:
		case NoRel:
			isComplete &= !originalVals[LABELS.arg1SpanList.ordinal()]
					.equals("");
			isComplete &= !originalVals[LABELS.arg2SpanList.ordinal()]
					.equals("");
			break;
		}
		return isComplete;
	}

	private RELTYPELABELS getRelTypeLabel() {
		try {
			return RELTYPELABELS.valueOf(originalVals[LABELS.rel.ordinal()]);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public int getLocationStart() {		
		if (isRejected) {
			String value = originalVals[LABELS.identifier.ordinal()];
			
			if (value.contains("..")) {
				return getLocationStart(LABELS.identifier);				 
			} else {		
				return getLocationStart(LABELS.arg2SpanList);
			}
		}
		
		RELTYPELABELS relTypeLabel = getRelTypeLabel();
				
		if (relTypeLabel == RELTYPELABELS.Explicit) {
//				|| relTypeLabel == RELTYPELABELS.AltLex) {
			return getLocationStart(LABELS.connSpanList);
		} else {
			int attrStart = getLocationStart(LABELS.arg2AttrSpanList);
			int argStart = getLocationStart(LABELS.arg2SpanList);

			if (attrStart == 0) {
				return argStart;
			} else if (argStart <= attrStart) {
				return argStart;
			} else {
				return attrStart;
			}

			//return getLocationStart(LABELS.arg2SpanList);
		}
	}
	
	public void setIdentifiers() {				
		String test = originalVals[LABELS.identifierType.ordinal()];
		
		if (test.equals("DEFAULT") || test.equals("")) {
			RELTYPELABELS relTypeLabel = getRelTypeLabel();
			if (relTypeLabel == RELTYPELABELS.Explicit || relTypeLabel == RELTYPELABELS.AltLex) {
				if (!originalVals[LABELS.connSpanList.ordinal()].equals("")) {
					originalVals[LABELS.identifier.ordinal()] = originalVals[LABELS.connSpanList.ordinal()].split(";")[0];
				}
				//else {
				//	originalVals[LABELS.identifier.ordinal()] = "0..1";
				//}
			} else if (relTypeLabel == RELTYPELABELS.Implicit || relTypeLabel == RELTYPELABELS.EntRel || relTypeLabel == RELTYPELABELS.NoRel) {
				if (originalVals[LABELS.arg2SpanList.ordinal()].equals("")) {
					//originalVals[LABELS.identifier.ordinal()] = "0";
				} else {
					SpanList arg2SpanList = new SpanList(originalVals[LABELS.arg2SpanList.ordinal()]);
					int arg2Start = ((Span) arg2SpanList.first()).getStart();
					String arg2StartStr = Integer.toString(arg2Start);
					originalVals[LABELS.identifier.ordinal()] = arg2StartStr;
				}
			}
		
			originalVals[LABELS.identifierType.ordinal()] = "DEFAULT";
		}
	}
	
	public String getTask() {
		return originalVals[LABELS.identifierType.ordinal()];
	}
	
	public String getIdentifier() {
		String ident = originalVals[LABELS.identifier.ordinal()];
		String identType = originalVals[LABELS.identifierType.ordinal()];
		return ident + "-" + identType;
	}
	
	public String getIdentifierSpan() {
		return originalVals[LABELS.identifier.ordinal()];
	}
	
	public String getRelationType() {
		return originalVals[LABELS.rel.ordinal()];
	}

	private int getLocationStart(LABELS label) {
		SpanList spanList = new SpanList(originalVals[label.ordinal()]);
		if (spanList.size() > 0) {
			Span span = (Span) spanList.first();
			return span.getStart();
		} else {
			return 0;
		}
	}

	public int getLocationEnd() {
		RELTYPELABELS relTypeLabel = getRelTypeLabel();
		if (relTypeLabel == RELTYPELABELS.Explicit
				|| relTypeLabel == RELTYPELABELS.AltLex) {
			return getLocationEnd(LABELS.connSpanList);
		} else {
			return getLocationEnd(LABELS.arg2SpanList);
		}
	}

	private int getLocationEnd(LABELS label) {
		SpanList spanList = new SpanList(originalVals[label.ordinal()]);
		if (spanList.size() > 0) {
			Span span = (Span) spanList.last();
			return span.getEnd();
		} else {
			return 0;
		}
	}

	private int getLocationRelType() {
		return getRelTypeLabel().ordinal();
	}

	public int getGroupNumber() {
		return groupNumber;
	}

	/**
	 * Adjudications are grouped together if they are "similar"
	 * */
	
	public boolean isSimilar(Relation o) {
		if ( (getRelTypeLabel() == RELTYPELABELS.EntRel && o.getRelTypeLabel() == RELTYPELABELS.AltLex)   ||
			 (getRelTypeLabel() == RELTYPELABELS.AltLex && o.getRelTypeLabel() == RELTYPELABELS.EntRel)	) {
			if (getIdentifier().equals(o.getIdentifier()) && getTask().equals(o.getTask())) {
				return true;
			}
		}
		
		if ( (getRelTypeLabel() == RELTYPELABELS.Implicit && o.getRelTypeLabel() == RELTYPELABELS.AltLex) ||
			 (getRelTypeLabel() == RELTYPELABELS.AltLex && o.getRelTypeLabel() == RELTYPELABELS.Implicit) ||
			 (getRelTypeLabel() == RELTYPELABELS.EntRel && o.getRelTypeLabel() == RELTYPELABELS.AltLex)   ||
			 (getRelTypeLabel() == RELTYPELABELS.AltLex && o.getRelTypeLabel() == RELTYPELABELS.EntRel)	  ||
			 (getRelTypeLabel() == RELTYPELABELS.NoRel && o.getRelTypeLabel() == RELTYPELABELS.AltLex)    ||
			 (getRelTypeLabel() == RELTYPELABELS.AltLex && o.getRelTypeLabel() == RELTYPELABELS.NoRel)	) {
			
			return getLocationStart() == o.getLocationStart() && getTask().equals(o.getTask());			
		} 		
		return getIdentifier().equals(o.getIdentifier());
	}
	
	
	public boolean isIdentical(Relation o) {
		if (getRelTypeLabel() == o.getRelTypeLabel()) {
			boolean arg1Same = originalVals[LABELS.arg1SpanList.ordinal()].equals(o.originalVals[LABELS.arg1SpanList.ordinal()]);
			boolean arg2Same = originalVals[LABELS.arg2SpanList.ordinal()].equals(o.originalVals[LABELS.arg2SpanList.ordinal()]);
			
			if (getRelTypeLabel() == RELTYPELABELS.Explicit) {
				boolean connSame = originalVals[LABELS.connSpanList.ordinal()].equals(o.originalVals[LABELS.connSpanList.ordinal()]);
				boolean sense1aSame = originalVals[LABELS.sClass1A.ordinal()].equals(o.originalVals[LABELS.sClass1A.ordinal()]);
				boolean sense1bSame = originalVals[LABELS.sClass1B.ordinal()].equals(o.originalVals[LABELS.sClass1B.ordinal()]);				
				return connSame && arg1Same && arg2Same && sense1aSame && sense1bSame;
			} else if (getRelTypeLabel() == RELTYPELABELS.Implicit) {
				boolean conn1Same = originalVals[LABELS.conn1.ordinal()].equals(o.originalVals[LABELS.conn1.ordinal()]);
				boolean conn2Same = originalVals[LABELS.conn2.ordinal()].equals(o.originalVals[LABELS.conn2.ordinal()]);				
				boolean sense1aSame = originalVals[LABELS.sClass1A.ordinal()].equals(o.originalVals[LABELS.sClass1A.ordinal()]);
				boolean sense1bSame = originalVals[LABELS.sClass1B.ordinal()].equals(o.originalVals[LABELS.sClass1B.ordinal()]);
				boolean sense2aSame = originalVals[LABELS.sClass2A.ordinal()].equals(o.originalVals[LABELS.sClass2A.ordinal()]);
				boolean sense2bSame = originalVals[LABELS.sClass2B.ordinal()].equals(o.originalVals[LABELS.sClass2B.ordinal()]);				
				return conn1Same && conn2Same && arg1Same && arg2Same && sense1aSame && sense1bSame && sense2aSame && sense2bSame;
			} else if (getRelTypeLabel() == RELTYPELABELS.AltLex) {
				boolean connSame = originalVals[LABELS.connSpanList.ordinal()].equals(o.originalVals[LABELS.connSpanList.ordinal()]);
				boolean sense1aSame = originalVals[LABELS.sClass1A.ordinal()].equals(o.originalVals[LABELS.sClass1A.ordinal()]);
				boolean sense1bSame = originalVals[LABELS.sClass1B.ordinal()].equals(o.originalVals[LABELS.sClass1B.ordinal()]);				
				return connSame && arg1Same && arg2Same && sense1aSame && sense1bSame;
			} else if (getRelTypeLabel() == RELTYPELABELS.AltLexC) {
				boolean connSame = originalVals[LABELS.connSpanList.ordinal()].equals(o.originalVals[LABELS.connSpanList.ordinal()]);
				boolean sense1aSame = originalVals[LABELS.sClass1A.ordinal()].equals(o.originalVals[LABELS.sClass1A.ordinal()]);
				boolean sense1bSame = originalVals[LABELS.sClass1B.ordinal()].equals(o.originalVals[LABELS.sClass1B.ordinal()]);				
				return connSame && arg1Same && arg2Same && sense1aSame && sense1bSame;				
			} else if (getRelTypeLabel() == RELTYPELABELS.Hypophora) {
				return arg1Same && arg2Same;
			} else if (getRelTypeLabel() == RELTYPELABELS.EntRel) {
				return arg1Same && arg2Same;
			} else if (getRelTypeLabel() == RELTYPELABELS.NoRel) {
				return arg1Same && arg2Same;
			} else {
				return false;
			}
		}
		
		return false;
	}
	
	
/*	public boolean isSimilar(Relation o) {	
		if ((getRelTypeLabel() == RELTYPELABELS.Explicit)
			&& 
			(o.getRelTypeLabel() == RELTYPELABELS.Explicit)
			&& 
			((getLocationStart() <= o.getLocationStart() && getLocationEnd() > o.getLocationStart()) 
			|| 
			(o.getLocationStart() <= getLocationStart() && o.getLocationEnd() > getLocationStart()))) {
			return true;
		} else if ((getRelTypeLabel() == RELTYPELABELS.Implicit
				|| getRelTypeLabel() == RELTYPELABELS.EntRel 
				|| getRelTypeLabel() == RELTYPELABELS.NoRel 
				|| getRelTypeLabel() == RELTYPELABELS.AltLex)
				&& 
				(o.getRelTypeLabel() == RELTYPELABELS.Implicit
				|| o.getRelTypeLabel() == RELTYPELABELS.EntRel 
				|| o.getRelTypeLabel() == RELTYPELABELS.NoRel
				|| o.getRelTypeLabel() == RELTYPELABELS.AltLex)
				&& 
				getIdentifier() == o.getIdentifier()) {
//				getLocationStart() == o.getLocationStart()) {			
			return true;
		}
		
		
//		if (getRelTypeLabel() == o.getRelTypeLabel()
//				|| (getRelTypeLabel() != RELTYPELABELS.Explicit && o
//						.getRelTypeLabel() != RELTYPELABELS.Explicit)) {
//			return getLocationStart() == o.getLocationStart();
//		}

		// if (getRelTypeLabel() == o.getRelTypeLabel()) {
		// if (getRelTypeLabel() == RELTYPELABELS.Explicit
		// || getRelTypeLabel() == RELTYPELABELS.AltLex) {
		// return getLocationStart() == o.getLocationStart();
		// } else {
		// /**
		// * If both have sentence nos, compare those, otherwise compare
		// * arg2 start
		// */
		// String sentenceNo1 = originalVals[LABELS.connSpanList.ordinal()];
		// String sentenceNo2 = o.originalVals[LABELS.connSpanList
		// .ordinal()];
		// if (sentenceNo1.equals("") || sentenceNo2.equals("")) {
		// return getLocationStart() == o.getLocationStart();
		// } else {
		// return sentenceNo1.equals(sentenceNo2);
		// }
		// }
		// }
//		return false;
//	}

	public int compareToDebugging(Relation o) {
		/* Current grouping */
		// if (getRelTypeLabel() == o.getRelTypeLabel()
		// || (getRelTypeLabel() != RELTYPELABELS.Explicit && o
		// .getRelTypeLabel() != RELTYPELABELS.Explicit)) {
		// return getLocationStart() - o.getLocationStart();
		// } else {
		// return -1;
		// }
		/* Just the starts */
		// return getLocationStart() - o.getLocationStart();
		/* AltLex/Expl vs Others */

		/* Explicits and Altlexs together */

		// if (getLocationStart() != o.getLocationStart()) {
		// return getLocationStart() - o.getLocationStart();
		// } else if (((getRelTypeLabel() == RELTYPELABELS.Explicit ||
		// getRelTypeLabel() == RELTYPELABELS.AltLex) && (o
		// .getRelTypeLabel() != RELTYPELABELS.Explicit && o
		// .getRelTypeLabel() != RELTYPELABELS.AltLex))
		// || (getRelTypeLabel() != RELTYPELABELS.Explicit
		// && getRelTypeLabel() != RELTYPELABELS.AltLex && (o
		// .getRelTypeLabel() == RELTYPELABELS.Explicit || o
		// .getRelTypeLabel() == RELTYPELABELS.AltLex))) {
		// return getLocationRelType() - o.getLocationRelType();
		// } else {
		// return 0;
		// }
				
		if ((getRelTypeLabel() == RELTYPELABELS.Explicit || getRelTypeLabel() == RELTYPELABELS.AltLex)
				&& (o.getRelTypeLabel() == RELTYPELABELS.Explicit || o
						.getRelTypeLabel() == RELTYPELABELS.AltLex)
				&& ((getLocationStart() <= o.getLocationStart() && getLocationEnd() > o
						.getLocationStart()) || (o.getLocationStart() <= getLocationStart() && o
						.getLocationEnd() > getLocationStart()))) {
			return 0;
		} else if ((getRelTypeLabel() == RELTYPELABELS.Implicit
				|| getRelTypeLabel() == RELTYPELABELS.EntRel || getRelTypeLabel() == RELTYPELABELS.NoRel)
				&& (o.getRelTypeLabel() == RELTYPELABELS.Implicit
						|| o.getRelTypeLabel() == RELTYPELABELS.EntRel || o
						.getRelTypeLabel() == RELTYPELABELS.NoRel)
				&& getLocationStart() == o.getLocationStart()) {
			return 0;
		} else {
			return -1;
		}

	}

	// @Override
	/**
	 * Used for sorting relations. negative if o is bigger If a duplicate
	 * relation is created, it does not save the relation and moves selection to
	 * the duplicate
	 */
	public int compareTo(Relation o) {	
		if (getLocationStart() != o.getLocationStart()) {
			return getLocationStart() - o.getLocationStart();
		} else if (this.isRejected() != o.isRejected()) {
//			return 1;
			return getLocationRelType() - o.getLocationRelType();
		} else if (getRelTypeLabel() != o.getRelTypeLabel()) {
			return getLocationRelType() - o.getLocationRelType();
		}
		/*
		 * else if (getLocationRelType() != o.getLocationRelType()) { return
		 * getLocationRelType() - o.getLocationRelType(); }
		 */
		else if ((getRelTypeLabel() == RELTYPELABELS.Explicit && o
				.getRelTypeLabel() != RELTYPELABELS.Explicit)
				|| (getRelTypeLabel() != RELTYPELABELS.Explicit && o
						.getRelTypeLabel() == RELTYPELABELS.Explicit)) {
			return getLocationRelType() - o.getLocationRelType();
		} else if (isIdentical(o)) {
			return 0;
		} else {
			return 1;
		}
	}
	
	/**
	 * Used by the contains method to see if a relation is already in the
	 * relation list. Not a full compare. Compares using the relation type and
	 * conn/arg2 starts
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		return compareTo((Relation) o) == 0;
	}


	public String getMismatches(boolean verbose) {
		String ret = "";
		List<String> mismatches = new ArrayList<String>();				
		boolean match = true;
		
		//childen count
		if (adjudicationRelations.size() == 1) {
			return "Single Annotator";
		}
		
		//check relation type
//		String temp = getChildrenValues(LABELS.rel);
//		if (temp.contains("/")) {
		if (!childrenMatch(LABELS.rel)) {
			match = false;
			mismatches.add("Relation type");
		}


		//check connective
		Set<String> childrenValues = new HashSet<String>();
		for (Relation relation : adjudicationRelations) {
			String val = relation.getConnString();
			if (!val.equals("")) {
				childrenValues.add(val);
			}
		}		
		if (childrenValues.size() > 1) {
			match = false;
			mismatches.add("Connective");
		}

		//check semantic classes
		//sense 1A
//		String val = getChildrenValues(LABELS.sClass1A);
//		if (!val.equals("")) {			
//			if (val.contains("/")) {
		if (!childrenMatch(LABELS.sClass1A)) {				
			match = false;
			mismatches.add("Sense 1A");
		}
	//	}

		//sense 1B
//		val = getChildrenValues(LABELS.sClass1B);
//		if (!val.equals("")) {
//			if (val.contains("/")) {
		if (!childrenMatch(LABELS.sClass1B)) {					
			match = false;
			mismatches.add("Sense 1B");
		}
//		}
			
		//sense 2A
//		val = getChildrenValues(LABELS.sClass2A);
//		if (!val.equals("")) {
//			if (val.contains("/")) {			
		if (!childrenMatch(LABELS.sClass2A)) {					
			match = false;
			mismatches.add("Sense 2A");
		}
//		}
			
		//sense 2B
//		val = getChildrenValues(LABELS.sClass2B);
//		if (!val.equals("")) {
//			if (val.contains("/")) {
		if (!childrenMatch(LABELS.sClass2B)) {	
			match = false;
			mismatches.add("Sense 2B");
		}				
//		}
		
		//check arg1
//		temp = getChildrenValues(LABELS.arg1SpanList);
//		if (temp.contains("/")) {
		if (!childrenMatch(LABELS.arg1SpanList)) {				
			match = false;
			mismatches.add("Arg1");
		}

		//check arg2
//		temp = getChildrenValues(LABELS.arg2SpanList);
//		if (temp.contains("/")) {
		if (!childrenMatch(LABELS.arg2SpanList)) {				
			match = false;
			mismatches.add("Arg2");
		}
		
		if (match || bothChildrenRejects()) {
			ret = "";
			if (verbose) {
				ret += " Annotators agree";
			}
		} else {
			if (verbose) {
				ret += " -- Disagreements: ";
			}
			String[] tempArray = new String[mismatches.size()];				
			mismatches.toArray(tempArray);
			ret += join(tempArray);
		}
		
		return ret;	
	}	
	
	// TODO: This has to be fixed for tokenization
	@Override
	public String toString() {
		String ret = "";
		
		if (isGhost()) {
			ret += "NOT ADJUDICATED. " + getMismatches(true);
		} else if(originalVals[LABELS.adjudicationReason.ordinal()].equals("Rejected")) {
			ret += "REJECTED";
		} else if(originalVals[LABELS.adjudicationReason.ordinal()].equals("Reannotate")) {
			ret += "REANNOTATE";
		} else {
			if (root != null) {
				File rootHandle = new File(root);
				ret += rootHandle.getName() + ": ";
			}	
			
			
			if (!originalVals[LABELS.linkGroup.ordinal()].equals("")) {
				//some legacy value
				if (!originalVals[LABELS.linkGroup.ordinal()].contains("LINK")) {
					ret += "";
				} else {
					String index = originalVals[LABELS.linkGroup.ordinal()].replace("LINK", "");
					ret += " (" + index + ") ";
				}
			}
			
			//relation type
			ret += originalVals[LABELS.rel.ordinal()];

			//connective
			String conn = getConnString();
			if (!conn.equals("")) {
				ret += " | " + conn;
			}
			
			//Semantic Classes
			String val;
			val = originalVals[LABELS.sClass1A.ordinal()];
			if (!val.equals("")) {
				ret += " | " + val;
			}
			val = originalVals[LABELS.sClass1B.ordinal()];
			if (!val.equals("")) {
				ret += " | " + val;
			}
			
			String conn2 = getConn2();
			if (!conn2.equals("")) {
				ret += " | " + conn2;				
			}
			
			val = originalVals[LABELS.sClass2A.ordinal()];
			if (!val.equals("")) {
				ret += " | " + val;
			}
			val = originalVals[LABELS.sClass2B.ordinal()];
			if (!val.equals("")) {
				ret += " | " + val;
			}
			
			//arg1
			ret += " | " + "Arg1(" + originalVals[LABELS.arg1SpanList.ordinal()] + ")";
			
			//arg2
			ret += " | " + "Arg2(" + originalVals[LABELS.arg2SpanList.ordinal()] + ")";
		}
		
		return ret;
	}
	
	private String join(String[] myArray) {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<myArray.length; i++) {
			if (i == myArray.length - 1) {
				sb.append(myArray[i]);
			} else {
				sb.append(myArray[i] + ", ");
			} 
		}
		return sb.toString();
	}

	public String getConnString() {
		String ret = "";
		RELTYPELABELS relTypeLabel = RELTYPELABELS
				.valueOf(originalVals[LABELS.rel.ordinal()]);
		switch (relTypeLabel) {
		case Explicit:
		case AltLex:
			SpanList spanList = new SpanList(
					originalVals[LABELS.connSpanList.ordinal()]);
			for (Iterator<Span> i = spanList.iterator(); i.hasNext();) {
				Span span = i.next();
				Span detokenizedSpan = fileManager.getIndexSpanMap()
						.tokenToChar(span);
				ret += fileManager
						.getRawText()
						.substring(detokenizedSpan.getStart(),
								detokenizedSpan.getEnd()).toLowerCase() + " ";
			}
			break;
		case Implicit:
			ret += originalVals[LABELS.conn1.ordinal()];
			break;
		}
		return ret.trim();
	}	
	
	public String getConn2() {
		RELTYPELABELS relTypeLabel = RELTYPELABELS.valueOf(originalVals[LABELS.rel.ordinal()]);		
		if (relTypeLabel == RELTYPELABELS.Implicit) {
			return originalVals[LABELS.conn2.ordinal()];
		} 
		return "";				
	}
	
	private boolean childrenMatch(LABELS label) {
		String[] childrenValues = new String[2];
		int count = 0;		
		for (Relation relation : adjudicationRelations) {
			String val = relation.originalVals[label.ordinal()];	
			childrenValues[count] = val;
			count++;
		}	
		return childrenValues[0].equals(childrenValues[1]);
	}
	
	private boolean bothChildrenRejects() {
		String[] childrenValues = new String[2];
		int count = 0;		
		for (Relation relation : adjudicationRelations) {
			String val = relation.originalVals[LABELS.adjudicationReason.ordinal()];	
			childrenValues[count] = val;
			count++;
		}
		return childrenValues[0].equals("Rejected") && childrenValues[1].equals("Rejected");
	}
	
	private String getChildrenValues(LABELS label) {
		Set<String> childrenValues = new HashSet<String>();
		for (Relation relation : adjudicationRelations) {
			String val = relation.originalVals[label.ordinal()];	
			if (!val.equals("")) {
				childrenValues.add(val);
			} else {
				
			}
		}

		String ret = "";
		for (Iterator<String> i = childrenValues.iterator(); i.hasNext();) {
			ret += i.next() + "/";
		}
		if (!ret.equals("")) {
			ret = ret.substring(0, ret.length() - 1);
		}
		return ret;
	}

	public static final DataFlavor flavor = new DataFlavor(Relation.class,
			Relation.class.getName());

	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		if (flavor.equals(flavor)) {
			return this;
		}
		throw new UnsupportedFlavorException(flavor);
	}

	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { flavor };
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.equals(flavor);
	}

}
