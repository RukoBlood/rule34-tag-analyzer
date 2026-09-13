package com.rule34analyzer.model;

public class DailyStats {
    private int total;
    private int ai;

    public DailyStats() {
        this.total = 0;
        this.ai = 0;
    }
    public void incrementTotal() {total++;}
    public void incrementAi() {ai++;}
    public int total() {return total;}
    public int ai() {return ai;}
    public void setTotal(int total) {this.total = total;}
    public void setAi(int ai) {this.ai = ai;}
}