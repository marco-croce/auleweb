package it.univaq.f4i.iw.ex.auleweb.data.model;

import it.univaq.f4i.iw.ex.auleweb.data.impl.TipologiaEvento;

import it.univaq.f4i.iw.ex.auleweb.data.impl.TipologiaRicorrenza;
import it.univaq.f4i.iw.framework.data.DataItem;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface Evento extends DataItem<Integer> {

    LocalDateTime getDataInizio();

    LocalDateTime getDataFine();

    String getNome();

    String getDescrizione();

    String getEmailResponsabile();

    Aula getAula();

    TipologiaEvento getTipologia();

    String getNomeCorso();

    TipologiaRicorrenza getTipologiaRicorrenza();

    LocalDate getDataFineRicorrenza();

    void setDataInizio(LocalDateTime dataInizio);

    void setDataFine(LocalDateTime dataFine);

    void setNome(String nome);

    void setDescrizione(String descrizione);

    void setEmailResponsabile(String emailResponsabile);

    void setAula(Aula aula);

    void setTipologia(TipologiaEvento tipologia);

    void setNomeCorso(String nomeCorso);

    void setTipologiaRicorrenza(TipologiaRicorrenza tipologiaRicorrenza);

    void setDataFineRicorrenza(LocalDate dataFineRicorrenza);

}
