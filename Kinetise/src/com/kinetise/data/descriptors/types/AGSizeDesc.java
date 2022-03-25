package com.kinetise.data.descriptors.types;

import com.kinetise.helpers.HashCodeBuilder;
import com.kinetise.helpers.calcmanagerhelper.CalcManagerHelper;

import java.io.Serializable;
import java.security.InvalidParameterException;


public class AGSizeDesc implements Serializable {
    public static final AGSizeDesc MAX = new AGSizeDesc(0, AGUnitType.MAX);
    public static final AGSizeDesc MIN = new AGSizeDesc(0, AGUnitType.MIN);
    public static final AGSizeDesc ZEROKPX = new AGSizeDesc(0, AGUnitType.KPX);
    private AGUnitType mDescUnit;
    private int mDescValue;

    public AGSizeDesc(int value, AGUnitType type) {
        mDescUnit = type;
        mDescValue = value;
    }

    public AGSizeDesc copy() {
        return new AGSizeDesc(getDescValue(), getDescUnit());
    }

    public AGUnitType getDescUnit() {
        return mDescUnit;
    }

    public void setDescUnit(AGUnitType descUnit) {
        this.mDescUnit = descUnit;
    }

    public int getDescValue() {
        return mDescValue;
    }

    public void setDescValue(int descValue) {
        this.mDescValue = descValue;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mDescValue).append(mDescUnit.name().hashCode()).toHashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AGSizeDesc) {
            return ((AGSizeDesc) o).getDescUnit().equals(getDescUnit()) && getDescValue() == ((AGSizeDesc) o).getDescValue();
        }
        return super.equals(o);
    }

    public double inPixels() {
        if (mDescUnit != AGUnitType.KPX)
            throw new InvalidParameterException("unit must be in KPX");
        return CalcManagerHelper.KPXtoPixels(mDescValue);
    }

}
