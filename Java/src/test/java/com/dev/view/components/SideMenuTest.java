package com.dev.view.components;

import org.junit.jupiter.api.Test;

import javax.swing.AbstractButton;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SideMenuTest {

    @Test
    void selectsOneKeyboardAccessibleDestinationAndRunsItsAction() throws Exception {
        AtomicInteger calls = new AtomicInteger();
        SideMenu menu = onEdt(() -> {
            SideMenu created = new SideMenu();
            created.addMenuItem("Dados Pessoais", "person", "Dados", () -> { });
            created.addMenuItem("Resumo", "document", "Resumo profissional", calls::incrementAndGet);
            return created;
        });

        List<JToggleButton> destinations = onEdt(() -> findDestinations(menu));
        AbstractButton toggle = onEdt(() -> findButton(menu, "Alternar navegação"));
        assertEquals(2, destinations.size());
        assertTrue(destinations.get(0).isSelected());
        assertNotNull(destinations.get(1).getIcon());
        assertEquals("Resumo", destinations.get(1).getAccessibleContext().getAccessibleName());
        assertNotNull(toggle);
        assertTrue(toggle.isFocusPainted());

        onEdt(() -> {
            destinations.get(1).doClick();
            return null;
        });

        assertTrue(destinations.get(1).isSelected());
        assertEquals(1, calls.get());
    }

    @Test
    void supportsEveryApplicationNavigationIcon() throws Exception {
        SideMenu menu = onEdt(() -> {
            SideMenu created = new SideMenu();
            for (String type : List.of("person", "document", "education", "briefcase",
                    "lightning", "settings", "code", "info")) {
                created.addMenuItem(type, type, type, () -> { });
            }
            return created;
        });

        List<JToggleButton> destinations = onEdt(() -> findDestinations(menu));
        assertEquals(8, destinations.size());
        assertTrue(destinations.stream().allMatch(AbstractButton::isFocusable));
    }

    private static List<JToggleButton> findDestinations(Container root) {
        List<JToggleButton> result = new ArrayList<>();
        for (Component component : root.getComponents()) {
            if (component instanceof JToggleButton button) result.add(button);
            if (component instanceof Container child) result.addAll(findDestinations(child));
        }
        return result;
    }

    private static AbstractButton findButton(Container root, String accessibleName) {
        for (Component component : root.getComponents()) {
            if (component instanceof AbstractButton button
                    && accessibleName.equals(button.getAccessibleContext().getAccessibleName())) {
                return button;
            }
            if (component instanceof Container child) {
                AbstractButton result = findButton(child, accessibleName);
                if (result != null) return result;
            }
        }
        return null;
    }

    private static <T> T onEdt(java.util.concurrent.Callable<T> action) throws Exception {
        if (SwingUtilities.isEventDispatchThread()) return action.call();
        FutureTask<T> task = new FutureTask<>(action);
        SwingUtilities.invokeAndWait(task);
        return task.get();
    }
}
