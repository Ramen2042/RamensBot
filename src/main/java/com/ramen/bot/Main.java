package com.ramen.bot;

import com.ramen.gui.BotFrame;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import discord4j.core.event.domain.lifecycle.ConnectEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.lifecycle.ReconnectEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.Channel;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Flux;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        final DiscordClient client = DiscordClient.create(args[0]);
        final GatewayDiscordClient gateway = client.login().block();

        long botGuildID = 1087812205438836827L;

        assert gateway != null;

        ObjectInputStream deserializeParameters = null;

        final Path guildParametersPath = Path.of("src/main/resources/guildParameters.botdata");

        try {
            if (Files.newInputStream(guildParametersPath).readAllBytes().length == 0) {
                ObjectOutputStream serializeParameters = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(guildParametersPath)));
                serializeParameters.writeObject(new GuildParameters().initializeForAllGuilds(gateway.getGuilds().collectList().block()));
                serializeParameters.close();
            }

            deserializeParameters = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(guildParametersPath)));
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

        gateway.getRestClient().getApplicationService()
                .createGuildApplicationCommand(applicationId, botGuildID, pingCmd)
                .subscribe();

        gateway.getRestClient().getApplicationService()
                .createGuildApplicationCommand(applicationId, botGuildID, setParametersCmd)
                .subscribe();

        //Objects.requireNonNull(gateway.getRestClient().getApplicationService().getGuildApplicationCommands(applicationId, botGuildID).collectSortedList().block()).forEach(commandData -> {
        //    System.out.println(commandData.name());
        //    gateway.getRestClient().getApplicationService().deleteGuildApplicationCommand(applicationId, botGuildID, commandData.id().asLong()).subscribe();
        //});

        gateway.on(GuildCreateEvent.class, event -> event.getGuild().getSystemChannel().block().createMessage("Bonjour !")).subscribe();

        gateway.on(ReconnectEvent.class, event -> {
            botFrame.println("Reconnecté !");
            ArrayList<Object> messagesFlux = new ArrayList<>();
            for (Guild guild : gateway.getGuilds().collectSortedList().block()) {
                messagesFlux.add(guild.getSystemChannel().block().createMessage("Rebonjour !"));
            }
            return Flux.just(messagesFlux.toArray());
        }).subscribe();

        gateway.on(ConnectEvent.class, event -> {
            botFrame.println("Connecté !");
            return null;
        }).subscribe();

        gateway.on(ChatInputInteractionEvent.class, event -> {
            switch (event.getCommandName()) {
                case "ping" -> {
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
                    if (!type.equals(Channel.Type.DM) & !type.equals(Channel.Type.GROUP_DM)) return event.reply("Pour utiliser cette commande, il faut que vous soyez dans un serveur.");
                    if (event.getInteraction().getMember().get().getBasePermissions().block().) {
                        return event.reply().withComponents(ActionRow.of(SelectMenu.of("botParametersMenu", SelectMenu.Option.of("Messages automatiques", "automaticMessagesParameter"))));
                    }
                }
                default -> {
                    return null;
                }
            }
        }).subscribe();

        ObjectInputStream finalDeserializeParameters = deserializeParameters;
        gateway.on(ComponentInteractionEvent.class, event -> {
            User user = event.getMessage().get().getAuthor().get();

            botFrame.println("L'utilisateur %s, id : %d a interagi avec le message \"%s\", id : %d ".formatted(user.toString(), user.getId().asLong(), event.getMessage().get().getContent(), event.getMessage().get().getId().asLong()));

            ObjectOutputStream serializeParameters;
            try {
                serializeParameters = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(guildParametersPath)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (event.getInteraction().getData().data().get().components().get().get(0).customId().get().equals("test"));
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
                            serializeParameters.writeObject(guildParameters);
                            serializeParameters.flush();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }

            try {
                serializeParameters.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return null;
        });

        HashMap<Snowflake, HashMap<Snowflake, String>> textToJavaCmdData = null;

        gateway.on(MessageCreateEvent.class).subscribe(event -> {
            if (event.getMessage().getChannel().block().getType().equals(Channel.Type.DM)) {
                System.out.printf("Message privé reçu de %s, id : %d, message : %s%n", event.getMessage().getAuthor().get().getUsername(), event.getMessage().getAuthor().get().getId().asLong(), event.getMessage().getContent());
                return;
            }

            switch (event.getMessage().getContent()) {
                case "!Text To Java" -> {
                    event.getMessage().getChannel().block()
                            .createMessage("Entre maintenant ton script. Si tu veux arrêter, fais !cancel.")
                            .withMessageReference(event.getMessage().getId()).subscribe();
                }
                case "!cancel" -> {
                    if (textToJavaCmdData.get(event.getMessage().getAuthorAsMember().block().getId()).equals(""))
                        event.getMessage().getChannel().block()
                                .createMessage("Tu as bien arrêté l'enregistrement de ton script.")
                                .withMessageReference(event.getMessage().getId()).subscribe();
                }
                default ->
                        botFrame.println("Message reçu de %s, id : %d, message : \"%s\" dans le salon \"%s\", id : %d, dans le serveur \"%s\", id : %d".formatted(
                                event.getMessage().getAuthor().get().getUsername(),
                                event.getMessage().getAuthor().get().getId().asLong(),
                                event.getMessage().getContent(),
                                event.getMessage().getChannel().block().getRestChannel().getData().block().name().get(),
                                event.getMessage().getChannel().block().getId().asLong(),
                                event.getMessage().getGuild().block().getName(),
                                event.getMessage().getGuild().block().getId().asLong()));
            }
        });

        String string;
        do {
            System.out.print(">>> ");
            string = botFrame.nextString();
            if (string.contains("getGuilds")) System.out.println(gateway.getGuilds().collectSortedList().block());
            else if (string.contains("getMembers")) {
                botFrame.println("Entre l'id du serveur :");

                final long guildID = Long.parseUnsignedLong(botFrame.nextString());

                Guild result = null;
                for (Guild guild : gateway.getGuilds().collectSortedList().block()) {
                    if (guildID == guild.getId().asLong())
                        result = guild;
                }
                botFrame.println(result.getMembers().collectSortedList().block());
            }
            else if (string.contains("sendDM")) {
                botFrame.println("Entre l'id de l'utilisateur :");
                String idString = botFrame.nextString();
                User user;
                try {
                    user = gateway.getUserById(Snowflake.of(Long.parseUnsignedLong(idString))).block();
                } catch (ArrayIndexOutOfBoundsException|NumberFormatException e) {
                    botFrame.println("Merci d'ajouter l'id d'un utilisateur");
                    continue;
                }
                assert user != null;
                botFrame.println("Entre ton message :");
                String message = botFrame.nextString();
                user.getPrivateChannel().block().createMessage(message).subscribe();
                botFrame.println("Message envoyé à l'utilisateur " + user.getUsername() + ", id : " + user.getId().asLong());
            } else gateway.getGuildById(Snowflake.of(botGuildID)).block().getSystemChannel().block().createMessage(string).subscribe();
        } while (!string.equals("stop"));

        gateway.onDisconnect().block();

        try {
            deserializeParameters.close();
            finalDeserializeParameters.close();
            finalDeserializeParameters.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}