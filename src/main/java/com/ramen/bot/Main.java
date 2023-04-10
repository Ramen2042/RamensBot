package com.ramen.bot;

import com.ramen.gui.BotFrame;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import discord4j.core.event.domain.lifecycle.ConnectEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.discordjson.possible.Possible;
import discord4j.rest.util.Permission;
import org.reactivestreams.Publisher;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.ramen.bot.GuildParameters.*;

public class Main {
    final static Path guildParametersPath = Path.of("src/main/resources/guildParameters.botdata");
    final static HashMap<String, List<String>> emojiGameMap = new HashMap<>();
    final static long botGuildID = 1087812205438836827L;

    static {
        emojiGameMap.putAll(Map.ofEntries(Map.entry("⚡\uD83D\uDC53\uD83E\uDE84", List.of("Harry Potter")), Map.entry("\uD83E\uDD11\uD83E\uDD86", List.of("Harry Potter"))));
    }

    public static void main(String[] args) {
        DiscordClient.create(args[0]).withGateway(gateway -> {


            assert gateway != null;

            try {

                if (Files.newInputStream(guildParametersPath).readAllBytes().length == 0) {

                    ObjectOutputStream serializeParameters = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(guildParametersPath)));
                    serializeParameters.writeObject(new GuildParameters().initializeForAllGuilds(gateway.getGuilds().collectList().block()));
                    serializeParameters.flush();
                    serializeParameters.close();
                }
            } catch (IOException e) {
                System.out.println("Erreur lors de la création des de/serializers : " + e.getMessage());
                e.printStackTrace();
            }

            BotFrame botFrame = new BotFrame();

            // Get our application's ID
            long applicationId = gateway.getRestClient().getApplicationId().block();

            // Build our command's definition
            ApplicationCommandRequest pingCmd = ApplicationCommandRequest.builder()
                    .name("ping")
                    .description("Lance la balle au bot")
                    .addOption(ApplicationCommandOptionData.builder()
                            .name("random")
                            .description("Déterminer si le bot utilisera un message aléatoire")
                            .type(ApplicationCommandOption.Type.BOOLEAN.getValue())
                            .required(false)
                            .build()
                    ).build();

            //ApplicationCommandRequest setMembersCountChannelCmd = ApplicationCommandRequest.builder()
            //        .name("setMembersCountChannel")
            //        .description("Définir le salon de comptage de membres")
            //        .addOption(ApplicationCommandOptionData.builder()
            //                .name("createNewChannel")
            //                .description("Déterminer si le bot va créer un nouveau salon, si oui idChannel est inutile")
            //                .type(ApplicationCommandOption.Type.BOOLEAN.getValue())
            //                .required(true)
            //                .build())
            //        .addOption(ApplicationCommandOptionData.builder()
            //                .name("idChannel")
            //                .description("L'ID du salon")
            //                .type(ApplicationCommandOption.Type.INTEGER.getValue())
            //                .required(false)
            //                .build()
            //        ).build();

            ApplicationCommandRequest setParametersCmd = ApplicationCommandRequest.builder()
                    .name("modifier-paramètres")
                    .description("Modifier les paramètres du bot de ce serveur")
                    .build();

            ApplicationCommandRequest textToJavaCmd = ApplicationCommandRequest.builder()
                    .name("Text-To-Java")
                    .description("Transforme votre texte en Java")
                    .addOption(ApplicationCommandOptionData.builder()
                            .name("random")
                            .description("Déterminer si le bot utilisera un message aléatoire")
                            .type(ApplicationCommandOption.Type.BOOLEAN.getValue())
                            .required(false)
                            .build()
                    ).build();

            ApplicationCommandRequest guessEmojisCmd = ApplicationCommandRequest.builder()
                    .name("devine-la-personne")
                    .description("Donne une série d'émojis, le premier qui trouve qui est-ce a gagné")
                    .build();

            ApplicationCommandRequest banCmd = ApplicationCommandRequest.builder()
                    .name("ban")
                    .description("Bannit définitivement un utilisateur")
                    .addOption(ApplicationCommandOptionData.builder()
                            .name("utilisateur")
                            .description("L'utilisateur banni")
                            .type(ApplicationCommandOption.Type.USER.getValue())
                            .required(true)
                            .build()
                    ).build();

            ApplicationCommandRequest tempBanCmd = ApplicationCommandRequest.builder()
                    .name("ban-temporaire")
                    .description("Bannit temporairement un utilisateur")
                    .addOption(ApplicationCommandOptionData.builder()
                            .name("utilisateur")
                            .description("L'utilisateur banni")
                            .type(ApplicationCommandOption.Type.USER.getValue())
                            .required(true)
                            .build())
                    .addOption(ApplicationCommandOptionData.builder()
                            .name("durée")
                            .description("La durée du bannissement")
                            .type(ApplicationCommandOption.Type.STRING.getValue())
                            .addChoice(ApplicationCommandOptionChoiceData.builder().name("30 minutes").value("30 minutes").build())
                            .addChoice(ApplicationCommandOptionChoiceData.builder().name("45 minutes").value("45 minutes").build())
                            .addChoice(ApplicationCommandOptionChoiceData.builder().name("1 heure").value("1 heure").build())
                            .addChoice(ApplicationCommandOptionChoiceData.builder().name("2 heures").value("2 heures").build())
                            .addChoice(ApplicationCommandOptionChoiceData.builder().name("5 heures").value("5 heures").build())
                            .addChoice(ApplicationCommandOptionChoiceData.builder().name("12 heures").value("12 heures").build())
                            .addChoice(ApplicationCommandOptionChoiceData.builder().name("1 jour").value("1 jour").build())
                            .addChoice(ApplicationCommandOptionChoiceData.builder().name("2 jours").value("2 jours").build())
                            .addChoice(ApplicationCommandOptionChoiceData.builder().name("3 jours").value("3 jours").build())
                            .addChoice(ApplicationCommandOptionChoiceData.builder().name("1 semaine").value("1 semaine").build())
                            .addChoice(ApplicationCommandOptionChoiceData.builder().name("2 semaines").value("2 semaines").build())
                            .addChoice(ApplicationCommandOptionChoiceData.builder().name("1 mois").value("1 mois").build())
                            .addChoice(ApplicationCommandOptionChoiceData.builder().name("2 mois").value("2 mois").build())
                            .addChoice(ApplicationCommandOptionChoiceData.builder().name("3 mois").value("3 mois").build())
                            .addChoice(ApplicationCommandOptionChoiceData.builder().name("6 mois").value("6 mois").build())
                            .addChoice(ApplicationCommandOptionChoiceData.builder().name("1 an").value("1 an").build())
                            .addChoice(ApplicationCommandOptionChoiceData.builder().name("2 ans").value("2 ans").build())
                            .required(true)
                            .build()
                    ).build();

            //gateway.getRestClient().getApplicationService()
            //        .createGuildApplicationCommand(applicationId, botGuildID, pingCmd)
            //        .subscribe();

            //Objects.requireNonNull(gateway.getRestClient().getApplicationService().getGuildApplicationCommands(applicationId, botGuildID).collectList().block()).forEach(commandData -> {
            //    System.out.println(commandData.name());
            //    gateway.getRestClient().getApplicationService().deleteGuildApplicationCommand(applicationId, botGuildID, commandData.id().asLong()).subscribe();
            //});

            //gateway.getRestClient().getApplicationService()
            //        .createGlobalApplicationCommand(applicationId, pingCmd)
            //        .subscribe();
            //
            //gateway.getRestClient().getApplicationService()
            //        .createGlobalApplicationCommand(applicationId, setParametersCmd)
            //        .subscribe();

            //gateway.getRestClient().getApplicationService()
            //        .createGlobalApplicationCommand(applicationId, guessEmojisCmd)
            //        .subscribe();

            //gateway.getRestClient().getApplicationService()
            //        .createGuildApplicationCommand(applicationId, botGuildID, setParametersCmd)
            //        .subscribe();

            gateway.getRestClient().getApplicationService()
                    .createGuildApplicationCommand(applicationId, botGuildID, banCmd)
                    .subscribe();

            gateway.getRestClient().getApplicationService()
                    .createGuildApplicationCommand(applicationId, botGuildID, tempBanCmd)
                    .subscribe();

            gateway.on(GuildCreateEvent.class, event -> {
                GuildParameters guildParameters;
                try {
                    ObjectInputStream deserializeParameters = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(guildParametersPath)));

                    guildParameters = ((GuildParameters) deserializeParameters.readObject());

                    deserializeParameters.close();
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                botFrame.println(guildParameters);
                if (((Boolean) (guildParameters.getGuildParameter(event.getGuild().getId().asLong(), HELLO_MESSAGE)))) {
                    if (guildParameters.getGuildParameter(event.getGuild().getId().asLong(), HELLO_MESSAGE_CHANNEL).equals(DEFAULT_MESSAGE_CHANNEL)) {
                        Object value = guildParameters.getGuildParameter(event.getGuild().getId().asLong(), AUTO_MESSAGE_CHANNEL);
                        if (value.equals(DEFAULT_MESSAGE_CHANNEL)) {
                            if (Boolean.FALSE.equals(event.getGuild().getSystemChannel().hasElement().block())) return event.getGuild().getOwner().block().getPrivateChannel().block().createMessage("Le serveur " + event.getGuild().getName() + " n'a pas de salon système ! Je fais comment, moi, pour savoir où est-ce que je dois dire bonjour quand je me connecte ? S'il-te-plaît, ajoute un salon système ou dis moi où est-ce que je dois parler avec la commande /modifier-paramètres !");
                            return event.getGuild().getSystemChannel().block().createMessage("Bonjour !");
                        }
                        return ((TextChannel) (gateway.getChannelById(Snowflake.of((long) guildParameters.getGuildParameter(event.getGuild().getId().asLong(), AUTO_MESSAGE_CHANNEL))).block())).createMessage("Bonjour !");
                    } else {
                        return ((TextChannel) (gateway.getChannelById(Snowflake.of((long) guildParameters.getGuildParameter(event.getGuild().getId().asLong(), HELLO_MESSAGE_CHANNEL))).block())).createMessage("Bonjour !");
                    }
                } else return null;
            }).subscribe();

