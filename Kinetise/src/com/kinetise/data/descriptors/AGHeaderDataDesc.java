package com.kinetise.data.descriptors;

public class AGHeaderDataDesc extends AbstractAGSectionDataDesc {

    @Override
    public AGHeaderDataDesc createInstance() {
        return new AGHeaderDataDesc();
    }

    @Override
    public AGHeaderDataDesc copy(){
		return (AGHeaderDataDesc) super.copy();
	}
	
}
