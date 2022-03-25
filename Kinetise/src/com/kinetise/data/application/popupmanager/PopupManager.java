package com.kinetise.data.application.popupmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.kinetise.components.activity.KinetiseActivity;
import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.descriptors.AbstractAGContainerDataDesc;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AbstractAGTemplateDataDesc;
import com.kinetise.data.exceptionmanager.ExceptionManager;
import com.kinetise.data.sourcemanager.LanguageManager;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.views.AGPopupView;
import com.kinetise.data.systemdisplay.views.IAGView;
import com.kinetise.data.systemdisplay.views.ViewFactoryManager;
import com.kinetise.helpers.asynccaller.AsyncCaller;

import java.security.InvalidParameterException;
import java.util.List;

/**
 * Manages showing dialogs and map popups
 */
public class PopupManager {

    private static Dialog mCurrentDialog;
    private static AGPopupView mPopupView;
    private static AlertDialog mCurrentAlertDialog;
    private static boolean isCanceled = true;

    public static AGPopupView getCurrentPopupView() {
        return mPopupView;
    }

    public static void showMapPopup(final AbstractAGTemplateDataDesc popupDataDesc) {
        try {
            final SystemDisplay display = AGApplicationState.getInstance().getSystemDisplay();
            if (display != null) {
                isCanceled = false;
                List<AbstractAGElementDataDesc> dialogContentControls = popupDataDesc.getAllControls();

                if (!areDialogControlsValid(dialogContentControls)) {
                    throw new InvalidParameterException("Popup template cannot have more than one control in root and root "
                            + "have to extend AbstractAGContainerDataDesc class");
                }

                AbstractAGContainerDataDesc containerDataDesc = (AbstractAGContainerDataDesc) dialogContentControls.get(0);
                containerDataDesc.resolveVariables();
                display.runCalcManager(containerDataDesc);

                showPopupOnUiThread(popupDataDesc, display);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void showDialogOverlay(final View view) {
        final Activity currentActivity = AGApplicationState.getInstance().getActivity();
        if (currentActivity != null) {
            final Dialog dialog = new Dialog(currentActivity, android.R.style.Theme_Translucent_NoTitleBar);
            mCurrentDialog = dialog;
            AsyncCaller.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Window window = dialog.getWindow();
                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                    dialog.setContentView(view);
                    if (!isCanceled)
                        dialog.show();
                }
            });
        }
    }

    private static void showPopupOnUiThread(final AbstractAGTemplateDataDesc popupDataDesc, final SystemDisplay display) {
        mPopupView = createPopUpView(display, popupDataDesc);
        showDialogOverlay(mPopupView);
    }

    private static AGPopupView createPopUpView(SystemDisplay display, AbstractAGTemplateDataDesc popupDataDesc) {
        List<AbstractAGElementDataDesc> dialogContentControls = popupDataDesc.getPresentControls();

        if (!areDialogControlsValid(dialogContentControls)) {
            throw new InvalidParameterException("Popup template cannot have more than one control in root and root "
                    + "have to extend AbstractAGContainerDataDesc class");
        }

        View view = ViewFactoryManager.createViewHierarchy(dialogContentControls.get(0), display);
        ((IAGView) view).loadAssets();

        return new AGPopupView(display, view);
    }

    private static boolean areDialogControlsValid(List<AbstractAGElementDataDesc> dialogContentControls) {
        return dialogContentControls.size() == 1 && (dialogContentControls.get(0) instanceof AbstractAGContainerDataDesc);
    }


    public static void closeMapPopup() {
        try {
            if (mCurrentDialog != null && mCurrentDialog.isShowing()) {
                mCurrentDialog.dismiss();
                mPopupView = null;
                mCurrentDialog = null;
            }
        } catch (Exception e) {
            ExceptionManager.getInstance().handleException(e);
        }
    }


    public static void showInfoPopup(final String message) {
        String headerText = LanguageManager.getInstance().getString(LanguageManager.POPUP_INFO_HEADER);
        showAlert(message, headerText);
    }


    public static void showErrorPopup(final String message) {
        String headerText = LanguageManager.getInstance().getString(LanguageManager.POPUP_ERROR_HEADER);
        showAlert(message, headerText);
    }

