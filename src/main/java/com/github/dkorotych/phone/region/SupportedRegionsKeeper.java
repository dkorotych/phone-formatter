package com.github.dkorotych.phone.region;

import com.github.dkorotych.phone.region.domain.Country;
import com.github.dkorotych.phone.region.domain.Region;
import com.github.dkorotych.phone.utils.Utilities;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.function.Supplier;

@Singleton
@RequiredArgsConstructor
public class SupportedRegionsKeeper extends AbstractRegionMapper<Region> {
    private final FlagsKeeper flagsKeeper;
    private final LocalesKeeper localesKeeper;

    @Override
    protected Supplier<Map<String, Region>> getSupplier() {
        return () -> {
            final Map<String, Region> map = new TreeMap<>();
            final PhoneNumberUtil numberUtil = PhoneNumberUtil.getInstance();
            for (String region : numberUtil.getSupportedRegions()) {
                final Region returnValue = new Region();
                returnValue.setName(region);
                returnValue.setCode(numberUtil.getCountryCodeForRegion(region));
                final Country country = new Country();
                country.setFlag(flagsKeeper.get(region));
                returnValue.setCountry(country);
                map.put(region, returnValue);
            }
            return map;
        };
    }

    public Region get(String region, Locale locale) {
        return copyAndAppend(get(region), locale);
    }

    public Region copyAndAppend(Region region, Locale locale) {
        if (Objects.nonNull(region)) {
            final Region response = Region.copy(region);
            final String country = Optional.ofNullable(localesKeeper.get(region.getName())).
                    map(item -> {
                        final Locale outputLocale = Utilities.getSupportedLocale(locale);
                        return item.getDisplayCountry(outputLocale);
                    }).
                    orElse(null);
            response.getCountry().setName(country);
            return response;
        }
        return null;
    }
}
