package com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers;

import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.FunctionLocalizeTextDataDesc;
import com.kinetise.data.descriptors.actions.functions.*;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.FunctionOpenFileDataDesc;

public class AGXmlFunctionDescFactory {

    public static AbstractFunctionDataDesc getFunctionDescriptor(String functionString,ActionDataDesc action) {
        String functionName = functionString.substring(0,functionString.indexOf('('));
        if (functionName.equals(XmlFunctions.FUNCTION_GO_TO_SCREEN_WITH_CONTEXT)) {
            return new FunctionGoToScreenWithContextDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_DELAY)) {
            return new FunctionDelayDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_GO_TO_SCREEN)) {
            return new FunctionGoToScreenDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_GET_CONTROL)) {
            return new FunctionGetControlDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_CLOSE_POPUP)) {
            return new FunctionClosePopupDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_LOAD_MORE)) {
            return new FunctionShowMoreDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_BACK_BY_STEPS)) {
            return new FunctionBackByStepsDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_BACK_TO_SCREEN)) {
            return new FunctionBackToScreenDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_LOGIN)) {
            return new FunctionLoginDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_LOGOUT)) {
            return new FunctionLogoutDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_NEXT_ELEMENT)) {
            return new FunctionNextElementDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_PREVIOUS_ELEMENT)) {
            return new FunctionPreviousElementDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_REFRESH)) {
            return new FunctionRefreshDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_RELOAD)) {
            return new FunctionReloadDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_UPDATE)) {
            return new FunctionUpdateDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_SEND_EMAIL)) {
            return new FunctionSendEmailDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_SEND_FORM)) {
            return new FunctionSendFormDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_SHOW_IN_WEBBROWSER)) {
            return new FunctionShowInWebBrowserDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_SHOW_IN_YOU_TUBE_PLAYER)) {
            return new FunctionShowInYouTubePlayerDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_GET_ITEM_FIELD)) {
            return new FunctionGetItemFieldDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_GO_TO_PREVIOUS_SCREEN)) {
            return new FunctionGoToPreviousScreenDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_GET_CONTEXT)) {
            return new FunctionGetContextDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_GET_ACTIVE_ITEM_FIELD)) {
            return new FunctionGetActiveItemFieldDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_POST_TO_FACEBOOK)) {
            return new FunctionPostToFacebookDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_OPEN_GALLERY)) {
            return new FunctionOpenGalleryDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_GET_GPS_ACCURACY)) {
            return new FunctionGetGpsAccuracyDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_GET_GPS_LATITUDE)) {
            return new FunctionGetGpsLatitudeDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_GET_GPS_LONGITUDE)) {
            return new FunctionGetGpsLongitudeDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_GET_SESSION_ID)) {
            return new FunctionGetSessionIdDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_GET_ALTERAPI_CONTEXT)) {
            return new FunctionGetAlterApiContextDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_GET_DEVICE_TOKEN)) {
            return new FunctionGetDeviceTokenDataDesc(action);
        }else if (functionName.equals(XmlFunctions.FUNCTION_HIDE_OVERLAY_AND_REFRESH)) {
            return new HideOverlayAndRefreshFunctionDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_MERGE)) {
            return new FunctionMergeDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_GET_FACEBOOK_ACCESS_TOKEN)) {
            return new FunctionGetFacebookAccessTokenDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_GET_GOOGLE_ACCESS_TOKEN)) {
            return new FunctionGetGoogleAccessTokenDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_TRANSLATE)) {
            return new FunctionTranslateDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_FACEBOOK_LOGIN)) {
            return new FunctionFacebookLoginDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_GET_TWITTER_ACCESS_TOKEN)) {
            return new FunctionGetTwitterTokenDataDesc(action);
        } else if (functionName.equals((XmlFunctions.FUNCTION_REGEX))) {
            return new FunctionRegexDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_CALL)) {
            return new FunctionCallDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_OPEN_EMAIL)) {
            return new FunctionOpenEmailDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_OPEN_SMS)) {
            return new FunctionOpenSmsDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_ENCODE)) {
            return new FunctionEncodeDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_DECODE)) {
            return new FunctionDecodeDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_LINKEDIN_LOGIN)) {
            return new FunctionLinkedInLoginDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_GOOGLE_LOGIN)) {
            return new FunctionGoogleLoginDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_PAYMENT)) {
            return new FunctionPaymentDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_ADD_CALENDAR_EVENT)) {
            return new FunctionAddCalendarEventDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_OPEN_MAP_CURRENT_LOCATION)) {
            return new FunctionOpenMapCurrentLocationDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_OPEN_MAP)) {
            return new FunctionOpenMapDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_SHOW_IN_VIDEO_PLAYER)) {
            return new FunctionShowInVideoPlayerDataDesc(action);
        } else if(functionName.equals(XmlFunctions.FUNCTION_BASIC_AUTH_LOGIN)) {
            return new FunctionBasicAuthLoginDataDesc(action);
        } else if(functionName.equals(XmlFunctions.FUNCTION_BASIC_AUTH_LOGOUT)){
            return new FunctionBasicAuthLogoutDataDesc(action);
        } else if(functionName.equals(XmlFunctions.FUNCTION_GET_BASIC_AUTH_BASE_64)) {
            return new FunctionGetBasicAuthBase64DataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_SHOW_OVERLAY)) {
            return new ShowOverlayFunctionDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_HIDE_OVERLAY)) {
            return new HideOverlayFunctionDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_INCREASE_TEXT_MULTIPLIER)) {
            return new FunctionIncreaseTextMultiplierDataDesc(action);
        } else if(functionName.equals(XmlFunctions.FUNCTION_LOCALIZE_TEXT)){
            return new FunctionLocalizeTextDataDesc(action);
        }else if(functionName.equals(XmlFunctions.FUNCTION_DECREASE_TEXT_MULTIPLIER)){
            return new FunctionDecreaseTextMultiplierDataDesc(action);
        } else if (functionName.equals(XmlFunctions.FUNCTION_SET_LOCALIZATION)) {
            return new FunctionSetLocalizationDataDesc(action);
        } else if(functionName.equals(XmlFunctions.FUNCTION_GET_LOCALIZATION)) {
            return new FunctionGetLocalizationDataDesc(action);
        } else if(functionName.equals(XmlFunctions.FUNCTION_GO_TO_PROTECTED_SCREEN)) {
            return new FunctionGoToProtectedScreenDataDesc(action);
        } else if(functionName.equals(XmlFunctions.FUNCTION_GET_HEADER_PARAM_VALUE)) {
            return new FunctionGetHeaderParamValueDataDesc(action);
        } else if(functionName.equals(XmlFunctions.SEND_ASYNC_FORM)) {
            return new FunctionSendAsyncFormDataDesc(action);
        } else if(functionName.equals(XmlFunctions.OFFLINE_READING)) {
            return new FunctionOfflineReadingDataDesc(action);
        } else if(functionName.equals(XmlFunctions.GET_ITEM)){
            return new FunctionGetItemDataDesc(action);
        } else if(functionName.equals(XmlFunctions.SCAN_QR_CODE)){
            return new FunctionScanQRCodeDataDesc(action);
        } else if(functionName.equals(XmlFunctions.GET_PHONE_CONTACT)){
            return new FunctionGetPhoneContactDataDesc(action);
        } else if(functionName.equals(XmlFunctions.SET_LOCAL_VALUE))
            return new FunctionSetLocalValueDataDesc(action);
        else if(functionName.equals(XmlFunctions.GET_LOCAL_VALUE))
            return new FunctionGetLocalValueDataDesc(action);
        else if(functionName.equals(XmlFunctions.GET_VALUE))
            return new FunctionGetValueDataDesc(action);
        else if(functionName.equals(XmlFunctions.SET_VALUE))
            return new FunctionSetValueDataDesc(action);
        else if(functionName.equals(XmlFunctions.CONDITION) || functionName.equals(XmlFunctions.IF))
            return new FunctionConditionDataDesc(action);
        else if(functionName.equals(XmlFunctions.EQUAL))
            return new FunctionEqualsDataDesc(action);
        else if(functionName.equals(XmlFunctions.GREATER))
            return new FunctionGreaterDataDesc(action);
        else if(functionName.equals(XmlFunctions.LOWER))
            return new FunctionLowerDataDesc(action);
        else if(functionName.equals(XmlFunctions.EQUAL_OR_GREATER))
            return new FunctionEqualOrGreaterDataDesc(action);
        else if(functionName.equals(XmlFunctions.EQUAL_OR_LOWER))
            return new FunctionEqualOrLowerDataDesc(action);
        else if(functionName.equals(XmlFunctions.SHOW_ALERT))
            return new FunctionShowAlertDataDesc(action);
        else if(functionName.equals(XmlFunctions.GET_PAGE_SIZE))
            return new FunctionGetPageSizeDataDesc(action);
        else if(functionName.equals(XmlFunctions.COMPUTE))
            return new FunctionComputeDataDesc(action);
        else if(functionName.equals(XmlFunctions.GET_APP_NAME))
            return new FunctionGetAppNameDataDesc(action);
        else if(functionName.equals(XmlFunctions.IS_LOGGED_IN))
            return new FunctionIsLoggedInDataDesc(action);
        else if(functionName.equals(XmlFunctions.OR))
            return new FunctionOrDataDesc(action);
        else if(functionName.equals(XmlFunctions.NOT))
            return new FunctionNotDataDesc(action);
        else if(functionName.equals(XmlFunctions.AND))
            return new FunctionAndDataDesc(action);
        else if(functionName.equals(XmlFunctions.FUNCTION_OPEN_FILE))
            return new FunctionOpenFileDataDesc(action);
        else if(functionName.equals(XmlFunctions.GET_CURRENT_TIME))
            return new FunctionGetCurrentTimeDataDesc(action);
        else if(functionName.equals(XmlFunctions.FUNCTION_START_GPS_TRACKING))
            return new FunctionStartGPSTrackingDataDesc(action);
        else if(functionName.equals(XmlFunctions.FUNCTION_END_GPS_TRACKING))
            return new FunctionEndGPSTrackingDataDesc(action);
        else if(functionName.equals(XmlFunctions.FUNCTION_SALESFORCE_LOGIN))
            return new FunctionSalesForceLoginDataDesc(action);
        else if(functionName.equals(XmlFunctions.FUNCTION_GET_SALESFORCE_ACCESS_TOKEN))
            return new FunctionGetSalesforceTokenDataDesc(action);
        else if(functionName.equals(XmlFunctions.FUNCTION_GET_PAGE_SIZE))
            return new FunctionGetPageSizeDataDesc(action);
        else if(functionName.equals(XmlFunctions.FUNCTION_SEND_ASYNC_FORM_V3))
            return new FunctionSendAsyncFormV3DataDesc(action);
        else if(functionName.equals(XmlFunctions.FUNCTION_SEND_FORM_V3))
            return new FunctionSendFormV3DataDesc(action);
        else if(functionName.equals(XmlFunctions.FUNCTION_NATIVE_SHARE))
            return new FunctionNativeShareDataDesc(action);
        else if(functionName.equals(XmlFunctions.FUNCTION_PLAY_SOUND))
            return new PlaySoundDataDesc(action);
        else if(functionName.equals(XmlFunctions.FUNCTION_STOP_ALL_SOUNDS))
            return new StopAllSoundsDataDesc(action);
        else if(functionName.equals(XmlFunctions.CALCULATE_GEODISTANCE))
            return new CalculateGeoDistanceDesc(action);
        else if(functionName.equals(XmlFunctions.FUNCTION_SAVE_TO_LOCAL))
            return new FunctionSaveFormDataDesc(action);
        else if(functionName.equals(XmlFunctions.SAVE_FORM_TO_LOCAL_DB))
            return new FunctionSaveFormToLocalDBDataDesc(action);
        else if(functionName.equals(XmlFunctions.FUNCTION_GET_GUID))
            return new FunctionGetGuidDataDesc(action);
        else if(functionName.equals(XmlFunctions.FUNCTION_GET_SCREEN_NAME))
            return  new FunctionGetScreenNameDesc(action);
        else if(functionName.equals(XmlFunctions.FUNCTION_SYNCHRONIZE_LOCAL_TABLE))
            return  new FunctionSynchronizeLocalTableDataDesc(action);
        else if(functionName.equals(XmlFunctions.FUNCTION_SYNCHRONIZE_LOCAL_DB))
            return  new FunctionSynchronizeLocalDBDataDesc(action);
        else if(functionName.equals(XmlFunctions.FUNCTION_OPEN_EXTERNAL_APP))
            return  new FunctionOpenExternalAppDataDesc(action);
        else {
            throw new IllegalArgumentException(String.format(
                    "Cannot parse function: '%s'", functionName));
        }
    }
}
