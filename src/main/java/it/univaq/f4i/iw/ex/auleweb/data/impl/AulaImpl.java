package it.univaq.f4i.iw.ex.auleweb.data.impl;

import java.util.List;

import it.univaq.f4i.iw.ex.auleweb.data.model.Aula;
import it.univaq.f4i.iw.ex.auleweb.data.model.Evento;
import it.univaq.f4i.iw.ex.auleweb.data.model.Gruppo;
import it.univaq.f4i.iw.framework.data.DataItemImpl;
import it.univaq.f4i.iw.ex.auleweb.data.model.Attrezzatura;

public class AulaImpl extends DataItemImpl<Integer> implements Aula {

    private String nome;
    private String luogo;
    private String edificio;
    private int piano;
    private int capienza;
    private String emailResponsabile;
    private int numeroPreseRete;
    private int numeroPreseElettriche;
    private String note;

    private List<Attrezzatura> attrezzatura;
    private List<Evento> eventi;
    private List<Gruppo> gruppi;

    public AulaImpl() {
        super();
        this.nome = "";
        this.luogo = "";
        this.edificio = "";
        this.piano = 0;
        this.capienza = 0;
        this.emailResponsabile = "";
        this.numeroPreseRete = 0;
        this.numeroPreseElettriche = 0;
        this.note = "";
        this.attrezzatura = null;
        this.eventi = null;
        this.gruppi = null;
    }

    @Override
    public String getNome() {
        return this.nome;
    }

    @Override
    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String getLuogo() {
        return this.luogo;
    }

    @Override
    public void setLuogo(String luogo) {
        this.luogo = luogo;
    }

    @Override
    public String getEdificio() {
        return this.edificio;
    }

    @Override
    public void setEdificio(String edificio) {
        this.edificio = edificio;
    }

    @Override
    public int getPiano() {
        return this.piano;
    }

    @Override
    public void setPiano(int piano) {
        this.piano = piano;
    }

    @Override
    public int getCapienza() {
        return this.capienza;
    }

    @Override
    public void setCapienza(int capienza) {
        this.capienza = capienza;
    }

    @Override
    public String getEmailResponsabile() {
        return this.emailResponsabile;
    }

    @Override
    public void setEmailResponsabile(String emailResponsabile) {
        this.emailResponsabile = emailResponsabile;
    }

    @Override
    public int getNumeroPreseRete() {
        return this.numeroPreseRete;
    }

    @Override
    public void setNumeroPreseRete(int numeroPreseRete) {
        this.numeroPreseRete = numeroPreseRete;
    }

    @Override
    public int getNumeroPreseElettriche() {
        return this.numeroPreseElettriche;
    }

    @Override
    public void setNumeroPreseElettriche(int numeroPreseElettriche) {
        this.numeroPreseElettriche = numeroPreseElettriche;
    }

    @Override
    public String getNote() {
        return this.note;
    }

    @Override
    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public List<Attrezzatura> getAttrezzatura() {
        return this.attrezzatura;
    }

    @Override
    public void setAttrezzatura(List<Attrezzatura> attrezzatura) {
        this.attrezzatura = attrezzatura;
    }

    @Override
    public List<Evento> getEventi() {
        return this.eventi;
    }

    @Override
    public void setEventi(List<Evento> eventi) {
        this.eventi = eventi;
    }

    @Override
    public List<Gruppo> getGruppi() {
        return this.gruppi;
    }

    @Override
    public void setGruppi(List<Gruppo> gruppi) {
        this.gruppi = gruppi;
    }

    @Override
    public void addAttrezzatura(Attrezzatura attrezzatura) {
        this.attrezzatura.add(attrezzatura);
    }

    @Override
    public boolean removeAttrezzatura(Attrezzatura attrezzatura) {
        return this.attrezzatura.remove(attrezzatura);
    }

    @Override
    public void addGruppo(Gruppo gruppo) {
        this.gruppi.add(gruppo);
    }

    @Override
    public boolean removeGruppo(Gruppo gruppo) {
        return this.gruppi.remove(gruppo);
    }

    @Override
    public void addEvento(Evento evento) {
        this.eventi.add(evento);
    }

    @Override
    public boolean removeEvento(Evento evento) {
        return this.eventi.remove(evento);
    }

    @Override
    public String getAttrezziString() {
        String result = "";
        if (attrezzatura != null && !attrezzatura.isEmpty()) {
            for (Attrezzatura attrezzo : attrezzatura) {
                result += attrezzo.getDescrizione() + ", ";
            }
            result = result.substring(0, result.length() - 2);
        }
        return result;

    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof AulaImpl)) {
            return false;
        }

        AulaImpl a = (AulaImpl) obj;
        return (this.nome.equalsIgnoreCase(a.getNome())
                && this.luogo.equalsIgnoreCase(a.getLuogo())
                && this.edificio.equalsIgnoreCase(a.getEdificio())
                && this.piano == a.getPiano());
    }

}
