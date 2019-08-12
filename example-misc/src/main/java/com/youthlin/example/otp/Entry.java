/*
 * Copyright (C) 2017-2018 Jakob Nixdorf
 * Copyright (C) 2015 Bruno Bierbaumer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
//package org.shadowice.flocke.andotp.Database;
package com.youthlin.example.otp;

import com.google.common.base.Splitter;
import lombok.Data;
import org.apache.commons.codec.binary.Base32;

import java.net.URI;
import java.util.Map;

/**
 * @author youthlin.chen
 * @date 2019-08-12 15:04
 */
@Data
@SuppressWarnings("UnstableApiUsage")
class Entry {
    public enum OTPType {
        //
        TOTP, HOTP, STEAM
    }

    private static final Splitter.MapSplitter MAP_SPLITTER = Splitter.on('&').trimResults().omitEmptyStrings()
            .withKeyValueSeparator('=');
    private static final String SCHEME = "otpauth";
    private String url;
    private OTPType type;
    private int period = TokenCalculator.TOTP_DEFAULT_PERIOD;
    private int digits = TokenCalculator.TOTP_DEFAULT_DIGITS;
    private TokenCalculator.HashAlgorithm algorithm = TokenCalculator.DEFAULT_ALGORITHM;
    private byte[] secret;
    private long counter;
    private String currentToken;
    private long lastUpdate = 0;

    static Entry of(String url) {
        try {
            return new Entry(url);
        } catch (Exception e) {
            throw new IllegalArgumentException("Bad url:" + url, e);
        }
    }

    private Entry(String url) throws Exception {
        this.url = url;
        URI uri = URI.create(url);

        if (!SCHEME.equals(uri.getScheme())) {
            throw new Exception("Invalid Protocol");
        }

        if (OTPType.TOTP.name().toLowerCase().equals(uri.getHost())) {
            type = OTPType.TOTP;
        } else if (OTPType.HOTP.name().toLowerCase().equals(uri.getHost())) {
            type = OTPType.HOTP;
        } else {
            throw new Exception("unknown otp type");
        }

        Map<String, String> params = MAP_SPLITTER.split(uri.getQuery());
        String secret = params.get("secret");

        String counter = params.get("counter");
        String period = params.get("period");
        String digits = params.get("digits");
        String algorithm = params.get("algorithm");

        if (type == OTPType.HOTP) {
            if (counter != null) {
                this.counter = Long.parseLong(counter);
            } else {
                throw new Exception("missing counter for HOTP");
            }
        } else {
            if (period != null) {
                this.period = Integer.parseInt(period);
            }
        }

        this.secret = new Base32().decode(secret.toUpperCase());

        if (digits != null) {
            this.digits = Integer.parseInt(digits);
        }

        if (algorithm != null) {
            this.algorithm = TokenCalculator.HashAlgorithm.valueOf(algorithm.toUpperCase());
        }
    }

    void updateToken() {
        if (type == OTPType.TOTP || type == OTPType.STEAM) {
            long time = System.currentTimeMillis() / 1000;
            long counter = time / this.period;

            if (counter > lastUpdate) {
                if (type == OTPType.TOTP) {
                    currentToken = TokenCalculator.TOTP_RFC6238(secret, period, digits, algorithm);
                } else if (type == OTPType.STEAM) {
                    currentToken = TokenCalculator.TOTP_Steam(secret, period, digits, algorithm);
                }
                lastUpdate = counter;
            }
        } else if (type == OTPType.HOTP) {
            currentToken = TokenCalculator.HOTP(secret, counter, digits, algorithm);
        }
    }

}
