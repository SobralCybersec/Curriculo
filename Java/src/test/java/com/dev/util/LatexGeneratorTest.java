package com.dev.util;

import org.junit.jupiter.api.Test;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void generatesNoEntriesForAnEmptyEducationTable() {
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Degree", "School", "Location", "Dates", "Description"}, 0);

        String tex = LatexGenerator.generateEducation(new JTable(model));

        assertTrue(tex.contains("\\begin{cventries}"));
        assertFalse(tex.contains("\\cventry"));
    }

    @Test
    void acceptsEntryTablesWithoutDescriptionColumn() {
        DefaultTableModel model = new DefaultTableModel(
            new Object[][]{{"Engineer", "Example", "Remote", "2024"}},
            new String[]{"Title", "Organization", "Location", "Dates"});

        String tex = LatexGenerator.generateExperience(new JTable(model));

        assertTrue(tex.contains("{Engineer} % Job title"));
        assertFalse(tex.contains("\\item"));
    }

    @Test
    void generatesEveryEducationEntryAndBullet() {
        DefaultTableModel model = new DefaultTableModel(
            new Object[][]{{"BSc", "School A", "City A", "2020", "First bullet"},
                {"MSc", "School B", "City B", "2024", "Second bullet\nThird bullet"}},
            new String[]{"Degree", "School", "Location", "Dates", "Description"});

        String tex = LatexGenerator.generateEducation(new JTable(model));

        assertEquals(2, tex.split("\\\\cventry", -1).length - 1);
        assertTrue(tex.contains("{First bullet}"));
        assertTrue(tex.contains("{Third bullet}"));
    }

    @Test
    void doesNotRenderNullSummaryText() {
        DefaultTableModel model = new DefaultTableModel(new Object[][]{{null}}, new String[]{"Summary"});

        assertFalse(LatexGenerator.generateSummary(model).contains("null"));
    }

    @Test
    void generatesEmptyShellsForExperienceAndSkills() {
        DefaultTableModel entries = new DefaultTableModel(
            new String[]{"Title", "Organization", "Location", "Dates", "Description"}, 0);
        DefaultTableModel skills = new DefaultTableModel(new String[]{"Category", "Skills"}, 0);

        String experience = LatexGenerator.generateExperience(new JTable(entries));
        String skillsTex = LatexGenerator.generateSkills(skills);

        assertTrue(experience.contains("\\begin{cventries}"));
        assertTrue(experience.contains("\\end{cventries}"));
        assertFalse(experience.contains("\\cventry"));
        assertTrue(skillsTex.contains("\\begin{cvskills}"));
        assertTrue(skillsTex.contains("\\end{cvskills}"));
        assertFalse(skillsTex.contains("\\cvskill"));
    }
}
