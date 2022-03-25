package com.kinetise.debug;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import android.view.View;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.parsermanager.ParserManager;
import com.kinetise.helpers.RWrapper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Created by Kuba Komorowski on 2014-07-02.
 *
 * Klasa pomocnicza sluzaca do wykrywania i obslugi kombinacji dotkniec sluzacej do wyswietlenia informacji o wersji aplikacji
 */

public class DebugTouchDetector implements View.OnTouchListener {
    /** Odleglosc od krawedzi ekranu, liczona w pikselach **/
    private final float mTouchOffsetPx;
    private final float mScreenWidthInPixels;
    private final float mScreenHeightInPixels;

    /** Odleglosc od krawedzi ekranu w dp **/
    private final static float TOUCH_OFFSET_DP = 100;
    private final static int TIMEOUT = 1000;

    private long mLastTouchTime;
    private Corner mLastTouchedCorner;
    private Context mContext;

    public DebugTouchDetector(Context context) {
        mContext = context;
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        mTouchOffsetPx = TOUCH_OFFSET_DP * metrics.density;

        mScreenWidthInPixels = metrics.widthPixels;
        mScreenHeightInPixels = metrics.heightPixels;
        mLastTouchedCorner = Corner.NONE;
    }

    private boolean withinTimeout(long touchTime) {
        return (touchTime - mLastTouchTime) < TIMEOUT;
    }

    private boolean touchedLeftBottomCorner(float x, float y) {
        return x < mTouchOffsetPx && y > mScreenHeightInPixels - mTouchOffsetPx;
    }

    private boolean touchedRightBottomCorner(float x, float y) {
        return x > mScreenWidthInPixels - mTouchOffsetPx && y > mScreenHeightInPixels - mTouchOffsetPx;
    }

    private boolean touchedRightTopCorner(float x, float y) {
        return x > mScreenWidthInPixels - mTouchOffsetPx && y < mTouchOffsetPx;
    }

    private boolean touchedLeftTopCorner(float x, float y) {
        return x < mTouchOffsetPx && y < mTouchOffsetPx;
    }

