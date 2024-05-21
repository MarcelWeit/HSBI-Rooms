package com.example.application.comparator;

import com.example.application.data.entities.Room;

import java.util.Comparator;

public class refNrComparator implements Comparator<Room> {

    @Override
    public int compare(Room ref1, Room ref2) {
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
