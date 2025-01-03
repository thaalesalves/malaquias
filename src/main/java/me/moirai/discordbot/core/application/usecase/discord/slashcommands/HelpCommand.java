package me.moirai.discordbot.core.application.usecase.discord.slashcommands;

import me.moirai.discordbot.common.usecases.UseCase;

public final class HelpCommand extends UseCase<String> {

    private final String commandToDescribe;

    private HelpCommand(String commandToDescribe) {
        this.commandToDescribe = commandToDescribe;
    }

    public static HelpCommand build(String commandToDescribe) {
        return new HelpCommand(commandToDescribe);
    }

    public String getCommandToDescribe() {
        return commandToDescribe;
    }
}
