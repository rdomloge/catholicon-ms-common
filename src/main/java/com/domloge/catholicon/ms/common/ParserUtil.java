package com.domloge.catholicon.ms.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

public class ParserUtil {
	
	private static final String QUOTED_DATE_REGEXP = "new Date\\((.*?)\\)";
	private static final Pattern datePattern = Pattern.compile(QUOTED_DATE_REGEXP);
	
	public static String parseDate(String s) {
		Matcher m = datePattern.matcher(s);
		SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
		if(m.find()) {
			Date dateObj;
			String dateString = m.group(1);
			try {
				dateObj = format.parse(dateString);
				return new SimpleDateFormat("yyyy-MM-dd").format(dateObj);	
			} 
			catch(NumberFormatException nfex) {
				throw new RuntimeException("Could not extract date from '"+dateString+"'", nfex);
			}
			catch (ParseException e) {
				return dateString;
			}
		}
		
		return "TBA";
	}
	
	/**
     * Returns a copy of the string, with leading and trailing whitespace
     * omitted.
     * <p>
     * If this <code>String</code> object represents an empty character
     * sequence, or the first and last characters of character sequence
     * represented by this <code>String</code> object both have codes
     * greater than <code>'&#92;u0020'</code> (the space character), then a
     * reference to this <code>String</code> object is returned.
     * <p>
     * Otherwise, if there is no character with a code greater than
     * <code>'&#92;u0020'</code> in the string, then a new
     * <code>String</code> object representing an empty string is created
     * and returned.
     * <p>
     * Otherwise, let <i>k</i> be the index of the first character in the
     * string whose code is greater than <code>'&#92;u0020'</code>, and let
     * <i>m</i> be the index of the last character in the string whose code
     * is greater than <code>'&#92;u0020'</code>. A new <code>String</code>
     * object is created, representing the substring of this string that
     * begins with the character at index <i>k</i> and ends with the
     * character at index <i>m</i>-that is, the result of
     * <code>this.substring(<i>k</i>,&nbsp;<i>m</i>+1)</code>.
     * <p>
     * This method may be used to trim whitespace (as defined above) from
     * the beginning and end of a string.
     *
     * @return  A copy of this string with leading and trailing white
     *          space removed, or this string if it has no leading or
     *          trailing white space.
     */
    public static String trim(String s) {
    	char[] value = s.toCharArray();
        int len = value.length;
        int st = 0;
        char[] val = value;    /* avoid getfield opcode */

        while ((st < len) && (val[st] <= ' ' || val[st] == (char)160)) {
            st++;
        }
        while ((st < len) && (val[len - 1] <= ' ')) {
            len--;
        }
        return ((st > 0) || (len < value.length)) ? new String(value, st, len - st) : s;
    }
    
    public static String nullIfEmpty(String s) {
		if(null == s || StringUtils.isEmpty(trim(s))) return null;
		return s;
	}

	public static String[] splitOnUnquotedCommas(String s) {
		List<String> parts = new LinkedList<>();
		StringBuilder buf = new StringBuilder();
		char[] chars = s.toCharArray();
		boolean inSingleQuotes = false;
		boolean inDoubleQuotes = false;
		
		for (char c : chars) {
			switch(c) {
				case ',':
					if(inSingleQuotes || inDoubleQuotes) {
						buf.append(c);
						break;
					}
					parts.add(buf.toString());
					buf.setLength(0);
					break;
				case '"':
					inDoubleQuotes = !inDoubleQuotes;
					buf.append(c);
					break;
				case '\'':
					inSingleQuotes = !inSingleQuotes;
					buf.append(c);
					break;
				default:
					buf.append(c);
			}
		}
		
		if(buf.length() > 0) parts.add(buf.toString());
		
		return parts.toArray(new String[parts.size()]);
	}
	
	public static Map<String, String> pairsToMap(String[] pairs) {
		Map<String, String> map = new HashMap<>();
		for (String pair : pairs) {
			String[] kvp = ParserUtil.splitOnUnquotedColons(pair);
			map.put(kvp[0].trim(), kvp.length < 2 ? null : kvp[1].trim());
		}
		return map;
	}
	
	public static Map<String,String> convertJsonToMap(String json) {
		return pairsToMap(splitOnUnquotedCommas(json.substring(1, json.length()-1)));
	}
	
	public static String[] splitOnUnquotedColons(String s) {
		List<String> parts = new LinkedList<>();
		StringBuilder buf = new StringBuilder();
		char[] chars = s.toCharArray();
		boolean inSingleQuotes = false;
		boolean inDoubleQuotes = false;
		
		for (char c : chars) {
			switch(c) {
				case ':':
					if(inSingleQuotes || inDoubleQuotes) {
						buf.append(c);
						break;
					}
					parts.add(buf.toString());
					buf.setLength(0);
					break;
				case '"':
					if(inSingleQuotes) buf.append(c);
					inDoubleQuotes = !inDoubleQuotes;
					
					break;
				case '\'':
					if(inDoubleQuotes) buf.append(c);
					inSingleQuotes = !inSingleQuotes;
					
					break;
				default:
					buf.append(c);
			}
		}
		
		if(buf.length() > 0) parts.add(buf.toString());
		
		return parts.toArray(new String[parts.size()]);
	}
	
	private static Pattern arrayPattern = Pattern.compile("(?s)\\{(.*)\\}");

	public static String[] splitArray(String jsonArray) {
		Matcher m = arrayPattern.matcher(jsonArray);
		List<String> parts = new LinkedList<>();
		
		while(m.find()) {
			parts.add(m.group());
		}
		
		return parts.toArray(new String[parts.size()]);
	}
	
	public static String[] splitArray2(String json) {
		char[] chars = json.toCharArray();
		List<String> list = new LinkedList<>();
		int inBraceCount = 0;
		StringBuilder buf = new StringBuilder();
		for (char c : chars) {
			if('{' == c) {
				inBraceCount++;
			}
			else if('}' == c) {
				inBraceCount--;
				if(0 == inBraceCount) {
					list.add(buf.toString());
					buf.setLength(0);
				}
			}
			else {
				if(inBraceCount > 0) {
					buf.append(c);
				}
				else {
					//we are between strings
				}
			}
		}
		return list.toArray(new String[list.size()]);
	}
}
