package com.rule34analyzer.model;

import java.time.LocalDate;
import java.util.Map;

public record AnalysisResult(String tag, Map<LocalDate, DailyStats> dailyStats, int total, int ai) {}
