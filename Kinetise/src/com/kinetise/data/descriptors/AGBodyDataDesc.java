package com.kinetise.data.descriptors;

public class AGBodyDataDesc extends AbstractAGSectionDataDesc {

    @Override
    public AGBodyDataDesc createInstance() {
        return new AGBodyDataDesc();
    }

    @Override
    public AGBodyDataDesc copy() {
        return (AGBodyDataDesc) super.copy();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AGBodyDataDesc) {
            if (((AGBodyDataDesc) o).getScreenDesc().getScreenId().equals(this.getScreenDesc().getScreenId())) {
                return true;
            }
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
