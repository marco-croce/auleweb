package it.univaq.f4i.iw.ex.auleweb.data.dao;

import java.util.List;

import it.univaq.f4i.iw.ex.auleweb.data.model.Aula;
import it.univaq.f4i.iw.ex.auleweb.data.model.Evento;
import it.univaq.f4i.iw.ex.auleweb.data.model.Gruppo;
import it.univaq.f4i.iw.framework.data.DataException;

public interface AulaDAO {

    Aula createAula();

    Aula getAula(int aula_key) throws DataException;

    Aula getAula(String edificio, String luogo, int piano, String nome) throws DataException;

    Aula getAula(Evento evento) throws DataException;

    List<Aula> getAule() throws DataException;

    List<Aula> getAule(Gruppo gruppo) throws DataException;

    List<Aula> getAuleByPreseRete(int numeroPreseRete) throws DataException;

    List<Aula> getAuleByPreseElettriche(int numeroPreseElettriche) throws DataException;

    List<Aula> getAuleByCapienza(int capienza) throws DataException;

    List<Aula> getAuleByPiano(int piano) throws DataException;

    List<Aula> getAuleByLuogo(String luogo) throws DataException;

    List<Aula> getAuleByEdificio(String edificio) throws DataException;

    List<String> getResponsabili() throws DataException;

    void setGruppo(Gruppo gruppo, Aula aula) throws DataException;

    void removeFromGruppo(Gruppo gruppo, Aula aula) throws DataException;

    void storeAula(Aula aula) throws DataException;

    void deleteAula(Aula aula) throws DataException;

}
