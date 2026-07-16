package com.dev.util;

import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.Arbitraries;

import javax.swing.table.DefaultTableModel;

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
        return Arbitraries.of("", "Java", "Spring Boot", "São Paulo", "C17", "CI CD");
    }
}
