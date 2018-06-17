package edu.upenn.cis.anntool.settings;

import edu.upenn.cis.anntool.util.OptFeat;

public class Constants {

	public static enum LABELS {

		rel("Relation Type:"), connSpanList("Conn Span"), connAttrSrc(
				"Conn Src:"), connAttrType("Conn Type:"), connAttrPol(
				"Conn Pol:"), connAttrDet("Conn Det:"), connAttrSpanList(
				"Conn Feat Span"), conn1("Conn1:"), sClass1A("SClass1A:"), sClass1B(
				"SClass1B:"), conn2("Conn2:"), sClass2A("SClass2A:"), sClass2B(
				"SClass2B:"), sup1SpanList("Sup1 Span"), arg1SpanList(
				"Arg1 Span"), arg1AttrSrc("Arg1 Src:"), arg1AttrType(
				"Arg1 Type:"), arg1AttrPol("Arg1 Pol:"), arg1AttrDet(
				"Arg1 Det:"), arg1AttrSpanList("Arg1 Feat Span"), arg2SpanList(
				"Arg2 Span"), arg2AttrSrc("Arg2 Src:"), arg2AttrType(
				"Arg2 Type:"), arg2AttrPol("Arg2 Pol:"), arg2AttrDet(
				"Arg2 Det:"), arg2AttrSpanList("Arg2 Feat Span"), sup2SpanList(
				"Sup2 Span"), adjudicationReason("AdjuReason"),
				adjudicationDisagreement("AdjuDisagr"),
				propBankRole("PBRole"), propBankVerb("PBVerb"),
				identifier("Identifier"), identifierType("Identifier Type"), linkGroup("Link Group");

		private final String name;

