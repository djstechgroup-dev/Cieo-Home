package com.kinetise.helpers;

import java.util.ArrayList;

public class ActionStringTokenizer {

    public static ArrayList<String> tokenize(String string, char separator) {
        ArrayList<String> result = new ArrayList<String>();

        if (string == null || string.equals(""))
            return result;

        if (string.endsWith(Character.toString(separator))) {
            string = string.substring(0, string.length() - 1);
        }

        int openParenthesisCounter = 0;
        int newTokenStartIndex = 0;
        boolean inString = false;

        for (int i = 0; i < string.length(); ++i) {
            char currentCharacter = string.charAt(i);
            if (currentCharacter == '\'') {
                if (inString && string.charAt(i - 1) != '\\') {
                    inString = false;
                } else if (!inString) {
                    inString = true;
                }
            } else if (!inString && currentCharacter == '(') {
                ++openParenthesisCounter;

            } else if (!inString && currentCharacter == ')') {
                --openParenthesisCounter;
                if (openParenthesisCounter < 0) {
                    throw new IllegalStateException("Mismatched parenthesis in string :" + string);
                }

            } else if (currentCharacter == separator && openParenthesisCounter == 0 && !inString) {
                String token = string.substring(newTokenStartIndex, i).trim();
                result.add(token);
                newTokenStartIndex = i + 1;
            }
        }

        String token = string.substring(newTokenStartIndex, string.length()).trim();
        if (!token.equals(""))
            result.add(token);

        return result;
    }

}
