package it.univaq.f4i.iw.ex.auleweb.data.dao;

import it.univaq.f4i.iw.ex.auleweb.data.model.Amministratore;
import it.univaq.f4i.iw.framework.data.DataException;

public interface AmministratoreDAO {

    Amministratore createAmministratore();

    Amministratore getAmministratore(int amministratore_key) throws DataException;

    Amministratore getAmministratoreByEmail(String email) throws DataException;

    void storeAmministratore(Amministratore amministratore) throws DataException;

    void deleteAmministratore(Amministratore amministratore) throws DataException;

}
