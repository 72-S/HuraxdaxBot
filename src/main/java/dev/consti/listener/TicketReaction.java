package dev.consti.listener;

import dev.consti.ticket.TicketHandler;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;

public class TicketReaction extends ListenerAdapter {
    private final TicketHandler ticketHandler = new TicketHandler();

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (Objects.requireNonNull(event.getUser()).isBot()) return;

        if (event.getReaction().getEmoji().equals(Emoji.fromUnicode("U+1F4E9"))) {
            String title = "Issue Title";
            String description = "Issue Description\nSteps to Reproduce: \nExpected Result: \nActual Result: ";
            ticketHandler.startTicketProcess(Objects.requireNonNull(event.getMember()), event.getChannel().asTextChannel(), title, description);
        }
    }
}
