package com.dev.util;

import org.junit.jupiter.api.Test;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LatexGeneratorTest {

    @Test
    void generatesSummaryFromFirstRowOnly() {
        DefaultTableModel model = new DefaultTableModel(new Object[][]{{"First summary"}, {"Ignored summary"}}, new String[]{"Summary"});

        String tex = LatexGenerator.generateSummary(model);

        assertTrue(tex.contains("First summary"));
        assertFalse(tex.contains("Ignored summary"));
    }

    @Test
    void generatesSummaryShellForAnEmptyModel() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Summary"}, 0);

        String tex = LatexGenerator.generateSummary(model);

        assertTrue(tex.contains("\\begin{cvparagraph}"));
        assertTrue(tex.contains("\\end{cvparagraph}"));
    }

    @Test
    void trimsManualSummaryTextAndSkipsBlankText() {
        assertTrue(LatexGenerator.generateSummaryFromText("  Hello world  ").contains("Hello world"));
        assertFalse(LatexGenerator.generateSummaryFromText("  ").contains("Hello world"));
    }

    @Test
    void generatesSkillEntriesForEveryRow() {
        DefaultTableModel model = new DefaultTableModel(new Object[][]{{"Backend", "Java"}, {"Frontend", "React"}}, new String[]{"Category", "Skills"});

        String tex = LatexGenerator.generateSkills(model);

        assertTrue(tex.contains("{Backend}"));
        assertTrue(tex.contains("{React}"));
    }

    @Test
    void rendersNullCellsAsEmptyLatexArguments() {
        DefaultTableModel skills = new DefaultTableModel(new Object[][]{{null, null}}, new String[]{"Category", "Skills"});
        DefaultTableModel education = new DefaultTableModel(
            new Object[][]{{null, null, null, null, null}},
            new String[]{"Degree", "School", "Location", "Dates", "Description"});

        String skillsTex = LatexGenerator.generateSkills(skills);
        String educationTex = LatexGenerator.generateEducation(new JTable(education));

        assertFalse(skillsTex.contains("null"));
        assertFalse(educationTex.contains("null"));
    }

    @Test
    void omitsEmptyDescriptionBullets() {
        DefaultTableModel model = new DefaultTableModel(
            new Object[][]{{"Degree", "School", "City", "2024", "  \n  "}},
            new String[]{"Degree", "School", "Location", "Dates", "Description"});

        String tex = LatexGenerator.generateEducation(new JTable(model));

        assertFalse(tex.contains("\\item"));
    }
}
