package com.github.dkorotych.phone.region;

import com.github.dkorotych.phone.region.domain.Flag;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import io.micronaut.core.util.StringUtils;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Singleton
@RequiredArgsConstructor
public class FlagsKeeper extends AbstractRegionMapper<Flag> {
    @Override
    protected Supplier<Map<String, Flag>> getSupplier() {
        return () -> {
            final Map<String, Emoji> flags = EmojiManager.getForTag("flag").stream().
                    filter(emoji -> emoji.getAliases().stream().
                            findFirst().
                            map(StringUtils::hasText).
                            isPresent()).
                    collect(Collectors.toMap(emoji -> {
                        String region = emoji.getAliases().getFirst().toUpperCase(Locale.ENGLISH);
                        if (region.endsWith("_FLAG")) {
                            region = region.substring(0, region.indexOf("_FLAG"));
                        }
                        return region;
                    }, Function.identity()));
            final Set<String> regions = PhoneNumberUtil.getInstance().getSupportedRegions();
            final Map<String, Flag> map = new HashMap<>(regions.size());
            for (final String region : regions) {
                final Emoji emoji = flags.get(region);
                if (emoji != null) {
                    final String code = emoji.getUnicode();
                    final String htmlCode = emoji.getHtmlDecimal();
                    final Flag flag = new Flag(code, htmlCode);
                    map.put(region, flag);
                }
            }
            return map;
        };
    }
}
