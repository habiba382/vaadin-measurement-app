package com.example.view;

import com.example.entity.Address;
import com.example.entity.Person;
import com.example.repository.AddressReopsitory;
import com.example.repository.PersonRepository;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.UI;

import java.util.List;
import java.util.stream.Collectors;

@Route("addresses")
@AnonymousAllowed
public class AddressView extends VerticalLayout {

    private final AddressReopsitory addressRepo;
    private final PersonRepository  personRepo;
    private final Grid<Address>     grid = new Grid<>(Address.class);


    private final TextField streetFilter = new TextField("Hae kadun perusteella");
    private final TextField cityFilter   = new TextField("Hae kaupungin perusteella");
    private final ComboBox<Person> personFilter = new ComboBox<>("Hae henkilön perusteella");

    @Autowired
    public AddressView(AddressReopsitory addressRepo,
                       PersonRepository  personRepo) {
        this.addressRepo = addressRepo;
        this.personRepo  = personRepo;


        add(new Button("Takaisin pääsivulle", e -> UI.getCurrent().navigate("")));
        add(new H1("Osoitteet"));


        personFilter.setItems(personRepo.findAll());
        personFilter.setItemLabelGenerator(Person::getName);

        streetFilter.setClearButtonVisible(true);
        cityFilter.setClearButtonVisible(true);
        personFilter.setClearButtonVisible(true);
        streetFilter.addValueChangeListener(e -> refreshGrid());
        cityFilter.addValueChangeListener(e -> refreshGrid());
        personFilter.addValueChangeListener(e -> refreshGrid());


        HorizontalLayout filters = new HorizontalLayout(
                streetFilter, cityFilter, personFilter
        );
        filters.setSpacing(true);
        add(filters);


        grid.setColumns("id", "street", "city", "postalCode");
        grid.addColumn(addr -> addr.getPerson() != null
                        ? addr.getPerson().getName() : "")
                .setHeader("Henkilö");
        refreshGrid();

        // CRUD-napit
        Button add   = new Button("Lisää osoite", e -> openForm(new Address()));
        Button edit  = new Button("Muokkaa", e -> {
            Address sel = grid.asSingleSelect().getValue();
            if (sel != null) openForm(sel);
            else Notification.show("Valitse osoite muokattavaksi");
        });
        Button del   = new Button("Poista", e -> {
            Address sel = grid.asSingleSelect().getValue();
            if (sel != null) {
                addressRepo.delete(sel);
                refreshGrid();
                Notification.show("Poistettu");
            }
        });

        add(grid, new HorizontalLayout(add, edit, del));

        // Footer
        Span footer = new Span("Copyright © 2025");
        footer.getStyle()
                .set("font-size", "16px")
                .set("color", "black")
                .set("margin-top", "60px")
                .set("text-align", "center");
        add(footer);
    }

    private void openForm(Address address) {
        Dialog dlg = new Dialog();

        TextField street     = new TextField("Katu");
        TextField city       = new TextField("Kaupunki");
        TextField postalCode = new TextField("Postinumero");

        ComboBox<Person> personSelect = new ComboBox<>("Henkilö");
        personSelect.setItems(personRepo.findAll());
        personSelect.setItemLabelGenerator(Person::getName);
        personSelect.setValue(address.getPerson());

        street.setValue(address.getStreet()      != null ? address.getStreet()      : "");
        city.setValue(address.getCity()          != null ? address.getCity()        : "");
        postalCode.setValue(address.getPostalCode() != null ? address.getPostalCode() : "");

        Button save = new Button("Tallenna", ev -> {
            address.setStreet(street.getValue());
            address.setCity(city.getValue());
            address.setPostalCode(postalCode.getValue());
            address.setPerson(personSelect.getValue());
            addressRepo.save(address);
            dlg.close();
            refreshGrid();
            Notification.show("Tallennettu");
        });

        dlg.add(new FormLayout(street, city, postalCode, personSelect), save);
        dlg.open();
    }

    private void refreshGrid() {
        String streetVal = streetFilter.getValue();
        String cityVal   = cityFilter.getValue();
        Person personVal = personFilter.getValue();

        List<Address> items = addressRepo.findAll().stream()

                .filter(a -> streetVal == null || streetVal.isEmpty()
                        || (a.getStreet() != null
                        && a.getStreet().toLowerCase().contains(streetVal.toLowerCase()))
                )

                .filter(a -> cityVal == null || cityVal.isEmpty()
                        || (a.getCity() != null
                        && a.getCity().toLowerCase().contains(cityVal.toLowerCase()))
                )

                .filter(a -> personVal == null
                        || (a.getPerson() != null
                        && a.getPerson().getId().equals(personVal.getId()))
                )
                .collect(Collectors.toList());

        grid.setItems(items);
    }
}

