 package com.example.view;

import com.example.entity.Person;
import com.example.repository.PersonRepository;
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
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.html.H1;

import java.util.List;
import java.util.stream.Collectors;

@AnonymousAllowed
@Route("persons")
public class PersonView extends VerticalLayout {

    private final PersonRepository personRepository;
    private final Grid<Person> grid = new Grid<>(Person.class);

    private final TextField nameSearchField = new TextField("Hae nimen perusteella");
    private final IntegerField ageSearchField = new IntegerField("Hae iän perusteella");
    private final IntegerField idSearchField = new IntegerField("Hae ID:llä");

    @Autowired
    public PersonView(PersonRepository personRepository) {
        this.personRepository = personRepository;

        Button backButton = new Button("Takaisin pääsivulle", e -> UI.getCurrent().navigate(""));
        add(backButton);

        H1 header = new H1("Henkilö Tiedot");
        add(header);

        grid.setColumns("id", "name", "age");
        refreshGrid();

        nameSearchField.addValueChangeListener(event -> refreshGrid());
        ageSearchField.addValueChangeListener(event -> refreshGrid());
        idSearchField.addValueChangeListener(event -> refreshGrid());
        add(new HorizontalLayout(nameSearchField, ageSearchField, idSearchField));

        Button addButton = new Button("Lisää henkilö", e -> openPersonForm(new Person()));

        Button editButton = new Button("Muokkaa", e -> {
            Person selected = grid.asSingleSelect().getValue();
            if (selected != null) {
                openPersonForm(selected);
            } else {
                Notification.show("Valitse henkilö muokattavaksi");
            }
        });

        Button deleteButton = new Button("Poista valittu", e -> {
            Person selected = grid.asSingleSelect().getValue();
            if (selected != null) {
                personRepository.delete(selected);
                refreshGrid();
                Notification.show("Henkilö poistettu");
            }
        });

        add(grid, new HorizontalLayout(addButton, editButton, deleteButton));

        add(grid, new HorizontalLayout(addButton, editButton, deleteButton));


        Span footer = new Span("Copyright © 2025");
        footer.getStyle()
                .set("font-size", "16px")
                .set("color", "black")
                .set("margin-top", "60px")
                .set("text-align", "center");

        add(footer);

    }

    private void openPersonForm(Person person) {
        Dialog dialog = new Dialog();

        TextField name = new TextField("Nimi");
        IntegerField age = new IntegerField("Ikä");

        name.setValue(person.getName() != null ? person.getName() : "");
        age.setValue(person.getAge());

        Button saveButton = new Button("Tallenna", event -> {
            person.setName(name.getValue());
            person.setAge(age.getValue());
            personRepository.save(person);
            dialog.close();
            refreshGrid();
            Notification.show("Tallennettu");
        });

        dialog.add(new FormLayout(name, age), saveButton);
        dialog.open();
    }
    private void refreshGrid() {
        String nameFilter = nameSearchField.getValue();
        Integer ageFilter = ageSearchField.getValue();
        Integer idFilter = idSearchField.getValue();

        List<Person> persons = personRepository.findAll();

        if (nameFilter != null && !nameFilter.isEmpty()) {
            persons = persons.stream()
                    .filter(person -> person.getName() != null &&
                            person.getName().toLowerCase().contains(nameFilter.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (ageFilter != null) {
            persons = persons.stream()
                    .filter(person -> person.getAge() == ageFilter)
                    .collect(Collectors.toList());
        }

        if (idFilter != null) {
            persons = persons.stream()
                    .filter(person -> person.getId() == idFilter.longValue())
                    .collect(Collectors.toList());
        }

        grid.setItems(persons);
    }
}
