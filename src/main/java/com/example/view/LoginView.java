package com.example.view;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@AnonymousAllowed
public class LoginView extends VerticalLayout {

    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        LoginForm loginForm = new LoginForm();
        loginForm.setAction("login");

        LoginI18n i18n = LoginI18n.createDefault();
        i18n.getForm().setTitle("Kirjaudu sisään");
        i18n.getForm().setUsername("Käyttäjänimi");
        i18n.getForm().setPassword("Salasana");
        i18n.getForm().setSubmit("Kirjaudu");
        i18n.getErrorMessage().setTitle("Virheellinen käyttäjä tai salasana");
        i18n.getErrorMessage().setMessage("Tarkista tunnuksesi ja yritä uudelleen.");
        loginForm.setI18n(i18n);

        add(loginForm);



// Lisää footer
        Span footer = new Span("Copyright © 2025");
        footer.getStyle()
                .set("font-size", "16px")
                .set("color", "black")
                .set("margin-top", "60px")
                .set("text-align", "center");

        add(footer);

    }
}