            //gateway.on(ReconnectEvent.class, event -> {
            //    ArrayList<Object> messagesFlux;
            //    try {
            //        ObjectInputStream deserializeParameters = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(guildParametersPath)));
//
            //        botFrame.println("Reconnecté !");
            //        messagesFlux = new ArrayList<>();
            //        for (Guild guild : gateway.getGuilds().collectSortedList().block()) {
            //            messagesFlux.add(((TextChannel) gateway.getChannelById(Snowflake.of((long) ((GuildParameters) deserializeParameters.readObject()).getGuildParameter(guild.getId().asLong(), AUTO_MESSAGE_CHANNEL))).block()).createMessage("Rebonjour !"));
            //        }
//
            //        deserializeParameters.close();
            //    } catch (IOException | ClassNotFoundException e) {
            //        throw new RuntimeException(e);
            //    }
//
            //    return Flux.just(messagesFlux.toArray());
            //}).subscribe();

            gateway.on(ConnectEvent.class, event -> {
                botFrame.println("Connecté !");
                return null;
            }).subscribe();

            gateway.on(ChatInputInteractionEvent.class, event -> {
                switch (event.getCommandName()) {
                    case "ping" -> {
                        if (event.getInteraction().getGuildId().isPresent()) {
                            try {
                                ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(guildParametersPath)));

                                if (((GuildParameters) objectInputStream.readObject()).getGuildParameter(event.getInteraction().getGuildId().get().asLong(), PING_COMMAND).equals(false)) {
                                    event.reply().withContent("Cette commande n'est pas activée dans ce serveur.");
                                }
                            } catch (IOException | ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        if (event.getOption("random").isPresent()) {
                            if (!event.getOption("random")
                                    .flatMap(ApplicationCommandInteractionOption::getValue)
                                    .map(ApplicationCommandInteractionOptionValue::asBoolean)
                                    .get()) {
                                return event.reply("pong !");
                            } else {
                                int random = new Random().nextInt(5);
                                return switch (random) {
                                    case 0 -> event.reply("Pong ^^");
                                    case 1 -> event.reply("Je relance la balle ! Pong !");
                                    case 2 -> event.reply("Hop, et pong !");
                                    case 3 -> event.reply("Non ! Arg, j'ai raté...");
                                    case 4 -> event.reply("Hupff, pong ! Ouf, de justesse !");
                                    default -> throw new IllegalStateException("Unexpected value: " + random);
                                };
                            }
                        } else {
                            int random = new Random().nextInt(5);
                            return switch (random) {
                                case 0 -> event.reply("Pong ^^");
                                case 1 -> event.reply("Je relance la balle ! Pong !");
                                case 2 -> event.reply("Hop, et pong !");
                                case 3 -> event.reply("Non ! Arg, j'ai raté...");
                                case 4 -> event.reply("Hupff, pong ! Ouf, de justesse !");
                                default -> throw new IllegalStateException("Unexpected value: " + random);
                            };
                        }
                    }
                    case "modifier-paramètres" -> {
                        Channel.Type type = event.getInteraction().getChannel().block().getType();
                        if (type.equals(Channel.Type.DM) || type.equals(Channel.Type.GROUP_DM))
                            return event.reply("Pour utiliser cette commande, il faut que vous soyez dans un serveur.");
                        if (event.getInteraction().getMember().get().getBasePermissions().block().contains(Permission.ADMINISTRATOR)) {
                            return event.reply().withComponents(ActionRow.of(SelectMenu.of(BOT_PARAMETERS, SelectMenu.Option.of("Messages automatiques", AUTOMATIC_MESSAGES), SelectMenu.Option.of("Commandes slash", SLASH_COMMANDS), SelectMenu.Option.of("Language", LANGUAGE_PARAMETER))));
                        } else
                            return event.reply("Vous n'avez pas les permissions nécessaires pour utiliser cette commande.");
                    }
                    case "devine-la-personne" -> {
                        return event.reply().withContent("Bientôt !");
                    }
                    case "ban" -> {
                        Channel.Type type = event.getInteraction().getChannel().block().getType();
                        if (type.equals(Channel.Type.DM) || type.equals(Channel.Type.GROUP_DM))
                            return event.reply("Pour utiliser cette commande, il faut que vous soyez dans un serveur.");
                        if (event.getInteraction().getMember().get().getBasePermissions().block().contains(Permission.ADMINISTRATOR)) {
                            return null;
                        } else
                            return event.reply("Vous n'avez pas les permissions nécessaires pour utiliser cette commande.");
                    }
                    default -> {
                        return null;
                    }
                }
            }).subscribe();

