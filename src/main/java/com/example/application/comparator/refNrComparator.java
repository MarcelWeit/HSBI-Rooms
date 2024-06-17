package com.example.application.comparator;

import com.example.application.data.entities.Raum;

import java.util.Comparator;

/**
 * Comparator um Räume nach ihrer Referenznummer zu sortieren.
 *
 * @author Marcel Weithoener
 */
public class refNrComparator implements Comparator<Raum> {

    @Override
    public int compare(Raum ref1, Raum ref2) {
        char char1 = ref1.getRefNr().charAt(0);
        int int1 = Integer.parseInt(ref1.getRefNr().substring(1, 2));
        char char2 = ref2.getRefNr().charAt(0);
        int int2 = Integer.parseInt(ref2.getRefNr().substring(1, 2));

        // Sort by character first
        if (char1 != char2) {
            return Character.compare(char1, char2);
        }

        // Then sort by integer
        return Integer.compare(int1, int2);
    }
}
