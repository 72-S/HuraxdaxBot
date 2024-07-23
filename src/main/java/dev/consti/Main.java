package dev.consti;

import dev.consti.listener.MemberJoin;
import dev.consti.listener.ButtonListener;
import dev.consti.ticket.TicketHandler;
import dev.consti.utils.ConfigHandler;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;

public class Main {
    public static void main(String[] args) throws LoginException {
        String token = ConfigHandler.getProperty("BOT_TOKEN");
        JDABuilder.createDefault(token, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS)
                .addEventListeners(new MemberJoin(), new ButtonListener(), new ListenerAdapter() {
                    @Override
                    public void onReady(@NotNull ReadyEvent event) {
                        TextChannel ticketChannel = event.getJDA().getTextChannelById(ConfigHandler.getProperty("TICKET_CHANNEL_ID"));
                        if (ticketChannel != null) {
                            new TicketHandler().sendTicketMessage(ticketChannel);
                        }
                    }
                })
                .build();
    }
}
