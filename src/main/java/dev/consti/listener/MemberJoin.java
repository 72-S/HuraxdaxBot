package dev.consti.listener;

import dev.consti.utils.ConfigHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MemberJoin extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Guild guild = event.getGuild();
        Role role = guild.getRolesByName(ConfigHandler.getProperty("WELCOME_ROLE_NAME"), true).stream().findFirst().orElse(null);

        if (role != null) {
            guild.addRoleToMember(event.getMember(), role).queue();
        } else {
            System.out.println("Role not found: " + ConfigHandler.getProperty("WELCOME_ROLE_NAME"));
        }
    }
}
