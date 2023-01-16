package com.example.oath2springsecurity5;

import java.util.HashMap;
import java.util.Map;

public enum Provider {
    SYSTEM("system"),
    GOOGLE("google"),
    FACEBOOK("facebook");

    private final String providerName;

    Provider(String provider) {
        this.providerName = provider;
    }

    public String getProviderName() {
        return this.providerName;
    }

    public static Provider getProvider(String providerName) {
        return Provider.valueOf(providerName.toUpperCase());
    }
}
