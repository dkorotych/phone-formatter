package com.github.dkorotych.phone.region;

import com.github.dkorotych.phone.utils.Utilities;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import jakarta.inject.Singleton;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

@Singleton
public class LocalesKeeper extends AbstractRegionMapper<Locale> {
    @Override
    protected Supplier<Map<String, Locale>> getSupplier() {
        return () -> {
            final Map<String, Locale> map = new HashMap<>();
            final PhoneNumberUtil numberUtil = PhoneNumberUtil.getInstance();
            for (String region : numberUtil.getSupportedRegions()) {
                Utilities.createOptionalLocaleForRegion(region).
                        ifPresent(locale -> map.put(region, locale));
            }
            return map;
        };
    }
}