            gateway.on(ComponentInteractionEvent.class, event -> {
                User user = event.getInteraction().getUser();

                botFrame.println("L'utilisateur %s, id : %d a interagi avec le message \"%s\", id : %d ".formatted(user.toString(), user.getId().asLong(), event.getMessage().get().getContent(), event.getMessage().get().getId().asLong()));

                switch (event.getCustomId()) {
                    case BOT_PARAMETERS -> {
                        botFrame.println("On modifie les paramètres du serveur :o");
                        return switch (event.getInteraction().getData().data().get().values().get().get(0)) {
                            case AUTOMATIC_MESSAGES -> event.edit().withComponents(ActionRow.of(SelectMenu.of(AUTOMATIC_MESSAGES, SelectMenu.Option.of("Modifier le salon où les messages apparaissent", AUTO_MESSAGE_CHANNEL), SelectMenu.Option.of("Paramètres du message \"Bonjour !\"", HELLO_MESSAGE), SelectMenu.Option.of("Paramètres du message de bienvenue", WELCOME_MESSAGE))));
                            case COMMAND_PARAMETERS -> event.edit().withComponents(ActionRow.of(SelectMenu.of(COMMAND_PARAMETERS, SelectMenu.Option.of("Modifier le(s) salon(s) où les commandes sont autorisées", COMMANDS_CHANNEL), SelectMenu.Option.of("Paramètres de la commande /ping", PING_COMMAND))));
                            default -> event.reply().withContent("Erreur");
                        };
                    }
                    case AUTOMATIC_MESSAGES -> {
                        botFrame.println("On modifie les paramètres des messages automatiques :o");
                        botFrame.println(event.getInteraction().getData().data().get());
                        Possible<List<String>> values = event.getInteraction().getData().data().get().values();
                        if (values.isAbsent()) return event.edit().withContent("").withComponents(ActionRow.of(SelectMenu.of(AUTOMATIC_MESSAGES, SelectMenu.Option.of("Modifier le salon où les messages apparaissent", AUTO_MESSAGE_CHANNEL), SelectMenu.Option.of("Paramètres du message \"Bonjour !\"", HELLO_MESSAGE), SelectMenu.Option.of("Paramètres du message de bienvenue", WELCOME_MESSAGE))));
                        switch (values.get().get(0)) {
                            case AUTO_MESSAGE_CHANNEL -> {
                                String channelString;
                                try {
                                    ObjectInputStream deserializeParameters = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(guildParametersPath)));

                                    GuildParameters guildParameters = (GuildParameters) deserializeParameters.readObject();
                                    Object channelId = guildParameters.getGuildParameter(event.getInteraction().getGuildId().get().asLong(), AUTO_MESSAGE_CHANNEL);
                                    if (channelId.equals(DEFAULT_MESSAGE_CHANNEL)) {
                                        channelString = event.getInteraction().getGuild().block().getSystemChannel().block().getName();
                                    } else
                                        channelString = ((TextChannel) gateway.getChannelById(Snowflake.of((long) channelId)).block()).getName();

                                    deserializeParameters.close();
                                } catch (IOException | ClassNotFoundException e) {
                                    throw new RuntimeException(e);
                                }
                                ArrayList<SelectMenu.Option> list = new ArrayList<>();
                                list.add(SelectMenu.Option.of("Salon par défaut", DEFAULT_MESSAGE_CHANNEL));
                                for (GuildChannel channel : event.getInteraction().getGuild().block().getChannels().collectList().block()) {
                                    if (channel.getType().equals(Channel.Type.GUILD_TEXT))
                                        list.add(SelectMenu.Option.of(channel.getName(), Long.toString(channel.getId().asLong())));
                                }
                                return event.edit().withContent("Le salon d'envoi des messages automatiques est " + channelString + ". Voulez le modifer ?").withComponents(ActionRow.of(Button.secondary(AUTOMATIC_MESSAGES, "Ne rien faire")), ActionRow.of(SelectMenu.of(HELLO_MESSAGE_CHANNEL, list)));
                            }
                            case HELLO_MESSAGE -> {
                                String channelString;
                                Boolean enabled;
                                try {
                                    ObjectInputStream deserializeParameters = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(guildParametersPath)));

                                    GuildParameters guildParameters = (GuildParameters) deserializeParameters.readObject();
                                    enabled = (Boolean) guildParameters.getGuildParameter(event.getInteraction().getGuildId().get().asLong(), HELLO_MESSAGE);
                                    Object channelId = guildParameters.getGuildParameter(event.getInteraction().getGuildId().get().asLong(), HELLO_MESSAGE_CHANNEL);
                                    if (channelId.equals(DEFAULT_MESSAGE_CHANNEL)) {
                                        Object value = guildParameters.getGuildParameter(event.getInteraction().getGuildId().get().asLong(), AUTO_MESSAGE_CHANNEL);
                                        if (value.equals(DEFAULT_MESSAGE_CHANNEL)) channelString = event.getInteraction().getGuild().block().getSystemChannel().block().getName();
                                        else channelString = ((TextChannel) gateway.getChannelById(Snowflake.of((long) value)).block()).getName();
                                    } else channelString = ((TextChannel) gateway.getChannelById(Snowflake.of((long) channelId)).block()).getName();

                                    deserializeParameters.close();
                                } catch (IOException | ClassNotFoundException e) {
                                    throw new RuntimeException(e);
                                }
                                botFrame.println(enabled);
                                ArrayList<SelectMenu.Option> list = new ArrayList<>();
                                list.add(SelectMenu.Option.of("Salon par défaut", DEFAULT_MESSAGE_CHANNEL));
                                for (GuildChannel channel : event.getInteraction().getGuild().block().getChannels().collectList().block()) {
                                    if (channel.getType().equals(Channel.Type.GUILD_TEXT)) list.add(SelectMenu.Option.of(channel.getName(), Long.toString(channel.getId().asLong())));
                                }
                                if (enabled) {
                                    return event.edit().withContent("Le message automatique \"Bonjour !\" qui apparaît quand le bot se connecte est activé. Son salon d'envoi est " + channelString + ". Voulez vous le désactiver ou modifier son salon d'envoi ?").withComponents(ActionRow.of(Button.primary(DISABLE_HELLO_MESSAGE, "Désactiver"), Button.secondary(AUTOMATIC_MESSAGES, "Ne rien faire")), ActionRow.of(SelectMenu.of(HELLO_MESSAGE_CHANNEL, list)));
                                } else {
                                    return event.edit().withContent("Le message automatique \"Bonjour !\" qui apparaît quand le bot se connecte est désactivé. Son salon d'envoi est " + channelString + ". Voulez vous l'activer ou modifier son salon d'envoi ?").withComponents(ActionRow.of(Button.primary(ENABLE_HELLO_MESSAGE, "Activer"), Button.secondary(AUTOMATIC_MESSAGES, "Ne rien faire")), ActionRow.of(SelectMenu.of(HELLO_MESSAGE_CHANNEL, list)));
                                }
                            }
                            case WELCOME_MESSAGE -> {
                                String channelString;
                                Boolean enabled;
                                try {
                                    ObjectInputStream deserializeParameters = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(guildParametersPath)));

                                    GuildParameters guildParameters = (GuildParameters) deserializeParameters.readObject();

                                    enabled = (Boolean) guildParameters.getGuildParameter(event.getInteraction().getGuildId().get().asLong(), WELCOME_MESSAGE);
                                    Object channelId = guildParameters.getGuildParameter(event.getInteraction().getGuildId().get().asLong(), WELCOME_MESSAGE_CHANNEL);
                                    if (channelId.equals(DEFAULT_MESSAGE_CHANNEL)) {
                                        Object value = guildParameters.getGuildParameter(event.getInteraction().getGuildId().get().asLong(), AUTO_MESSAGE_CHANNEL);
                                        if (value.equals(DEFAULT_MESSAGE_CHANNEL)) channelString = event.getInteraction().getGuild().block().getSystemChannel().block().getName();
                                        else channelString = ((TextChannel) gateway.getChannelById(Snowflake.of((long) value)).block()).getName();
                                    } else channelString = ((TextChannel) gateway.getChannelById(Snowflake.of((long) channelId)).block()).getName();

                                    deserializeParameters.close();
                                } catch (IOException | ClassNotFoundException e) {
                                    throw new RuntimeException(e);
                                }
                                botFrame.println(enabled);
                                ArrayList<SelectMenu.Option> list = new ArrayList<>();
                                list.add(SelectMenu.Option.of("Salon par défaut", DEFAULT_MESSAGE_CHANNEL));
                                for (GuildChannel channel : event.getInteraction().getGuild().block().getChannels().collectList().block()) {
                                    if (channel.getType().equals(Channel.Type.GUILD_TEXT)) list.add(SelectMenu.Option.of(channel.getName(), Long.toString(channel.getId().asLong())));
                                }
                                if (enabled) {
                                    return event.edit().withContent("Le message automatique de bienvenue est activé. Son salon d'envoi est " + channelString + ". Voulez vous le désactiver ou modifier son salon d'envoi ?").withComponents(ActionRow.of(Button.primary(DISABLE_WELCOME_MESSAGE, "Désactiver"), Button.secondary(AUTOMATIC_MESSAGES, "Ne rien faire")), ActionRow.of(SelectMenu.of(WELCOME_MESSAGE_CHANNEL, list)));
                                } else {
                                    return event.edit().withContent("Le message automatique de bienvenue est désactivé. Son salon d'envoi est " + channelString + ". Voulez vous l'activer ou modifier son salon d'envoi ?").withComponents(ActionRow.of(Button.primary(ENABLE_WELCOME_MESSAGE, "Activer"), Button.secondary(AUTOMATIC_MESSAGES, "Ne rien faire")), ActionRow.of(SelectMenu.of(WELCOME_MESSAGE_CHANNEL, list)));
                                }
                            }
                            default -> event.reply().withContent("Erreur");
                        }
                    }
                    case PING_COMMAND -> {
                        GuildParameters guildParameters;
                        try {
                            ObjectInputStream deserializeParameters = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(guildParametersPath)));

                            guildParameters = (GuildParameters) deserializeParameters.readObject();

                            deserializeParameters.close();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }

                        ObjectOutputStream serializeParameters;
                        try {
                            serializeParameters = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(guildParametersPath)));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }


                        Object value = guildParameters.getGuildParameter(event.getMessage().get().getGuildId().get().asLong(), PING_COMMAND);

                        if (value != null) {
                            if ((Boolean) value) ;
                        } else {
                            guildParameters.setGuildParameter(event.getMessage().get().getGuildId().get().asLong(), PING_COMMAND, true);
                            try {
                                serializeParameters.writeObject(guildParameters);
                                serializeParameters.flush();
                                serializeParameters.close();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    case HELLO_MESSAGE_CHANNEL -> {
                        return changeMessageChannel(HELLO_MESSAGE_CHANNEL, event);
                    }
                    case ENABLE_HELLO_MESSAGE -> {
                        return enableMessage(HELLO_MESSAGE, event);
                    }
                    case DISABLE_HELLO_MESSAGE -> {
                        return disableMessage(HELLO_MESSAGE, event);
                    }
                    case WELCOME_MESSAGE_CHANNEL -> {
                        return changeMessageChannel(WELCOME_MESSAGE_CHANNEL, event);
                    }
                    case ENABLE_WELCOME_MESSAGE -> {
                        return enableMessage(WELCOME_MESSAGE, event);
                    }
                    case DISABLE_WELCOME_MESSAGE -> {
                        return disableMessage(WELCOME_MESSAGE, event);
                    }
                    case DO_NOTHING -> {
                        botFrame.println(event);
                        return event.edit().withContent("Ok ! Rien n'a changé !").withComponents();
                    }
                }

                return null;
            }).subscribe();

            HashMap<Snowflake, HashMap<Snowflake, String>> textToJavaCmdData = null;

            gateway.on(MessageCreateEvent.class).subscribe(event -> {
                if (event.getMessage().getChannel().block().getType().equals(Channel.Type.DM)) {
                    botFrame.println("Message privé reçu de %s, id : %d, message : %s%n".formatted(event.getMessage().getAuthor().get().getUsername(), event.getMessage().getAuthor().get().getId().asLong(), event.getMessage().getContent()));
                    return;
                }
                String content = event.getMessage().getContent();
                if (content.equals("<@1087828765754785842>") || content.equals("<@1087828765754785842> ")) {
                    event.getMessage().getChannel().block().createMessage("Je n'ai pas de préfix, utilisez les commandes slash :D");
                } else {
                    if (event.getGuildId().get().asLong() == botGuildID)
                        botFrame.println("Message reçu de %s, id : %d, message : \"%s\" dans le salon \"%s\", id : %d, dans le serveur \"%s\", id : %d".formatted(
                                event.getMessage().getAuthorAsMember().block().getUsername(),
                                event.getMessage().getAuthorAsMember().block().getId().asLong(),
                                event.getMessage().getContent(),
                                event.getMessage().getChannel().block().getRestChannel().getData().block().name().get(),
                                event.getMessage().getChannel().block().getId().asLong(),
                                event.getMessage().getGuild().block().getName(),
                                event.getMessage().getGuild().block().getId().asLong()));
                }
            });

            gateway.on(MemberJoinEvent.class, event -> {
                event.getGuild().block().getSystemChannel().block().createMessage("Bienvenue " + event.getMember().getMention() + " sur le serveur support de Ramen's Bot, c'est-à-dire moi !").subscribe();
                event.getGuild().block().getSystemChannel().block().createMessage("<:gwenhey:1091016305911533568>").subscribe();
                return event.getGuild().block().getSystemChannel().block().createMessage("Je suis encore en développement, mais je pourrais bientôt faire plein de choses géniales !\n\nMes trois mots préférés : utile, fun et mignon !");
            }).subscribe();

            //gateway.getGuildById(Snowflake.of(botGuildID)).block().getSystemChannel().block().createMessage("Bienvenue <@828399905080213535> sur le serveur support de Ramen's Bot, c'est-à-dire moi !").subscribe();
            //gateway.getGuildById(Snowflake.of(botGuildID)).block().getSystemChannel().block().createMessage("<:gwenhey:1091016305911533568>").subscribe();
            //gateway.getGuildById(Snowflake.of(botGuildID)).block().getSystemChannel().block().createMessage("Je suis encore en développement, mais je pourrais bientôt faire plein de choses géniales !\n\nMes trois mots préférés : utile, fun et mignon !").subscribe();
