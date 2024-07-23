package dev.consti.ticket;

import dev.consti.utils.ConfigHandler;
import dev.consti.utils.GithubService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;


import java.awt.Color;
import java.util.List;


public class TicketHandler {

    public void sendTicketMessage(TextChannel channel) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Ticket System")
                .setDescription("To create a ticket, click the button below.")
                .setFooter("HuraxdaxBot - Ticketing without clutter", null)
                .setColor(Color.GREEN);

        MessageEmbed messageEmbed = embedBuilder.build();
        channel.sendMessageEmbeds(messageEmbed)
                .setActionRow(Button.primary("create_ticket", "Create Ticket"))
                .queue();
    }

    public void startTicketProcess(Member member, TextChannel channel, String title, String description) {
        GithubService githubService = new GithubService();

        String supportRoleId = ConfigHandler.getProperty("SUPPORT_ROLE_ID"); // Add the role ID in your config

        channel.createThreadChannel("ticket-" + member.getId(), true) // true for private thread
                .queue(threadChannel -> {
                    // Grant the support role access to the thread
                    Role supportRole = channel.getGuild().getRoleById(supportRoleId);
                    if (supportRole != null) {
                        List<Member> supportMembers = threadChannel.getGuild().getMembersWithRoles(supportRole);
                        for (Member supportMember : supportMembers) {
                            threadChannel.addThreadMemberById(supportMember.getId()).queue();
                        }
                    }

                    threadChannel.sendMessage("Ticket created by " + member.getAsMention()).queue();

                    sendCloseTicketMessage(threadChannel);
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
