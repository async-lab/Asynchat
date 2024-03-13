package club.asyncraft.asynchat.util

import club.asyncraft.asynchat.Asynchat
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.translation.GlobalTranslator
import java.util.*

object Utils {
    fun getTextComponent(key: String): TextComponent {
        return GlobalTranslator.render(Component.translatable(key), Asynchat.instance!!.config.locale) as TextComponent
    }

    fun getTextComponent(key: String, locale: Locale?): TextComponent {
        return GlobalTranslator.render(Component.translatable(key), locale!!) as TextComponent
    }
}