package dev.consti.ticket;

import dev.consti.utils.ConfigHandler;
import dev.consti.utils.GithubService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TicketHandler {

    public void sendTicketMessage(TextChannel channel) {
        purgeAllMessages(channel).thenRun(() -> {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Ticket System")
                    .setDescription("To create a ticket, click the button below.")
                    .setFooter("HuraxdaxBot - I can help you here ;0", null)
                    .setColor(Color.GREEN);

            MessageEmbed messageEmbed = embedBuilder.build();
            channel.sendMessageEmbeds(messageEmbed)
                    .setActionRow(Button.primary("create_ticket", "\uD83D\uDCE9 Create Ticket"))
                    .queue();
        });
    }

    public void startTicketProcess(Member member, TextChannel channel, String title, String description) {
        GithubService githubService = new GithubService();

        String supportRoleId = ConfigHandler.getProperty("SUPPORT_ROLE_ID"); // Add the role ID in your config

        channel.createThreadChannel("ticket-" + member.getId(), true) // true for private thread
                .queue(threadChannel -> {
                    // Grant the support role access to the thread
                    Role supportRole = channel.getGuild().getRoleById(supportRoleId);
                    if (supportRole != null) {
                        // Fetch all members in the guild and filter those with the support role
                        List<Member> supportMembers = channel.getGuild().getMembers().stream()
                                .filter(guildMember -> guildMember.getRoles().contains(supportRole))
                                .toList();

                        for (Member supportMember : supportMembers) {
                            threadChannel.addThreadMemberById(supportMember.getId()).queue();
                        }
                    }

                    threadChannel.sendMessage("Ticket created by " + member.getAsMention()).queue();
                    sendCloseTicketMessage(threadChannel);
                });
    }

    private CompletableFuture<Void> purgeAllMessages(TextChannel channel) {
        return channel.getIterableHistory().takeAsync(100).thenAccept(messages -> {
            if (!messages.isEmpty()) {
                List<Message> messagesToDelete = messages.subList(0, messages.size());
                channel.purgeMessages(messagesToDelete);
            }
        });
    }

    private void sendCloseTicketMessage(ThreadChannel threadChannel) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Close Ticket")
                .setDescription("To close this ticket, click the button below.")
                .setColor(Color.RED);

        MessageEmbed messageEmbed = embedBuilder.build();
        threadChannel.sendMessageEmbeds(messageEmbed)
                .setActionRow(Button.danger("close_ticket", "Close Ticket"))
                .queue();
    }
}
