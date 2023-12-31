package it.univaq.f4i.iw.ex.auleweb.data.model;

import it.univaq.f4i.iw.framework.data.DataItem;

public interface Amministratore extends DataItem<Integer> {

    String getNome();

    String getCognome();

    String getEmail();

    String getPassword();

    String getTelefono();

    void setNome(String nome);

    void setCognome(String cognome);

    void setEmail(String email);

    void setPassword(String password);

    void setTelefono(String telefono);

}
