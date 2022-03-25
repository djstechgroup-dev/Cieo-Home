package com.kinetise.helpers.DescriptorCompiler;

/**
 * @author: Marcin Narowski
 * Date: 20.02.14
 * Time: 14:17
 */
public class GUID {
	private static int index = 0;
	
    public static String get(){
        return "a" + (index++);
    }

    public static void reset(){
        index = 0;
    }

}
