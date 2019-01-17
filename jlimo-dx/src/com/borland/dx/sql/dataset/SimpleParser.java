//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/SimpleParser.java,v 7.0 2002/08/08 18:39:58 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import java.util.Vector;
import com.borland.jb.util.DiagnosticJLimo;

// This Parser is only intended to find parameters, column names,
// and table names from a select query. This code is included
// in the DataExpress runtime part, and should be kept at minimal size.
// This parser may fail for certain select queries.

class SimpleParser extends SimpleScanner implements SQLToken {

/*!
  public static void main(String argv[]) {
    test("select scott.sampleemp.lname, ipgdev.sampleemp.lname from scott.sampleemp, ipgdev.sampleemp where scott.sampleemp.empid = ipgdev.sampleemp.empid");
    test("select A.NAME, B.STREET, B.NAME as Place from M_PERSON A, M_ADDRESS B where A.ID=B.ID");
    test("select distinct col1,?,:par,fct(2,:hello,9) from table1 where y = :param and x = :1.param order by :param");
    test("select col1,col2,col3+45,db.schema.table1.colx,avg(col) from table1,table2 where x = 3.5 or g <= u");
    test("select \"SELECT\" from \"DATE\"");
    test("select \"\"\"SE\"\"LECT\" from \"DATE\"\"\"");
  }
  static void test(String query) {
    System.out.println("---------------------------------------------------------------------------------");
    System.out.println("Original Query:\n   "+query);
    SimpleParser parser = new SimpleParser(query,'"');
    QueryParseToken tokens;
    tokens = parser.getParameterTokens();
    System.out.println("Generated from Parameter Parse ("+parser.getParameterCount()+" parameters):\n   "+parser.format(tokens));
    tokens = parser.getParsedTokens();
    System.out.println("Generated from Fully Parse ("+parser.getParameterCount()+" parameters):\n   "+parser.format(tokens));
  }
*/

  SimpleParser(String selectQuery, char identifierQuoteChar) {
    super(selectQuery,identifierQuoteChar);
    this.identifierQuoteChar = identifierQuoteChar;
//    Diagnostic.println("Query to parse: "+selectQuery);
  }

  QueryParseToken getParameterTokens() {
    if (startToken == null)
      parseParameters();
    return startToken;
  }

  QueryParseToken getParsedTokens() {
    if (!parsed)
      parse();
    return startToken;
  }

  int getParameterCount() {
    if (startToken == null)
      parseParameters();
    return nParams;
  }

  boolean isSummary() {
    if (!parsed)
      parse();
    return summary;
  }

  String format(QueryParseToken tokens, boolean removeNamedParameters) {
    StringBuffer buffer = new StringBuffer(100);
    boolean firstField = true;

    while (tokens != null) {
      switch (tokens.getType()) {
        case SQLToken.SELECT:
           if (buffer.length() > 0)
             buffer.append(' ');
           buffer.append(SYM_SELECT);
           break;
        case SQLToken.FROM:
           buffer.append(' ');
           buffer.append(SYM_FROM);
           firstField = true;
           break;
        case SQLToken.WHERE:
           buffer.append(' ');
           buffer.append(tokens.getName());
           break;
        case SQLToken.GROUP:
           buffer.append(' ');
           buffer.append(tokens.getName());
           break;
        case SQLToken.HAVING:
           buffer.append(' ');
           buffer.append(tokens.getName());
           break;
        case SQLToken.ORDER:
           buffer.append(' ');
           buffer.append(tokens.getName());
           break;
        case SQLToken.OTHER:
           if (buffer.length() > 0)
             buffer.append(' ');
           buffer.append(tokens.getName());
           break;
        case SQLToken.FIELD:
        case SQLToken.TABLE:
           if (firstField)
             buffer.append(' ');
           else
             buffer.append(", ");
           firstField = false;
           buffer.append(tokens.generateString(identifierQuoteChar,false,false));
           break;
        case SQLToken.EXPRESSION:
           if (firstField)
             buffer.append(' ');
           else
             buffer.append(", ");
           firstField = false;
           buffer.append(tokens.getName());
           break;
        case SQLToken.PARAMETER:
           buffer.append(' ');
           if (removeNamedParameters || tokens.getName() == null)
             buffer.append('?');
           else {
             buffer.append(':');
             buffer.append(tokens.getName());
           }
           break;
      }
      tokens = tokens.getNextToken();
    }
//    Diagnostic.println(buffer.toString());
    return buffer.toString();
  }

