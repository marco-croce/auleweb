package it.univaq.f4i.iw.ex.auleweb.data.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import it.univaq.f4i.iw.ex.auleweb.data.model.Aula;
import it.univaq.f4i.iw.ex.auleweb.data.model.Gruppo;
import it.univaq.f4i.iw.ex.auleweb.data.proxy.GruppoProxy;
import it.univaq.f4i.iw.framework.data.DAO;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.data.DataItemProxy;
import it.univaq.f4i.iw.framework.data.DataLayer;
import it.univaq.f4i.iw.framework.data.OptimisticLockException;

public class GruppoDAO_MySQL extends DAO implements GruppoDAO {

    private PreparedStatement sGruppoByID, sGruppoByNome, sGruppi, sGruppiByAula;
    private PreparedStatement iGruppo, uGruppo, dGruppo;

    public GruppoDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            //Precompilazione delle query
            sGruppoByID = connection.prepareStatement("SELECT * FROM gruppo WHERE ID=?");
            sGruppoByNome = connection.prepareStatement("SELECT * FROM gruppo WHERE nome=?");
            sGruppi = connection.prepareStatement("SELECT ID AS gruppoID FROM gruppo");
            sGruppiByAula = connection.prepareStatement("SELECT ID_gruppo AS gruppoID FROM gruppo_aula WHERE ID_aula=?");

            iGruppo = connection.prepareStatement("INSERT INTO gruppo (nome,descrizione) VALUES(?,?)", Statement.RETURN_GENERATED_KEYS);
            uGruppo = connection.prepareStatement("UPDATE gruppo SET nome=?,descrizione=?,version=? WHERE ID=? and version=?");
            dGruppo = connection.prepareStatement("DELETE FROM gruppo WHERE ID=?");

        } catch (SQLException ex) {
            throw new DataException("Error initializing auleweb data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        //Chiusura dei prepareStatement
        try {

            sGruppoByID.close();
            sGruppoByNome.close();
            sGruppi.close();
            sGruppiByAula.close();
            iGruppo.close();
            uGruppo.close();
            dGruppo.close();

        } catch (SQLException ex) {

        }
        super.destroy();
    }

    @Override
    public Gruppo createGruppo() {
        return new GruppoProxy(getDataLayer());
    }

    private GruppoProxy createGruppo(ResultSet rs) throws DataException {
        GruppoProxy g = (GruppoProxy) createGruppo();

        try {
            g.setKey(rs.getInt("ID"));
            g.setNome(rs.getString("nome"));
            g.setDescrizione(rs.getString("descrizione"));
            g.setVersion(rs.getLong("version"));
        } catch (SQLException ex) {
            throw new DataException("Unable to create gruppo object from ResultSet", ex);
        }

        return g;
    }

    @Override
    public Gruppo getGruppo(int gruppo_key) throws DataException {
        Gruppo gruppo = null;

        if (dataLayer.getCache().has(Gruppo.class, gruppo_key)) {
            gruppo = dataLayer.getCache().get(Gruppo.class, gruppo_key);
        } else {
            try {
                sGruppoByID.setInt(1, gruppo_key);
                try ( ResultSet rs = sGruppoByID.executeQuery()) {
                    if (rs.next()) {
                        gruppo = createGruppo(rs);
                        dataLayer.getCache().add(Gruppo.class, gruppo);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Unable to load gruppo by ID", ex);
            }
        }

        return gruppo;
    }

    @Override
    public Gruppo getGruppo(String nome) throws DataException {
        Gruppo gruppo = null;

        try {
            sGruppoByNome.setString(1, nome);
            try ( ResultSet rs = sGruppoByNome.executeQuery()) {
                if (rs.next()) {
                    gruppo = createGruppo(rs);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load gruppo by nome", ex);
        }

        return gruppo;
    }

    @Override
    public List<Gruppo> getGruppi() throws DataException {
        List<Gruppo> gruppi = new ArrayList<>();

        try ( ResultSet rs = sGruppi.executeQuery()) {
            while (rs.next()) {
                gruppi.add((Gruppo) getGruppo(rs.getInt("gruppoID")));
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load gruppi", ex);
        }

        return gruppi;
    }

    @Override
    public List<Gruppo> getGruppi(Aula aula) throws DataException {
        List<Gruppo> gruppi = new ArrayList<>();

        try {
            sGruppiByAula.setInt(1, aula.getKey());
            try ( ResultSet rs = sGruppiByAula.executeQuery()) {
                while (rs.next()) {
                    gruppi.add((Gruppo) getGruppo(rs.getInt("gruppoID")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load gruppi by aula", ex);
        }

        return gruppi;
    }

    @Override
    public void storeGruppo(Gruppo gruppo) throws DataException {
        try {
            if (gruppo.getKey() != null && gruppo.getKey() > 0) { //update
                if (gruppo instanceof DataItemProxy && !((DataItemProxy) gruppo).isModified()) {
                    return;
                }
                uGruppo.setString(1, gruppo.getNome());
                uGruppo.setString(2, gruppo.getDescrizione());

                long current_version = gruppo.getVersion();
                long next_version = current_version + 1;

                uGruppo.setLong(3, next_version);
                uGruppo.setInt(4, gruppo.getKey());
                uGruppo.setLong(5, current_version);

                if (uGruppo.executeUpdate() == 0) {
                    throw new OptimisticLockException(gruppo);
                } else {
                    gruppo.setVersion(next_version);
                }
            } else { //insert

                iGruppo.setString(1, gruppo.getNome());
                iGruppo.setString(2, gruppo.getDescrizione());

                if (iGruppo.executeUpdate() == 1) {
                    try ( ResultSet keys = iGruppo.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            gruppo.setKey(key);
                            dataLayer.getCache().add(Gruppo.class, gruppo);
                        }
                    }
                }
            }

            if (gruppo instanceof DataItemProxy) {
                ((DataItemProxy) gruppo).setModified(false);
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Unable to store gruppo", ex);
        }
    }

    @Override
    public void deleteGruppo(Gruppo gruppo) throws DataException {
        if (gruppo.getKey() == null || gruppo.getKey() <= 0) {
            return;
        }

        try {
            dGruppo.setInt(1, gruppo.getKey());
            if (dGruppo.executeUpdate() == 0) {
                throw new OptimisticLockException(gruppo);
            } else if (dataLayer.getCache().has(Gruppo.class, gruppo.getKey())) {
                dataLayer.getCache().delete(Gruppo.class, gruppo.getKey());
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Unable to delete gruppo", ex);
        }
    }

}
