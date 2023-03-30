package com.ramen.bot;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import discord4j.core.event.domain.lifecycle.ReconnectEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.entity.Guild;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Random;

public class Main {

    public static void main(String[] args) {

        final DiscordClient client = DiscordClient.create(args[0]);
        final GatewayDiscordClient gateway = client.login().block();

        ObjectOutputStream serializeParameters = null;
        ObjectInputStream deserializeParameters = null;
        try {
            Path path = Path.of("src/main/resources/guildParameters.botdata");
            serializeParameters = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(path)));
            serializeParameters.defaultWriteObject();
            serializeParameters.flush();
            deserializeParameters = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(path)));
        } catch (IOException e) {
            System.out.println("Erreur lors de la création des de/serializers : " + e.getMessage());
            e.printStackTrace();
        }

        assert gateway != null;

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

        long guildID = 1087812205438836827L;

        gateway.getRestClient().getApplicationService()
                .createGuildApplicationCommand(applicationId, guildID, pingCmd)
                .subscribe();

        gateway.getRestClient().getApplicationService()
                .createGuildApplicationCommand(applicationId, guildID, setParametersCmd)
                .subscribe();

        //Objects.requireNonNull(gateway.getRestClient().getApplicationService().getGuildApplicationCommands(applicationId, guildID).collectSortedList().block()).forEach(commandData -> {
        //    System.out.println(commandData.name());
        //    gateway.getRestClient().getApplicationService().deleteGuildApplicationCommand(applicationId, guildID, commandData.id().asLong()).subscribe();
        //});

        gateway.on(GuildCreateEvent.class, event -> event.getGuild().getSystemChannel().block().createMessage("Bonjour !")).subscribe();

        gateway.on(ReconnectEvent.class, event -> {
            for (Guild guild : gateway.getGuilds().collectSortedList().block()) {
                guild.getSystemChannel().block().createMessage("Rebonjour !").subscribe();
            }
            return null;
        });

        gateway.on(ChatInputInteractionEvent.class, event -> {
            switch (event.getCommandName()) {
                case "ping":
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
                case "setMembersCountChannel":
                    if (event.getOption("createNewChannel").isPresent()) {

                    }
                    break;
                case "modifier-paramètres":
                    return event.reply().withComponents(ActionRow.of(SelectMenu.of("botParametersMenu", SelectMenu.Option.of("Messages automatiques", "automaticMessagesParameter"))));
                default:
            }
            return null;
        }).subscribe();

        HashMap<Snowflake, String> textToJavaCmdData;

        gateway.on(MessageCreateEvent.class).subscribe(event -> {
            switch (event.getMessage().getContent()) {
                case "!Text To Java" ->
                        event.getMessage().getChannel().block()
                                .createMessage("Entre maintenant ton script. Si tu veux arrêter, fais !cancel.")
                                .withMessageReference(event.getMessage().getId()).subscribe();
                case "!cancel" ->
                        event.getMessage().getChannel().block()
                                .createMessage("Tu as bien arrêté l'enregistrement de ton script.")
                                .withMessageReference(event.getMessage().getId()).subscribe();
                case "!emoji test" ->
                        event.getMessage().getChannel().block()
                                .createMessage("<:gwen_coeur:1049284716831981618>")
                                .withMessageReference(event.getMessage().getId()).subscribe();
            }
        });

        ObjectOutputStream finalSerializeParameters = serializeParameters;
        ObjectInputStream finalDeserializeParameters = deserializeParameters;
        gateway.on(ComponentInteractionEvent.class, event -> {
            if (event.getInteraction().getData().data().get().components().get().get(0).customId().get().equals("test"))
            switch (event.getCustomId()) {
                case "automaticMessagesParameter" ->
                        event.edit().withComponents(ActionRow.of(SelectMenu.of("selectCommandParameter", SelectMenu.Option.of("Commande /ping", "pingCommandParameter"))));
                case "pingCommandParameter" -> {
                    GuildParameters guildParameters;
                    try {
                        guildParameters = (GuildParameters) finalDeserializeParameters.readObject();
                    } catch (IOException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                    Object value = guildParameters.getGuildParameter(event.getMessage().get().getGuildId().get().asLong(), "pingCommandParameter");

                    if (value != null) {
                        if ((Boolean) value) ;
                    } else {
                        guildParameters.setGuildParameter(event.getMessage().get().getGuildId().get().asLong(), "pingCommandParameter", true);
                        try {
                            finalSerializeParameters.writeObject(guildParameters);
                            finalSerializeParameters.flush();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            return null;
        });

        gateway.onDisconnect().block();
    }
}