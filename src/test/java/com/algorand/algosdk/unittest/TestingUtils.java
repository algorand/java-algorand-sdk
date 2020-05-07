package com.algorand.algosdk.unittest;

import java.util.Arrays;
import java.util.Comparator;

public class TestingUtils {

	public static boolean comparePathUrls(String url1, String url2, String skip) {
		url1 = url1.replace(skip, "");
		url2 = url2.replace(skip, "");
		
		String[] segments1 = url1.split("[&?]");
		String[] segments2 = url2.split("[&?]");
		
		Arrays.sort(segments1, Comparator.naturalOrder());
		Arrays.sort(segments2, Comparator.naturalOrder());
		
		if (segments1.length != segments2.length) {
			return false;
		}
		
		int s2 = 0;
		for (String seg1 : segments1) {
			if (!seg1.equals(segments2[s2])) {
				return false;
			}
			s2++;
		}
		return true;
	}
	
	public static boolean notEmpty(String str) {
		return !str.isEmpty() && !str.equals("none");		
	}
	
	public static boolean notEmpty(Long val) {
		return val != 0;
	}
}
