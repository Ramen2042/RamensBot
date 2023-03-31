package com.ramen.bot;

import discord4j.core.object.entity.Guild;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class GuildParameters implements Serializable {
    HashMap<Long, HashMap<String, Object>> parameters = new HashMap<>();

    public GuildParameters() {
    }

    public void setGuildParameter(long guildId, String parameter, Object value) {
        if (!parameters.containsKey(guildId) || parameters.get(guildId) == null) {
            parameters.put(guildId, new HashMap<>());
        }
        parameters.get(guildId).putIfAbsent(parameter, value);
    }

    public Object getGuildParameter(long guildId, String parameter) {
        return parameters.get(guildId).get(parameter);
    }

    public GuildParameters initializeForAllGuilds(List<Guild> guilds) {
        for (Guild guild : guilds) {
            setGuildParameter(guild.getId().asLong(), "pingCommandParameter", true);
            setGuildParameter(guild.getId().asLong(), "helloMessage", true);
        }
        return this;
    }
}
