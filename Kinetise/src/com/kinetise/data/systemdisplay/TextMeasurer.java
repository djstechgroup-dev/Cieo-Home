package com.kinetise.data.systemdisplay;

import com.kinetise.data.descriptors.calcdescriptors.TextCalcDesc;
import com.kinetise.data.descriptors.datadescriptors.components.TextDescriptor;
import com.kinetise.data.descriptors.types.AGTextAlignType;
import com.kinetise.data.descriptors.types.AGTextVAlignType;
import com.kinetise.data.systemdisplay.helpers.AGTypefaceLocation;
import com.kinetise.data.systemdisplay.views.text.CharWidthLoaderHelper;
import com.kinetise.data.systemdisplay.views.text.LineData;
import com.kinetise.helpers.calcmanagerhelper.PrecisionFixHelper;

import java.util.ArrayList;
import java.util.List;

public class TextMeasurer {
    TextDescriptor mDescriptor;
    private double mMeasuredHeight = 0;
    private double mMeasuredWidth = 0;
    private int mMaxLines = -1;
    private int mMaxCharacters = 0;
    private double mFontSize = 0;
    private ArrayList<LineData> mLinesData;
    private double mFontInterline;
    private CharWidthLoaderHelper mCharWidthLoader;
    private String mFontNameValue;

    public TextMeasurer(TextDescriptor descriptor){
        setDescriptor(descriptor);
    }

    public void measure(String text, double maxWidth) {
        int lineCount;
        String measuredText = text;

        mLinesData = new ArrayList<>();
        mMeasuredWidth = 0;
        mMeasuredHeight = 0;

        TextCalcDesc calcDesc = mDescriptor.getCalcDescriptor();
        calculatePaddings();
        maxWidth -= (calcDesc.getPadding().left + calcDesc.getPadding().right);
        if (maxWidth < 0) {
            maxWidth = 0;
        } else {
            maxWidth = PrecisionFixHelper.toPrecision(maxWidth, 6);
        }
        if (measuredText == null) {
            measuredText = "";
        }


        // cut text if longer than max characters
        if (mMaxCharacters > 0 && measuredText.length() > mMaxCharacters) {
            measuredText = measuredText.substring(0, mMaxCharacters);
        }

        //We split text into lines based on new line characters in the string,
        //next for each line we calculate lineData which consists of how this line of text
        //should be broken into lines based on the available width.
        if(!measuredText.equals("")) {
            String[] textByLines = measuredText.split("\\n", -1);
            for (String textByLine : textByLines) {
                calculateAndPopulateLines(textByLine, maxWidth);
                if (mLinesData.size() >= mMaxLines) {
                    break;
                }
            }
        }

        lineCount = mLinesData.size();
        double rowHeight = mCharWidthLoader.getLineHeight(mFontNameValue, mFontSize);
        mMeasuredHeight = lineCount * rowHeight;
        if(lineCount>0)
            mMeasuredHeight += (lineCount - 1) * mFontInterline;

        writeToCalcDesc();
    }

    public void layout(double width, double height){
        TextCalcDesc calcDesc = mDescriptor.getCalcDescriptor();
        List<LineData> lines =  calcDesc.getLinesData();
        double textBlockWidth = (width - calcDesc.getPadding().left) - calcDesc.getPadding().right;
        double textBlockHeight = (height - calcDesc.getPadding().top) - calcDesc.getPadding().bottom;
        double textBlockYPos=getYPosForTextBlock(mDescriptor, textBlockHeight);
        LineData line;

        for (int i=0;i<lines.size();++i){
            line = lines.get(i);
            line.positionY = (int) Math.round(calcDesc.getFontSize() + textBlockYPos + getLineYPosInBlock(i,calcDesc.getRowHeight(),calcDesc.getTextInterline()));
            line.positionX = calcDesc.getPadding().left + getXPosForLine(textBlockWidth, line.width, mDescriptor.getTextAlign());
        }
    }

    private double getLineYPosInBlock(int lineNumber, double lineHight, double interLine){
        int numberOfInterlines = Math.max((lineNumber-1),0);
        double totalInterline = numberOfInterlines * interLine;
        return (lineNumber*lineHight)+totalInterline;
    }



