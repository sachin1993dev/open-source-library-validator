package com.skv.library.validator.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;

@Component
public class GitHubApiClient {

    private final String apiUrl = "https://api.github.com";
    private final RestTemplate restTemplate;
    private final String oauthToken;

    @Autowired
    public GitHubApiClient(@Value("${github.oauth.token}") String oauthToken) {
        this.restTemplate = new RestTemplate();
        this.oauthToken = oauthToken;
        this.restTemplate.setInterceptors(Collections.singletonList((request, body, execution) -> {
            request.getHeaders().set(HttpHeaders.AUTHORIZATION, "Bearer " + oauthToken);
            return execution.execute(request, body);
        }));
    }

    public ResponseEntity<String> fetchRepositoryDetails(String owner, String repoName) {
        String url = apiUrl + "/repos/{owner}/{repo}";
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                .queryParam("owner", owner)
                .queryParam("repo", repoName);

        return restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
    }
}
