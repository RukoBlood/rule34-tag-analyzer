package com.rule34analyzer.analysis;

import com.rule34analyzer.model.Rule34Post;

public final class AiClassifier {
    private AiClassifier() {}

    public static boolean isAi(Rule34Post post) {
        return post.tags().contains("ai_generated")
            || post.tags().contains("ai_assisted");
    }
}
