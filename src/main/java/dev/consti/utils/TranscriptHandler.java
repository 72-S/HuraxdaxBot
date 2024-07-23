package dev.consti.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import java.awt.Color;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class TranscriptHandler {

    public void sendTranscript(ThreadChannel threadChannel) {
        String transcriptChannelId = ConfigHandler.getProperty("TRANSCRIPT_CHANNEL_ID");
        if (transcriptChannelId == null || transcriptChannelId.isEmpty()) {
            System.err.println("Transcript channel ID is not set in the configuration!");
            return;
        }

        TextChannel transcriptChannel = threadChannel.getGuild().getTextChannelById(transcriptChannelId);
        if (transcriptChannel == null) {
            System.err.println("Transcript channel not found! Check if the channel ID is correct and if the bot has access to it.");
            return;
        }

        if (!transcriptChannel.canTalk()) {
            System.err.println("Bot doesn't have permission to send messages in the transcript channel!");
            return;
        }

        retrieveMessages(threadChannel)
                .thenAccept(messages -> sendMessagesAsEmbed(transcriptChannel, messages, threadChannel))
                .exceptionally(throwable -> {
                    handleException(throwable);
                    return null;
                });
    }

    private CompletableFuture<List<Message>> retrieveMessages(ThreadChannel threadChannel) {
        List<Message> allMessages = new ArrayList<>();
        return threadChannel.getIterableHistory().takeAsync(100) // Adjust the number as needed
                .thenApply(messages -> {
                    allMessages.addAll(messages);
                    Collections.reverse(allMessages); // Reverse to get chronological order
                    return allMessages;
                });
    }

    private void sendMessagesAsEmbed(TextChannel transcriptChannel, List<Message> messages, ThreadChannel threadChannel) {
        if (transcriptChannel == null) {
            System.err.println("Transcript channel is null. Aborting message sending.");
            return;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Transcript of " + threadChannel.getName())
                .setColor(Color.BLUE)
                .setFooter("End of transcript for thread: " + threadChannel.getName(), null);

        StringBuilder transcriptContent = new StringBuilder();
        for (Message message : messages) {
            transcriptContent.append("**").append(message.getAuthor().getAsTag()).append(":** ")
                    .append(message.getContentDisplay()).append("\n");
        }

        // If the content is too long, split it into multiple embeds
        List<String> chunks = splitString(transcriptContent.toString(), 4096);
        for (int i = 0; i < chunks.size(); i++) {
            EmbedBuilder pageEmbed = new EmbedBuilder(embedBuilder);
            pageEmbed.setDescription(chunks.get(i));
            if (i > 0) {
                pageEmbed.setTitle("Transcript of " + threadChannel.getName() + " (Continued)");
            }
            transcriptChannel.sendMessageEmbeds(pageEmbed.build()).queue(null, throwable -> handleException(throwable));
        }
    }

    private List<String> splitString(String str, int size) {
        List<String> chunks = new ArrayList<>();
        for (int i = 0; i < str.length(); i += size) {
            chunks.add(str.substring(i, Math.min(str.length(), i + size)));
        }
        return chunks;
    }

    private void handleException(Throwable throwable) {
        if (throwable instanceof ErrorResponseException) {
            ErrorResponseException ere = (ErrorResponseException) throwable;
            switch (ere.getErrorCode()) {
                case 10003:
                    System.err.println("The channel no longer exists or the bot doesn't have access to it.");
                    break;
                case 50001:
                    System.err.println("The bot doesn't have the necessary permissions to send messages in this channel.");
                    break;
                case 50013:
                    System.err.println("The bot doesn't have the necessary permissions to perform this action.");
                    break;
                default:
                    System.err.println("An error occurred: " + ere.getMeaning());
            }
        } else {
            System.err.println("An unexpected error occurred: " + throwable.getMessage());
        }
        throwable.printStackTrace();
    }

}