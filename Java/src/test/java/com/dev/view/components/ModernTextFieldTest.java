package com.dev.view.components;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModernTextFieldTest {

    @Test
    void validatesNonBlankTextWithoutStartingAnAnimation() {
        ModernTextField field = new ModernTextField("Name");
        field.setText("Ana");

        assertTrue(field.validateNotEmpty());
    }

    @Test
    void createsTransparentFieldsWithConfiguredText() {
        ModernTextField field = new ModernTextField();
        field.setPlaceholder("Email");
        field.setText("ana@example.com");

        assertFalse(field.isOpaque());
        assertTrue(field.validateNotEmpty());
    }
}
