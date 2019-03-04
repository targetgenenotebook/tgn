package com.tgn;

class QuoteEscape {
	static String quote_escape(String in) {
		StringBuffer sb = new StringBuffer();
		char[] ina = in.toCharArray();
		for (int i=0; i<ina.length; ++i) {
			if (ina[i]=='\'' || ina[i]=='\"') sb.append("\\");
			sb.append(ina[i]);
		}
		return sb.toString();
	}
}