  private void resetParser() {
    summary = false;
    parsed = false;
    startToken = null;
    lastToken = null;
    nTables = 0;
    nParams = 0;
  }

  private void parse() {
    resetScanner();
    resetParser();
    if (cannotParse)
      return;
    try {
      select_stmt();
      parsed = true;
    }
    catch (Exception ex) {
      cannotParse = true;
      resetParser();
//      Diagnostic.printStackTrace(ex);
    }
  }

  //  select_stmt =  SELECT [ ALL | DISTINCT ] select_item { "," select_item }
  //                 FROM   table_ref { "," table_ref }
  //               [ WHERE  expr ]
  //               [ GROUP BY column_ref { "," column_ref } ]
  //               [ HAVING expr ]
  //               [ ORDER BY order_item { "," order_item } ]
  private void select_stmt() {
    symbol = nextToken();
    if (symbol != SCAN_SELECT)
      throw new RuntimeException();
    addToken(new QueryParseToken(SQLToken.SELECT));
    symbol = nextToken();

    if (symbol == SCAN_DISTINCT || symbol == SCAN_ALL) {
      if (symbol == SCAN_DISTINCT) {
        addToken(new QueryParseToken(SQLToken.OTHER,getCurrentID()));
        summary = true;
      }
      symbol = nextToken();
    }

    if (symbol == SCAN_STAR) {
      addToken(new QueryParseToken(SQLToken.FIELD,"*"));
      symbol = nextToken();
    }
    else {
      select_item();
      while (symbol == SCAN_COMMA) {
        symbol = nextToken();
        select_item();
      }
    }

    if (symbol != SCAN_FROM)
      throw new RuntimeException();
    addToken(new QueryParseToken(SQLToken.FROM));
    symbol = nextToken();

    table_ref();
    while (symbol == SCAN_COMMA) {
      symbol = nextToken();
      table_ref();
    }

    switch (symbol) {
      case SCAN_WHERE:
      case SCAN_GROUP:
      case SCAN_HAVING:
      case SCAN_ORDER:
      case SCAN_EOQ:
        break;
      default:
        throw new RuntimeException(); // We cannot parse this... Fix for RAID91456
    }

    if (symbol == SCAN_WHERE) {
      setMark();
      symbol = nextToken();
      ignore(SQLToken.WHERE);
    }

    if (symbol == SCAN_GROUP) {
      setMark();
      summary = true;
      symbol = nextToken();
      ignore(SQLToken.GROUP);
    }

    if (symbol == SCAN_HAVING) {
      setMark();
      summary = true;
      symbol = nextToken();
      ignore(SQLToken.HAVING);
    }

    if (symbol == SCAN_ORDER) {
      setMark();
      summary = true;
      symbol = nextToken();
      ignore(SQLToken.ORDER);
    }
  }

  // select_item = scalar_expr [[AS] ident]
  private void select_item() {
    setMark();
    isExpression = false;
    isExpressionPart = false;
    name    = null;
    prefix1 = null;
    prefix2 = null;
    prefix3 = null;
    String alias = null;
    scalar_expr();

    if (symbol == SCAN_AS) {
      symbol = nextToken();
      if (symbol != SCAN_ID)
        throw new RuntimeException();
    }
    if (symbol == SCAN_ID) {
      alias = getCurrentID();
      symbol = nextToken();
    }

    if (!isExpression)
      addToken(new QueryParseToken(SQLToken.FIELD,name,prefix1,prefix2,prefix3,alias));
    else
      addExpressionToken();
  }

  // table_ref = [ prefix2 "." [ prefix1 "." ]]] name [[AS] ident]
  private void table_ref() {
    prefix2 = null;
    prefix1 = null;
    name    = getCurrentID();
    String alias = null;

    symbol = nextToken();
    while (symbol == SCAN_DOT) {
      symbol = nextToken();
      if (symbol != SCAN_ID || prefix2 != null)
        throw new RuntimeException();
      prefix2 = prefix1;
      prefix1 = name;
      name    = getCurrentID();
      symbol  = nextToken();
    }

    if (symbol == SCAN_AS) {
      symbol = nextToken();
      if (symbol != SCAN_ID)
        throw new RuntimeException();
    }
    if (symbol == SCAN_ID) {
      alias = getCurrentID();
      symbol = nextToken();
    }
    addToken(new QueryParseToken(SQLToken.TABLE,name,prefix1,prefix2,null,alias));

    nTables++;
  }

