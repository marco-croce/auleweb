package it.univaq.f4i.iw.ex.auleweb.data.dao;

import it.univaq.f4i.iw.ex.auleweb.data.model.Aula;
import it.univaq.f4i.iw.ex.auleweb.data.proxy.AttrezzaturaProxy;
import it.univaq.f4i.iw.framework.data.DAO;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.data.DataItemProxy;
import it.univaq.f4i.iw.framework.data.DataLayer;
import it.univaq.f4i.iw.framework.data.OptimisticLockException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import it.univaq.f4i.iw.ex.auleweb.data.model.Attrezzatura;

public class AttrezzaturaDAO_MySQL extends DAO implements AttrezzaturaDAO {

    private PreparedStatement sAttrezzaturaByID, sAttrezzaturaByNumeroSeriale;
    private PreparedStatement sAttrezzatura, sAttrezzaturaByAula;
    private PreparedStatement iAttrezzatura, uAttrezzatura, dAttrezzatura;

    public AttrezzaturaDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            //Precompilazione delle query
            sAttrezzaturaByID = connection.prepareStatement("SELECT * FROM attrezzatura WHERE ID=?");
            sAttrezzaturaByNumeroSeriale = connection.prepareStatement("SELECT * FROM attrezzatura WHERE numero_seriale=?");
            sAttrezzaturaByAula = connection.prepareStatement("SELECT ID AS attrezzaturaID FROM attrezzatura WHERE ID_aula=?");
            sAttrezzatura = connection.prepareStatement("SELECT ID AS attrezzaturaID FROM attrezzatura");

