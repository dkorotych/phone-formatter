package com.github.dkorotych.phone.region;

import io.micronaut.core.async.SupplierUtil;
import io.micronaut.core.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@Slf4j
public abstract class AbstractRegionMapper<T> {
    private final Supplier<Map<String, T>> supplier;

    protected AbstractRegionMapper() {
        supplier = SupplierUtil.memoized(getSupplier());
    }

    public T get(String region) {
        if (StringUtils.hasText(region)) {
            final T value = getMapper().get(region);
            if (Objects.isNull(value)) {
                log.warn("Unsupported region: {}", region);
            }
            return value;
        }
        return null;
    }

    protected abstract Supplier<Map<String, T>> getSupplier();

    Map<String, T> getMapper() {
        return supplier.get();
    }
}
