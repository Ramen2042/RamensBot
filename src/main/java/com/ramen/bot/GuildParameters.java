package com.ramen.bot;

import java.io.Serializable;
import java.util.HashMap;

public class GuildParameters implements Serializable {
    HashMap<Long, HashMap<String, Object>> parameters = new HashMap<>();

    public GuildParameters() {
    }

    public void setGuildParameter(long guildId, String parameter, Object value) {
        parameters.get(guildId).putIfAbsent(parameter, value);
    }

    public Object getGuildParameter(long guildId, String parameter) {
        return parameters.get(guildId).get(parameter);
    }
}
