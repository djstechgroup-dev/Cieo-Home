package com.kinetise.data.descriptors.datadescriptors.feeddatadesc;

import java.util.ArrayList;
import java.util.List;

public class UsingFields {
    private List<Field> mFields = new ArrayList<Field>();

    public void addField(Field field) {
        mFields.add(field);
    }

    public List<Field> getFields() {
        return mFields;
    }

    public UsingFields copy() {
        UsingFields copied = new UsingFields();

        for (Field field : mFields) {
            copied.addField(field.copy());
        }

        return copied;
    }

    public int getSize() {
        return mFields.size();
    }

    public String getFieldIdByIndex(int index) {
        if (index < mFields.size()) {
            return mFields.get(index).getId();
        }
        return null;
    }

    public String getFieldXPathURIByIndex(int index) {
        if (index < mFields.size()) {
            return mFields.get(index).getXpath();
        }
        return null;
    }

    public static UsingFields combine(UsingFields fields1, UsingFields fields2) {
        if (fields1 == null) {
            if (fields2 == null) {
                return new UsingFields();
            } else {
                return fields2.copy();
            }
        } else if (fields2 == null) {
            return fields1.copy();
        }

        UsingFields result = fields1.copy();
        List<Field> resultFields = result.getFields();
        for (Field field : fields2.getFields()) {
            if (!resultFields.contains(field))
                result.addField(field);
        }
        return result;
    }
}
