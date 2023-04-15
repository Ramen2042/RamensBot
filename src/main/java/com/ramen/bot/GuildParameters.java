package com.ramen.bot;

import com.ramen.language.Language;
import discord4j.core.object.entity.Guild;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class GuildParameters implements Serializable {
    @Serial
    private static final long serialVersionUID = 5693541294334144560L;
    private final HashMap<Long, HashMap<String, Object>> parameters = new HashMap<>();

    public static final String BOT_PARAMETERS = "botParameters";
    public static final String COMMAND_PARAMETERS = "commandParameters";
    public static final String AUTOMATIC_MESSAGES = "automaticMessages";
    public static final String SLASH_COMMANDS = "slashCommands";
    public static final String PING_COMMAND = "pingCommand";
    public static final String AUTO_MESSAGE_CHANNEL = "autoMessageChannel";

    public static final String COMMANDS_CHANNEL = "commandsChannel";
    public static final String DEFAULT_MESSAGE_CHANNEL = "defaultMessageChannel";
    public static final String HELLO_MESSAGE_CHANNEL = "helloMessageChannel";
    public static final String HELLO_MESSAGE = "helloMessage";
    public static final String ENABLE_HELLO_MESSAGE = "enableHelloMessage";
    public static final String DISABLE_HELLO_MESSAGE = "disableHelloMessage";
    public static final String WELCOME_MESSAGE_CHANNEL = "welcomeMessageChannel";
    public static final String WELCOME_MESSAGE = "welcomeMessage";
    public static final String ENABLE_WELCOME_MESSAGE = "enableWelcomeMessage";
    public static final String DISABLE_WELCOME_MESSAGE = "disableWelcomeMessage";
    public static final String DO_NOTHING = "doNothing";
    public static final String PERMISSIONS = "permissions";
    public static final String LANGUAGE_PARAMETER = "languageParameter";
    public static final String CUTE_MODE = "cuteMode";
    public static final String ENABLE_SERIOUS_MODE = "seriousMode";
    public static final String ENABLE_CUTE_MODE = "cuteMode";

    public GuildParameters() {
    }

    public GuildParameters setGuildParameter(long guildId, String parameter, Object value) {
        parameters.putIfAbsent(guildId, new HashMap<>());
        parameters.get(guildId).put(parameter, value);
        return this;
    }

    private GuildParameters setGuildParameterIfAbsent(long guildId, String parameter, Object value) {
        parameters.get(guildId).putIfAbsent(parameter, value);
        return this;
    }

    public Object getGuildParameter(long guildId, String parameter) {
        if (parameters.get(guildId) == null) {
            initializeForGuild(guildId);
            return parameters.get(guildId).get(parameter);
        }

        if (parameters.get(guildId).get(parameter) == null) {
            initializeForGuild(guildId);
            return parameters.get(guildId).get(parameter);
        }

        return parameters.get(guildId).get(parameter);
    }

    public GuildParameters initializeForAllGuilds(List<Guild> guilds) {
        for (Guild guild : guilds) {
            initializeForGuild(guild);
        }
        return this;
    }

    private GuildParameters initializeForGuild(Guild guild) {
        parameters.computeIfAbsent(guild.getId().asLong(), k -> new HashMap<>());

        setGuildParameterIfAbsent(guild.getId().asLong(), PING_COMMAND, true);
        setGuildParameterIfAbsent(guild.getId().asLong(), AUTO_MESSAGE_CHANNEL, DEFAULT_MESSAGE_CHANNEL);
        setGuildParameterIfAbsent(guild.getId().asLong(), HELLO_MESSAGE, true);
        setGuildParameterIfAbsent(guild.getId().asLong(), HELLO_MESSAGE_CHANNEL, DEFAULT_MESSAGE_CHANNEL);
        setGuildParameterIfAbsent(guild.getId().asLong(), WELCOME_MESSAGE, true);
        setGuildParameterIfAbsent(guild.getId().asLong(), WELCOME_MESSAGE_CHANNEL, DEFAULT_MESSAGE_CHANNEL);
        setGuildParameterIfAbsent(guild.getId().asLong(), PERMISSIONS, new ArrayList<String>());
        setGuildParameterIfAbsent(guild.getId().asLong(), LANGUAGE_PARAMETER, Language.Francais);
        setGuildParameterIfAbsent(guild.getId().asLong(), CUTE_MODE, true);
        return this;
    }

    private GuildParameters initializeForGuild(long guild) {
        parameters.computeIfAbsent(guild, k -> new HashMap<>());

        setGuildParameterIfAbsent(guild, PING_COMMAND, true);
        setGuildParameterIfAbsent(guild, AUTO_MESSAGE_CHANNEL, DEFAULT_MESSAGE_CHANNEL);
        setGuildParameterIfAbsent(guild, HELLO_MESSAGE, true);
        setGuildParameterIfAbsent(guild, HELLO_MESSAGE_CHANNEL, DEFAULT_MESSAGE_CHANNEL);
        setGuildParameterIfAbsent(guild, WELCOME_MESSAGE, true);
        setGuildParameterIfAbsent(guild, WELCOME_MESSAGE_CHANNEL, DEFAULT_MESSAGE_CHANNEL);
        setGuildParameterIfAbsent(guild, PERMISSIONS, new ArrayList<String>());
        setGuildParameterIfAbsent(guild, LANGUAGE_PARAMETER, Language.Francais);
        setGuildParameterIfAbsent(guild, CUTE_MODE, true);
        return this;
    }

    @Override
    public String toString() {
        return "GuildParameters : " + parameters;
    }
}
