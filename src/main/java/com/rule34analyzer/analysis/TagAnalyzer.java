package com.rule34analyzer.analysis;

import com.rule34analyzer.api.Rule34Client;
import com.rule34analyzer.model.AnalysisResult;
import com.rule34analyzer.model.DailyStats;
import com.rule34analyzer.model.Rule34Post;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

public class TagAnalyzer {

    private final Rule34Client client;
    private final Consumer<String> status;

    public TagAnalyzer(Rule34Client client, Consumer<String> status) {
        this.client = client;
        this.status = status;
    }

    public AnalysisResult analyze(String tag) throws Exception {
        Map<LocalDate, DailyStats> stats = new TreeMap<>();

        int total = 0;
        int ai = 0;

        for (int pid = 0; ; pid++) {
            status.accept("Loading posts... page " + (pid + 1));

            List<Rule34Post> posts = client.getPosts(tag, pid);

            if (posts.isEmpty()) break;
            for (Rule34Post post : posts) {
                LocalDate date = post.date();
                DailyStats day = stats.computeIfAbsent(date, d -> new DailyStats());
                day.incrementTotal();
                total++;
                if (AiClassifier.isAi(post)) {
                    day.incrementAi();
                    ai++;
                }
            }

            if (posts.size() < 1000) break;
            Thread.sleep(250);
        }
        int cumulativeTotal = 0;
        int cumulativeAi = 0;

        for (Map.Entry<LocalDate, DailyStats> entry : stats.entrySet()) {
            DailyStats day = entry.getValue();
            cumulativeTotal += day.total();
            cumulativeAi += day.ai();
            day.setTotal(cumulativeTotal);
            day.setAi(cumulativeAi);
        }
        status.accept("Analysis complete: " + total + " posts");

        return new AnalysisResult(tag, stats, total, ai);
    }
}