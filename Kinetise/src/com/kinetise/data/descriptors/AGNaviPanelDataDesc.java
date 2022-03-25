package com.kinetise.data.descriptors;

public class AGNaviPanelDataDesc extends AbstractAGSectionDataDesc {

    @Override
    public AGNaviPanelDataDesc createInstance() {
        return new AGNaviPanelDataDesc();
    }

    @Override
    public AGNaviPanelDataDesc copy(){
		return (AGNaviPanelDataDesc) super.copy();
	}
	
}
