package com.github.dkorotych.phone.region.domain;

import lombok.Data;

@Data
public class Country {
    private String name;
    private Flag flag;

    public static Country copy(Country from) {
        if (from != null) {
            final Country to = new Country();
            to.setName(from.name);
            to.setFlag(from.flag);
            return to;
        } else {
            return null;
        }
    }
}
