package com.dev.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.net.URI;

public final class LatexEnvironment {
    private LatexEnvironment() {
    }

    static boolean isMiKTeX(String compilerVersion) {
        return compilerVersion.toLowerCase(Locale.ROOT).contains("miktex");
    }

    static List<String> compilationCommand(String compilerVersion) {
        List<String> command = new ArrayList<>(List.of(
            "xelatex", "-no-shell-escape", "-interaction=nonstopmode", "-halt-on-error"
        ));
        if (isMiKTeX(compilerVersion)) {
            command.add("--enable-installer");
            command.add("--record-package-usages=resume.packages.txt");
        }
        command.add("resume.tex");
        return command;
    }

    public static URI installationUri(String operatingSystem) {
        if (operatingSystem.toLowerCase(Locale.ROOT).contains("win")) {
            return URI.create("https://miktex.org/download");
        }
        return URI.create("https://tug.org/texlive/acquire.html");
    }

    static String missingCompilerMessage(String operatingSystem) {
        if (operatingSystem.toLowerCase(Locale.ROOT).contains("win")) {
            return "XeLaTeX não foi encontrado. Instale o MiKTeX Basic para o usuário atual em "
                + installationUri(operatingSystem) + " e tente novamente.";
        }
        return "XeLaTeX não foi encontrado. Instale a distribuição TeX Live da sua distribuição Linux "
            + "com suporte a XeLaTeX e tente novamente: " + installationUri(operatingSystem);
    }
}
