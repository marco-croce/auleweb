package it.univaq.f4i.iw.ex.auleweb.data.model;

import java.util.List;

import it.univaq.f4i.iw.framework.data.DataItem;

public interface Gruppo extends DataItem<Integer> {

    String getNome();

    String getDescrizione();

    List<Aula> getAule();

    void setNome(String nome);

    void setDescrizione(String descrizione);

    void setAule(List<Aula> aule);

    void addAula(Aula aula);

    boolean removeAula(Aula aula);

}
