package it.univaq.f4i.iw.ex.auleweb.data.impl;

import java.time.LocalDateTime;

import it.univaq.f4i.iw.ex.auleweb.data.model.Evento;
import it.univaq.f4i.iw.ex.auleweb.data.model.EventoRicorrente;
import it.univaq.f4i.iw.framework.data.DataItemImpl;

public class EventoRicorrenteImpl extends DataItemImpl<Integer> implements EventoRicorrente {

    private LocalDateTime dataInizio;
    private LocalDateTime dataFine;
    private Evento eventoMaster;

    public EventoRicorrenteImpl() {
        this.dataInizio = null;
        this.dataFine = null;
        this.eventoMaster = null;
    }

    @Override
    public LocalDateTime getDataInizio() {
        return this.dataInizio;
    }

    @Override
    public void setDataInizio(LocalDateTime dataInizio) {
        this.dataInizio = dataInizio;
    }

    @Override
    public LocalDateTime getDataFine() {
        return this.dataFine;
    }

    @Override
    public void setDataFine(LocalDateTime dataFine) {
        this.dataFine = dataFine;
    }

    @Override
    public Evento getEventoMaster() {
        return this.eventoMaster;
    }

    @Override
    public void setEventoMaster(Evento eventoMaster) {
        this.eventoMaster = eventoMaster;
    }

}
