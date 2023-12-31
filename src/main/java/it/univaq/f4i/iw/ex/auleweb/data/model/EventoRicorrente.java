package it.univaq.f4i.iw.ex.auleweb.data.model;

import java.time.LocalDateTime;

import it.univaq.f4i.iw.framework.data.DataItem;

public interface EventoRicorrente extends DataItem<Integer> {

    LocalDateTime getDataInizio();

    LocalDateTime getDataFine();

    Evento getEventoMaster();

    void setDataInizio(LocalDateTime dataInizio);

    void setDataFine(LocalDateTime dataFine);

    void setEventoMaster(Evento evento);

}