//
            //gateway.getGuildById(Snowflake.of(botGuildID)).block().getSystemChannel().block().createMessage("Bienvenue <@900399686295384137> sur le serveur support de Ramen's Bot, c'est-à-dire moi !").subscribe();
            //gateway.getGuildById(Snowflake.of(botGuildID)).block().getSystemChannel().block().createMessage("<:gwenhey:1091016305911533568>").subscribe();
            //gateway.getGuildById(Snowflake.of(botGuildID)).block().getSystemChannel().block().createMessage("Je suis encore en développement, mais je pourrais bientôt faire plein de choses géniales !\n\nMes trois mots préférés : utile, fun et mignon !").subscribe();

            //new Thread(() -> {
            //    String string;
            //    while (true) {
            //        try {
            //            string = botFrame.nextString();
            //            if (string.contains("getGuilds"))
            //                botFrame.println(gateway.getGuilds().collectSortedList().block().toArray());
            //            else if (string.contains("getMembers")) {
            //                botFrame.println("Entre l'identifiant du serveur :");
//
            //                final long guildID = Long.parseUnsignedLong(botFrame.nextString());
//
            //                Guild result = null;
            //                for (Guild guild : gateway.getGuilds().collectSortedList().block()) {
            //                    if (guildID == guild.getId().asLong())
            //                        result = guild;
            //                }
            //                try {
            //                    botFrame.println(result.getMembers().collectSortedList().block());
            //                } catch (IllegalArgumentException e) {
            //                    if (e.getMessage().equals("GUILD_MEMBERS intent is required to request the entire member list"))
            //                        botFrame.println("L'autorisation \"GUILD_MEMBERS\" est nécessaire pour accéder à la liste des membres. Demandez au propriétaire du bot d'ajouter cette autorisation.");
            //                }
            //            } else if (string.contains("sendDM")) {
            //                botFrame.println("Entre l'identifiant de l'utilisateur :");
            //                String idString = botFrame.nextString();
            //                User user;
            //                botFrame.println("Entre ton message :");
            //                String message = botFrame.nextString();
            //                try {
            //                    user = gateway.getUserById(Snowflake.of(Long.parseUnsignedLong(idString))).block();
            //                } catch (ClientException e) {
            //                    botFrame.println(e.getMessage());
            //                    continue;
            //                }
            //                MessageCreateMono messageMono;
            //                Message message1 = null;
            //                if (user != null) {
            //                    messageMono = user.getPrivateChannel().block().createMessage(message);
            //                    messageMono.subscribe();
            //                    message1 = messageMono.block();
            //                } else botFrame.println("Identifiant incorrect.");
            //                botFrame.println("Message envoyé à l'utilisateur %s, id : %d, contenu : \"%s\", id : %d".formatted(user.getUsername(), user.getId().asLong(), message1.getContent(), message1.getId().asLong()));
            //            } else if (string.contains("getDM")) {
            //                botFrame.println("Entre l'identifiant de l'utilisateur :");
            //                String idString = botFrame.nextString();
            //                User user;
            //                try {
            //                    user = gateway.getUserById(Snowflake.of(Long.parseUnsignedLong(idString))).block();
            //                } catch (ClientException e) {
            //                    botFrame.println(e.getMessage());
            //                    continue;
            //                }
            //                if (user != null) {
            //                    PrivateChannel privateChannel = user.getPrivateChannel().block();
            //                    Message lastMessage = privateChannel.getLastMessage().block();
            //                    botFrame.println("Voici la liste des messages avec l'utilisateur " + user.getUsername());
            //                    privateChannel.getMessagesBefore(lastMessage.getId()).collectSortedList().block().forEach(objectsToPrint -> {
            //                        User user1 = objectsToPrint.getAuthor().get();
            //                        botFrame.println("Message par %s, id : %d, contenu : \"%s\", id : %d".formatted(user1.getUsername(), user1.getId().asLong(), objectsToPrint.getContent(), objectsToPrint.getId().asLong()));
            //                    });
            //                    User user1 = lastMessage.getAuthor().get();
            //                    botFrame.println("Message par %s, id : %d, contenu : \"%s\", id : %d".formatted(user1.getUsername(), user1.getId().asLong(), lastMessage.getContent(), lastMessage.getId().asLong()));
            //                } else botFrame.println("Identifiant incorrect.");
            //            } else
            //                gateway.getGuildById(Snowflake.of(botGuildID)).block().getSystemChannel().block().createMessage(string).subscribe();
            //        } catch (Exception e) {
            //            botFrame.println("Erreur : " + e.getMessage());
            //        }
            //    }
            //}).start();

            return gateway.onDisconnect();
        }).block();
    }

    private static Publisher enableMessage(String messageParameter, ComponentInteractionEvent event) {
        GuildParameters guildParameters;
        try {
            ObjectInputStream deserializeParameters = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(guildParametersPath)));

            guildParameters = ((GuildParameters) deserializeParameters.readObject());

            deserializeParameters.close();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        ObjectOutputStream serializeParameters;
        try {
            serializeParameters = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(guildParametersPath)));

            serializeParameters.writeObject(guildParameters.setGuildParameter(event.getInteraction().getGuildId().get().asLong(), messageParameter, true));
            serializeParameters.flush();
            serializeParameters.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return event.edit().withContent("<:renard_OK:1094671840720457840> Paramètre activé !").withComponents();
    }

    private static Publisher disableMessage(String messageParameter, ComponentInteractionEvent event) {
        GuildParameters guildParameters;
        try {
            ObjectInputStream deserializeParameters = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(guildParametersPath)));

            guildParameters = ((GuildParameters) deserializeParameters.readObject());

            deserializeParameters.close();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        ObjectOutputStream serializeParameters;
        try {
            serializeParameters = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(guildParametersPath)));

            serializeParameters.writeObject(guildParameters.setGuildParameter(event.getInteraction().getGuildId().get().asLong(), messageParameter, false));
            serializeParameters.flush();
            serializeParameters.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return event.edit().withContent("<:renard_OK:1094671840720457840> Paramètre désactivé !").withComponents();
    }

    private static Publisher changeMessageChannel(String messageChannelParameter, ComponentInteractionEvent event) {
        GuildParameters guildParameters;
        try {
            ObjectInputStream deserializeParameters = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(guildParametersPath)));

            guildParameters = ((GuildParameters) deserializeParameters.readObject());

            deserializeParameters.close();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        ObjectOutputStream serializeParameters;
        try {
            serializeParameters = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(guildParametersPath)));

            String value = event.getInteraction().getData().data().get().values().get().get(0);

            if (value.equals(DEFAULT_MESSAGE_CHANNEL)) {
                serializeParameters.writeObject(guildParameters.setGuildParameter(event.getInteraction().getGuildId().get().asLong(), messageChannelParameter, DEFAULT_MESSAGE_CHANNEL));
            } else serializeParameters.writeObject(guildParameters.setGuildParameter(event.getInteraction().getGuildId().get().asLong(), messageChannelParameter, Long.parseUnsignedLong(value)));
            serializeParameters.flush();
            serializeParameters.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return event.edit().withContent("<:renard_OK:1094671840720457840> Paramètre changé !").withComponents();
    }
}