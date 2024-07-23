package dev.consti.listener;

import dev.consti.ticket.TicketHandler;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;

public class ButtonListener extends ListenerAdapter {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (Objects.equals(event.getButton().getId(), "create_ticket")) {
            TicketHandler ticketHandler = new TicketHandler();
            ticketHandler.startTicketProcess(Objects.requireNonNull(event.getMember()), event.getChannel().asTextChannel(), "Ticket Title", "Ticket Description");

        } else if (Objects.equals(event.getButton().getId(), "close_ticket")) {
            event.getChannel().delete().queue();
        }
    }
}
