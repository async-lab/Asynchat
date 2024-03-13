package club.asyncraft.asynchat

import club.asyncraft.asynchat.util.Utils
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.PlayerSettingsChangedEvent
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyReloadEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import org.slf4j.Logger
import java.nio.file.Path

@Plugin(
    id = BuildConstants.PLUGIN_ID,
    name = BuildConstants.PLUGIN_NAME,
    version = BuildConstants.PLUGIN_VERSION,
    description = BuildConstants.PLUGIN_DESCRIPTION,
    url = BuildConstants.PLUGIN_URL,
    authors = [BuildConstants.PLUGIN_AUTHORS]
)
class Asynchat @Inject constructor(
    val proxyServer: ProxyServer,
    @param:DataDirectory val dataDir: Path,
    val logger: Logger
) {
    lateinit var config: Config
    lateinit var commands: Commands

    init {
        instance = this
    }

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent?) {
        this.commands = Commands(this.proxyServer)
        this.config = Config(this.dataDir)
        logger.info(Utils.getTextComponent("asynchat.loaded").content())
    }

    @Subscribe
    fun onProxyReload(event: ProxyReloadEvent?) {
        this.onProxyInitialization(null)
    }

    @Subscribe
    fun onPlayerSettingsChangedEvent(event: PlayerSettingsChangedEvent) {
        event.playerSettings.locale
        //TODO
    }

    private fun register(x: Any) {
        // 将对象注册到事件管理器，使其可以使用@Subscribe注解
        proxyServer.eventManager.register(this, x)
    }

    companion object {
        @JvmField
        var instance: Asynchat? = null
    }
}

val <T : Any> T.logger: Logger
    get() = Asynchat.instance!!.logger