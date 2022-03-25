package com.kinetise.helpers.parser.xmlNodePath;

import java.util.Stack;

public class XMLNodePath extends Stack<String> {

    private final String mItemPath;
    private StringBuilder mCurrentAbsPathBuilder;

    public XMLNodePath(String itemPath) {
        super();
        mCurrentAbsPathBuilder = new StringBuilder();
        mItemPath = itemPath;
    }

    public void push(String nodeName, String prefix) {
        push(mCurrentAbsPathBuilder.toString());
        mCurrentAbsPathBuilder.append("/");
        if (prefix != null) {
            mCurrentAbsPathBuilder.append(prefix);
            mCurrentAbsPathBuilder.append(":");
        }
        mCurrentAbsPathBuilder.append(nodeName);
    }

    @Override
    public synchronized String pop() {
        String previousPathString = super.pop();
        mCurrentAbsPathBuilder.delete(previousPathString.length(), mCurrentAbsPathBuilder.length());
        return previousPathString;
    }

    public String getRelativePath() {
        String currentAbsPath = mCurrentAbsPathBuilder.toString();
        if (currentAbsPath.length() == mItemPath.length())
            return "";
        else
            return currentAbsPath.substring(mItemPath.length() + 1);
    }

    public boolean isInItemRoot() {
        return mItemPath.equals(mCurrentAbsPathBuilder.toString());
    }
}
