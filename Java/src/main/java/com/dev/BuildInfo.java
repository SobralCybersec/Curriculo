package com.dev;

public final class BuildInfo {
    private static final String DEFAULT_VERSION = "1.3.0-SNAPSHOT";

    private BuildInfo() {
    }

    public static String version() {
        String version = BuildInfo.class.getPackage().getImplementationVersion();
        return version == null || version.isBlank() ? DEFAULT_VERSION : version;
    }
}
