//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/jb/util/PatternMatch.java,v 7.0.2.1 2004/05/22 00:53:32 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jb.util;

/**
 * Utility class for performing pattern matching on <code>String</code>
 * objects. The <code>pattern</code>, property must be set
 * first. The <code>EscapeChar</code>, <code>MultiChar</code>,
 * and <code>SingleChar</code> properties have
 * default values.
 *
 * The <code>validate()</code> method must return a value
 * greater than -1 before the <code>match()</code>
 * method is called.
 */

public class PatternMatch {


  public PatternMatch() {
    this.escapeChar     = '\\';
    this.multiChar      = '*';
    this.singleChar     = '?';
  }

  /**
   * The pattern to match on.
   */
  public final void setPattern(String patternString) {
    this.patternString  = patternString;
  }

  /**
      The char that will cause MultiChar and SingleChar chars to
      be taken literally.
  */
  public final void setEscapeChar(char escapeChar) {
    this.escapeChar  = escapeChar;
  }

  /**
      The char that will match 0 or more occurances of any char.
      Defaults to '*'
  */
  public final void setMultiChar(char multiChar) {
    this.multiChar  = multiChar;
  }

  /**
      The char that matches 1 occurance of any char.
      Defaults to '?'
  */
  public final void setSingleChar(char singleChar) {
    this.singleChar  = singleChar;
  }

  /**
   * The <code>validate</code> method must return a value greater
   * than -1 before this method can be called.
   *
   * @returns <b>true</b> if <code>expString</code> matches the pattern property.
  */
  public final boolean match(String expString) {
    this.exp = expString.toCharArray();
    this.exp_len = this.exp.length - 1;
    if (lastMultiIndex < 0 && exp_len != pat_len)
      return false;
    return match(-1,-1);
  }

  public final boolean getSimplePattern(String[] limits) {
    int index = -1;
    while (index < pat_len && pattern[++index] > 10)
      ;
    if (index == pat_len && (index < 0 || pattern[index] > 10)) {
      limits[0] = limits[1] = new String(pattern);
      return true;                    // This LIKE operator can be replaced by a comparison
    }
    if (index <= 0) {
      limits[0] = "\u0000";
      limits[1] = "\ufffe";           // This LIKE operator cannot be used as an index lookup
      return false;                   // dont care
    }
    else {
      limits[0] = new String(pattern,0,index);
      limits[1] = limits[0] + '\ufffe';
      if (index == pat_len && pattern[index] == PatternMatch.MULTI)
        return true;                  // This LIKE operator can be replaced by a comparison
      else
        return false;                 // This LIKE operator cannot be replaced by a comparison, but index lookup may still be used
    }
  }

  /**
   * Validates the <code>setPattern</code> property.  Must be
   * called before <code>match()</code> method is called.
   *
   * @returns -1 if the pattern is valid, otherwise returns
   * the character position of the char that is not valid.
  */

  public final int validate() {
    int errorPos = 0;
    this.lastMultiIndex = -1;
    this.lastSingleIndex = -1;
    char[] pattern = patternString.toCharArray();
    int len    = pattern.length - 1;
    int j      = -1;
    int i      = -1;
    while (i < len) {
      char ch = pattern[++i];
      if (ch == singleChar) {
        pattern[++j] = PatternMatch.SINGLE;
        lastSingleIndex = j;
      }
      else if (ch == multiChar) {
        while (i < len && (pattern[i+1] == multiChar || pattern[i+1] == singleChar)) {
          ch = pattern[++i];
          if (ch == singleChar)
            pattern[++j] = PatternMatch.SINGLE;
        }
/*
        if (lastMultiIndex >= 0) {
          // Identify Boyer-Moore optimizations
          int max_len = 0;
          int max_start = 0;
          int k = lastMultiIndex;
          int start;
          char ch_max = 0;
          char ch_min = 0x7fff;
          while (k < j) {
            start = k;
            ch = pattern[++k];
            while (k < j && ch > PatternMatch.SINGLE) {
              if (ch > ch_max)
                ch_max = ch;
              if (ch < ch_min)
                ch_min = ch;
              ch = pattern[++k];
            }
            if (k - start > best_len && ch_max - ch_min
          }

        }
*/
        pattern[++j] = PatternMatch.MULTI;
        lastMultiIndex = j;
      }
      else {
        if (ch == escapeChar) {
          ch = i < len ? pattern[++i] : 0;
          if (ch != multiChar && ch != singleChar && ch != escapeChar) {
              return errorPos;
//            QueryError.invalidLikePattern(errorPos);
          }
        }
        pattern[++j] = ch;
      }
    }
    if (j < len) {
      char[] realloc = new char[j+1];
      System.arraycopy(pattern, 0, realloc, 0, j+1);
      pattern = realloc;
    }
    this.pattern = pattern;
    this.pat_len = pattern.length - 1;
    return -1;
  }

