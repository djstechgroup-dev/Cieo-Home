package com.kinetise.helpers.regexp;

import java.util.regex.Pattern;

/**
 * @author: Marcin Narowski
 * Date: 08.04.14
 * Time: 11:03
 */
public class RegexpRule {

    /**
     * Also regexName, name of the rule
     */
    public String controlType;

    /**
     * marks if the regex should
     */
    public Pattern pattern;
    public boolean returnMatch = false;
    public String replaceWith;

    public void setRule(String rule){
        pattern = Pattern.compile(rule, Pattern.CASE_INSENSITIVE);
    }

    public Pattern getRule(){
        return pattern;
    }

}