    public static void showPopup(String message,String title){
        showAlert(message,title);
    }

    public static void showAlert(final String message, final String header, String okButtonLabel, final Runnable positiveAction, String cancelButtonLabel, final Runnable negativeAction) {
        isCanceled = false;
        final Activity currentActivity = AGApplicationState.getInstance().getActivity();
        if (currentActivity != null) {
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(currentActivity);
            alertDialogBuilder.setMessage(message);
            if (header != null)
                alertDialogBuilder.setTitle(header);
            if (okButtonLabel != null && !okButtonLabel.equals(""))
                alertDialogBuilder.setPositiveButton(okButtonLabel, createDialogListener(positiveAction));
            if (cancelButtonLabel != null && !cancelButtonLabel.equals(""))
                alertDialogBuilder.setNegativeButton(cancelButtonLabel, createDialogListener(negativeAction));
            AsyncCaller.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Double check if user is still interacting with app.
                    //Run on UIThread is added to queue, so app could be killed before it is called.
                    if (!AGApplicationState.getInstance().isPaused()) {
                        mCurrentAlertDialog = alertDialogBuilder.create();
                        mCurrentAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                mCurrentAlertDialog = null;
                            }
                        });
                        mCurrentAlertDialog.show();
                    }
                }
            });
        } else {
            isCanceled = true;
        }
    }

    private static DialogInterface.OnClickListener createDialogListener(final Runnable action) {
        return new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if (action != null)
                    action.run();
            }
        };
    }

    public static void showAlert(final String message, final String header) {
        String okLabel = LanguageManager.getInstance().getString(LanguageManager.CLOSE_POPUP);
        showAlert(message, header, okLabel, null, "", null);
    }

    public static void onAppPaused() {
        if (mCurrentAlertDialog != null  && mCurrentAlertDialog.isShowing()) {
            mCurrentAlertDialog.dismiss();
        }
        isCanceled = true;
    }

    public static void showToast(final String message) {
        AsyncCaller.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AGApplicationState.getInstance().getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static void showInvalidFormToast(final String message) {
        AsyncCaller.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(AGApplicationState.getInstance().getActivity(), message, Toast.LENGTH_SHORT);
                View view = toast.getView();
                view.setBackgroundColor(AGApplicationState.getInstance().getApplicationDescription().getValidationErrorToastColor());
                toast.show();
            }
        });
    }

    public static void showProgressDialog(final String description) {
        showProgressDialog(null, description, false);
    }

    public static void showProgressDialog(final String description, int max) {
        showProgressDialog(null, description, false, max);
    }

    public static void showProgressDialog(final String title, final String description, final boolean cancelable) {
        showProgressDialog(title, description, cancelable, 0);
    }

    public static void showProgressDialog(final String title, final String description, final boolean cancelable, final int max) {
        AsyncCaller.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Activity currentActivity = AGApplicationState.getInstance().getActivity();
                if (currentActivity != null) {
                    dismissDialog();
                    isCanceled = false;
                    ProgressDialog progressDialog = new ProgressDialog(currentActivity);
                    progressDialog.setTitle(title);
                    progressDialog.setMessage(description);
                    progressDialog.setCancelable(cancelable);
                    if (max > 0) {
                        progressDialog.setProgressNumberFormat(null);
                        progressDialog.setIndeterminate(false);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        progressDialog.setProgress(0);
                        progressDialog.setMax(max);
                    }
                    mCurrentDialog = progressDialog;
                    if (!isCanceled)
                        mCurrentDialog.show();
                }
            }
        });
    }

    public static void updateProgressDialog(final int progress) {
        AsyncCaller.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mCurrentDialog != null) {
                    ((ProgressDialog) mCurrentDialog).setProgress(progress);
                }
            }
        });
    }

    public static void dismissDialog() {
        isCanceled = true;
        AsyncCaller.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mCurrentDialog != null && mCurrentDialog.isShowing()) {
                    mCurrentDialog.dismiss();
                    mCurrentDialog = null;
                }
            }
        });
    }

}
