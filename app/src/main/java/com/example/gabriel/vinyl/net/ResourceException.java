package com.example.gabriel.vinyl.net;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gabriel on 11/15/2016.
 */

public class ResourceException extends RuntimeException {
    private final List<Issue> mIssues;

    public ResourceException(List<Issue> issues) {
        super();
        mIssues = issues;
    }

    public ResourceException(Exception e) {
        super();
        mIssues = new ArrayList<Issue>();
        mIssues.add(new Issue().add("error", e.getMessage()));
    }

    public List<Issue> getIssues() {
        return mIssues;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        for(Issue issue: mIssues) {
            sb.append(issue.toString()).append("\n");
        }
        return sb.toString();
    }
}
