package com.kinetise.data;

import com.kinetise.data.sourcemanager.AbstractGetSourceCommand;

import java.io.File;


public class SoundSetterCommand extends AbstractGetSourceCommand<File> {

    public SoundSetterCommand(String baseUri, String source) {
        super(baseUri, source);
    }

    @Override
    public void postGetSource(File obj) {

    }

    @Override
    public void onError() {

    }

    @Override
    public Object[] getParams() {
        return new Object[0];
    }
}
