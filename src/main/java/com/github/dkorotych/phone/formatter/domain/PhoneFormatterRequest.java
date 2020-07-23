package com.github.dkorotych.phone.formatter.domain;

import lombok.Data;

@Data
public class PhoneFormatterRequest {
    /**
     * Phone number
     */
    private String phoneNumber;
    private String country;
    /**
     * Show only valid phones. If this parameter set to {@code false}
     */
    private boolean onlyValid = true;
}