    private int getXPosForLine(double availableSpace, double textWidth, AGTextAlignType textAlign) {
        double xPos;
        double freeSpace = (float)(availableSpace - textWidth);

        switch (textAlign){
            case CENTER:
                xPos = (freeSpace * 0.5d);
                break;
            case RIGHT:
                xPos = freeSpace;
                break;
            case LEFT:
            default: xPos = 0;
        }

        if (xPos >= 0)
            return (int)Math.round(xPos);
        else
            return 0;
    }

    private double getYPosForTextBlock(TextDescriptor descriptor, double textSpaceHeight) {
        AGTextVAlignType textVAlign = descriptor.getTextVAlign();
        TextCalcDesc calcDescriptor = descriptor.getCalcDescriptor();
        double textHeight = calcDescriptor.getTextHeight();
        double paddingTop = calcDescriptor.getPadding().top;
        double paddingBottom = calcDescriptor.getPadding().bottom;
        double textHeightWithoutPaddings = (textHeight - paddingBottom) - paddingTop;
        if (AGTextVAlignType.TOP.equals(textVAlign)) {
            return paddingTop;
        } else if (AGTextVAlignType.CENTER.equals(textVAlign)) {
            return paddingTop+((textSpaceHeight - textHeightWithoutPaddings) / 2);
        } else if (AGTextVAlignType.BOTTOM.equals(textVAlign)) {
            return (paddingTop + textSpaceHeight) - textHeightWithoutPaddings;
        }
        return 0;
    }

    private void calculatePaddings() {
        TextCalcDesc calcDesc = mDescriptor.getCalcDescriptor();
        calcDesc.getPadding().left = (int) Math.round(mDescriptor.getPadding().getLeft().inPixels());
        calcDesc.getPadding().right = (int) Math.round(mDescriptor.getPadding().getRight().inPixels());
        calcDesc.getPadding().top = (int) Math.round(mDescriptor.getPadding().getTop().inPixels());
        calcDesc.getPadding().bottom = (int) Math.round(mDescriptor.getPadding().getBottom().inPixels());

    }

    private void writeToCalcDesc() {
        TextCalcDesc calcDesc = mDescriptor.getCalcDescriptor();
        int horizontalPaddings = calcDesc.getPadding().left + calcDesc.getPadding().right;
        int verticalPaddings = calcDesc.getPadding().top + calcDesc.getPadding().bottom;
        calcDesc.setTextWidth(mMeasuredWidth + horizontalPaddings);
        calcDesc.setTextHeight(mMeasuredHeight + verticalPaddings);
        calcDesc.setLinesData(getLinesData());
        calcDesc.setRowHeight(getRowHeight());
        calcDesc.setTextInterline(getInterline());
    }

    /**
     * Calculates length of the text string from start index to end index
     * Uses font size set in this textMeasurer using setTextParams method.
     *
     * @param text  - text which size we want to calculate
     * @param start - index of the first character we want to measure
     * @param end   - index of the character after the one we want to end measuring.
     * @return returns width of the part of string from start index to (end - 1) index
     */
    private double calcStringWidth(String text, int start, int end, double maxWidth) {

        double width = 0.0f;
        int index = start;

        if (text == null) {
            return 0;
        }

        while (index < end && width <= maxWidth) {
            width += mCharWidthLoader.getCharWidth(text.charAt(index++), mFontSize);
        }

        return width;
    }

    /**
     * Splits the text into lines based on text parameters and max line width
     * Also counts each lines width and sets it inside each lines LineData object;
     *
     * @param text  text to be split into lines
     * @param width maximal width of a single line
     */
    private void calculateAndPopulateLines(String text, double width) {

        //If the line is empty, we still add it to the LinesData collection
        if (text.equals("")) {
            mLinesData.add(new LineData("", 0));
            return;
        }

        int stringIndex = 0;

        //We loop until we reach the end of the string, or get to lines limit
        while ((mLinesData.size() < mMaxLines) && (stringIndex != text.length())) {

            LineData lineData = new LineData();
            stringIndex = calcLineForWidth(text, stringIndex, width, lineData);
            addLine(lineData);

            if ((stringIndex < text.length() - 1) && text.charAt(stringIndex) == ' ') { //omit one space after last line
                stringIndex++;
            }
        }

    }
    private void addLine(LineData line) {
        mLinesData.add(line);
        if (line.width > mMeasuredWidth)
            mMeasuredWidth = line.width;
    }