		LABELS(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	public static enum RELTYPELABELS {
		Explicit, Implicit, AltLex, Hypophora, EntRel, NoRel
	}

	public static final LABELS[] expLabels = new LABELS[] { LABELS.rel,
			LABELS.connSpanList, LABELS.connAttrSrc, LABELS.connAttrType,
			LABELS.connAttrPol, LABELS.connAttrDet, LABELS.connAttrSpanList,
			LABELS.sClass1A, LABELS.sClass1B, LABELS.sup1SpanList,
			LABELS.arg1SpanList, LABELS.arg1AttrSrc, LABELS.arg1AttrType,
			LABELS.arg1AttrPol, LABELS.arg1AttrDet, LABELS.arg1AttrSpanList,
			LABELS.arg2SpanList, LABELS.arg2AttrSrc, LABELS.arg2AttrType,
			LABELS.arg2AttrPol, LABELS.arg2AttrDet, LABELS.arg2AttrSpanList,
			LABELS.sup2SpanList, LABELS.adjudicationReason, LABELS.adjudicationDisagreement,
			LABELS.propBankRole, LABELS.propBankVerb, LABELS.identifier, LABELS.identifierType, LABELS.linkGroup};

	/* Added ConnSpanList for sentence location */
	public static final LABELS[] impLabels = new LABELS[] { LABELS.rel,
			LABELS.connSpanList, LABELS.connAttrSrc, LABELS.connAttrType,
			LABELS.connAttrPol, LABELS.connAttrDet, LABELS.connAttrSpanList,
			LABELS.conn1, LABELS.sClass1A, LABELS.sClass1B, LABELS.conn2,
			LABELS.sClass2A, LABELS.sClass2B, LABELS.sup1SpanList,
			LABELS.arg1SpanList, LABELS.arg1AttrSrc, LABELS.arg1AttrType,
			LABELS.arg1AttrPol, LABELS.arg1AttrDet, LABELS.arg1AttrSpanList,
			LABELS.arg2SpanList, LABELS.arg2AttrSrc, LABELS.arg2AttrType,
			LABELS.arg2AttrPol, LABELS.arg2AttrDet, LABELS.arg2AttrSpanList,
			LABELS.sup2SpanList, LABELS.adjudicationReason, LABELS.adjudicationDisagreement,
			LABELS.propBankRole, LABELS.propBankVerb, LABELS.identifier, LABELS.identifierType, LABELS.linkGroup};

	public static final LABELS[] altLabels = expLabels;

	public static final LABELS[] hypLabels = { LABELS.rel, LABELS.connAttrSpanList,
			LABELS.arg1SpanList, LABELS.arg2SpanList, LABELS.arg1AttrSpanList, LABELS.arg2AttrSpanList,
			LABELS.adjudicationReason, LABELS.adjudicationDisagreement,
			LABELS.propBankRole, LABELS.propBankVerb, LABELS.identifier, LABELS.identifierType, LABELS.linkGroup};
	
	public static final LABELS[] entLabels = { LABELS.rel, LABELS.connAttrSpanList,
			LABELS.arg1SpanList, LABELS.arg2SpanList, LABELS.arg1AttrSpanList, LABELS.arg2AttrSpanList,
			LABELS.adjudicationReason, LABELS.adjudicationDisagreement,
			LABELS.propBankRole, LABELS.propBankVerb, LABELS.identifier, LABELS.identifierType, LABELS.linkGroup};

	public static final LABELS[] noLabels = entLabels;

	public static final PDTBOptions options = new PDTBOptions("Options.cfg");

	public static final OptFeat relTypes = new OptFeat(new String[] {
			RELTYPELABELS.Explicit.toString(),
			RELTYPELABELS.Implicit.toString(), RELTYPELABELS.AltLex.toString(),
			RELTYPELABELS.Hypophora.toString(), RELTYPELABELS.EntRel.toString(), 
			RELTYPELABELS.NoRel.toString() },
			0);

	public static final int spacing = 10;

	public static final int fontSize = 18;

	public static final LABELS[] getLabels(String relTypeLabel) {
		return getLabels(RELTYPELABELS.valueOf(relTypeLabel));
	}

	public static final LABELS[] getLabels(RELTYPELABELS relTypeLabel) {
		switch (relTypeLabel) {
		case Explicit:
			return expLabels;
		case Implicit:
			return impLabels;
		case AltLex:
			return altLabels;
		case Hypophora:
			return hypLabels;
		case EntRel:
			return entLabels;
		case NoRel:
			return noLabels;
		default:
			return null;
		}
	}

	public static final Languages languages = new Languages();

	public static final String ptbTipsterFilename = "ptb_tip.tbl";
	public static final String ptbDualTipsterFilename = "ptb_dual_tip.tbl";
	public static final String ptbDuplicatesFilename = "ptb_duplicates.tbl";
	public static final String adjudicationGroupsFilename = "adjudication_groups.xml";

	// public static enum LANGUAGE {
	//
	// English("en", "US", "UTF8", "Default", "word"), Hindi("hi", "IN",
	// "UTF8", "Default", "character"), Japanese("ja", "JP", "EUC_JP",
	// "Arial Unicode MS", "character"), Tamil("ta", "IN", "UTF8",
	// "Latha", "character"), Urdu("ur", "IN", "UTF8",
	// "Nafees Web Naskh", "character");
	//
	// //Urdu("ur", "IN", "UTF8",
	// // "Nafees Naskh v2.01", "character"),
	//
	// private final String language;
	// private final String country;
	// private final String charset;
	// private final String font;
	// private final String breaktype;
	//
	// LANGUAGE(String language, String country, String charset, String font,
	// String breaktype) {
	// this.language = language;
	// this.country = country;
	// this.charset = charset;
	// this.font = font;
	// this.breaktype = breaktype;
	// }
	//
	// public String getLanguage() {
	// return language;
	// }
	//
	// public String getCountry() {
	// return country;
	// }
	//
	// public String getCharset() {
	// return charset;
	// }
	//
	// public String getFont() {
	// return font;
	// }
	//
	// public String getBreaktype() {
	// return breaktype;
	// }
	// }
}
