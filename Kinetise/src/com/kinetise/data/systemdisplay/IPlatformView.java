package com.kinetise.data.systemdisplay;

import com.kinetise.data.descriptors.types.AGScreenTransition;
import com.kinetise.data.systemdisplay.views.AGScreenView;

/**
 * User: Mateusz
 * Date: 06.05.13
 * Time: 15:32
 */
public interface IPlatformView {

    void setMainView(SystemDisplay display, AGScreenView view, AGScreenTransition transition);

    void removeMainView();

    void addWebViewCallback(AGWebViewCallback pCallback);

    void removeWebViewCallback(AGWebViewCallback pCallback);

}
