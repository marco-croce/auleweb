package it.univaq.f4i.iw.ex.auleweb.data.model;

import it.univaq.f4i.iw.framework.data.DataItem;
import java.util.List;

public interface Aula extends DataItem<Integer> {

    String getNome();

    String getLuogo();

    String getEdificio();

    int getPiano();

    int getCapienza();

    String getEmailResponsabile();

    int getNumeroPreseRete();

    int getNumeroPreseElettriche();

    String getNote();

    String getAttrezziString();

    List<Attrezzatura> getAttrezzatura();

    List<Gruppo> getGruppi();

    List<Evento> getEventi();

    void setNome(String nome);

    void setLuogo(String luogo);

    void setEdificio(String edificio);

    void setPiano(int piano);

    void setCapienza(int capienza);

    void setEmailResponsabile(String email);

    void setNumeroPreseRete(int numeroPreseRete);

    void setNumeroPreseElettriche(int numeroPreseElettriche);

    void setNote(String note);

    void setAttrezzatura(List<Attrezzatura> attrezzatura);

    void setGruppi(List<Gruppo> gruppi);

    void setEventi(List<Evento> eventi);

    void addAttrezzatura(Attrezzatura attrezzatura);

    boolean removeAttrezzatura(Attrezzatura attrezzatura);

    void addGruppo(Gruppo gruppo);

    boolean removeGruppo(Gruppo gruppo);

    void addEvento(Evento evento);

    boolean removeEvento(Evento evento);

}
