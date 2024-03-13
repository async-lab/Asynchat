package club.asyncraft.asynchat

import club.asyncraft.asynchat.util.Utils
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.proxy.ProxyServer
import net.kyori.adventure.text.minimessage.MiniMessage


class Commands(
    private val proxyServer: ProxyServer
) {
    private val talkMeta = this.proxyServer.commandManager.metaBuilder("talk")
        .plugin(Asynchat.instance)
        .build()

    private val talkCommand = BrigadierCommand(BrigadierCommand.literalArgumentBuilder("talk")
        .executes { context ->
            context.source.sendMessage(Utils.getTextComponent("asynchat.wrong_usage"))
            Command.SINGLE_SUCCESS
        }.then(
            BrigadierCommand.requiredArgumentBuilder("player", StringArgumentType.word())
                .suggests { _, builder ->
                    builder.apply {
                        this@Commands.proxyServer.allPlayers.forEach {
                            suggest(
                                it.username
                            )
                        }
                    }.buildFuture()
                }
        ).executes { context ->
            context.source.sendMessage(Utils.getTextComponent("asynchat.wrong_usage"))
            Command.SINGLE_SUCCESS
        }.then(BrigadierCommand.requiredArgumentBuilder("message", StringArgumentType.word()))
        .executes { context ->
            val player = this.proxyServer.getPlayer(context.getArgument("player", String::class.java))
            when {
                player.isPresent -> player.get().sendMessage(Utils.getTextComponent("asynchat.wrong_usage"))
                else -> context.source.sendRichMessage(context.getArgument("message", String::class.java))
            }
            Command.SINGLE_SUCCESS
        }
        .build()
    )

    private val roarMeta = this.proxyServer.commandManager.metaBuilder("roar")
        .plugin(Asynchat.instance)
        .build()

    private val roarCommand = BrigadierCommand(BrigadierCommand.literalArgumentBuilder("roar")
        .executes {
            it.source.sendMessage(Utils.getTextComponent("asynchat.wrong_usage"))
            Command.SINGLE_SUCCESS
        }.then(BrigadierCommand.requiredArgumentBuilder("message", StringArgumentType.word()))
        .executes { context ->
            this.proxyServer.allServers.forEach { server ->
                server.sendMessage(
                    MiniMessage.miniMessage().deserialize(context.getArgument("message", String::class.java))
                )
            }
            Command.SINGLE_SUCCESS
        }
        .build()
    )

    init {
        this.proxyServer.commandManager.run {
            unregister(talkMeta)
            unregister(roarMeta)
            register(talkMeta, talkCommand)
            register(roarMeta, roarCommand)
        }
    }
}