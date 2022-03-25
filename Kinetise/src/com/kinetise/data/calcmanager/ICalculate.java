package com.kinetise.data.calcmanager;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;

public interface ICalculate {

    /**
     * Calculates children positions inside view associated with descriptor passed to 
     * method as argument. Results are expressed in pixels and assign it to dataDesc's calcDesc 
     */
    void layout(AbstractAGElementDataDesc desc);

    /**
     * Calculates real size of control associated with parameter desc and its children.
     * Results are expressed in pixels and assign it to dataDesc's calcDesc 
     */
    void measureBlockHeight(AbstractAGElementDataDesc desc, double maxFreeSpaceHeight, double maxSpaceForMax);
    
    /**
     * Calculates real size of control associated with parameter desc and its children.
     * Results are expressed in pixels and assign it to dataDesc's calcDesc 
     */
    void measureBlockWidth(AbstractAGElementDataDesc desc, double maxFreeSpaceWidth, double maxSpaceForMax);
}
