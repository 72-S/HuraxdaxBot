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

public class TicketHandler {

    public void sendTicketMessage(TextChannel channel) {
        purgeAllMessages(channel).thenRun(() -> {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Support System")
                    .setDescription("To create a ticket, click the button below.")
                    .setFooter("HuraxdaxBot - I can help you ;0", null)
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
                    threadChannel.addThreadMemberById(member.getId()).queue();

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

                    // Determine the type label and color
                    String typeLabel;
                    Color typeColor;
                    switch (type.toLowerCase()) {
                        case "bug_report":
                            typeLabel = "Bug Report";
                            typeColor = Color.RED;
                            break;
                        case "feature_request":
                            typeLabel = "Feature Request";
                            float hue = 231f / 360f; // Convert degrees to a fraction (0.0 - 1.0)
                            float saturation = 0.636f;
                            float brightness = 0.949f;
                            typeColor = Color.getHSBColor(hue, saturation, brightness);
                            break;
                        case "custom":
                            typeLabel = "Custom Ticket";
                            typeColor = Color.GRAY;
                            break;
                        default:
                            typeLabel = "Unknown Type";
                            typeColor = Color.GREEN;
                            break;
                    }

                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setTitle(title)
                            .setAuthor(member.getUser().getAsTag(), null, member.getUser().getEffectiveAvatarUrl())
                            .addField("Type", typeLabel, true)
                            .addField("Description", description, false);

                    // Add additional info fields if provided
                    for (String info : additionalInfo) {
                        if (!info.isEmpty()) {
                            String[] parts = info.split(":", 2);
                            if (parts.length == 2) {
                                embedBuilder.addField(parts[0].trim(), parts[1].trim(), false);
                            }
                        }
                    }

                    embedBuilder.setColor(typeColor)
                            .setDescription("Support will assist you shortly. In addition, feel free to provide any extra information or further details in the chat if it is not covered in the description.")
                            .setFooter("To close this ticket, click the button below.", null);

                    MessageEmbed messageEmbed = embedBuilder.build();

                    threadChannel.sendMessageEmbeds(messageEmbed)
                            .addActionRow(Button.danger("close_ticket", "\uD83D\uDD12 Close Ticket"))
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
