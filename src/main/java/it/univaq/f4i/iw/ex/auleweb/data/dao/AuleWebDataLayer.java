package it.univaq.f4i.iw.ex.auleweb.data.dao;

import it.univaq.f4i.iw.ex.auleweb.data.model.Amministratore;
import it.univaq.f4i.iw.ex.auleweb.data.model.Attrezzatura;
import it.univaq.f4i.iw.ex.auleweb.data.model.Aula;
import it.univaq.f4i.iw.ex.auleweb.data.model.Evento;
import it.univaq.f4i.iw.ex.auleweb.data.model.EventoRicorrente;
import it.univaq.f4i.iw.ex.auleweb.data.model.Gruppo;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.data.DataLayer;
import java.sql.SQLException;
import javax.sql.DataSource;

public class AuleWebDataLayer extends DataLayer {

    public AuleWebDataLayer(DataSource datasource) throws SQLException {
        super(datasource);
    }

    @Override
    public void init() throws DataException {
        super.init();
    }

    //helpers 
    public AulaDAO getAulaDAO() {
        return (AulaDAO) getDAO(Aula.class);
    }

    public EventoDAO getEventoDAO() {
        return (EventoDAO) getDAO(Evento.class);
    }

    public GruppoDAO getGruppoDAO() {
        return (GruppoDAO) getDAO(Gruppo.class);
    }

    public AmministratoreDAO getAmministratoreDAO() {
        return (AmministratoreDAO_MySQL) getDAO(Amministratore.class);
    }

    public AttrezzaturaDAO getAttrezzaturaDAO() {
        return (AttrezzaturaDAO) getDAO(Attrezzatura.class);
    }

    public EventoRicorrenteDAO getEventoRicorrenteDAO() {
        return (EventoRicorrenteDAO) getDAO(EventoRicorrente.class);
    }

}
