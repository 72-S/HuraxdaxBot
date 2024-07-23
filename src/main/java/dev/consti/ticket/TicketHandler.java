package dev.consti.ticket;

import dev.consti.utils.GithubService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.awt.*;

public class TicketHandler {

    public void sendTicketMessage(TextChannel channel) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Ticket System")
                .setDescription("To create a ticket react with :envelope_with_arrow:")
                .setFooter("TicketTool.xyz - Ticketing without clutter", null)
                .setColor(Color.GREEN);

        MessageEmbed messageEmbed = embedBuilder.build();
        channel.sendMessageEmbeds(messageEmbed).queue(message -> message.addReaction(Emoji.fromUnicode("U+1F4E9")).queue());
    }

    public void startTicketProcess(Member member, TextChannel channel, String title, String description) {
        GithubService githubService = new GithubService();
        String issueUrl = githubService.createIssue(title, description);

        channel.createThreadChannel("ticket-" + member.getEffectiveName())
                .queue(threadChannel -> {
                    threadChannel.sendMessage("Ticket created by " + member.getAsMention()).queue();
                    threadChannel.sendMessage("Issue created: " + issueUrl).queue();
                });
    }
}
