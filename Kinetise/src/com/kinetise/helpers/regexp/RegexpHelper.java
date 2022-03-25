package com.kinetise.helpers.regexp;

import com.kinetise.data.sourcemanager.AssetsManager;
import com.kinetise.data.sourcemanager.AssetsManager.ResultType;
import com.kinetise.helpers.time.GetConfigCommand;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexpHelper {
    private static final String CONTROL = "control";
    public static final String URI_REGEX = "^(((local|http|https|assets):\\/\\/)|(\\/)).*";
    private static List<RegexpRule> sRules = new ArrayList<RegexpRule>();

    public static final String OPTIMIZED_RULE_CONTROLIMAGE = "controlimage";
    public static final String OPTIMIZED_RULE_CONTROLTEXT = "controltext";
    public static final String OPTIMIZED_RULE_FACEBOOKURL = "facebookurl";
    public static final String OPTIMIZED_RULE_URL = "url";

    private static final String[] OPTIMIZED_RULES = {OPTIMIZED_RULE_CONTROLIMAGE, OPTIMIZED_RULE_CONTROLTEXT, OPTIMIZED_RULE_FACEBOOKURL, OPTIMIZED_RULE_URL};

    /**
     * Parses list of rules contained int the json, saves them into sRules list
     *
     * @param JSON
     */
    public static void initRules(String JSON) {
        JSONObject jsonObject;
        sRules = new ArrayList<RegexpRule>();

        try {
            jsonObject = new JSONObject(JSON);

            Iterator<?> keys = jsonObject.keys();

            while (keys.hasNext()) {
                String control = keys.next().toString();

                /*if (isRuleOptimized(control)) {
                    // Ignore rules for which we have optimized non-regex implementation
                    continue;
                }*/

                JSONArray ImageRulesArray = (JSONArray) jsonObject.get(control);

                for (int i = 0; i < ImageRulesArray.length(); i++) {
                    String rule = ImageRulesArray.getJSONObject(i).toString();
                    sRules.add(createRule(control, rule));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static boolean isRuleOptimized(String ruleName) {
        boolean isRuleOptimized = false;
        for (String r : OPTIMIZED_RULES) {
            if (r.equals(ruleName)) {
                isRuleOptimized = true;
                break;
            }
        }
        return isRuleOptimized;
    }

    public static RegexpRule createRule(String ruleName, String inputString) {
        RegexpRule rule = new RegexpRule();
        rule.controlType = ruleName.replace(CONTROL, "");
        try {
            JSONObject jsonObject = new JSONObject(inputString);
            rule.setRule(jsonObject.getString("tag"));
            rule.replaceWith = jsonObject.getString("replaceWith");
            if (jsonObject.has("returnMatch")) {
                rule.returnMatch = jsonObject.getBoolean("returnMatch");
            }
            return rule;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String parseValue(String ruleName, String inputString) {
        if (ruleName.equals(OPTIMIZED_RULE_CONTROLIMAGE)) {
            if (isUri(inputString)) {
                return inputString.trim();
            } else {
                HtmlScanner scanner = new HtmlScanner(inputString);
                scanner.processText();
                return scanner.getFirstValidImageLink();
            }
        } else if (ruleName.equals(OPTIMIZED_RULE_CONTROLTEXT)) {
            HtmlScanner scanner = new HtmlScanner(inputString);
            return scanner.processText();
        } else if (ruleName.equals(OPTIMIZED_RULE_FACEBOOKURL)) {
            return extractUrlFromFacebookUrl(inputString);
        } else if (ruleName.equals(OPTIMIZED_RULE_URL)) {
            if (isUri(inputString)) {
                return inputString;
            } else {
                HtmlScanner scanner = new HtmlScanner(inputString);
                scanner.processText();
                return scanner.getFirstValidUrl();
            }
        }

        if (ruleName == null) {
            return inputString;
        }
        String result = inputString;
        boolean matchFound;
        String identifierLower = ruleName.toLowerCase(Locale.US);
        //we search for the rule with matching name
        for (RegexpRule rule : sRules) {
            if (identifierLower.contains(rule.controlType)) {
                try {
                    Pattern pattern = rule.getRule();
                    Matcher matcher = pattern.matcher(result);
                    matchFound = matcher.find();
                    if (rule.returnMatch) {
                        if (!matchFound) {
                            result = "";
                            continue;
                        }
                        StringBuffer buffer = new StringBuffer();
                        matcher.appendReplacement(buffer, rule.replaceWith);
                        int start = matcher.start();
                        result = buffer.substring(start);
                    } else {
                        result = matcher.replaceAll(rule.replaceWith);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        result = result.trim();

        return result;
    }

    private static boolean isUri(String inputString) {
        return inputString.matches(URI_REGEX);
    }

    private static String extractUrlFromFacebookUrl(String facebookUrl) {
        int index = facebookUrl.indexOf("url=");
        if (index >= 0) {
            StringBuffer result = new StringBuffer();
            for (int i = index + 4; i < facebookUrl.length(); ++i) {
                char currentChar = facebookUrl.charAt(i);
                if (currentChar != '&') {
                    result.append(currentChar);
                } else {
                    return result.toString();
                }
            }

            return result.toString();
        } else {
            return "";
        }
    }

    public static void loadConfig() {
        AssetsManager.getInstance()
                .getAsset(new GetConfigCommand(), ResultType.JSON);
    }
}
