package it.univaq.f4i.iw.ex.auleweb.data.dao;

import java.util.List;

import it.univaq.f4i.iw.ex.auleweb.data.model.Aula;
import it.univaq.f4i.iw.ex.auleweb.data.model.Evento;
import it.univaq.f4i.iw.ex.auleweb.data.model.Gruppo;
import it.univaq.f4i.iw.framework.data.DataException;
import java.time.LocalDate;

public interface EventoDAO {

    Evento createEvento();

    Evento getEvento(int evento_key) throws DataException;

    List<Evento> getEventi() throws DataException;

    List<Evento> getEventi(Aula aula) throws DataException;

    List<Evento> getEventi(String corso) throws DataException;

    List<Evento> getEventi(LocalDate inizio, LocalDate fine) throws DataException;

    List<Evento> getEventi(LocalDate giorno) throws DataException;

    List<Evento> getEventiAttuali() throws DataException;

    List<Evento> getEventi(Gruppo gruppo) throws DataException;

    List<String> getCorsi() throws DataException;

    List<String> getCorsi(Gruppo gruppo) throws DataException;

    List<String> getResponsabili() throws DataException;

    void storeEvento(Evento evento) throws DataException;

    void deleteEvento(Evento evento) throws DataException;

}
