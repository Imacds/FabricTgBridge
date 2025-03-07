@file:OptIn(DelicateCoroutinesApi::class)

package cuteneko.tgbridge

import cuteneko.tgbridge.Bridge.Companion.LOGGER
import cuteneko.tgbridge.tgbot.User
import kotlinx.coroutines.DelicateCoroutinesApi
import net.minecraft.text.LiteralTextContent
import net.minecraft.text.Text
import net.minecraft.text.TranslatableTextContent
import java.util.*

fun Text?.toPlainString(formatted: Boolean = true): String {
    if (this == null) {
        return ""
    }
    var result = ""
    if(siblings.size == 0){
        result = when (val content = content) {
            is LiteralTextContent -> {
                content.string.escapeHTML()
            }

            is TranslatableTextContent -> {
                val lang = Bridge.LANG
                if(!lang.containsKey(content.key)) {
                    val guess = content.key.split('.').last().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                    LOGGER.warn("Key '${content.key}' is missing from LANG - using '${guess}'.")
                    return guess
                }
                val args = content.args.map {
                    if(it is Text) it.toPlainString()
                    else it.toString()
                }.toTypedArray()
                String.format(lang[content.key]!!.escapeHTML(), *args)
            }

            else -> {
                string.escapeHTML()
            }
        }
    }
    else {
        siblings.forEach {
            result += it.toPlainString()
        }
    }

    if(!formatted) return result
    var format = ""
    if (style.isBold) format += "<b>"
    if (style.isItalic) format += "<i>"
    if (style.isUnderlined) format += "<u>"
    if (style.isStrikethrough) format += "<s>"
    if (style.isObfuscated) format += "<tg-spoiler>"
    format += "%s"
    if (style.isObfuscated) format += "</tg-spoiler>"
    if (style.isStrikethrough) format += "</s>"
    if (style.isUnderlined) format += "</u>"
    if (style.isItalic) format += "</i>"
    if (style.isBold) format += "</b>"

    return String.format(format, result)
}

fun String.escapeHTML(): String = this
    .replace("&", "&amp;")
    .replace(">", "&gt;")
    .replace("<", "&lt;")

fun User.rawUserMention(): String = firstName + (lastName?.let { " $it" } ?: "")