package com.kinetise.data.systemdisplay.helpers;

import android.app.Activity;
import android.content.Intent;

import com.kinetise.components.activity.ScanCodeActivity;
import com.kinetise.data.application.popupmanager.PopupManager;
import com.kinetise.data.sourcemanager.LanguageManager;
import com.kinetise.data.systemdisplay.views.AGCodeScannerView;

public class CodeScannerSetter {

    private static AGCodeScannerView mCodeScannerView;

    public static AGCodeScannerView getClickedCodeScannerView() {
        return mCodeScannerView;
    }

    public static void setClickedCodeScannerView(AGCodeScannerView codeScannerView) {
        mCodeScannerView = codeScannerView;
    }

    public static void setScannedCode(int resultCode, Intent data) {
            if (resultCode == Activity.RESULT_OK) {
                String contents = data.getStringExtra(ScanCodeActivity.SCAN_RESULT);
                if (contents != null && mCodeScannerView != null) {
                    mCodeScannerView.setScannedCode(contents);
                    setClickedCodeScannerView(null);
                }
            } else {
                handleCodeScannerError();
            }
    }

    public static void handleCodeScannerError() {
        PopupManager.showErrorPopup(LanguageManager.getInstance().getString(LanguageManager.ERROR_CODE_SCANNER));
    }
}
