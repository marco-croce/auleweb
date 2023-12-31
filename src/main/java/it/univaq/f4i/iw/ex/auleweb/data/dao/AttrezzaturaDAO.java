package it.univaq.f4i.iw.ex.auleweb.data.dao;

import java.util.List;

import it.univaq.f4i.iw.ex.auleweb.data.model.Aula;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.ex.auleweb.data.model.Attrezzatura;

public interface AttrezzaturaDAO {

    Attrezzatura createAttrezzatura();

    Attrezzatura getAttrezzatura(int attrezzatura_key) throws DataException;

    Attrezzatura getAttrezzatura(String numeroSeriale) throws DataException;

    List<Attrezzatura> getAttrezzatura() throws DataException;

    List<Attrezzatura> getAttrezzatura(Aula aula) throws DataException;

    void setAula(Aula aula, Attrezzatura attrezzatura) throws DataException;

    void removeFromAula(Aula aula, Attrezzatura attrezzatura) throws DataException;

    void storeAttrezzatura(Attrezzatura attrezzatura) throws DataException;

    void deleteAttrezzatura(Attrezzatura attrezzatura) throws DataException;

}
