package com.dev.view;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ResumeEditorViewLayoutTest {

    @Test
    void keepsBothEditorAndPreviewReachableAtCommonLogicalWidths() {
        int compact = ResumeEditorView.initialDividerLocation(640);
        int laptop = ResumeEditorView.initialDividerLocation(1024);
        int desktop = ResumeEditorView.initialDividerLocation(1600);

        assertTrue(compact > 0 && compact < 412);
        assertTrue(laptop > compact && laptop < 796);
        assertTrue(desktop > laptop && desktop < 1372);
    }
}
