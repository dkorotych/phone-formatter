package com.github.dkorotych.phone.region;

import com.github.dkorotych.phone.region.domain.Flag;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import emoji4j.EmojiUtils;
import io.micronaut.core.util.StringUtils;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@Singleton
public class FlagsKeeper extends AbstractRegionMapper<Flag> {
    @Inject
    private LocalesKeeper localesKeeper;

    @Override
    protected Supplier<Map<String, Flag>> getSupplier() {
        return () -> {
            final Map<String, String> replaceMap = new HashMap<>() {{
                put("åland_islands", "aland_islands");
                put("st_barthélemy", "st_barthelemy");
                put("côte_d’ivoire", "cote_divoire");
                put("china", "cn");
                put("curaçao", "curacao");
                put("czechia", "czech_republic");
                put("germany", "de");
                put("spain", "es");
                put("france", "fr");
                put("united_kingdom", "gb");
                put("italy", "it");
                put("south_korea", "kr");
                put("réunion", "reunion");
                put("russia", "ru");
                put("são_tomé_príncipe", "sao_tome_principe");
                put("united_states", "us");
                put("u.s._virgin_islands", "us_virgin_islands");
            }};
            final Map<String, Flag> map = new HashMap<>();
            final PhoneNumberUtil numberUtil = PhoneNumberUtil.getInstance();
            for (String region : numberUtil.getSupportedRegions()) {
                Optional.ofNullable(localesKeeper.get(region)).
                        map(tmp -> tmp.getDisplayCountry(Locale.ENGLISH)).
                        filter(StringUtils::hasText).
                        map(country -> country.toLowerCase(Locale.ENGLISH)).
                        map(country -> country.replace(" & ", "_")).
                        map(country -> country.replace(" - ", "_")).
                        map(country -> country.replace(' ', '_')).
                        map(country -> country.replace('-', '_')).
                        map(country -> country.replace("st._", "st_")).
                        map(country -> country.replace("_sar_china", "")).
                        map(country -> country.replaceAll("_\\(\\w+\\)", "")).
                        map(country -> {
                            if (replaceMap.containsKey(country)) {
                                return replaceMap.get(country);
                            }
                            return country;
                        }).
                        map(country -> ':' + country + ':').
                        map(EmojiUtils::getEmoji).
                        ifPresent(emoji -> {
                            final Flag flag = new Flag();
                            flag.setCode(emoji.getEmoji());
                            flag.setHtmlCode(emoji.getDecimalHtml());
                            map.put(region, flag);
                        });
            }
            return map;
        };
    }
}