  // scalar_expr =   scalar_term { "+" | "-" | "*" | "/" | "||" } scalar_expr
  //               | scalar_term
  private void scalar_expr() {
    scalar_term();
    while (symbol == SCAN_PLUS_MINUS || symbol == SCAN_STAR || symbol == SCAN_OTHER_OPERATOR) {
      isExpression = true;
      symbol = nextToken();
      scalar_term();
    }
  }

  // scalar_term =   [ "+" | "-" ] scalar_primary
  private void scalar_term() {
    if (symbol == SCAN_PLUS_MINUS) {
      isExpression = true;
      symbol = nextToken();
    }
    scalar_primary();
  }

  // scalar_primary =   column-ref--or--function-ref
  //                  | literal-string
  //                  | literal-datetime
  //                  | literal-number
  //                  | current_datetime_functions
  //                  | parameter
  //                  | "(" garbage_with_parens ")"
  //                  | agg-function
  private void scalar_primary() {
    if (symbol != SCAN_ID)
      isExpression = true;
    switch (symbol) {
      case SCAN_ID:
        column_ref__or__function_ref();
        break;
      case SCAN_SGL_QUOTED_STRING:
        symbol = nextToken();
        break;
      case SCAN_DATE:
      case SCAN_TIME:
      case SCAN_TIMESTAMP:
        symbol = nextToken();
        if (symbol != SCAN_SGL_QUOTED_STRING)
          throw new RuntimeException();
        symbol = nextToken();
        break;
      case SCAN_NUMBER:
        symbol = nextToken();
        break;
      case SCAN_CURRENT_DATE:
      case SCAN_CURRENT_TIME:
      case SCAN_CURRENT_TIMESTAMP:
        symbol = nextToken();
        break;
      case SCAN_QUESTIONMARK:
      case SCAN_COLON:
        addExpressionToken();
        addToken(parseFoundParameter());
        symbol = nextToken();
        setMark();
        break;
      case SCAN_LEFT_PAREN:
        garbage_with_parens();
        break;
      case SCAN_AGG:
        summary = true;
        symbol = nextToken();
        if (symbol != SCAN_LEFT_PAREN)
          throw new RuntimeException();
        garbage_with_parens();
        break;
    }
  }

  // garbage_with_parens = "(" <anything, but match parenthesis> ")"
  private void garbage_with_parens() {
    DiagnosticJLimo.check(symbol == SCAN_LEFT_PAREN);
    int parenLevel = 1;
    symbol = nextToken();
    while (symbol != SCAN_EOQ && parenLevel > 0) {
      switch (symbol) {
        case SCAN_QUESTIONMARK:
        case SCAN_COLON:
          addExpressionToken();
          addToken(parseFoundParameter());
          symbol = nextToken();
          setMark();
          break;
        case SCAN_LEFT_PAREN:
          parenLevel++;
          break;
        case SCAN_RIGHT_PAREN:
          parenLevel--;
          break;
      }
      symbol = nextToken();
    }
    if (symbol == SCAN_EOQ)
      throw new RuntimeException();
  }

  // ignore = <anything but GROUP, HAVING, ORDER, EOQ>
  private void ignore(int type) {
    while (symbol != SCAN_EOQ) {
      switch (symbol) {
        case SCAN_GROUP:
        case SCAN_HAVING:
        case SCAN_ORDER:
          addMarkedToken(type);
          return;
        case SCAN_QUESTIONMARK:
        case SCAN_COLON:
          addMarkedToken(type);
          addToken(parseFoundParameter());
          symbol = nextToken();
          setMark();
          type = SQLToken.OTHER;
          break;
      }
      symbol = nextToken();
    }
    addMarkedToken(type);
  }

