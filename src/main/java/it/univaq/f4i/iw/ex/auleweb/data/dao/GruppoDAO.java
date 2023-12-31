package it.univaq.f4i.iw.ex.auleweb.data.dao;

import java.util.List;

import it.univaq.f4i.iw.ex.auleweb.data.model.Aula;
import it.univaq.f4i.iw.ex.auleweb.data.model.Gruppo;
import it.univaq.f4i.iw.framework.data.DataException;

public interface GruppoDAO {

    Gruppo createGruppo();

    Gruppo getGruppo(int gruppo_key) throws DataException;

    Gruppo getGruppo(String nome) throws DataException;

    List<Gruppo> getGruppi() throws DataException;

    List<Gruppo> getGruppi(Aula aula) throws DataException;

    void storeGruppo(Gruppo gruppo) throws DataException;

    void deleteGruppo(Gruppo gruppo) throws DataException;

}
