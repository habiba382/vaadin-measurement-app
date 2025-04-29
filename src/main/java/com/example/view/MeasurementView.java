package com.example.view;

import com.example.entity.Measurement;
import com.example.entity.Person;
import com.example.entity.Place;
import com.example.repository.MeasurementRepository;
import com.example.repository.PersonRepository;
import com.example.repository.PlaceRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
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
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.html.H1;

import java.util.List;
import java.util.stream.Collectors;

@AnonymousAllowed
@Route("measurements")
public class MeasurementView extends VerticalLayout {

    private final MeasurementRepository measurementRepository;
    private final PersonRepository personRepository;
    private final PlaceRepository placeRepository;
    private final Grid<Measurement> grid = new Grid<>(Measurement.class);

    private final TextField personSearchField = new TextField("Hae henkilön nimellä");
    private final TextField bloodPressureSearchField = new TextField("Hae verenpaineella");
    private final IntegerField weightSearchField = new IntegerField("Hae painolla");

    @Autowired
    public MeasurementView(MeasurementRepository measurementRepository,
                           PersonRepository personRepository,
                           PlaceRepository placeRepository) {
        this.measurementRepository = measurementRepository;
        this.personRepository = personRepository;
        this.placeRepository = placeRepository;

        Button backButton = new Button("Takaisin pääsivulle", e -> UI.getCurrent().navigate(""));
        add(backButton);

        H1 header = new H1("Mittaus Tiedot");
        add(header);

        grid.setColumns("id", "bloodPressure", "weight");
        grid.addColumn(m -> m.getPerson() != null ? m.getPerson().getName() : "").setHeader("Henkilö");
        grid.addColumn(m -> m.getPlace() != null ? m.getPlace().getName() : "").setHeader("Paikka");

        refreshGrid();

        personSearchField.addValueChangeListener(event -> refreshGrid());
        bloodPressureSearchField.addValueChangeListener(event -> refreshGrid());
        weightSearchField.addValueChangeListener(event -> refreshGrid());
        add(new HorizontalLayout(personSearchField, bloodPressureSearchField, weightSearchField));

        Button addButton = new Button("Lisää mittaus", e -> openForm(new Measurement()));

        Button editButton = new Button("Muokkaa", e -> {
            Measurement selected = grid.asSingleSelect().getValue();
            if (selected != null) {
                openForm(selected);
            } else {
                Notification.show("Valitse mittaus muokattavaksi");
            }
        });

        Button deleteButton = new Button("Poista", e -> {
            Measurement selected = grid.asSingleSelect().getValue();
            if (selected != null) {
                measurementRepository.delete(selected);
                refreshGrid();
                Notification.show("Mittaus poistettu");
            }
        });

        add(grid, new HorizontalLayout(addButton, editButton, deleteButton));

        add(grid, new HorizontalLayout(addButton, editButton, deleteButton));

// Lisää footer
        Span footer = new Span("Copyright © 2025");
        footer.getStyle()
                .set("font-size", "16px")
                .set("color", "black")
                .set("margin-top", "60px")
                .set("text-align", "center");

        add(footer);

    }

    private void openForm(Measurement measurement) {
        Dialog dialog = new Dialog();
        TextField bloodPressure = new TextField("Verenpaine");
        TextField weight = new TextField("Paino");
        ComboBox<Person> personSelect = new ComboBox<>("Henkilö");
        ComboBox<Place> placeSelect = new ComboBox<>("Paikka");

        personSelect.setItems(personRepository.findAll());
        personSelect.setItemLabelGenerator(Person::getName);

        placeSelect.setItems(placeRepository.findAll());
        placeSelect.setItemLabelGenerator(Place::getName);

        if (measurement.getPerson() != null) {
            personSelect.setValue(measurement.getPerson());
        }
        if (measurement.getPlace() != null) {
            placeSelect.setValue(measurement.getPlace());
        }

        bloodPressure.setValue(measurement.getBloodPressure() != null ? measurement.getBloodPressure() : "");
        weight.setValue(String.valueOf(measurement.getWeight()));

        Button saveButton = new Button("Tallenna", event -> {
            try {
                int parsedWeight = Integer.parseInt(weight.getValue());
                measurement.setBloodPressure(bloodPressure.getValue());
                measurement.setWeight(parsedWeight);
                measurement.setPerson(personSelect.getValue());
                measurement.setPlace(placeSelect.getValue());

                if (measurement.getPerson() == null || measurement.getPlace() == null) {
                    Notification.show("Valitse henkilö ja paikka");
                    return;
                }

                measurementRepository.save(measurement);
                dialog.close();
                refreshGrid();
                Notification.show("Tallennettu");
            } catch (NumberFormatException e) {
                Notification.show("Paino pitää olla numero!");
            }
        });

        dialog.add(new FormLayout(bloodPressure, weight, personSelect, placeSelect), saveButton);
        dialog.open();
    }

    private void refreshGrid() {
        String personFilter = personSearchField.getValue();
        String bloodPressureFilter = bloodPressureSearchField.getValue();
        Integer weightFilter = weightSearchField.getValue();

        List<Measurement> measurements = measurementRepository.findAll();

        if (personFilter != null && !personFilter.isEmpty()) {
            measurements = measurements.stream()
                    .filter(m -> m.getPerson() != null &&
                            m.getPerson().getName() != null &&
                            m.getPerson().getName().toLowerCase().contains(personFilter.toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (bloodPressureFilter != null && !bloodPressureFilter.isEmpty()) {
            measurements = measurements.stream()
                    .filter(m -> m.getBloodPressure() != null &&
                            m.getBloodPressure().toLowerCase().contains(bloodPressureFilter.toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (weightFilter != null) {
            measurements = measurements.stream()
                    .filter(m -> m.getWeight() == weightFilter)
                    .collect(Collectors.toList());
        }

        grid.setItems(measurements);
    }
}

