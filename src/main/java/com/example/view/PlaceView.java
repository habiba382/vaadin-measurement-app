package com.example.view;

import com.example.entity.Place;
import com.example.repository.PlaceRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.html.H1;

import java.util.List;
import java.util.stream.Collectors;

@RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
@Route("places")
public class PlaceView extends VerticalLayout {

    private final PlaceRepository placeRepository;
    private final Grid<Place> grid = new Grid<>(Place.class);

    private final TextField nameSearchField = new TextField("Hae paikan nimellä");
    private final TextField locationSearchField = new TextField("Hae sijainnilla");
    private final IntegerField idSearchField = new IntegerField("Hae ID:llä");

    @Autowired
    public PlaceView(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;

        Button backButton = new Button("Takaisin pääsivulle", e -> UI.getCurrent().navigate(""));
        add(backButton);

        H1 header = new H1("Paikojen Tiedot");
        add(header);

        grid.setColumns("id", "name", "location");
        refreshGrid();

        nameSearchField.addValueChangeListener(event -> refreshGrid());
        locationSearchField.addValueChangeListener(event -> refreshGrid());
        idSearchField.addValueChangeListener(event -> refreshGrid());
        add(new HorizontalLayout(nameSearchField, locationSearchField, idSearchField));

        Button addButton = new Button("Lisää paikka", e -> openPlaceForm(new Place()));

        Button editButton = new Button("Muokkaa", e -> {
            Place selected = grid.asSingleSelect().getValue();
            if (selected != null) {
                openPlaceForm(selected);
            } else {
                Notification.show("Valitse paikka muokattavaksi");
            }
        });

        Button deleteButton = new Button("Poista", e -> {
            Place selected = grid.asSingleSelect().getValue();
            if (selected != null) {
                placeRepository.delete(selected);
                refreshGrid();
                Notification.show("Paikka poistettu");
            }
        });

        add(grid, new HorizontalLayout(addButton, editButton, deleteButton));

        Span footer = new Span("Copyright © 2025");
        footer.getStyle()
                .set("font-size", "16px")
                .set("color", "black")
                .set("margin-top", "60px")
                .set("text-align", "center");

        add(footer);

    }


    private void openPlaceForm(Place place) {
        Dialog dialog = new Dialog();
        TextField name = new TextField("Nimi");
        TextField location = new TextField("Sijainti");

        name.setValue(place.getName() != null ? place.getName() : "");
        location.setValue(place.getLocation() != null ? place.getLocation() : "");

        Button saveButton = new Button("Tallenna", event -> {
            place.setName(name.getValue());
            place.setLocation(location.getValue());
            placeRepository.save(place);
            dialog.close();
            refreshGrid();
            Notification.show("Tallennettu");
        });

        dialog.add(new FormLayout(name, location), saveButton);
        dialog.open();
    }

    private void refreshGrid() {
        String nameFilter = nameSearchField.getValue();
        String locationFilter = locationSearchField.getValue();
        Integer idFilter = idSearchField.getValue();

        List<Place> places = placeRepository.findAll();

        if (nameFilter != null && !nameFilter.isEmpty()) {
            places = places.stream()
                    .filter(place -> place.getName() != null && place.getName().toLowerCase().contains(nameFilter.toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (locationFilter != null && !locationFilter.isEmpty()) {
            places = places.stream()
                    .filter(place -> place.getLocation() != null && place.getLocation().toLowerCase().contains(locationFilter.toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (idFilter != null) {
            places = places.stream()
                    .filter(place -> place.getId() != null && place.getId().equals(Long.valueOf(idFilter)))
                    .collect(Collectors.toList());
        }


        grid.setItems(places);
    }
}
