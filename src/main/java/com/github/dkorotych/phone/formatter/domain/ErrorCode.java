package com.github.dkorotych.phone.formatter.domain;

public enum ErrorCode {
    /**
     * Number has more digits than any valid phone number could have
     */
    TOO_LONG_NUMBER,
    /**
     * Country calling code not found or was not recognised
     */
    INVALID_COUNTRY_CODE,
    /**
     * The input string did not seem to be a phone number
     */
    NOT_A_NUMBER,
    /**
     * Phone number has an International Direct Dial prefix, but after this prefix was not long enough to be a valid phone number
     */
    TOO_SHORT_NUMBER_AFTER_IDD,
    /**
     * The entered string is too short to be a national phone number for selected country
     */
    TOO_SHORT_NUMBER,
    /**
     * Unknown error
     */
    UNKNOWN
}
