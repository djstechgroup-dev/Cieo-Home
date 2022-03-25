package com.kinetise.data.systemdisplay.viewvisitors;

import com.kinetise.data.systemdisplay.views.IAGView;

/**
 * User: Mateusz
 * Date: 22.03.13
 * Time: 13:08
 */
public interface IViewVisitor {

    boolean visit(IAGView view);

}
