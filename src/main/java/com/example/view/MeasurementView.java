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
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
@Route("measurements")
public class MeasurementView extends VerticalLayout {

    private final MeasurementRepository measurementRepository;
    private final PersonRepository       personRepository;
    private final PlaceRepository        placeRepository;
    private final Grid<Measurement>      grid = new Grid<>(Measurement.class);

    private final ComboBox<Person> personFilter           = new ComboBox<>("Hae henkilöllä");
    private final ComboBox<Place>  placeFilter            = new ComboBox<>("Hae paikalla");
    private final TextField        bpFilter               = new TextField("Hae verenpaineella");
    private final IntegerField     weightFilter           = new IntegerField("Hae painolla");

    @Autowired
    public MeasurementView(MeasurementRepository mRepo,
                           PersonRepository pRepo,
                           PlaceRepository  plRepo) {
        this.measurementRepository = mRepo;
        this.personRepository      = pRepo;
        this.placeRepository       = plRepo;


        add(new Button("Takaisin pääsivulle",
                e -> UI.getCurrent().navigate("")));
        add(new H1("Mittaus Tiedot"));

        // --- Grid setup ---
        grid.setColumns("id", "bloodPressure", "weight");
        grid.addColumn(m -> m.getPerson() != null
                        ? m.getPerson().getName()
                        : "")
                .setHeader("Henkilö");
        grid.addColumn(m -> m.getPlace() != null
                        ? m.getPlace().getName()
                        : "")
                .setHeader("Paikka");


        personFilter.setItems(personRepository.findAll());
        personFilter.setItemLabelGenerator(Person::getName);
        personFilter.setClearButtonVisible(true);
        personFilter.addValueChangeListener(e -> refreshGrid());

        placeFilter.setItems(placeRepository.findAll());
        placeFilter.setItemLabelGenerator(Place::getName);
        placeFilter.setClearButtonVisible(true);
        placeFilter.addValueChangeListener(e -> refreshGrid());

        bpFilter.setClearButtonVisible(true);
        bpFilter.addValueChangeListener(e -> refreshGrid());

        weightFilter.setClearButtonVisible(true);
        weightFilter.addValueChangeListener(e -> refreshGrid());

        add(new HorizontalLayout(personFilter,
                placeFilter,
                bpFilter,
                weightFilter));


        refreshGrid();


        Button addBtn = new Button("Lisää mittaus",
                e -> openForm(new Measurement()));
        Button editBtn = new Button("Muokkaa", e -> {
            Measurement sel = grid.asSingleSelect().getValue();
            if (sel != null) openForm(sel);
            else Notification.show("Valitse mittaus muokattavaksi");
        });
        Button delBtn = new Button("Poista", e -> {
            Measurement sel = grid.asSingleSelect().getValue();
            if (sel != null) {
                measurementRepository.delete(sel);
                refreshGrid();
                Notification.show("Mittaus poistettu");
            }
        });
        add(grid, new HorizontalLayout(addBtn, editBtn, delBtn));


        Span footer = new Span("Copyright © 2025");
        footer.getStyle()
                .set("font-size", "14px")
                .set("margin-top", "20px")
                .set("text-align", "center");
        add(footer);
    }

    private void openForm(Measurement m) {
        Dialog dlg = new Dialog();
        TextField bpField   = new TextField("Verenpaine");
        TextField wField    = new TextField("Paino");
        ComboBox<Person> personSelect = new ComboBox<>("Henkilö");
        ComboBox<Place>  placeSelect  = new ComboBox<>("Paikka");

        personSelect.setItems(personRepository.findAll());
        personSelect.setItemLabelGenerator(Person::getName);
        personSelect.setValue(m.getPerson());

        placeSelect.setItems(placeRepository.findAll());
        placeSelect.setItemLabelGenerator(Place::getName);
        placeSelect.setValue(m.getPlace());

        bpField.setValue(m.getBloodPressure() != null ? m.getBloodPressure() : "");
        wField.setValue(String.valueOf(m.getWeight()));

        Button save = new Button("Tallenna", ev -> {
            try {
                m.setBloodPressure(bpField.getValue());
                m.setWeight(Integer.parseInt(wField.getValue()));
                m.setPerson(personSelect.getValue());
                m.setPlace(placeSelect.getValue());
                if (m.getPerson() == null || m.getPlace() == null) {
                    Notification.show("Valitse henkilö ja paikka");
                    return;
                }
                measurementRepository.save(m);
                dlg.close();
                refreshGrid();
                Notification.show("Tallennettu");
            } catch (NumberFormatException x) {
                Notification.show("Paino pitää olla numero!");
            }
        });

        dlg.add(new FormLayout(bpField, wField, personSelect, placeSelect), save);
        dlg.open();
    }

    private void refreshGrid() {
        List<Measurement> items = measurementRepository.findAll();


        Person pf = personFilter.getValue();
        if (pf != null) {
            Long pid = pf.getId();
            items = items.stream()
                    .filter(m -> m.getPerson() != null
                            && pid.equals(m.getPerson().getId()))
                    .collect(Collectors.toList());
        }

        Place plf = placeFilter.getValue();
        if (plf != null) {
            Long plid = plf.getId();
            items = items.stream()
                    .filter(m -> m.getPlace() != null
                            && plid.equals(m.getPlace().getId()))
                    .collect(Collectors.toList());
        }

        String bp = bpFilter.getValue();
        if (bp != null && !bp.isEmpty()) {
            items = items.stream()
                    .filter(m -> m.getBloodPressure() != null
                            && m.getBloodPressure()
                            .toLowerCase()
                            .contains(bp.toLowerCase()))
                    .collect(Collectors.toList());
        }

        Integer w = weightFilter.getValue();
        if (w != null) {
            items = items.stream()
                    .filter(m -> m.getWeight() == w)
                    .collect(Collectors.toList());
        }

        grid.setItems(items);
    }
}
