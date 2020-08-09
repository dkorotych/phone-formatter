package com.github.dkorotych.phone.region.domain;

import lombok.Data;

@Data
public class Region {
    private String name;
    private int code;
    private Country country;

    public static Region copy(Region from) {
        if (from != null) {
            final Region to = new Region();
            to.setCode(from.code);
            to.setName(from.name);
            to.setCountry(Country.copy(from.country));
            return to;
        } else {
            return null;
        }
    }
}
