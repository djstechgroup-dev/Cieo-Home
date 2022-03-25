package com.kinetise.data.descriptors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CodeScannerTypesDataDesc implements Serializable {
    private List<String> mCodes = new ArrayList<String>();

    public CodeScannerTypesDataDesc() {
        mCodes = new ArrayList<>();
    }

    public CodeScannerTypesDataDesc(List<String> codeTypes) {
        mCodes = new ArrayList<>();
        for (String codeType : codeTypes) {
            mCodes.add(codeType);
        }
    }

    public CodeScannerTypesDataDesc copy(CodeScannerTypesDataDesc codeScannerTypesDataDesc) {
        CodeScannerTypesDataDesc dataDesc = new CodeScannerTypesDataDesc();
        for (String type : codeScannerTypesDataDesc.getCodeTypes()) {
            dataDesc.addTypeCode(type);
        }
        return dataDesc;
    }

    public void addTypeCode(String type) {
        mCodes.add(type);
    }

    public List<String> getCodeTypes() {
        return mCodes;
    }

}
