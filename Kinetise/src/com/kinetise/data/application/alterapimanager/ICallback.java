package com.kinetise.data.application.alterapimanager;
/**
 * Interface alows us to specify code that should be executed when AlterApiRequest that are asynchronous are finished.
 * */
public interface ICallback {
	void onError();
	void onSuccess();
}