  // column_ref__or__function_ref = [ prefix3 "." [ prefix2 "." [ prefix1 "." ]]] name [ "(" garbage_with_parens ")" ]
  private void column_ref__or__function_ref() {
    prefix3 = null;
    prefix2 = null;
    prefix1 = null;
    name    = null;
    int prevSymbol = symbol;

    if (!isExpression)
      name = getCurrentID();

    symbol = nextToken();
    while (symbol == SCAN_DOT) {
      symbol = nextToken();
      if ((symbol != SCAN_ID && symbol != SCAN_STAR) ||
          (symbol == SCAN_STAR && prevSymbol != SCAN_ID) ||
          (prefix3 != null))
        throw new RuntimeException();
      if (!isExpression) {
        prefix3 = prefix2;
        prefix2 = prefix1;
        prefix1 = name;
        name    = symbol == SCAN_STAR ? "*" : getCurrentID();
      }
      prevSymbol = symbol;
      symbol  = nextToken();
    }

    if (symbol == SCAN_LEFT_PAREN) {
      isExpression = true;
      garbage_with_parens();
    }
  }

  private void addExpressionToken() {
    addMarkedToken(isExpressionPart ? SQLToken.OTHER : SQLToken.EXPRESSION);
    isExpressionPart = true;
  }

  private void addMarkedToken(int kind) {
    addToken(new QueryParseToken(kind,getMarkedText(),null));
  }

  private void addToken(QueryParseToken token) {
    if (lastToken == null) {
      startToken = lastToken = token;
    }
    else {
      lastToken.setNextToken(token);
      lastToken = token;
    }
  }

  private QueryParseToken parseFoundParameter() {
    nParams++;
    if (symbol == SCAN_QUESTIONMARK)
      return new QueryParseToken(SQLToken.PARAMETER);
    DiagnosticJLimo.check(symbol == SCAN_COLON);
    String name = null;
    symbol = nextToken();
    if (symbol == SCAN_NUMBER) {
      name = getCurrentID();
      symbol = nextToken();
    }
    if (symbol != SCAN_ID) {
      nParams--;
      return null;
    }
    name = (name == null ? getCurrentID() : name + getCurrentID());
    return new QueryParseToken(SQLToken.PARAMETER,name);
  }

  private void parseParameters() {
    try {
      setMark();
      symbol = nextToken();
      while (symbol != SCAN_EOQ) {
        if (symbol != SCAN_QUESTIONMARK && symbol != SCAN_COLON)
          symbol = nextToken();
        else {
          String other = getMarkedText();
          QueryParseToken parameter = parseFoundParameter();
          if (parameter != null) {
            addToken(new QueryParseToken(SQLToken.OTHER,other));
            addToken(parameter);
            symbol = nextToken();
            setMark();
          }
        }
      }
      addToken(new QueryParseToken(SQLToken.OTHER,getMarkedText()));
    }
    catch (Exception ex) {
      ex.printStackTrace();
      resetParser();
    }
  }

  private QueryParseToken startToken;
  private QueryParseToken lastToken;
  private int             symbol;
  private boolean         summary;
  private char            identifierQuoteChar;
  private int             nTables;
  private int             nParams;
  private boolean         parsed;
  private boolean         isExpression;
  private boolean         isExpressionPart;
  private String          prefix1;
  private String          prefix2;
  private String          prefix3;
  private String          name;
  private boolean         cannotParse;
}

class SimpleScanner {

  SimpleScanner(String selectQuery, char identifierQuoteChar) {
    this.selectQuery = selectQuery.toCharArray();
    this.pos = 0;
    this.end = this.selectQuery.length - 1;
    this.idQuote = (identifierQuoteChar == '"');
  }

  void resetScanner() {
    pos = 0;
    mark = prevPos = 0;
  }

