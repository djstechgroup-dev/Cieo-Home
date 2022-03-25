package com.kinetise.data.descriptors;

import java.util.List;

public interface IAGCollectionDataDesc{

	void addControl(AbstractAGElementDataDesc control);

	List<AbstractAGElementDataDesc> getAllControls();

	List<AbstractAGElementDataDesc> getPresentControls();

	void removeAllControls();

    void removeControl(AbstractAGElementDataDesc control);

    AbstractAGElementDataDesc getParent();

}
