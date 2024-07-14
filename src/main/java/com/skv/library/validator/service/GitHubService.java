package com.skv.library.validator.service;

public interface GitHubService {

    boolean isGoodToUse(String owner, String repo);
}
