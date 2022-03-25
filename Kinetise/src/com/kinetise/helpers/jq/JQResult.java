package com.kinetise.helpers.jq;

public class JQResult {

    private int mResultStatus;
    private String mOutput;

    public JQResult(int resultStatus) {
        this.mResultStatus = resultStatus;
    }

    /**
     *
     * @return 0 for success, >0 for error
     */
    public int getResultStatus() {
        return mResultStatus;
    }

    /**
     *
     * @return Transformed JSON if getResultStatus() == 0, or error message otherwise
     */
    public String getOutput() {
        return mOutput;
    }

    void setOutput(String output) {
        this.mOutput = output;
    }
}

