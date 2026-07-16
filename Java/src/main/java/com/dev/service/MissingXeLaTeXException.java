package com.dev.service;

import java.io.IOException;
import java.net.URI;

public final class MissingXeLaTeXException extends IOException {
    private final URI installationUri;

    MissingXeLaTeXException(String operatingSystem, IOException cause) {
        super(LatexEnvironment.missingCompilerMessage(operatingSystem), cause);
        installationUri = LatexEnvironment.installationUri(operatingSystem);
    }

    public URI getInstallationUri() {
        return installationUri;
    }
}
