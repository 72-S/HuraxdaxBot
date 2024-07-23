package dev.consti.ticket;

import dev.consti.utils.ConfigHandler;
import net.dv8tion.jda.api.EmbedBuilder;
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
import java.util.stream.Collectors;

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

    public void startTicketProcess(Member member, TextChannel channel, String type, String title, String description, String... additionalInfo) {
        String supportRoleId = ConfigHandler.getProperty("SUPPORT_ROLE_ID"); // Add the role ID in your config

        channel.createThreadChannel("ticket-" + member.getId(), true) // true for private thread
                .queue(threadChannel -> {
                    // Grant the support role access to the thread
                    Role supportRole = channel.getGuild().getRoleById(supportRoleId);
                    if (supportRole != null) {
                        List<Member> supportMembers = channel.getGuild().getMembers().stream()
                                .filter(guildMember -> guildMember.getRoles().contains(supportRole))
                                .toList();

                        for (Member supportMember : supportMembers) {
                            threadChannel.addThreadMemberById(supportMember.getId()).queue();
                        }
                    }

                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setTitle("Ticket Details")
                            .addField("Created By", member.getAsMention(), false)
                            .addField("Title", title, false)
                            .addField("Description", description, false);

                    // Only add Additional Info field if there is additional info provided
                    if (additionalInfo.length > 0 && !additionalInfo[0].isEmpty()) {
                        StringBuilder additionalInfoBuilder = new StringBuilder();
                        for (String info : additionalInfo) {
                            additionalInfoBuilder.append(info).append("\n");
                        }
                        embedBuilder.addField("Additional Info", additionalInfoBuilder.toString(), false);
                    } else {
                        embedBuilder.addField("Additional Info", "None", false);
                    }

                    // Set the color based on the ticket type
                    switch (type.toLowerCase()) {
                        case "bug_report":
                            embedBuilder.setColor(Color.RED);
                            break;
                        case "feature_request":
                            embedBuilder.setColor(Color.getHSBColor(231f / 360f, 0.636f, 0.949f));
                            break;
                        case "custom":
                            embedBuilder.setColor(Color.GRAY);
                            break;
                        default:
                            embedBuilder.setColor(Color.GREEN);
                            break;
                    }

                    embedBuilder.setDescription("Please wait for a support member to assist you.")
                            .setFooter("To close this ticket, click the button below.", null);

                    MessageEmbed messageEmbed = embedBuilder.build();

                    threadChannel.sendMessageEmbeds(messageEmbed)
                            .addActionRow(Button.danger("close_ticket", "Close Ticket"))
                            .queue();
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
}
