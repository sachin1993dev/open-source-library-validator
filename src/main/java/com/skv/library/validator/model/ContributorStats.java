package com.skv.library.validator.model;

import java.util.List;

public class ContributorStats {
    private Contributor author;
    private int total;
    private List<WeekStats> weeks;

    // getters and setters
}

class WeekStats {
    private long w;
    private int a;
    private int d;
    private int c;

    // getters and setters
}