    /**
     * Calculates lineData for given text, starting from start index, that will fit into maxWidth
     * <p/>
     * Function will try to split at whole words but if single word is too long to fit inside maxWidth
     * it will split that word and return that.
     *
     * @param text          string containing the text we are going to split
     * @param startingIndex index of the first character of the line inside the text
     * @param maxWidth      maximal width of the line
     * @param result        lineData object to be populated with calculated line and its width
     * @return index of the first character outside of the calculated line
     */
    int calcLineForWidth(String text, int startingIndex, double maxWidth, LineData result) {
        int nextSpaceIndex,
                previousSpaceIndex,
                endIndex;//index of the last character in calculated line
        double textWidth;


        //we calculate length of the string adding one word each loop
        // until we will exceed maxWidth or get to the end of string
        nextSpaceIndex = startingIndex;
        endIndex = text.length();
        double lastTextWidth = 0;
        do {
            previousSpaceIndex = nextSpaceIndex;
            nextSpaceIndex = text.indexOf(' ', nextSpaceIndex + 1);
            if (nextSpaceIndex > 0)
                textWidth = lastTextWidth + calcStringWidth(text, previousSpaceIndex, nextSpaceIndex, maxWidth - lastTextWidth);
            else //this is the last word in the string so we calculate for the whole string
                textWidth = lastTextWidth + calcStringWidth(text, previousSpaceIndex, text.length(), maxWidth - lastTextWidth);
            if (textWidth > maxWidth) {
                if (previousSpaceIndex == startingIndex) {//single word is too long
                    endIndex = breakWord(text, startingIndex, maxWidth);
                    lastTextWidth = calcStringWidth(text, startingIndex, endIndex, maxWidth);
                } else {
                    endIndex = previousSpaceIndex;
                }
                break;
            } else {
                lastTextWidth = textWidth;
            }
        } while (nextSpaceIndex > 0);

        result.text = text.substring(startingIndex, endIndex);
        result.width = lastTextWidth;
        return endIndex;
    }

    /**
     * Calculates a position of the first character in text that doesn't fit into maxWidth starting from string index
     *
     * @param text          text with the word we are trying to break
     * @param startingIndex index of the firs character in the word
     * @param maxWidth      width that we need to fit our word into
     * @return position of the first character that will not fit inside max width
     */
    int breakWord(String text, int startingIndex, double maxWidth) {
        int index = startingIndex;
        double width = 0;
        int stringLength = text.length();
        while (index < stringLength) {
            width += mCharWidthLoader.getCharWidth(text.charAt(index), mFontSize);

            if (width >= maxWidth) {
                break;
            }
            index++;
        }
        //we take at least one character
        if (index == startingIndex)
            index++;
        return index;
    }

    /**
     * Calculates font name based on style and returns font name calculated based on the font filename without extension
     *
     * @return name of the font with given style
     */
    private String getFontName(boolean bold, boolean italic) {

        if (bold && italic) {
            return AGTypefaceLocation.FONT_BOLD_ITALIC_NAME;
        }

        if (bold) {
            return AGTypefaceLocation.FONT_BOLD_NAME;
        }

        if (italic) {
            return AGTypefaceLocation.FONT_ITALIC_NAME;
        }


        return AGTypefaceLocation.FONT_NORMAL_NAME;
    }

    public void setDescriptor(TextDescriptor descriptor){
        mDescriptor = descriptor;
        setTextParams(descriptor);
    }

    private void setTextParams(TextDescriptor textDesc) {
        mFontSize = textDesc.getFontSize();
        int maxLines = textDesc.getMaxLines();
        mMaxLines = maxLines == -1 ? Integer.MAX_VALUE : maxLines;
        mMaxCharacters = textDesc.getMaxCharacters();
        mFontNameValue = getFontName(textDesc.isBold(), textDesc.isItalic());
        mCharWidthLoader = new CharWidthLoaderHelper(mFontNameValue);
        mFontInterline = (-mFontSize * 0.15);
    }

    public ArrayList<LineData> getLinesData() {
        return mLinesData;
    }

    public double getRowHeight() {
        return mCharWidthLoader.getLineHeight(mFontNameValue, mFontSize);
    }

    public double getInterline() {
        return mFontInterline;
    }
}
