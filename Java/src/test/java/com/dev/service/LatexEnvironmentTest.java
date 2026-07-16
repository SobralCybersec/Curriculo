package com.dev.service;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LatexEnvironmentTest {

    @Test
    void enablesOnDemandPackagesOnlyForMiKTeX() {
        List<String> command = LatexEnvironment.compilationCommand("MiKTeX-xetex 24.1");

        assertTrue(command.contains("--enable-installer"));
        assertTrue(command.contains("--record-package-usages=resume.packages.txt"));
        assertTrue(command.contains("-no-shell-escape"));
    }

    @Test
    void leavesTexLivePackageManagementUntouched() {
        List<String> command = LatexEnvironment.compilationCommand("XeTeX 3.141592653-2.6-0.999995 (TeX Live)");

        assertFalse(command.contains("--enable-installer"));
        assertFalse(command.contains("--record-package-usages=resume.packages.txt"));
    }

    @Test
    void givesPlatformSpecificMissingCompilerGuidance() {
        assertTrue(LatexEnvironment.missingCompilerMessage("Windows 11").contains("MiKTeX"));
        assertTrue(LatexEnvironment.missingCompilerMessage("Linux").contains("TeX Live"));
        assertTrue(LatexEnvironment.installationUri("Windows 11").toString().startsWith("https://"));
        assertTrue(LatexEnvironment.installationUri("Linux").toString().startsWith("https://"));
    }

    @Test
    void identifiesMiKTeXWithoutCaseSensitivity() {
        assertTrue(LatexEnvironment.isMiKTeX("MiKtEx 24"));
        assertFalse(LatexEnvironment.isMiKTeX("TeX Live 2025"));
    }

    @Test
    void buildsStableMiKTeXCommandOrder() {
        assertEquals(List.of("xelatex", "-no-shell-escape", "-interaction=nonstopmode", "-halt-on-error",
            "--enable-installer", "--record-package-usages=resume.packages.txt", "resume.tex"),
            LatexEnvironment.compilationCommand("MiKTeX"));
    }

    @Test
    void keepsMissingCompilerCauseAndInstallationUri() {
        IOException cause = new IOException("missing executable");
        MissingXeLaTeXException exception = new MissingXeLaTeXException("Windows 11", cause);

        assertSame(cause, exception.getCause());
        assertEquals(LatexEnvironment.installationUri("Windows 11"), exception.getInstallationUri());
    }

    @Test
    void usesExactOfficialInstallationUris() {
        assertEquals("https://miktex.org/download", LatexEnvironment.installationUri("Windows").toString());
        assertEquals("https://tug.org/texlive/acquire.html", LatexEnvironment.installationUri("Linux").toString());
    }

    @Test
    void keepsTexLiveCommandFreeOfMiKTeXFlags() {
        List<String> command = LatexEnvironment.compilationCommand("XeTeX (TeX Live 2025)");

        assertEquals(List.of("xelatex", "-no-shell-escape", "-interaction=nonstopmode", "-halt-on-error", "resume.tex"), command);
    }
}