  int nextToken() {
    prevPos = pos;
    while (pos <= end) {
      switch (selectQuery[pos++]) {
        case ' ':
        case '\t':
        case '\r':
        case '\n':
        case '\f': break;
        case '?' : return SCAN_QUESTIONMARK;
        case ':' : return SCAN_COLON;
        case ',' : return SCAN_COMMA;
        case ';' : return SCAN_EOQ;
        case '(' : return SCAN_LEFT_PAREN;
        case ')' : return SCAN_RIGHT_PAREN;
        case '+' : return SCAN_PLUS_MINUS;
        case '~' : return SCAN_PLUS_MINUS;     // Sybase bitwise NOT operator
        case '%' : return SCAN_OTHER_OPERATOR; // Sybase modulo operator
        case '&' : return SCAN_OTHER_OPERATOR; // Sybase bitwise AND operator
        case '^' : return SCAN_OTHER_OPERATOR; // Sybase bitwise EXCLUSIZE OR operator
        case '*' : return SCAN_STAR;
        case '/' : if (pos > end || selectQuery[pos] != '*')
                     return SCAN_OTHER_OPERATOR;
                   pos++;
                   skipUntilEndComment();
                   break;
        case '-' : if (pos > end || selectQuery[pos] != '-')
                     return SCAN_PLUS_MINUS;
                   while (pos < end && selectQuery[++pos] != '\n');
                   pos++;
                   break;
        case '|' : if (pos > end || selectQuery[pos] != '|')
                     return SCAN_OTHER_OPERATOR;  // Sybase bitwise OR operator
                   pos++;
                   return SCAN_OTHER_OPERATOR;
        case '\'':
        case '\"': start = --pos;
                   return skipUntilEndQuote(selectQuery[pos]);
        case '.' : if (selectQuery[pos] < '0' || selectQuery[pos] > '9')
                     return SCAN_DOT;
                   start = --pos;
                   skipNumber();
                   return SCAN_NUMBER;
        default:
          char ch = selectQuery[pos-1];
          if (ch >= '0' && ch <= '9') {
            start = --pos;
            skipNumber();
            return SCAN_NUMBER;
          }
          else if (ch >= 'a' && ch <= 'z' ||            //NORES
              ch >= 'A' && ch <= 'Z' ||
              Character.isLetter(ch))
          {
            start = pos - 1;
            return skipID();
          }
          else {
            return SCAN_ERROR;
          }
      }
    }
    return SCAN_EOQ;
  }

  void setMark() {
    mark = prevPos;
  }

  String getCurrentID() {
    return new String(selectQuery,start,pos-start);
  }

  String getMarkedText() {
    return new String(selectQuery,mark,prevPos-mark);
  }

  private int skipUntilEndQuote(char quoteChar) {
    while (pos < end && selectQuery[++pos] != quoteChar);
    while (pos < end && selectQuery[++pos] == quoteChar) {
      if (pos == end)
        return SCAN_ERROR;
      while (pos < end && selectQuery[++pos] != quoteChar);
    }
    if (pos < end || selectQuery[pos++] == quoteChar)
      return idQuote && quoteChar == '"' ? SCAN_ID : SCAN_SGL_QUOTED_STRING;
    else
      return SCAN_ERROR;
  }

  private void skipNumber() {
    while (pos <= end && (selectQuery[pos] >= '0' && selectQuery[pos] <= '9'))
      pos++;
    if (pos <= end && selectQuery[pos] == '.')
      pos++;
    while (pos <= end && (selectQuery[pos] >= '0' && selectQuery[pos] <= '9'))
      pos++;
    if (pos <= end && (selectQuery[pos] == 'e' || selectQuery[pos] == 'E'))             //NORES
      pos++;
    if (pos <= end && (selectQuery[pos] == '+' || selectQuery[pos] == '-'))
      pos++;
    while (pos <= end && (selectQuery[pos] >= '0' && selectQuery[pos] <= '9'))
      pos++;
  }

  private void skipUntilEndComment() {
    int stop = end - 1;
    while (pos <= stop && !(selectQuery[pos] == '*' && selectQuery[pos+1] == '/'))
      pos++;
    pos += 2;
  }

  private int skipID() {
    if (pos <= end) {
      while (pos <= end) {
        char ch = selectQuery[pos++];
        if (!(ch >= 'a' && ch <= 'z' ||         //NORES
              ch >= 'A' && ch <= 'Z' ||
              ch >= '0' && ch <= '9' ||
              ch == '_' || ch == '$' ||
              Character.isLetter(ch)))
        {
          pos--;
          break;
        }
      }
    }
    return lookupID();
  }

