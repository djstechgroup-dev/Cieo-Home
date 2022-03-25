package com.kinetise.data.descriptors;

import java.io.Serializable;

/**
 * Base interface for copy method on descriptors<br>
 * User: Mateusz Kołodziejczy
 * Date: 16.07.13
 * Time: 16:25
 * To change this template use File | Settings | File Templates.
 */
public interface IDeepCopy<T> extends Serializable {

    T copy();
}
