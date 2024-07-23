package dev.consti.utils;

import okhttp3.*;

import java.io.IOException;

public class GithubService {

    private static final String GITHUB_API_URL = "https://api.github.com/repos/" + ConfigHandler.getProperty("GITHUB_REPO") + "/issues";
    private static final String GITHUB_TOKEN = ConfigHandler.getProperty("GITHUB_TOKEN");

    public String createIssue(String title, String description) {
        OkHttpClient client = new OkHttpClient();

        String json = "{\"title\":\"" + title + "\",\"body\":\"" + description + "\"}";

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(GITHUB_API_URL)
                .post(body)
                .header("Authorization", "token " + GITHUB_TOKEN)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            // Parse response to get the issue URL
            String responseBody = response.body().string();
            // Extract the URL from the response body (this is a simplified example)
            return responseBody.split("\"html_url\":\"")[1].split("\"")[0];
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}