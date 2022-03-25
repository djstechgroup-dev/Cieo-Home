package com.kinetise.data.descriptors;

import com.kinetise.data.descriptors.actions.VariableDataDesc;

public class ApplicationDescriptionDataDesc {

    private String mName;
    private VariableDataDesc mStartScreenId;
    private VariableDataDesc mLoginScreenId;
    private VariableDataDesc mMainScreenId;
    private VariableDataDesc mProtectedLoginScreenId;
    private String mVersion;
    private String mApiVersion;
    private String mDefaultUserAgent;
    private float minFontSizeMultiplier;
    private float maxFontSizeMultiplier;
    private int validationErrorToastColor;

    private String mCreatedVersion;

    public String getCreatedVersion() {
        return mCreatedVersion;
    }

    public void setCreatedVersion(String createVersion) {
        mCreatedVersion = createVersion;
    }

    public String getName() {
        return mName;
    }

    public String getApiVersion() {
        return mApiVersion;
    }

    public void setApiVersion(String apiVersion) {
        mApiVersion = apiVersion;
    }

    public void resolveDynamicFields() {
        resolveField(mStartScreenId);
        resolveField(mLoginScreenId);
        resolveField(mMainScreenId);
        resolveField(mProtectedLoginScreenId);
    }

    public void resolveField(VariableDataDesc variable) {
        if (variable != null)
            variable.resolveVariable();
    }

    public String getMainScreenId() {
        if (mMainScreenId == null)
            return null;
        return mMainScreenId.getStringValue();
    }

    public void setMainScreenId(VariableDataDesc mainScreenId) {
        mMainScreenId = mainScreenId;
    }

    public String getStartScreenId() {
        if (mStartScreenId == null)
            return null;
        return mStartScreenId.getStringValue();
    }

    public void setName(String name) {
        mName = name;
    }

    public void setStartScreenId(VariableDataDesc startScreenId) {
        mStartScreenId = startScreenId;
    }

    public String getProtectedLoginScreenId() {
        if (mProtectedLoginScreenId == null)
            return null;
        return mProtectedLoginScreenId.getStringValue();
    }

    public void setProtectedLoginScreenId(VariableDataDesc protectedLoginScreenId) {
        mProtectedLoginScreenId = protectedLoginScreenId;
    }

    public boolean hasSplashScreen() {
        return getLoginScreenId() == null;
    }

    public boolean hasLoginScreen() {
        return getLoginScreenId() != null;
    }

    public String getLoginScreenId() {
        if (mLoginScreenId == null)
            return null;
        return mLoginScreenId.getStringValue();
    }

    public void setLoginScreenId(VariableDataDesc pLoginScreen) {
        mLoginScreenId = pLoginScreen;
    }

    public void setVersion(String version) {
        mVersion = version;
    }

    public String getVersion() {
        return mVersion;
    }

    public String getDefaultUserAgent() {
        return mDefaultUserAgent;
    }

    public void setDefaultUserAgent(String defaultUserAgent) {
        mDefaultUserAgent = defaultUserAgent;
    }

    public void setMinFontSizeMultiplier(float minFontSizeMultiplier) {
        this.minFontSizeMultiplier = minFontSizeMultiplier;
    }

    public void setMaxFontSizeMultiplier(float maxFontSizeMultiplier) {
        this.maxFontSizeMultiplier = maxFontSizeMultiplier;
    }

    public float getMinFontSizeMultiplier() {
        return minFontSizeMultiplier;
    }

    public float getMaxFontSizeMultiplier() {
        return maxFontSizeMultiplier;
    }

    public int getValidationErrorToastColor() {
        return validationErrorToastColor;
    }

    public void setValidationErrorToastColor(int validationErrorToastColor) {
        this.validationErrorToastColor = validationErrorToastColor;
    }
}
