package com.skv.library.validator.controller;


import com.skv.library.validator.service.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/github")
public class GitHubController {

    private final GitHubService gitHubService;

    @Autowired
    public GitHubController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping("/validate/{owner}/{repo}")
    public String validateRepository(@PathVariable String owner, @PathVariable String repo) {
        boolean isGoodToUse = gitHubService.isGoodToUse(owner, repo);
        if (isGoodToUse) {
            return "The GitHub repository " + owner + "/" + repo + " is good to use!";
        } else {
            return "The GitHub repository " + owner + "/" + repo + " does not meet the criteria.";
        }
    }
}
