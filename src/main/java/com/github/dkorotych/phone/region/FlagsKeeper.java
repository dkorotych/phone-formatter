package com.github.dkorotych.phone.region;

import com.github.dkorotych.phone.region.domain.Flag;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Singleton
@RequiredArgsConstructor
public class FlagsKeeper extends AbstractRegionMapper<Flag> {
    @Override
    protected Supplier<Map<String, Flag>> getSupplier() {
        return () -> {
            final Set<String> regions = PhoneNumberUtil.getInstance().getSupportedRegions();
            final Map<String, Flag> map = new HashMap<>(regions.size());
            for (final String region : regions) {
                final int[] codePoints = region.codePoints().
                        map(operand -> 127397 + operand).
                        toArray();
                final String code = new String(codePoints, 0, codePoints.length);
                final String htmlCode = Arrays.stream(codePoints).
                        mapToObj("&#%d;"::formatted).
                        collect(Collectors.joining());
                final Flag flag = new Flag(code, htmlCode);
                map.put(region, flag);
            }
            return map;
        };
    }
}