    private void displayDebugDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        //Wersja aplikcaji z pliku AndroidManifest
        String appVersion;
        String packageName;
        String certificateHash;
        String certificateFingerprint;

        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            appVersion = pInfo.versionName;
            packageName = pInfo.packageName;
        } catch (PackageManager.NameNotFoundException nnfe){
            nnfe.printStackTrace();
            appVersion = "unknown";
            packageName = "unknown";
        }

        Signature[] signatures = getSignatures(mContext);
        if (signatures!=null && signatures.length > 0) {
            certificateHash = getCertificateSHA1Hash(signatures[0]);
            certificateFingerprint = getCertificateSHA1Fingerprint(signatures[0]);
        } else {
            certificateHash = "unknown";
            certificateFingerprint = "unknown";
        }

        //Wersja kompilacji z pliku xml
        String xmlVersion = ParserManager.getInstance().getApplicationDescription().getVersion();

        String debugMessage;

        //Reszta informacji o wersjach jest przechowywana w pliku strings.xml
        debugMessage = "Compilation version: " + mContext.getString(RWrapper.string.compilation_version) + "\n" +
            "Compilation date: " + mContext.getString(RWrapper.string.compilation_date) + "\n \n" +
            "Xml version: " + xmlVersion + "\n" +
            "Xml date: " + mContext.getString(RWrapper.string.xml_dat) + "\n \n" +
            "Vbj version: " + mContext.getString(RWrapper.string.blowjobber_version) + "\n" +
            "Vbj date: " + mContext.getString(RWrapper.string.blowjobber_date) + "\n \n" +
            "Package name: " + packageName + "\n" +
            "Certificate hash: " + certificateHash + "\n" +
            "Certificate fingerprint: " + certificateFingerprint + "\n \n" +
            "Code version: " + appVersion + "\n" +
            "Code date: " + mContext.getString(RWrapper.string.code_date) + "\n \n" +
            "Descriptor compiler version: " + mContext.getString(RWrapper.string.descriptor_compier_version) + "\n" +
            "Descriptor compiler date: " + mContext.getString(RWrapper.string.descriptor_compiler_date)+"\n"+

            "ScreeanId: " + AGApplicationState.getInstance().getCurrentScreenDesc().getScreenId()+"\n" +
            "Api Version: " + AGApplicationState.getInstance().getApplicationDescription().getApiVersion()+"\n"+
            "Created Version: " + AGApplicationState.getInstance().getApplicationDescription().getCreatedVersion()+"\n"+
            "Analitycs Tag: " + AGApplicationState.getInstance().getCurrentScreenDesc().getAnalitycsTag();


        builder.setMessage(debugMessage).setNegativeButton("Close",null).show();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        long touchTime = event.getEventTime();

        if (touchedLeftTopCorner(x, y)) {
            mLastTouchTime = touchTime;
            mLastTouchedCorner = Corner.LEFT_TOP;
        }

        else if (touchedRightTopCorner(x, y) && mLastTouchedCorner == Corner.LEFT_TOP && withinTimeout(touchTime)) {
            mLastTouchTime = touchTime;
            mLastTouchedCorner = Corner.RIGHT_TOP;
        }

        else if (touchedRightBottomCorner(x, y) && mLastTouchedCorner == Corner.RIGHT_TOP && withinTimeout(touchTime)) {
            mLastTouchTime = touchTime;
            mLastTouchedCorner = Corner.RIGHT_BOTTOM;
        }

        else if (touchedLeftBottomCorner(x, y) && mLastTouchedCorner == Corner.RIGHT_BOTTOM && withinTimeout(touchTime)){
            mLastTouchTime = touchTime;
            mLastTouchedCorner = Corner.LEFT_BOTTOM;
            displayDebugDialog();
        }
        else {
            mLastTouchedCorner = Corner.NONE;
        }
        return false;
    }

    enum Corner{
        LEFT_TOP,RIGHT_TOP,RIGHT_BOTTOM,LEFT_BOTTOM,NONE
    }

    private static Signature[] getSignatures(Context context) {
        Signature[] signatures = null;
        try {
            signatures = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES).signatures;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return signatures;
    }

    private static String getCertificateSHA1Hash(Signature signature) {
        String hexString = "not signed";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");md.update(signature.toByteArray());
            hexString = new String(Base64.encode(md.digest(), 0)).trim();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hexString;
    }

    private String getCertificateSHA1Fingerprint(Signature signature) {
            byte[] cert = signature.toByteArray();

            InputStream input = new ByteArrayInputStream(cert);
            CertificateFactory cf = null;
            try {
                cf = CertificateFactory.getInstance("X509");
            } catch (CertificateException e) {
                e.printStackTrace();
            }
            X509Certificate c = null;
            try {
                c = (X509Certificate) cf.generateCertificate(input);
            } catch (CertificateException e) {
                e.printStackTrace();
            }
            String hexString = "not signed";
            try {
                MessageDigest md = MessageDigest.getInstance("SHA1");
                byte[] publicKey = md.digest(c.getEncoded());
                hexString = byte2HexFormatted(publicKey);
            } catch (NoSuchAlgorithmException e1) {
                e1.printStackTrace();
            } catch (CertificateEncodingException e) {
                e.printStackTrace();
            }
            return hexString;
    }

    public static String byte2HexFormatted(byte[] arr) {
        StringBuilder str = new StringBuilder(arr.length * 2);
        for (int i = 0; i < arr.length; i++) {
            String h = Integer.toHexString(arr[i]);
            int l = h.length();
            if (l == 1) h = "0" + h;
            if (l > 2) h = h.substring(l - 2, l);
            str.append(h.toUpperCase());
            if (i < (arr.length - 1)) str.append(':');
        }
        return str.toString();
    }
}
