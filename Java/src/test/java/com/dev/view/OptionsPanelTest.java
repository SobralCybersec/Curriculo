package com.dev.view;

import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OptionsPanelTest {

    @Test
    void storesQuoteAndClearsDisabledQuote() throws Exception {
        OptionsPanel panel = onEdt(OptionsPanel::new);

        onEdt(() -> {
            panel.setQuote("Keep building");
            return null;
        });
        assertEquals("Keep building", onEdt(panel::getQuote));

        onEdt(() -> {
            panel.setQuote(null);
            return null;
        });
        assertNull(onEdt(panel::getQuote));
    }

    @Test
    void mapsPhotoFormatsOnlyWhenPhotoIsEnabled() throws Exception {
        OptionsPanel panel = onEdt(OptionsPanel::new);

        onEdt(() -> {
            panel.setPhotoFormat("right");
            panel.setPhotoEnabled(true);
            return null;
        });
        assertEquals("right", onEdt(panel::getPhotoFormat));

        onEdt(() -> {
            panel.setPhotoEnabled(false);
            return null;
        });
        assertNull(onEdt(panel::getPhotoFormat));
    }

    @Test
    void acceptsValidThemeColorsAndTracksEnabledSocials() throws Exception {
        OptionsPanel panel = onEdt(OptionsPanel::new);

        onEdt(() -> {
            panel.setThemeColor("aa11bb");
            panel.setSocial("gitlab", "ana-dev");
            panel.setSocial("twitter", "");
            return null;
        });

        assertEquals("AA11BB", onEdt(panel::getThemeColor));
        assertEquals(Map.of("gitlab", "ana-dev"), onEdt(panel::getEnabledSocials));
    }

    private static <T> T onEdt(Callable<T> action) throws Exception {
        if (SwingUtilities.isEventDispatchThread()) return action.call();
        FutureTask<T> task = new FutureTask<>(action);
        SwingUtilities.invokeAndWait(task);
        return task.get();
    }
}
