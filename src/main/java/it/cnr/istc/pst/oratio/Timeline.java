package it.cnr.istc.pst.oratio;

import java.util.List;

public interface Timeline<TV extends Timeline.TimelineValue> {

    default String getType() {
        return this.getClass().getSimpleName();
    }

    InfRational getOrigin();

    InfRational getHorizon();

    List<TV> getValues();

    public interface TimelineValue {

        InfRational getFrom();

        InfRational getTo();
    }
}