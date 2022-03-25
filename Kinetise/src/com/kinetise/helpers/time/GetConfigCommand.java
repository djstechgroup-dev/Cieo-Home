package com.kinetise.helpers.time;

import com.kinetise.data.packagemanager.AppPackage;
import com.kinetise.data.sourcemanager.AbstractGetSourceCommand;
import com.kinetise.helpers.regexp.RegexpHelper;

import java.io.InputStream;

public class GetConfigCommand extends AbstractGetSourceCommand<InputStream> {


    public GetConfigCommand() {
        super("","assets://stripHtmlTagsConfig.json");
    }

	@Override
	public void postGetSource(InputStream stream) {
		String string = AppPackage.streamToString(stream);
		RegexpHelper.initRules(string);
	}

    @Override
	public void onError() {
        // nothing to do here, interface method implementation

	}

	@Override
	public void cancel() {
        // nothing to do here, interface method implementation

	}

	@Override
	public Object[] getParams() {
        // nothing to do here, interface method implementation
		return null;
	}

}
