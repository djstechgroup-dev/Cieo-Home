package com.kinetise.data.descriptors;

import java.util.ArrayList;
import java.util.List;


public class TableIdentifiersDataDesc {

    private List<TableIdentifierDataDesc> mIdentifiers;

    public TableIdentifiersDataDesc() {
        mIdentifiers = new ArrayList<>();
    }

    public void addIdentifier(TableIdentifierDataDesc identifier) {
        mIdentifiers.add(identifier);
    }

    public List<TableIdentifierDataDesc> getIdentifiers() {
        return mIdentifiers;
    }

}
