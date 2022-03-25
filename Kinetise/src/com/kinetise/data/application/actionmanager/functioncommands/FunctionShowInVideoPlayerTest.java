package com.kinetise.data.application.actionmanager.functioncommands;

import android.content.Intent;
import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.descriptors.actions.StringVariableDataDesc;
import com.kinetise.data.descriptors.actions.functions.FunctionShowInVideoPlayerDataDesc;
import junit.framework.TestCase;
import android.net.Uri;

public class FunctionShowInVideoPlayerTest extends TestCase {

    private class TestableFunctionShowInVideoPlayer extends FunctionShowInVideoPlayer{
        boolean startActivityCalled = false;
        Intent intentPassed = null;

        TestableFunctionShowInVideoPlayer(FunctionShowInVideoPlayerDataDesc dataDesc, AGApplicationState instance){
            super(dataDesc,instance);
        }
    }

    TestableFunctionShowInVideoPlayer function;

    public void createFunction(FunctionShowInVideoPlayerDataDesc dataDesc){
        function =  new TestableFunctionShowInVideoPlayer(dataDesc,null);
    }

    public void testExecute_sendingYoutubeVideoURL_opensIntentWithURLasURIandNoType(){
        String youtubeVideoURL = "https://www.youtube.com/watch?v=eA-ic5fePTA";
        String expectedAction = Intent.ACTION_VIEW;
        Uri expectedURI = Uri.parse(youtubeVideoURL);

        createFunctionWithStringParam(youtubeVideoURL);

        function.execute(null);
        Uri sentUri = function.intentPassed.getData();

        assertTrue(function.startActivityCalled);
        assertTrue(URIsAreEqual(expectedURI,sentUri));
        assertEquals(expectedAction , function.intentPassed.getAction());
        assertNull(function.intentPassed.getType());
    }

    public void testExecute_sendingYoutubeVideoURLwithAdditionalParams_opensIntentWithURLasURIandNoType(){
        String youtubeVideoURL = "https://www.youtube.com/watch?v=H6rBJqXEEdU&list=PLB443061BD30F3264&index=2";
        String expectedAction = Intent.ACTION_VIEW;
        Uri expectedURI = Uri.parse(youtubeVideoURL);

        createFunctionWithStringParam(youtubeVideoURL);

        function.execute(null);
        Uri sentUri = function.intentPassed.getData();

        assertTrue(function.startActivityCalled);
        assertTrue(URIsAreEqual(expectedURI,sentUri));
        assertEquals(expectedAction , function.intentPassed.getAction());
        assertNull(function.intentPassed.getType());
    }

    public void testExecute_sendingURLtoVideoStream_opensIntentWithURLasURIandVideoType(){
        String expectedType = "video/mp4";
        String videoURL = "https://randomvideourl.com/page?param=value";
        String expectedAction = Intent.ACTION_VIEW;
        Uri expectedURI = Uri.parse(videoURL);

        createFunctionWithStringParam(videoURL);

        function.execute(null);
        Uri sentUri = function.intentPassed.getData();

        assertTrue(function.startActivityCalled);
        assertTrue(URIsAreEqual(expectedURI,sentUri));
        assertEquals(expectedAction , function.intentPassed.getAction());
        assertEquals(expectedType, function.intentPassed.getType());
    }

    public void testExecute_sendingNonUrl_appendsDataToYoutubeURLandSendsItasIntentURI(){
        String videoURL = "eA-ic5fePTA";
        String expectedUrl = "https://www.youtube.com/watch?v=eA-ic5fePTA";
        String expectedAction = Intent.ACTION_VIEW;
        Uri expectedURI = Uri.parse(expectedUrl);

        createFunctionWithStringParam(videoURL);

        function.execute(null);
        Uri sentUri = function.intentPassed.getData();

        assertTrue(function.startActivityCalled);
        assertTrue(URIsAreEqual(expectedURI,sentUri));
        assertEquals(expectedAction,function.intentPassed.getAction());
        assertNull(function.intentPassed.getType());
    }

    public boolean URIsAreEqual(Uri uri1,Uri uri2){
        return uri1.compareTo(uri2)==0;
    }

    private void createFunctionWithStringParam(String videoURL) {
        FunctionShowInVideoPlayerDataDesc functionDescriptor = new FunctionShowInVideoPlayerDataDesc(null);
        functionDescriptor.addAttribute(new StringVariableDataDesc(videoURL));
        createFunction(functionDescriptor);
    }



}