  private int lookupID() {
    switch (pos - start) {
      case 17: if (match(SYM_CURRENT_TIMESTAMP)) return SCAN_CURRENT_TIMESTAMP;
               break;
      case 12: if (match(SYM_CURRENT_DATE))      return SCAN_CURRENT_DATE;
               if (match(SYM_CURRENT_TIME))      return SCAN_CURRENT_TIME;
               break;
      case  9: if (match(SYM_TIMESTAMP)) return SCAN_TIMESTAMP;
               break;
      case  8: if (match(SYM_DISTINCT))  return SCAN_DISTINCT;
               break;
      case  6: if (match(SYM_SELECT))    return SCAN_SELECT;
               if (match(SYM_HAVING))    return SCAN_HAVING;
               break;
      case  5: if (match(SYM_WHERE))     return SCAN_WHERE;
               if (match(SYM_GROUP))     return SCAN_GROUP;
               if (match(SYM_ORDER))     return SCAN_ORDER;
               if (match(SYM_COUNT))     return SCAN_AGG;
               break;
      case  4: if (match(SYM_FROM))      return SCAN_FROM;
               if (match(SYM_TIME))      return SCAN_TIME;
               if (match(SYM_DATE))      return SCAN_DATE;
               break;
      case  3: if (match(SYM_ALL))       return SCAN_ALL;
               if (match(SYM_AVG))       return SCAN_AGG;
               if (match(SYM_SUM))       return SCAN_AGG;
               if (match(SYM_MAX))       return SCAN_AGG;
               if (match(SYM_MIN))       return SCAN_AGG;
               break;
      case  2: if (match(SYM_AS))        return SCAN_AS;
               break;
    }
    return SCAN_ID;
  }

  private boolean match(String keywordString) {
    char[] keyword = keywordString.toCharArray();
    for (int index=0; index < keyword.length; index++) {
      if (keyword[index] != (selectQuery[index+start] & ~0x20))
        return false;
    }
    return true;
  }

  // Scanner Symbols:
  final static int SCAN_ERROR             = -1;
  final static int SCAN_EOQ               =  0;
  final static int SCAN_QUESTIONMARK      =  1;
  final static int SCAN_COLON             =  2;
  final static int SCAN_DOT               =  3;
  final static int SCAN_COMMA             =  4;
  final static int SCAN_LEFT_PAREN        =  5;
  final static int SCAN_RIGHT_PAREN       =  6;
  final static int SCAN_NUMBER            =  7;
  final static int SCAN_STAR              =  8;
  final static int SCAN_OTHER_OPERATOR    =  9;
  final static int SCAN_PLUS_MINUS        = 10;
  final static int SCAN_DISTINCT          = 11;
  final static int SCAN_SELECT            = 12;
  final static int SCAN_HAVING            = 13;
  final static int SCAN_WHERE             = 14;
  final static int SCAN_GROUP             = 15;
  final static int SCAN_ORDER             = 16;
  final static int SCAN_FROM              = 17;
  final static int SCAN_ALL               = 18;
  final static int SCAN_AS                = 19;
  final static int SCAN_ID                = 20;
  final static int SCAN_SGL_QUOTED_STRING = 21;
  final static int SCAN_DATE              = 22;
  final static int SCAN_TIME              = 23;
  final static int SCAN_TIMESTAMP         = 24;
  final static int SCAN_CURRENT_DATE      = 25;
  final static int SCAN_CURRENT_TIME      = 26;
  final static int SCAN_CURRENT_TIMESTAMP = 27;
  final static int SCAN_AGG               = 28;

  final static String SYM_CURRENT_TIMESTAMP = "CURRENT_TIMESTAMP";
  final static String SYM_CURRENT_DATE      = "CURRENT_DATE";
  final static String SYM_CURRENT_TIME      = "CURRENT_TIME";
  final static String SYM_TIMESTAMP         = "TIMESTAMP";
  final static String SYM_DISTINCT          = "DISTINCT";
  final static String SYM_SELECT            = "SELECT";
  final static String SYM_HAVING            = "HAVING";
  final static String SYM_WHERE             = "WHERE";
  final static String SYM_GROUP             = "GROUP";
  final static String SYM_ORDER             = "ORDER";
  final static String SYM_COUNT             = "COUNT";
  final static String SYM_FROM              = "FROM";
  final static String SYM_TIME              = "TIME";
  final static String SYM_DATE              = "DATE";
  final static String SYM_ALL               = "ALL";
  final static String SYM_AVG               = "AVG";
  final static String SYM_SUM               = "SUM";
  final static String SYM_MIN               = "MIN";
  final static String SYM_MAX               = "MAX";
  final static String SYM_AS                = "AS";

  // Members:
  private char[]  selectQuery;
  private int     mark;
  private int     start;
  private int     pos;
  private int     end;
  private int     prevPos;
  private boolean idQuote;
}