            iAttrezzatura = connection.prepareStatement("INSERT INTO attrezzatura (numero_seriale,descrizione,ID_aula) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS);
            uAttrezzatura = connection.prepareStatement("UPDATE attrezzatura SET numero_seriale=?,descrizione=?,ID_aula=?, version=? WHERE ID=? and version=?");
            dAttrezzatura = connection.prepareStatement("DELETE FROM attrezzatura WHERE ID=?");

        } catch (SQLException ex) {
            throw new DataException("Error initializing auleweb data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        //Chiusura dei prepareStatement
        try {

            sAttrezzaturaByID.close();
            sAttrezzaturaByNumeroSeriale.close();
            sAttrezzaturaByAula.close();
            sAttrezzatura.close();
            iAttrezzatura.close();
            uAttrezzatura.close();
            dAttrezzatura.close();

        } catch (SQLException ex) {

        }
        super.destroy();
    }

    @Override
    public Attrezzatura createAttrezzatura() {
        return new AttrezzaturaProxy(getDataLayer());
    }

    private AttrezzaturaProxy createAttrezzatura(ResultSet rs) throws DataException {
        AttrezzaturaProxy a = (AttrezzaturaProxy) createAttrezzatura();

        try {
            a.setKey(rs.getInt("ID"));
            a.setNumeroSeriale(rs.getString("numero_seriale"));
            a.setDescrizione(rs.getString("descrizione"));
            a.setAulaKey(rs.getInt("ID_aula"));
            a.setVersion(rs.getLong("version"));
        } catch (SQLException ex) {
            throw new DataException("Unable to create attrezzatura object from ResultSet", ex);
        }

        return a;
    }

    @Override
    public Attrezzatura getAttrezzatura(int attrezzatura_key) throws DataException {
        Attrezzatura a = null;

        if (dataLayer.getCache().has(Attrezzatura.class, attrezzatura_key)) {
            a = dataLayer.getCache().get(Attrezzatura.class, attrezzatura_key);
        } else {
            try {
                sAttrezzaturaByID.setInt(1, attrezzatura_key);
                try ( ResultSet rs = sAttrezzaturaByID.executeQuery()) {
                    if (rs.next()) {
                        a = createAttrezzatura(rs);
                        dataLayer.getCache().add(Attrezzatura.class, a);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Unable to load attrezzatura by ID", ex);
            }
        }

        return a;
    }

    @Override
    public Attrezzatura getAttrezzatura(String numeroSeriale) throws DataException {
        Attrezzatura a = null;

        try {
            sAttrezzaturaByNumeroSeriale.setString(1, numeroSeriale);
            try ( ResultSet rs = sAttrezzaturaByNumeroSeriale.executeQuery()) {
                if (rs.next()) {
                    a = createAttrezzatura(rs);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load attrezzatura by numero seriale", ex);
        }

        return a;
    }

    @Override
    public List<Attrezzatura> getAttrezzatura() throws DataException {
        List<Attrezzatura> result = new ArrayList();

        try {
            try ( ResultSet rs = sAttrezzatura.executeQuery()) {
                while (rs.next()) {
                    result.add((Attrezzatura) getAttrezzatura(rs.getInt("attrezzaturaID")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load attrezzatura", ex);
        }

        return result;
    }

    @Override
    public List<Attrezzatura> getAttrezzatura(Aula aula) throws DataException {
        List<Attrezzatura> result = new ArrayList();

        try {
            sAttrezzaturaByAula.setInt(1, aula.getKey());
            try ( ResultSet rs = sAttrezzaturaByAula.executeQuery()) {
                while (rs.next()) {
                    result.add((Attrezzatura) getAttrezzatura(rs.getInt("attrezzaturaID")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load attrezzatura by aula", ex);
        }

        return result;
    }

    @Override
    public void setAula(Aula aula, Attrezzatura attrezzatura) throws DataException {
        attrezzatura.setAula(aula);
        List<Attrezzatura> attr = aula.getAttrezzatura();
        attr.add(attrezzatura);
        aula.setAttrezzatura(attr);
        storeAttrezzatura(attrezzatura);

    }

    @Override
    public void removeFromAula(Aula aula, Attrezzatura attrezzatura) throws DataException {
        attrezzatura.setAula(null);
        storeAttrezzatura(attrezzatura);
    }

    @Override
    public void storeAttrezzatura(Attrezzatura attrezzatura) throws DataException {
        try {
            if (attrezzatura.getKey() != null && attrezzatura.getKey() > 0) { //update
                if (attrezzatura instanceof DataItemProxy && !((DataItemProxy) attrezzatura).isModified()) {
                    return;
                }
                uAttrezzatura.setString(1, attrezzatura.getNumeroSeriale());
                uAttrezzatura.setString(2, attrezzatura.getDescrizione());
                if (attrezzatura.getAula() != null) {
                    uAttrezzatura.setInt(3, attrezzatura.getAula().getKey());
                } else {
                    uAttrezzatura.setNull(3, java.sql.Types.INTEGER);
                }

                long current_version = attrezzatura.getVersion();
                long next_version = current_version + 1;

                uAttrezzatura.setLong(4, next_version);
                uAttrezzatura.setInt(5, attrezzatura.getKey());
                uAttrezzatura.setLong(6, current_version);

                if (uAttrezzatura.executeUpdate() == 0) {
                    throw new OptimisticLockException(attrezzatura);
                } else {
                    attrezzatura.setVersion(next_version);
                }
            } else { //insert
                iAttrezzatura.setString(1, attrezzatura.getNumeroSeriale());
                iAttrezzatura.setString(2, attrezzatura.getDescrizione());
                if (attrezzatura.getAula() != null) {
                    iAttrezzatura.setInt(3, attrezzatura.getAula().getKey());
                } else {
                    iAttrezzatura.setNull(3, java.sql.Types.INTEGER);
                }
                if (iAttrezzatura.executeUpdate() == 1) {
                    try ( ResultSet keys = iAttrezzatura.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            attrezzatura.setKey(key);
                            dataLayer.getCache().add(Attrezzatura.class, attrezzatura);
                        }
                    }
                }
            }

            if (attrezzatura instanceof DataItemProxy) {
                ((DataItemProxy) attrezzatura).setModified(false);
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Unable to store attrezzatura", ex);
        }
    }

    @Override
    public void deleteAttrezzatura(Attrezzatura attrezzatura) throws DataException {
        if (attrezzatura.getKey() == null || attrezzatura.getKey() <= 0) {
            return;
        }

        try {
            dAttrezzatura.setInt(1, attrezzatura.getKey());
            if (dAttrezzatura.executeUpdate() == 0) {
                throw new OptimisticLockException(attrezzatura);
            } else if (dataLayer.getCache().has(Attrezzatura.class, attrezzatura.getKey())) {
                dataLayer.getCache().delete(Attrezzatura.class, attrezzatura.getKey());
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Unable to delete attrezzatura", ex);
        }
    }

}
