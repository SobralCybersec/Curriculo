package com.dev.util;

import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.Arbitraries;

import javax.swing.table.DefaultTableModel;
import javax.swing.JTable;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LatexPropertyTest {

    @Property
    void preservesPlainSkillRows(@ForAll("plainText") String category, @ForAll("plainText") String skills) {
        DefaultTableModel source = new DefaultTableModel(
            new Object[][]{{category, skills}}, new String[]{"Category", "Skills"});
        DefaultTableModel parsed = new DefaultTableModel(new String[]{"Category", "Skills"}, 0);

        LatexParser.parseSkills(LatexGenerator.generateSkills(source), parsed);

        assertEquals(category, parsed.getValueAt(0, 0));
        assertEquals(skills, parsed.getValueAt(0, 1));
    }

    @Provide
    Arbitrary<String> plainText() {
        return Arbitraries.strings().withChars('a', 'z').ofMinLength(0).ofMaxLength(30);
    }

    @Property(tries = 60)
    void preservesPlainEducationRows(@ForAll("plainText") String degree, @ForAll("plainText") String institution,
                                     @ForAll("plainText") String location, @ForAll("plainText") String dates,
                                     @ForAll("plainText") String description) {
        DefaultTableModel source = new DefaultTableModel(new Object[][]{{degree, institution, location, dates, description}},
            new String[]{"Degree", "Institution", "Location", "Dates", "Description"});
        DefaultTableModel parsed = new DefaultTableModel(new String[]{"Degree", "Institution", "Location", "Dates", "Description"}, 0);

        LatexParser.parseEducation(LatexGenerator.generateEducation(new JTable(source)), parsed);

        for (int column = 0; column < source.getColumnCount(); column++) {
            assertEquals(source.getValueAt(0, column), parsed.getValueAt(0, column));
        }
    }

    @Property(tries = 60)
    void preservesPlainExperienceRows(@ForAll("plainText") String title, @ForAll("plainText") String organization,
                                      @ForAll("plainText") String location, @ForAll("plainText") String dates,
                                      @ForAll("plainText") String description) {
        DefaultTableModel source = new DefaultTableModel(new Object[][]{{title, organization, location, dates, description}},
            new String[]{"Title", "Organization", "Location", "Dates", "Description"});
        DefaultTableModel parsed = new DefaultTableModel(new String[]{"Title", "Organization", "Location", "Dates", "Description"}, 0);

        LatexParser.parseExperience(LatexGenerator.generateExperience(new JTable(source)), parsed);

        for (int column = 0; column < source.getColumnCount(); column++) {
            assertEquals(source.getValueAt(0, column), parsed.getValueAt(0, column));
        }
    }
}
