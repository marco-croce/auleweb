package it.univaq.f4i.iw.ex.auleweb.data.dao;

import java.util.List;

import it.univaq.f4i.iw.ex.auleweb.data.model.Aula;
import it.univaq.f4i.iw.ex.auleweb.data.model.Evento;
import it.univaq.f4i.iw.ex.auleweb.data.model.EventoRicorrente;
import it.univaq.f4i.iw.ex.auleweb.data.model.Gruppo;
import it.univaq.f4i.iw.framework.data.DataException;
import java.time.LocalDate;

public interface EventoRicorrenteDAO {

    EventoRicorrente createEventoRicorrente();

    EventoRicorrente getEventoRicorrente(int evento_ricorrente_key) throws DataException;

    Evento getEventoMaster(int evento_ricorrente_key) throws DataException;

    List<EventoRicorrente> getEventiRicorrenti() throws DataException;

    List<EventoRicorrente> getEventiRicorrenti(Evento evento) throws DataException;

    List<EventoRicorrente> getEventiRicorrenti(Aula aula) throws DataException;

    List<EventoRicorrente> getEventiRicorrenti(String corso) throws DataException;

    List<EventoRicorrente> getEventiRicorrenti(LocalDate inizio, LocalDate fine) throws DataException;

    List<EventoRicorrente> getEventiRicorrenti(LocalDate giorno) throws DataException;

    List<EventoRicorrente> getEventiRicorrentiAttuali() throws DataException;

    List<EventoRicorrente> getEventiRicorrenti(Gruppo gruppo) throws DataException;

    void deleteEventoRicorrente(EventoRicorrente evento) throws DataException;

}
