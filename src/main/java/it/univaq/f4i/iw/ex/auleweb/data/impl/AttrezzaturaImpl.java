package it.univaq.f4i.iw.ex.auleweb.data.impl;

import it.univaq.f4i.iw.framework.data.DataItemImpl;
import it.univaq.f4i.iw.ex.auleweb.data.model.Attrezzatura;
import it.univaq.f4i.iw.ex.auleweb.data.model.Aula;

public class AttrezzaturaImpl extends DataItemImpl<Integer> implements Attrezzatura {

    private String numeroSeriale;
    private String descrizione;
    private Aula aula;

    public AttrezzaturaImpl() {
        this.numeroSeriale = "";
        this.descrizione = "";
        this.aula = null;
    }

    @Override
    public String getNumeroSeriale() {
        return this.numeroSeriale;
    }

    @Override
    public void setNumeroSeriale(String numeroSeriale) {
        this.numeroSeriale = numeroSeriale;
    }

    @Override
    public String getDescrizione() {
        return this.descrizione;
    }

    @Override
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    @Override
    public Aula getAula() {
        return this.aula;
    }

    @Override
    public void setAula(Aula aula) {
        this.aula = aula;
    }

}
