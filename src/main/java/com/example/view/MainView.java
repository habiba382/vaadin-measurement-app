package com.example.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
@Route("")
public class MainView extends VerticalLayout {

    public MainView() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.START);
        getStyle().set("position", "relative");


        H1 header = new H1("Tervetuloa");
        H2 header2 = new H2("Mittaus Sovellukselle");
        header.getStyle().set("color", "#2c3e50").set("margin-bottom", "0");
        header2.getStyle().set("color", "#2c3e50").set("margin-top", "0");


        Button measurementBtn = new Button("Mittaus tiedot", evt ->
                UI.getCurrent().navigate("measurements"));
        Button personBtn = new Button("Henkilö tiedot", evt ->
                UI.getCurrent().navigate("persons"));
        Button placeBtn = new Button("Paikka tiedot", evt ->
                UI.getCurrent().navigate("places"));
        Button addressBtn = new Button("Osoite tiedot", evt ->
                UI.getCurrent().navigate("addresses"));

        String btnStyle = "font-size: 20px; width: 200px; height: 50px;";
        measurementBtn.getElement().setAttribute("style", btnStyle);
        personBtn.getElement().setAttribute("style", btnStyle);
        placeBtn.getElement().setAttribute("style", btnStyle);
        addressBtn.getElement().setAttribute("style", btnStyle);

        HorizontalLayout buttonLayout = new HorizontalLayout(
                measurementBtn, personBtn, placeBtn, addressBtn);
        buttonLayout.setSpacing(true);
        buttonLayout.getStyle().set("margin-top", "150px"); // Siirto keskelle


        Span footer = new Span("Copyright © 2025");
        footer.getStyle()
                .set("font-size", "16px")
                .set("color", "black")
                .set("position", "absolute")
                .set("bottom", "10px")
                .set("left", "10px");

        add(header, header2, buttonLayout, footer);
    }
}
