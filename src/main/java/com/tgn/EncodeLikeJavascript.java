package com.tgn;

import java.io.*;

public class EncodeLikeJavascript {

	/*

      To recreate in Java the same escaping as done in Javascript

      Taken from https://sangupta.com/tech/encodeuricomponent-and.html

      Javascript encodeURIComponent(str) encodes all characters except a-zA-Z0-9.-_*~'()!
      assumes/provides UTF-8 encoding

      Java URLEncoder encodes all characters except a-zA-Z0-9.-_*
      Spaces are also converted into + signs
      assumes/provides UTF-8 encoding

	 */

	public static String decodeURIComponent(String encodedURI) {
		char actualChar;
		StringBuffer buffer = new StringBuffer();
		int bytePattern, sumb = 0;
		for (int i = 0, more = -1; i < encodedURI.length(); i++) {
			actualChar = encodedURI.charAt(i);
			switch (actualChar) {
			case '%': {
				actualChar = encodedURI.charAt(++i);
				int hb = (Character.isDigit(actualChar) ? actualChar - '0'
						: 10 + Character.toLowerCase(actualChar) - 'a') & 0xF;
				actualChar = encodedURI.charAt(++i);
				int lb = (Character.isDigit(actualChar) ? actualChar - '0'
						: 10 + Character.toLowerCase(actualChar) - 'a') & 0xF;
				bytePattern = (hb << 4) | lb;
				break;
			}
			case '+': { // these shouldn't appear is string was encoded by Javascript encodeURIComponent
				bytePattern = ' ';
				break;
			}
			default: {
				bytePattern = actualChar;
			}
			}

			if ((bytePattern & 0xc0) == 0x80) { // 10xxxxxx
				sumb = (sumb << 6) | (bytePattern & 0x3f);
				if (--more == 0)
					buffer.append((char) sumb);
			} else if ((bytePattern & 0x80) == 0x00) { // 0xxxxxxx
				buffer.append((char) bytePattern);
			} else if ((bytePattern & 0xe0) == 0xc0) { // 110xxxxx
				sumb = bytePattern & 0x1f;
				more = 1;
			} else if ((bytePattern & 0xf0) == 0xe0) { // 1110xxxx
				sumb = bytePattern & 0x0f;
				more = 2;
			} else if ((bytePattern & 0xf8) == 0xf0) { // 11110xxx
				sumb = bytePattern & 0x07;
				more = 3;
			} else if ((bytePattern & 0xfc) == 0xf8) { // 111110xx
				sumb = bytePattern & 0x03;
				more = 4;
			} else { // 1111110x
				sumb = bytePattern & 0x01;
				more = 5;
			}
		}
		return buffer.toString();
	}

	public static String encodeURIComponent(String input) {
		if(input=="") {
			return input;
		}
		String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_.!~*'()";
		int l = input.length();
		StringBuilder o = new StringBuilder(l * 3);
		try {
			for (int i = 0; i < l; i++) {
				String e = input.substring(i, i + 1);
				if (ALLOWED_CHARS.indexOf(e) == -1) {
					byte[] b = e.getBytes("utf-8");
					o.append(getHex(b));
					continue;
				}
				o.append(e);
			}
			return o.toString();
		} catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return input;
	}

	public static String escapeForHTML(String input) {
		if(input=="") {
			return input;
		}
		int l = input.length();
		StringBuilder o = new StringBuilder(l);
		for (int i = 0; i < l; i++) {
			String e = input.substring(i, i + 1);
			if (e.equals("<")) o.append("&lt;");
			else if (e.equals(">")) o.append("&gt;");
			else if (e.equals("&")) o.append("&amp;");
			else o.append(e);
		}
		return o.toString();
	}    

	private static String getHex(byte buf[]) {
		StringBuilder o = new StringBuilder(buf.length * 3);
		for (int i = 0; i < buf.length; i++) {
			int n = (int) buf[i] & 0xff;
			o.append("%");
			if (n < 0x10) {
				o.append("0");
			}
			o.append(Long.toString(n, 16).toUpperCase());
		}
		return o.toString();
	}

}



