package com.rule34analyzer.model;

import java.time.LocalDate;
import java.util.Set;

public record Rule34Post(long id, LocalDate date, Set<String> tags) {}
