package edu.upenn.cis.pdtb.lexyacc;
import edu.upenn.cis.anntool.util.PDTBOptionsAccum;
import java.io.IOException;

%%
%class Options
%public
%type PDTBOptionsAccum
%table
%eofval{
  return opts;
%eofval}

%{
  
  private PDTBOptionsAccum opts = new PDTBOptionsAccum();
  
  public void reset(java.io.Reader reader){
    yyreset(reader);
  }

%}

%unicode
%state YYCLASS
%state YYCONN
%state YYCONNSOURCE
%state YYCONNTYPE
%state YYCONNPOLARITY
%state YYCONNDET
%state YYARGSOURCE
%state YYARGTYPE
%state YYARGPOLARITY
%state YYARGDET

WHITESPACE=[\_\n\r\t\f\ ]+ 
NEWLINE=[\f\n\r]+
OPEN=[\#]+[ ]+
CLOSE=[ ]+[\#]+{NEWLINE}
CLASSSTART={OPEN}"Class"{CLOSE}
CONNSTART={OPEN}"Conn"{CLOSE}
CONNSOURCESTART={OPEN}"ConnSource"{CLOSE}
CONNTYPESTART={OPEN}"ConnType"{CLOSE}
CONNPOLARITYSTART={OPEN}"ConnPolarity"{CLOSE}
CONNDETSTART={OPEN}"ConnDet"{CLOSE}
ARGSOURCESTART={OPEN}"ArgSource"{CLOSE}
ARGTYPESTART={OPEN}"ArgType"{CLOSE}
ARGPOLARITYSTART={OPEN}"ArgPolarity"{CLOSE}
ARGDETSTART={OPEN}"ArgDet"{CLOSE}
END=[\#]+
DEC=[0-9]+
WORD=[^\f\n\r\#]*

%%

<YYINITIAL> {
{WHITESPACE} { }
{CLASSSTART} {yybegin(YYCLASS);}
{CONNSTART} {yybegin(YYCONN);}
{CONNSOURCESTART} {yybegin(YYCONNSOURCE);}
{CONNTYPESTART} {yybegin(YYCONNTYPE);}
{CONNPOLARITYSTART} {yybegin(YYCONNPOLARITY);}
{CONNDETSTART} {yybegin(YYCONNDET);}
{ARGSOURCESTART} {yybegin(YYARGSOURCE);}
{ARGTYPESTART} {yybegin(YYARGTYPE);}
{ARGPOLARITYSTART} {yybegin(YYARGPOLARITY);}
{ARGDETSTART} {yybegin(YYARGDET);}
}

<YYCLASS> { 
{WORD} {opts.addClass(yytext());}
{NEWLINE} {}
{END} {yybegin(YYINITIAL);}
}

<YYCONN> { 
{WORD} {opts.addConn(yytext());}
{NEWLINE} {}
{END} {yybegin(YYINITIAL);}
}

<YYCONNSOURCE> {
{DEC} {opts.setConnSource(yytext());}
{WORD} {opts.getConnSource().addFeatVal(yytext());}
{NEWLINE} {}
{END} {yybegin(YYINITIAL);}
}

<YYCONNTYPE> { 
{DEC} {opts.setConnType(yytext());}
{WORD} {opts.getConnType().addFeatVal(yytext());}
{NEWLINE} {}
{END} {yybegin(YYINITIAL);}
}

<YYCONNPOLARITY> { 
{DEC} {opts.setConnPolarity(yytext());}
{WORD} {opts.getConnPolarity().addFeatVal(yytext());}
{NEWLINE} {}
{END} {yybegin(YYINITIAL);}
}

<YYCONNDET> { 
{DEC} {opts.setConnDet(yytext());}
{WORD} {opts.getConnDet().addFeatVal(yytext());}
{NEWLINE} {}
{END} {yybegin(YYINITIAL);}
}

<YYARGSOURCE> { 
{DEC} {opts.setArgSource(yytext());}
{WORD} {opts.getArgSource().addFeatVal(yytext());}
{NEWLINE} {}
{END} {yybegin(YYINITIAL);}
}

<YYARGTYPE> { 
{DEC} {opts.setArgType(yytext());}
{WORD} {opts.getArgType().addFeatVal(yytext());}
{NEWLINE} {}
{END} {yybegin(YYINITIAL);}
}

<YYARGPOLARITY> { 
{DEC} {opts.setArgPolarity(yytext());}
{WORD} {opts.getArgPolarity().addFeatVal(yytext());}
{NEWLINE} {}
{END} {yybegin(YYINITIAL);}
}

<YYARGDET> { 
{DEC} {opts.setArgDet(yytext());}
{WORD} {opts.getArgDet().addFeatVal(yytext());}
{NEWLINE} {}
{END} {yybegin(YYINITIAL);}
}

<YYINITIAL> . {throw(new IOException("Malformed file."));}