package it.univaq.f4i.iw.framework.data;

import it.univaq.f4i.iw.ex.auleweb.data.dao.AmministratoreDAO_MySQL;
import it.univaq.f4i.iw.ex.auleweb.data.dao.AttrezzaturaDAO_MySQL;
import it.univaq.f4i.iw.ex.auleweb.data.dao.AulaDAO_MySQL;
import it.univaq.f4i.iw.ex.auleweb.data.dao.EventoDAO_MySQL;
import it.univaq.f4i.iw.ex.auleweb.data.dao.EventoRicorrenteDAO_MySQL;
import it.univaq.f4i.iw.ex.auleweb.data.dao.GruppoDAO_MySQL;
import it.univaq.f4i.iw.ex.auleweb.data.model.Amministratore;
import it.univaq.f4i.iw.ex.auleweb.data.model.Attrezzatura;
import it.univaq.f4i.iw.ex.auleweb.data.model.Aula;
import it.univaq.f4i.iw.ex.auleweb.data.model.Evento;
import it.univaq.f4i.iw.ex.auleweb.data.model.EventoRicorrente;
import it.univaq.f4i.iw.ex.auleweb.data.model.Gruppo;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

public class DataLayer implements AutoCloseable {

    private final DataSource datasource;
    private Connection connection;
    private final Map<Class, DAO> daos;
    private final DataCache cache;

    public DataLayer(DataSource datasource) throws SQLException {
        super();
        this.datasource = datasource;
        this.connection = datasource.getConnection();
        this.daos = new HashMap<>();
        this.cache = new DataCache();
    }

    public void registerDAO(Class entityClass, DAO dao) throws DataException {
        daos.put(entityClass, dao);
        dao.init();
    }

    public DAO getDAO(Class entityClass) {
        return daos.get(entityClass);
    }

    public void init() throws DataException {
        registerDAO(Attrezzatura.class, new AttrezzaturaDAO_MySQL(this));
        registerDAO(Aula.class, new AulaDAO_MySQL(this));
        registerDAO(Evento.class, new EventoDAO_MySQL(this));
        registerDAO(Gruppo.class, new GruppoDAO_MySQL(this));
        registerDAO(Amministratore.class, new AmministratoreDAO_MySQL(this));
        registerDAO(EventoRicorrente.class, new EventoRicorrenteDAO_MySQL(this));
    }

    public void destroy() {
        try {
            if (connection != null) {
                connection.close();
                connection = null;
            }
        } catch (SQLException ex) {
            //
        }
    }

    public DataSource getDatasource() {
        return datasource;
    }

    public Connection getConnection() {
        return connection;
    }

    public DataCache getCache() {
        return cache;
    }

    //metodo dell'interfaccia AutoCloseable (permette di usare questa classe nei try-with-resources)
    @Override
    public void close() throws Exception {
        destroy();
    }

}
