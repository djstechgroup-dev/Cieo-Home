package com.kinetise.helpers.DescriptorCompiler;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.List;

public class EqualsUtil {

	  public static boolean LogAndReturnFalse(Object obj, String fieldName) {
		  String message = String.format("Inequality in [%s] class: field [%s]\n", obj.getClass().getSimpleName(), fieldName);
		  System.out.print(message);
		  return false;
	  }
	  
	  public static boolean LogMessageAndReturnFalse(String msg) {
		  System.out.print(msg);
		  return false;
	  }
	  
	  static public boolean areEqual(List aThis, List aThat) {
		  while(aThis.remove(null));
		  while(aThat.remove(null));  
		  
		  if (aThat.size() != aThis.size()) {
			  LogMessageAndReturnFalse("Lists are uneqal size, debug manually");
		  }
		  

		  for (int i = 0; i < aThis.size(); i++) {

			  
			  if (!aThat.contains(aThis.get(i))) {
				  LogMessageAndReturnFalse(String.format("Element %s not found in array %s\n", aThis.get(i), aThat));
				  return false;
			  }
		  }
		  
		  return true;
	  }
	  
	  public static void LogClassName(String msg) {
		  System.out.print(
				  String.format("Testing %s \n", msg));
	  }
	  
	  public static String escapeJava(String java) {
		  if (java != null) {
			  return StringEscapeUtils.escapeJava(java);  
		  } else {
			  return "";
		  }
		  
	  }
}