  private final boolean match(int pat_i, int exp_i) {
    while (pat_i < pat_len) {
      ch = pattern[++pat_i];
      if (ch > PatternMatch.SINGLE) {
        if (++exp_i > exp_len || ch != exp[exp_i])
          return false;
      }
      else if (ch == PatternMatch.SINGLE) {
        if (++exp_i > exp_len)
          return false;
      }
      else if (ch == PatternMatch.MULTI) {
        if (lastMultiIndex <= pat_i) {
          if (pat_len - pat_i > exp_len - exp_i)
            return false;
          exp_i = exp_len - (pat_len - pat_i);
        }
        else {
          while (exp_i < exp_len) {
            ch = pattern[pat_i+1];
            while (exp_i < exp_len && exp[++exp_i] != ch)
              {};
            if (exp_i == exp_len && exp[exp_i] != ch)
              return false;
            if (match(pat_i, exp_i-1))
              return true;
          }
          return false;
        }
      }
      else if (ch == PatternMatch.JMPTABLE) {
        // Boyer-Moore Search for characters "xyz" in a pattern like: "...%a_xyz__%..."
        byte[] map = jmpTable[pattern[++pat_i]];
        int    part_len   = map[map.length-1]; // length of part "xyz"     i.e. 3
        int    part_start = map[map.length-2]; // start of "xyz" after "%" i.e. 2
        int    min_range  = map[map.length-3]; // lowest char value        i.e. 'x'
        int    max_range  = map[map.length-4]; // highest char value       i.e. 'z'

        int    delta        = part_start + part_len;
        int    part_start_i = pat_i + part_start + 1;
        pat_i += delta;

        while (true) {
          while (delta > 0) {    // <- we want to spend most of the time here in this loop!
            exp_i += delta;
            if (exp_i >= exp_len)
              return false;
            ch     = exp[exp_i];
            delta  = ch < min_range || ch >= max_range ? part_len : map[ch-min_range];
          }

          while (exp[--exp_i] == pattern[--pat_i] && pat_i > part_start_i)
          {};

          if (pat_i == part_start && match(pat_i-part_start, exp_i-part_start))
            return true;

          ch     = exp[exp_i];
          delta  = ch < min_range || ch >= max_range ? part_len : map[ch-min_range];
          if (delta <= part_start_i + part_len - pat_i)
            delta = part_start_i + part_len - pat_i + 1;
          pat_i = part_start_i + part_len;
        }
      }
    }
    return exp_i == exp_len;
  }

  private char[] exp;
  private char[] pattern;
  private byte[] jmpTable[];
  private int    lastMultiIndex;
  private int    lastSingleIndex;
  private char   ch;
  private int    exp_len;
  private int    pat_len;
  private int    singles;
  private static char SINGLE = 2;
  private static char MULTI  = 1;
  private static char JMPTABLE = 0;

  private String  patternString;
  private char    escapeChar;
  private char    multiChar;
  private char    singleChar;
}
