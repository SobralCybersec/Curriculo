package com.dev.service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
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
}
