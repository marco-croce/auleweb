package it.univaq.f4i.iw.ex.auleweb.data.model;

import it.univaq.f4i.iw.framework.data.DataItem;

public interface Attrezzatura extends DataItem<Integer> {

    String getNumeroSeriale();

    String getDescrizione();

    Aula getAula();

    void setNumeroSeriale(String numeroSeriale);

    void setDescrizione(String descrizone);

    void setAula(Aula aula);

}
