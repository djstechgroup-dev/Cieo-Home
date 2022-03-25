package com.kinetise.helpers.jq;

import android.content.Context;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class JQBridge {

    public static final String TRANSFORM_OUTPUT_FILENAME = "kin_jq_out.tmp";

    /**
     * Runs jq transform and writes result in file under provided path
     * @param transform jq transform
     * @param inputJson input json
     * @param tmpOutputFilename temp file to write transform result to
     * @return
     */
    public native int jq(String transform, String inputJson, String tmpOutputFilename);

    static boolean loaded = false;

    static public JQResult jqTransform(String transform, String inputJson, Context context) {
        if (!loaded) {
            System.loadLibrary("jq");
            loaded = true;
        }

        JQBridge jqBridge = new JQBridge();
        final String tmpOutputFile = context.getCacheDir().getAbsolutePath() + File.separator + TRANSFORM_OUTPUT_FILENAME;
        int status = jqBridge.jq(transform, inputJson, tmpOutputFile);
        JQResult result = new JQResult(status);

        File tmp = new File(tmpOutputFile);
        try {
            FileInputStream is = new FileInputStream(tmp);
            String outputStr = IOUtils.toString(is, "UTF-8");
            result.setOutput(outputStr);
            IOUtils.closeQuietly(is);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            result.setOutput(e.getMessage());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            result.setOutput(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            result.setOutput(e.getMessage());
        }

        return result;
    }

    /**
     *
     * @return returns transformed json, or input if transform was not succesful
     */
    static public String runTransform(String transform, String input, Context context){
        if (transform != null && !transform.equals("")) {
            JQResult jqResult = jqTransform(transform, input, context);

            if (jqResult.getResultStatus() == 0) {
                input = jqResult.getOutput();
            }
        }
        return input;
    }
}



