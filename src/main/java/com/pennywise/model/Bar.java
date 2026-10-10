package com.pennywise.model;

/** One bar of a simple HTML bar chart: label, value and width (0..100). */
public class Bar {
    private final String label;
    private final Number value;
    private final int percent;

    public Bar(String label, Number value, int percent) { this.label = label; this.value = value; this.percent = percent; }
    public String getLabel() { return label; }
    public Number getValue() { return value; }
    public int getPercent() { return percent; }
}
