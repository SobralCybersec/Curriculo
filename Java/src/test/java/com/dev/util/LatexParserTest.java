package com.dev.util;

import org.junit.jupiter.api.Test;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LatexParserTest {

    @Test
    void preservesNestedBracesAcrossEducationRoundTrip() {
        DefaultTableModel source = new DefaultTableModel(
            new Object[][]{{"\\textbf{Bachelor}", "\\href{https://example.com}{Example University}", "São Paulo", "2020--2024", "Built {reliable} systems"}},
            new String[]{"Degree", "Institution", "Location", "Dates", "Description"});
        DefaultTableModel parsed = new DefaultTableModel(new String[]{"Degree", "Institution", "Location", "Dates", "Description"}, 0);

        LatexParser.parseEducation(LatexGenerator.generateEducation(new JTable(source)), parsed);

        for (int column = 0; column < source.getColumnCount(); column++) {
            assertEquals(source.getValueAt(0, column), parsed.getValueAt(0, column));
        }
    }

    @Test
    void parsesSummaryTextImmediatelyAfterOpeningMarker() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Summary"}, 0);

        LatexParser.parseSummary("\\begin{cvparagraph}Hello\\end{cvparagraph}", model);

        assertEquals("Hello", model.getValueAt(0, 0));
    }

    @Test
    void extractsBalancedPositionValueAndHandlesMissingMarkers() {
        String tex = "\\position{Developer{\\enskip\\cdotp\\enskip}Engineer}";

        assertEquals("Developer{\\enskip\\cdotp\\enskip}Engineer", LatexParser.extractValue(tex, "\\position{", "}"));
        assertEquals("", LatexParser.extractValue(tex, "\\email{", "}"));
    }

    @Test
    void preservesNestedBracesAcrossSkillsRoundTrip() {
        DefaultTableModel source = new DefaultTableModel(
            new Object[][]{{"Backend", "Java, \\href{https://example.com}{Spring}"}, {"Frontend", "\\textbf{React}"}},
            new String[]{"Category", "Skills"});
        DefaultTableModel parsed = new DefaultTableModel(new String[]{"Category", "Skills"}, 0);

        LatexParser.parseSkills(LatexGenerator.generateSkills(source), parsed);

        assertEquals(source.getRowCount(), parsed.getRowCount());
        for (int row = 0; row < source.getRowCount(); row++) {
            assertEquals(source.getValueAt(row, 0), parsed.getValueAt(row, 0));
            assertEquals(source.getValueAt(row, 1), parsed.getValueAt(row, 1));
        }
    }

    @Test
    void preservesMultipleExperienceEntriesAndBullets() {
        DefaultTableModel source = new DefaultTableModel(
            new Object[][]{{"Engineer", "Example", "Remote", "2023", "Built {stable} APIs\nImproved latency"}, {"Intern", "Other", "São Paulo", "2022", "Learned"}},
            new String[]{"Title", "Organization", "Location", "Dates", "Description"});
        DefaultTableModel parsed = new DefaultTableModel(new String[]{"Title", "Organization", "Location", "Dates", "Description"}, 0);

        LatexParser.parseExperience(LatexGenerator.generateExperience(new JTable(source)), parsed);

        assertEquals(source.getRowCount(), parsed.getRowCount());
        for (int row = 0; row < source.getRowCount(); row++) {
            for (int column = 0; column < source.getColumnCount(); column++) {
                assertEquals(source.getValueAt(row, column), parsed.getValueAt(row, column));
            }
        }
    }

    @Test
    void clearsModelWhenSectionIsMissing() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Category", "Skills"}, 0);
        model.addRow(new Object[]{"Old", "Value"});

        LatexParser.parseSkills("No skill section", model);

        assertEquals(0, model.getRowCount());
    }
}
