package com.geo.points.distance.elasticSearchGeoDisanceDemo.model;

public class Page {
    private Integer number;
    private Integer size;
    private String cursorId;

    public Page() {
    }

    public String getCursorId() {
        return this.cursorId;
    }

    public Page setCursorId(String cursorId) {
        this.cursorId = cursorId;
        return this;
    }

    public Integer getNumber() {
        if (this.number == null) {
            this.number = 1;
        }

        return this.number;
    }

    public Page setNumber(Integer number) {
        this.number = number;
        return this;
    }

    public Integer getSize() {
        return this.size;
    }

    public Page setSize(Integer size) {
        this.size = size;
        return this;
    }

    public boolean hasCursor() {
        return this.cursorId != null;
    }

    public String toString() {
        return "Page [number=" + this.number + ", size=" + this.size + "]";
    }
}