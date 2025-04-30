package com.example.view;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@Route("dashboard")
@RolesAllowed({ "ROLE_USER", "ROLE_ADMIN" })
public class DashboardView extends VerticalLayout {

    public DashboardView() {
        add(new H1("Yhteinen Dashboard‐sivu"));

    }

}


