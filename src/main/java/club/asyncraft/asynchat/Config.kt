package club.asyncraft.asynchat

import club.asyncraft.asynchat.util.Reference
import net.kyori.adventure.key.Key
import net.kyori.adventure.translation.GlobalTranslator
import net.kyori.adventure.translation.TranslationRegistry
import net.kyori.adventure.util.UTF8ResourceBundleControl
import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.ConfigurateException
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

class Config(
    private val dataDir: Path
) {

    private val rootNodeMap: MutableMap<String, CommentedConfigurationNode> = HashMap()

    lateinit var locale: Locale

    init {
        try {
            this.loadFile("config.yml")
            this.locale = Locale.forLanguageTag(getRootNode("config.yml").orElseThrow().node("lang").getString("en_US"))

            val registry = TranslationRegistry.create(Key.key("asynchat"))
            GlobalTranslator.translator().removeSource(registry)

            Reference.locales.forEach {
                registry.registerAll(
                    it,
                    ResourceBundle.getBundle("club.asyncraft.asynchat.Bundle", it, UTF8ResourceBundleControl.get()),
                    true
                )
            }
            GlobalTranslator.translator().addSource(registry)
        } catch (e: Exception) {
            logger.error("Error", e)
        }
    }

    fun getRootNode(fileName: String): Optional<CommentedConfigurationNode> {
        return Optional.ofNullable(rootNodeMap[fileName])
    }

    @Throws(ConfigurateException::class)
    private fun loadFile(fileName: String) {
        val dataDirFile = dataDir.toFile().also { it.mkdir() }

        val dataFile = File(dataDirFile, fileName)
        if (!dataFile.exists()) {
            try {
                val `in` = this.javaClass.getResourceAsStream("/$fileName")
                if (`in` != null) {
                    Files.copy(`in`, dataFile.toPath())
                }
            } catch (e: IOException) {
                throw RuntimeException("ERROR: Can't write default configuration file (permissions/filesystem error?)")
            }
        }

        rootNodeMap[fileName] = YamlConfigurationLoader.builder().path(dataFile.toPath()).build().load()
    }
}