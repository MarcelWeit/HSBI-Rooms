package com.example.application.comparator;

import com.example.application.data.entities.Dozent;
import java.util.Comparator;

public class VornameComparator implements Comparator<Dozent> {

    @Override
    public int compare(Dozent d1, Dozent d2) {
        return d1.getVorname().compareToIgnoreCase(d2.getVorname());
    }
}