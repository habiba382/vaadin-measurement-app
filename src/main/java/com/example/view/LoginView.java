package com.example.view;


import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.auth.AnonymousAllowed;


@Route("login")
@PageTitle("Kirjaudu sisään")
@AnonymousAllowed
public class LoginView extends VerticalLayout {

    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        LoginForm loginForm = new LoginForm();
        loginForm.setAction("login");

        // lokalisoidaan napit yms.
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.getForm().setTitle("Kirjaudu sisään");
        i18n.getForm().setUsername("Käyttäjänimi");
        i18n.getForm().setPassword("Salasana");
        i18n.getForm().setSubmit("Kirjaudu");
        i18n.getErrorMessage().setTitle("Virhe");
        i18n.getErrorMessage().setMessage("Virheellinen käyttäjänimi tai salasana");
        loginForm.setI18n(i18n);

        add(loginForm);
    }
}
