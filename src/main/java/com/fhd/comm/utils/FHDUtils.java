package com.fhd.comm.utils;

public class FHDUtils {
	public static Boolean isInteger(String string){
		
		return string.matches("^[-+]?([0-9]+)$");
	}
}
