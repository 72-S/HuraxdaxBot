package dev.consti.utils;

import okhttp3.*;

import java.io.IOException;

public class GithubService {

    private static final String GITHUB_API_URL = "https://api.github.com/repos/" + ConfigHandler.getProperty("GITHUB_REPO") + "/issues";
    private static final String GITHUB_TOKEN = ConfigHandler.getProperty("GITHUB_TOKEN");

    public String createIssue(String title, String type, String description, String additionalInfo) {
        OkHttpClient client = new OkHttpClient();

        // Escape special characters and newlines properly for JSON
        String issueBody = String.format("### %s\\n\\n**Type:** %s\\n\\n**Description:**\\n%s\\n\\n**Additional Info:**\\n%s",
                escapeString(title), escapeString(type), escapeString(description), escapeString(additionalInfo));

        // Construct JSON payload correctly
        String json = String.format("{\"title\":\"%s\",\"body\":\"%s\"}", escapeString(title), issueBody);

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(GITHUB_API_URL)
                .post(body)
                .header("Authorization", "token " + GITHUB_TOKEN)
                .build();

        System.out.println("JSON Payload: " + json); // Log JSON payload

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("Response code: " + response.code());
                System.out.println("Response body: " + response.body().string());
                throw new IOException("Unexpected code " + response);
            }

            // Parse response to get the issue URL
            String responseBody = response.body().string();
            // Extract the URL from the response body (this is a simplified example)
            return responseBody.split("\"html_url\":\"")[1].split("\"")[0];
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Helper method to escape JSON special characters
    private String escapeString(String input) {
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
                .replace("\b", "\\b")
                .replace("\f", "\\f");
    }
}
