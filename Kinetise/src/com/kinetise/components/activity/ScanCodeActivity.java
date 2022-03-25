package com.kinetise.components.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.kinetise.components.application.KinetiseApplication;
import com.kinetise.data.application.externalapplications.WebBrowserApp;
import com.kinetise.data.sourcemanager.LanguageManager;
import com.kinetise.helpers.regexp.RegexpHelper;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanCodeActivity extends Activity implements ZXingScannerView.ResultHandler {

    public static final int REQUEST_SCAN_CODE_FUNCTION = 32;
    public static final int REQUEST_SCAN_CODE_CONTROL = 33;
    public static final int RESULT_URL_ERROR = 400;
    public static final String REQUEST_CODE = "requestCode";
    public static final String FORMATS = "formats";
    public static final String SCAN_RESULT = "scanResult";
    private Context mContext;
    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        KinetiseApplication.getInstance().logToCrashlytics("[SCA] onCreate");
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setFormats();
        mContext = this;
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    private void setFormats() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            List<BarcodeFormat> formats = new ArrayList<>();
            if (getIntent().getExtras().getInt(REQUEST_CODE) == REQUEST_SCAN_CODE_CONTROL && getIntent().getExtras().getStringArrayList(FORMATS)!=null) {
                for (String format : getIntent().getExtras().getStringArrayList(FORMATS)) {
                    formats.add(getBarcodeFormat(format));
                }
            } else {
                formats.add(BarcodeFormat.QR_CODE);
            }
            mScannerView.setFormats(formats);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        KinetiseApplication.getInstance().logToCrashlytics("[SCA] onResume");

        if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            finish();
        }

        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        KinetiseApplication.getInstance().logToCrashlytics("[SCA] onPause");
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        String result = rawResult.getText();
        if (getIntent().getExtras().getInt(REQUEST_CODE) == REQUEST_SCAN_CODE_FUNCTION) {
            if (result != null) {
                String url = RegexpHelper.parseValue(RegexpHelper.OPTIMIZED_RULE_URL, result);
                if (url != null && !url.equals("")) {
                    Uri uri = Uri.parse(url);
                    if (uri.getHost() != null) {
                        WebBrowserApp webBrowserApp = new WebBrowserApp(uri);
                        webBrowserApp.open(this);
                        finish();
                    } else {
                        handleCodeScannerUrlError();
                    }
                } else {
                    handleCodeScannerUrlError();
                }
            } else {
                handleCodeScannerUrlError();
            }
        } else {
            Intent intent = new Intent();
            intent.putExtra(SCAN_RESULT, rawResult.getText());
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void handleCodeScannerUrlError() {
        String headerText = LanguageManager.getInstance().getString(LanguageManager.POPUP_ERROR_HEADER);
        String message = LanguageManager.getInstance().getString(LanguageManager.ERROR_QRCODE_INVALID_URL);
        String okLabel = LanguageManager.getInstance().getString(LanguageManager.CLOSE_POPUP);

        showAlert(message, headerText, okLabel);
    }

    public void showAlert(String message, String header, String okButtonLabel) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setTitle(header);
        alertDialogBuilder.setPositiveButton(okButtonLabel, null);
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.setOnDismissListener(dialog1 -> {
            setResult(RESULT_URL_ERROR);
            finish();
        });
        dialog.show();
    }

    private BarcodeFormat getBarcodeFormat(String format) {
        if (format.equals("upce"))
            return BarcodeFormat.UPC_E;
        else if (format.equals("code39"))
            return BarcodeFormat.CODE_39;
        else if (format.equals("code93"))
            return BarcodeFormat.CODE_93;
        else if (format.equals("code128"))
            return BarcodeFormat.CODE_128;
        else if (format.equals("ean13"))
            return BarcodeFormat.EAN_13;
        else if (format.equals("ean8"))
            return BarcodeFormat.EAN_8;
        else if (format.equals("qr"))
            return BarcodeFormat.QR_CODE;
        else if (format.equals("itf14"))
            return BarcodeFormat.ITF;
        else if (format.equals("datamatrix"))
            return BarcodeFormat.DATA_MATRIX;
        else if (format.equals("pdf417"))
            return BarcodeFormat.PDF_417;
        else if (format.equals("aztec"))
            return BarcodeFormat.AZTEC;
        else
            return BarcodeFormat.QR_CODE;
    }

    @Override
    protected void onStart() {
        super.onStart();
        KinetiseApplication.getInstance().logToCrashlytics("[SCA] onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        KinetiseApplication.getInstance().logToCrashlytics("[SCA] onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        KinetiseApplication.getInstance().logToCrashlytics("[SCA] onDestroy");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        KinetiseApplication.getInstance().logToCrashlytics("[SCA] onSaveInstanceState");
    }
}