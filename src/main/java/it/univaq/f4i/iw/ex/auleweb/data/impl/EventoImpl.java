package it.univaq.f4i.iw.ex.auleweb.data.impl;

import it.univaq.f4i.iw.ex.auleweb.data.model.Aula;
import it.univaq.f4i.iw.ex.auleweb.data.model.Evento;
import it.univaq.f4i.iw.framework.data.DataItemImpl;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class EventoImpl extends DataItemImpl<Integer> implements Evento {

    private LocalDateTime dataInizio;
    private LocalDateTime dataFine;
    private String nome;
    private String descrizione;
    private String emailResponsabile;
    private Aula aula;
    private TipologiaEvento tipologia;
    private String nomeCorso;
    private TipologiaRicorrenza tipologiaRicorrenza;
    private LocalDate dataFineRicorrenza;

    public EventoImpl() {
        this.dataInizio = null;
        this.dataFine = null;
        this.nome = "";
        this.descrizione = "";
        this.emailResponsabile = "";
        this.aula = null;
        this.tipologia = null;
        this.nomeCorso = "";
        this.tipologiaRicorrenza = null;
        this.dataFineRicorrenza = null;
    }

    @Override
    public LocalDateTime getDataInizio() {
        return dataInizio;
    }

    @Override
    public void setDataInizio(LocalDateTime dataInizio) {
        this.dataInizio = dataInizio;
    }

    @Override
    public LocalDateTime getDataFine() {
        return dataFine;
    }

    @Override
    public void setDataFine(LocalDateTime dataFine) {
        this.dataFine = dataFine;
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
    public String getEmailResponsabile() {
        return emailResponsabile;
    }

    @Override
    public void setEmailResponsabile(String emailResponsabile) {
        this.emailResponsabile = emailResponsabile;
    }

    @Override
    public Aula getAula() {
        return aula;
    }

    @Override
    public void setAula(Aula aula) {
        this.aula = aula;
    }

    @Override
    public TipologiaEvento getTipologia() {
        return tipologia;
    }

    @Override
    public void setTipologia(TipologiaEvento tipologia) {
        this.tipologia = tipologia;
    }

    @Override
    public String getNomeCorso() {
        return nomeCorso;
    }

    @Override
    public void setNomeCorso(String nomeCorso) {
        this.nomeCorso = nomeCorso;
    }

    @Override
    public TipologiaRicorrenza getTipologiaRicorrenza() {
        return tipologiaRicorrenza;
    }

    @Override
    public void setTipologiaRicorrenza(TipologiaRicorrenza tipologiaRicorrenza) {
        this.tipologiaRicorrenza = tipologiaRicorrenza;
    }

    @Override
    public LocalDate getDataFineRicorrenza() {
        return dataFineRicorrenza;
    }

    @Override
    public void setDataFineRicorrenza(LocalDate dataFineRicorrenza) {
        this.dataFineRicorrenza = dataFineRicorrenza;
    }

}
