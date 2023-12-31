package it.univaq.f4i.iw.ex.auleweb.data.impl;

import it.univaq.f4i.iw.ex.auleweb.data.model.Amministratore;
import it.univaq.f4i.iw.framework.data.DataItemImpl;

public class AmministratoreImpl extends DataItemImpl<Integer> implements Amministratore {

    private String nome;
    private String cognome;
    private String email;
    private String password;
    private String telefono;

    public AmministratoreImpl() {
        super();
        nome = "";
        cognome = "";
        email = "";
        password = "";
        telefono = "";
    }

    @Override
    public String getNome() {
        return nome;
    }

    @Override
    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String getCognome() {
        return cognome;
    }

    @Override
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getTelefono() {
        return telefono;
    }

    @Override
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

}
