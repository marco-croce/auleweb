package it.univaq.f4i.iw.ex.auleweb.data.impl;

import it.univaq.f4i.iw.ex.auleweb.data.model.Aula;
import it.univaq.f4i.iw.ex.auleweb.data.model.Gruppo;
import it.univaq.f4i.iw.framework.data.DataItemImpl;
import java.util.List;

public class GruppoImpl extends DataItemImpl<Integer> implements Gruppo {

    private String nome;
    private String descrizione;

    private List<Aula> aule;

    public GruppoImpl() {
        this.nome = "";
        this.descrizione = "";
        this.aule = null;
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
    public String getDescrizione() {
        return descrizione;
    }

    @Override
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    @Override
    public List<Aula> getAule() {
        return aule;
    }

    @Override
    public void setAule(List<Aula> aule) {
        this.aule = aule;
    }

    @Override
    public void addAula(Aula aula) {
        this.aule.add(aula);
    }

    @Override
    public boolean removeAula(Aula aula) {
        return this.aule.remove(aula);
    }

}
