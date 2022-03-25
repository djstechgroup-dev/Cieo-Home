package com.kinetise.helpers.regexp;

import java.util.ArrayList;

public class HtmlScanner {
    private final String mText;

    private ArrayList<HtmlTag> mTags;
    private int mCurrentIndex;

    public HtmlScanner(String text) {
        mText = text;
        mTags = new ArrayList<HtmlTag>();
    }

    public ArrayList<HtmlTag> getTags() {
        return mTags;
    }

    public String processText() {
        StringBuffer resultText = new StringBuffer();
        int textLength = mText.length();
        for (mCurrentIndex = 0; mCurrentIndex < textLength; ++mCurrentIndex) {
            char currentChar = getCurrentCharacter();
            if (currentChar == '<') {
                ++mCurrentIndex;
                HtmlTag currentTag = new HtmlTag();
                currentTag.mTagName = readTagName();
                readAttributes(currentTag);
                mTags.add(currentTag);
                if(currentTag.mTagName.equals("br") || currentTag.mTagName.equals("br/") || currentTag.mTagName.equals("/p") || currentTag.mTagName.equals("p")){
                    resultText.append('\n');
                }
            } else {
                resultText.append(currentChar);
            }
        }
        return resultText.toString().trim();
    }

    private String readTagName() {
        StringBuffer result = new StringBuffer();
        while (mCurrentIndex<mText.length()) {
            char currentCharacter = getCurrentCharacter();
            if (currentCharacter == ' ' || currentCharacter == '>') {
                break;
            }
            result.append(currentCharacter);
            ++mCurrentIndex;
        }

        return result.toString();
    }

    private void readAttributes(HtmlTag htmlTag) {
        while (mCurrentIndex<mText.length()) {
            char currentChar = getCurrentCharacter();

            if (currentChar == '>' || currentChar == '/') {
                if(currentChar == '/') {
                    while (currentChar != '>') {
                        ++mCurrentIndex;
                        currentChar = getCurrentCharacter();
                    }
                }
                break;
            } else if (currentChar != ' ') {
                String[] nameValuePair = readAttribute();
                htmlTag.mAttributes.put(nameValuePair[0], nameValuePair[1]);
            } else {
                ++mCurrentIndex;
            }
        }
    }

    private String[] readAttribute() {
        String[] result = new String[2];

        boolean readingName = false;
        boolean nameRead = false;
        boolean readingValue = false;
        boolean isValueInQuotes = false;
        boolean metEqualsSign = false;

        StringBuffer readValue = new StringBuffer();

        while (mCurrentIndex<mText.length()) {
            char currentCharacter = getCurrentCharacter();

            if (!nameRead) {
                if (currentCharacter == ' ' || currentCharacter == '=' || currentCharacter == '>') {
                    if (readingName) {
                        result[0] = readValue.toString();

                        readValue = new StringBuffer();
                        nameRead = true;

                        --mCurrentIndex;
                    }
                } else {
                    readingName = true;
                    readValue.append(currentCharacter);
                }
            } else {
                if (!isValueInQuotes && (currentCharacter == ' ' || currentCharacter == '>' || currentCharacter == '/' || currentCharacter == '=')) {
                    if (readingValue) {
                        result[1] = readValue.toString();
                        break;
                    } else if(currentCharacter == '='){
                        metEqualsSign = true;
                    } else if(currentCharacter == '>' || currentCharacter == '/') {
                        result[1] = "";
                        break;
                    }
                } else {
                    if(metEqualsSign) {
                        if (currentCharacter == '"' || currentCharacter == '\'') {
                            if (!isValueInQuotes)
                                readingValue = true;

                            isValueInQuotes = !isValueInQuotes;
                        }
                        else {
                            if(readingValue)
                            readValue.append(currentCharacter);
                            else
                                break;
                        }
                    } else {
                        result[1] = "";
                        break;
                    }
                }
            }
            ++mCurrentIndex;
        }

        return result;
    }

    public String getFirstValidUrl(){
        for (HtmlTag htmlTag : mTags) {
            if (htmlTag.mTagName.equals("a") && htmlTag.mAttributes.get("href") != null) {
                return htmlTag.mAttributes.get("href").trim();
            }
        }
        return "";
    }

    public String getFirstValidImageLink() {
        for (HtmlTag htmlTag : mTags) {
            String width = htmlTag.mAttributes.get("width");
            if (width == null) width = "";
            String height = htmlTag.mAttributes.get("height");
            if (height == null) height = "";
            if (htmlTag.mTagName.equals("img") && (!width.equals("1") && !height.equals("1"))) {
                String imgSrc = htmlTag.mAttributes.get("src");
                if (imgSrc != null)
                    return imgSrc.trim();
            }
        }
        return "";
    }

    private char getCurrentCharacter() {
        return mText.charAt(mCurrentIndex);
    }
}
