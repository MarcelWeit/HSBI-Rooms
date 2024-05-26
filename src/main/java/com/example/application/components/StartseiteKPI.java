package com.example.application.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;

@Tag("startseite-kpi")
public class StartseiteKPI extends Component {

    public StartseiteKPI(String title, String current, String change) {
        this.getElement()
                .setAttribute("title", title)
                .setAttribute("current", current)
                .setAttribute("change", change);
    }

}
