/*
 * MIT License
 *
 * Copyright (c) 2025 Kinhiro Zane and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.kinhiro.rgkcn.util;

import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.DyeColor;
import org.jspecify.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;

public enum MessageFormatting {
    WHITE('0', (style, _) -> style.withColor(DyeColor.WHITE.getTextColor())),
    ORANGE('1', (style, _) -> style.withColor(DyeColor.ORANGE.getTextColor())),
    MAGENTA('2',  (style, _) -> style.withColor(DyeColor.MAGENTA.getTextColor())),
    LIGHT_BLUE('3', (style, _) -> style.withColor(DyeColor.LIGHT_BLUE.getTextColor())),
    YELLOW('4',  (style, _) -> style.withColor(DyeColor.YELLOW.getTextColor())),
    LIME('5',  (style, _) -> style.withColor(DyeColor.LIME.getTextColor())),
    PINK('6',  (style, _) -> style.withColor(DyeColor.PINK.getTextColor())),
    GRAY('7',  (style, _) -> style.withColor(DyeColor.GRAY.getTextColor())),
    LIGHT_GRAY('8', (style, _) -> style.withColor(DyeColor.LIGHT_GRAY.getTextColor())),
    CYAN('9', (style, _) -> style.withColor(DyeColor.CYAN.getTextColor())),
    PURPLE('a', (style, _) -> style.withColor(DyeColor.PURPLE.getTextColor())),
    BLUE('b', (style, _) -> style.withColor(DyeColor.BLUE.getTextColor())),
    BROWN('c', (style, _) -> style.withColor(DyeColor.BROWN.getTextColor())),
    GREEN('d', (style, _) -> style.withColor(DyeColor.GREEN.getTextColor())),
    RED('e', (style, _) -> style.withColor(DyeColor.RED.getTextColor())),
    BLACK('f', (style, _) -> style.withColor(DyeColor.BLACK.getTextColor())),
    COLOR('#', (style, format) -> {
        try {
            final var color = TextColor.parseColor("#" + format).getOrThrow(RuntimeException::new);
            return style.withColor(color);
        } catch (final RuntimeException _) {
            return style;
        }
    }, format -> {
        final var index = format.indexOf('#');
        return index < 0 ? null : format.substring(index + 1);
    }),
    ITALIC('i', (style, _) -> style.withItalic(true)),
    BOLD('l',  (style, _) -> style.withBold(true)),
    OBFUSCATED('o', (style, _) -> style.withObfuscated(true)),
    RESET('r', (style, _) -> Style.EMPTY),
    STRIKETHROUGH('s', (style, _) -> style.withStrikethrough(true)),
    UNDERLINE('u', (style, _) -> style.withUnderlined(true));

    public final char code;
    public final BiFunction<Style, String, Style> applier;
    public final Function<String, String> container;

    MessageFormatting(final char code, final BiFunction<Style, String, Style> applier) {
        this(code, applier, format -> format.indexOf(code) >= 0 ? Character.toString(code) : null);
    }

    MessageFormatting(
        final char code,
        final BiFunction<Style, String, Style> applier,
        final Function<String, @Nullable String> container
    ) {
        this.code = code;
        this.applier = applier;
        this.container = container;
    }

    public Style apply(final Style old, String format) {
        if ((format = container.apply(format)) != null) return applier.apply(old, format);
        return old;
    }
}
