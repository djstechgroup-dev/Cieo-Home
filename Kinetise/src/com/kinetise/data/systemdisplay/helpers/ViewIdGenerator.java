package com.kinetise.data.systemdisplay.helpers;

public class ViewIdGenerator {

	/**
	 * This code generates a hashcode and then computes the absolute value of that hashcode. 
	 * If the hashcode is Integer.MIN_VALUE, then the result will be negative as well 
	 * (since Math.abs(Integer.MIN_VALUE) == Integer.MIN_VALUE).
	 * One out of 2^32 strings have a hashCode of Integer.MIN_VALUE, 
	 * including "polygenelubricants" "GydZG_" and ""DESIGNING WORKHOUSES".
	 */
	public static int generateViewId(String id){
		int hashCode = id.hashCode();

		if (hashCode == Integer.MIN_VALUE) {
			hashCode += 1;
		}

		return Math.abs(hashCode);
	}
}
