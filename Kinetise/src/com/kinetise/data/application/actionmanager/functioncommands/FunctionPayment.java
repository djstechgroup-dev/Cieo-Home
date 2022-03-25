package com.kinetise.data.application.actionmanager.functioncommands;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.actionmanager.ExecuteActionManager;
import com.kinetise.data.descriptors.HttpParamsDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionVariableDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;
import com.kinetise.data.sourcemanager.AssetsManager;
import com.kinetise.views.FullscreenWebview;

public class FunctionPayment extends AbstractFunction {
    private final static String PAYMENT_SUCCEEDED_URI = "payment://succeed";
    private final static String PAYMENT_FAILED_URI = "payment://failed";
    private static final String SUCCESS_PARAMETER_NAME = "redirect_success";
    private static final String FAILURE_PARAMETER_NAME = "redirect_failed";
    private ActionVariableDataDesc successAction;
    private ActionVariableDataDesc failureAction;
    private String paymentUrl;
    private HttpParamsDataDesc httpParamsDataDesc;

    public FunctionPayment(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc,application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        parseFunctionAttributes(mFunctionDataDesc.getAttributes());

        httpParamsDataDesc.addHttpParam(SUCCESS_PARAMETER_NAME, PAYMENT_SUCCEEDED_URI);
        httpParamsDataDesc.addHttpParam(FAILURE_PARAMETER_NAME, PAYMENT_FAILED_URI);
        paymentUrl = AssetsManager.addHttpQueryParams(paymentUrl, httpParamsDataDesc);

        FullscreenWebview.getInstance().showWebView(new PaymentWebviewClient(), paymentUrl, null, mApplication.getActivity());

        return null;
    }

    private void parseFunctionAttributes(VariableDataDesc[] attributes) {
        paymentUrl = attributes[0].getStringValue();

        String successActionString = attributes[1].getStringValue();
        successActionString = AGXmlActionParser.unescape(successActionString);
        successAction = AGXmlActionParser.getVariableForEscapedActionString(successActionString);

        String failureActionString = attributes[2].getStringValue();
        failureActionString = AGXmlActionParser.unescape(failureActionString);
        failureAction = AGXmlActionParser.getVariableForEscapedActionString(failureActionString);

        String httpParams = mFunctionDataDesc.getAttributes()[3].getStringValue();
        httpParamsDataDesc = HttpParamsDataDesc.getHttpParams(httpParams, null);
    }

    private class PaymentWebviewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if(url.startsWith(PAYMENT_SUCCEEDED_URI)){
                ExecuteActionManager.executeMultiAction(successAction.getActions());
                FullscreenWebview.getInstance().closeWebView();
            } else if(url.startsWith(PAYMENT_FAILED_URI)) {
                ExecuteActionManager.executeMultiAction(failureAction.getActions());
                FullscreenWebview.getInstance().closeWebView();
            }
        }
    }
}
