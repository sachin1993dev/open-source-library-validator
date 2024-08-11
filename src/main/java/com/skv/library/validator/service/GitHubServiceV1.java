package com.skv.library.validator.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@Service
public class GitHubServiceV1 {

    @Value("${github.token}")
    private String githubToken;

    private final String GITHUB_API_URL = "https://api.github.com/repos/{owner}/{repo}";

    public Map<String, Object> fetchRepositoryMetrics(String owner, String repo) {
        RestTemplate restTemplate = new RestTemplate();
        String url = GITHUB_API_URL.replace("{owner}", owner).replace("{repo}", repo);

        // Set up headers with the GitHub token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + githubToken);

        // Set up the entity with headers
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Make the API request
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        String response = responseEntity.getBody();

        JSONObject jsonObject = new JSONObject(response);

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("Stars", jsonObject.getInt("stargazers_count"));
        metrics.put("Forks", jsonObject.getInt("forks_count"));
        metrics.put("Subscribers", jsonObject.optInt("subscribers_count", 0));
        metrics.put("Watchers", jsonObject.optInt("watchers_count", 0));

        // License Type
        JSONObject license = jsonObject.optJSONObject("license");
        metrics.put("License Type", license != null ? license.optString("name", "Unknown") : "Unknown");

        // Additional Metrics
        metrics.put("License Name", license != null ? license.optString("name", "Unknown") : "Unknown");
        metrics.put("OsiApproved", license != null && license.optBoolean("spdx_id", false));
        metrics.put("FsfLibre", license != null && license.optBoolean("free", false));
        metrics.put("IsDeprecatedLicenseId", license != null && license.optBoolean("deprecated", false));

        metrics.put("Owner", jsonObject.optJSONObject("owner").optString("login", "Unknown"));
        metrics.put("Name", jsonObject.optString("name", "Unknown"));
        metrics.put("Description", jsonObject.optString("description", "No description"));
        metrics.put("Topics", jsonObject.optJSONArray("topics") != null ? jsonObject.optJSONArray("topics").toList() : "No topics");
        metrics.put("API URL", url);

        metrics.put("Age", jsonObject.optString("created_at", "Unknown"));
        metrics.put("Last updated", jsonObject.optString("updated_at", "Unknown"));
        metrics.put("Created At", jsonObject.optString("created_at", "Unknown"));
        metrics.put("Recent Release", jsonObject.optString("pushed_at", "Unknown"));
        metrics.put("Language", jsonObject.optString("language", "Unknown"));

        metrics.put("Wiki", jsonObject.optString("has_wiki", "Unknown"));
        metrics.put("Documentation", jsonObject.optString("has_issues", "Unknown"));

        metrics.put("Issue count", jsonObject.optInt("open_issues_count", 0));

        return metrics;
    }
}
