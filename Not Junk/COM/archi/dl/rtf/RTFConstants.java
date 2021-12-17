package COM.archi.dl.rtf;

/* Generated By:JavaCC: Do not edit this line. RTFConstants.java */
public interface RTFConstants {

  int EOF = 0;
  int GroupStart = 5;
  int GroupEnd = 6;
  int FontTableStart = 7;
  int ColorTableStart = 8;
  int SkipDest = 9;
  int HexCharStart = 10;
  int FontNumber = 11;
  int FontSize = 12;
  int Paragraph = 13;
  int ParagraphReset = 14;
  int Plain = 15;
  int Bold = 16;
  int Italic = 17;
  int Underline = 18;
  int Color = 19;
  int LeftAligned = 20;
  int RightAligned = 21;
  int Justified = 22;
  int Centered = 23;
  int FirstLineIndent = 24;
  int LeftIndent = 25;
  int RightIndent = 26;
  int SpaceAbove = 27;
  int SpaceBelow = 28;
  int LineSpacing = 29;
  int PlainText = 30;
  int ControlSymbol = 31;
  int ControlWord = 32;
  int HexChar = 37;
  int FontBegin = 42;
  int FontEnd = 43;
  int FontTableNumber = 44;
  int DefaultFamily = 45;
  int RomanFamily = 46;
  int SwissFamily = 47;
  int ModernFamily = 48;
  int DecorFamily = 49;
  int ScriptFamily = 50;
  int TechFamily = 51;
  int BidiFamily = 52;
  int FontName = 53;
  int FontTableControlWord = 54;
  int FontTableEnd = 55;
  int Red = 60;
  int Green = 61;
  int Blue = 62;
  int Value = 63;
  int EndColor = 64;
  int ColorTableEnd = 65;

  int DEFAULT = 0;
  int WithinHexChar = 1;
  int WithinFontTable = 2;
  int WithinColorTable = 3;

  String[] tokenImage = {
    "<EOF>",
    "\"\\n\"",
    "\"\\r\"",
    "\"\\r\\n\"",
    "\"\\u0000\"",
    "\"{\"",
    "\"}\"",
    "\"\\\\fonttbl\"",
    "\"\\\\colortbl\"",
    "<SkipDest>",
    "\"\\\\\\\'\"",
    "<FontNumber>",
    "<FontSize>",
    "<Paragraph>",
    "<ParagraphReset>",
    "<Plain>",
    "<Bold>",
    "<Italic>",
    "<Underline>",
    "<Color>",
    "<LeftAligned>",
    "<RightAligned>",
    "<Justified>",
    "<Centered>",
    "<FirstLineIndent>",
    "<LeftIndent>",
    "<RightIndent>",
    "<SpaceAbove>",
    "<SpaceBelow>",
    "<LineSpacing>",
    "<PlainText>",
    "<ControlSymbol>",
    "<ControlWord>",
    "\"\\n\"",
    "\"\\r\"",
    "\"\\r\\n\"",
    "\"\\u0000\"",
    "<HexChar>",
    "\"\\n\"",
    "\"\\r\"",
    "\"\\r\\n\"",
    "\"\\u0000\"",
    "\"{\"",
    "<FontEnd>",
    "<FontTableNumber>",
    "\"\\\\fnil \"",
    "\"\\\\froman \"",
    "\"\\\\fswiss \"",
    "\"\\\\fmodern \"",
    "\"\\\\fdecor \"",
    "\"\\\\fscript \"",
    "\"\\\\ftech \"",
    "\"\\\\fbidi \"",
    "<FontName>",
    "<FontTableControlWord>",
    "\"}\"",
    "\"\\n\"",
    "\"\\r\"",
    "\"\\r\\n\"",
    "\"\\u0000\"",
    "\"\\\\red\"",
    "\"\\\\green\"",
    "\"\\\\blue\"",
    "<Value>",
    "\";\"",
    "\"}\"",
  };

}
