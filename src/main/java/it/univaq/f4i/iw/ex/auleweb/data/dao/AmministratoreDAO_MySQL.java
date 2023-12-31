package it.univaq.f4i.iw.ex.auleweb.data.dao;

import it.univaq.f4i.iw.ex.auleweb.data.model.Amministratore;
import it.univaq.f4i.iw.ex.auleweb.data.proxy.AmministratoreProxy;
import it.univaq.f4i.iw.framework.data.DAO;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.data.DataItemProxy;
import it.univaq.f4i.iw.framework.data.DataLayer;
import it.univaq.f4i.iw.framework.data.OptimisticLockException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AmministratoreDAO_MySQL extends DAO implements AmministratoreDAO {

    private PreparedStatement sAmministratoreById, sAmministratoreByEmail;
    private PreparedStatement iAmministratore, uAmministratore, dAmministratore;

    public AmministratoreDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            sAmministratoreById = connection.prepareStatement("SELECT * FROM amministratore WHERE ID=?");
            sAmministratoreByEmail = connection.prepareStatement("SELECT * FROM amministratore WHERE email = ? ");

            iAmministratore = connection.prepareStatement("INSERT INTO amministratore (nome,cognome,email,password,telefono) VALUES(?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            uAmministratore = connection.prepareStatement("UPDATE amministratore SET nome=?,cognome=?,email=?,password=?,telefono=? version=? WHERE ID=? and version=?");
            dAmministratore = connection.prepareStatement("DELETE FROM amministratore WHERE ID=?");

        } catch (SQLException ex) {
            throw new DataException("Error initializing auleweb data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {

        try {

            sAmministratoreById.close();
            sAmministratoreByEmail.close();
            iAmministratore.close();
            uAmministratore.close();
            dAmministratore.close();

        } catch (SQLException ex) {

        }
        super.destroy();
    }

    @Override
    public Amministratore createAmministratore() {
        return new AmministratoreProxy(getDataLayer());
    }

    private AmministratoreProxy createAmministratore(ResultSet rs) throws DataException {
        AmministratoreProxy a = (AmministratoreProxy) createAmministratore();
        try {
            a.setKey(rs.getInt("ID"));
            a.setNome(rs.getString("nome"));
            a.setCognome(rs.getString("cognome"));
            a.setEmail(rs.getString("email"));
            a.setPassword(rs.getString("password"));
            a.setTelefono(rs.getString("telefono"));
            a.setVersion(rs.getLong("version"));
        } catch (SQLException ex) {
            throw new DataException("Unable to create amministratore object from ResultSet", ex);
        }
        return a;
    }

    @Override
    public Amministratore getAmministratore(int amministratore_key) throws DataException {
        Amministratore a = null;

        if (dataLayer.getCache().has(Amministratore.class, amministratore_key)) {
            a = dataLayer.getCache().get(Amministratore.class, amministratore_key);
        } else {
            try {
                sAmministratoreById.setInt(1, amministratore_key);
                try ( ResultSet rs = sAmministratoreById.executeQuery()) {
                    if (rs.next()) {
                        a = createAmministratore(rs);
                        dataLayer.getCache().add(Amministratore.class, a);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Unable to load amministratore by ID", ex);
            }
        }

        return a;
    }

    @Override
    public Amministratore getAmministratoreByEmail(String email) throws DataException {

        try {
            sAmministratoreByEmail.setString(1, email);
            try ( ResultSet rs = sAmministratoreByEmail.executeQuery()) {
                if (rs.next()) {
                    return getAmministratore(rs.getInt("ID"));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load amministratore by email", ex);
        }

        return null;
    }

    @Override
    public void storeAmministratore(Amministratore amministratore) throws DataException {
        try {
            if (amministratore.getKey() != null && amministratore.getKey() > 0) { //update
                if (amministratore instanceof DataItemProxy && !((DataItemProxy) amministratore).isModified()) {
                    return;
                }
                uAmministratore.setString(1, amministratore.getNome());
                uAmministratore.setString(2, amministratore.getCognome());
                uAmministratore.setString(3, amministratore.getEmail());
                uAmministratore.setString(4, amministratore.getPassword());
                uAmministratore.setString(5, amministratore.getTelefono());

                long current_version = amministratore.getVersion();
                long next_version = current_version + 1;

                uAmministratore.setLong(6, next_version);
                uAmministratore.setInt(7, amministratore.getKey());
                uAmministratore.setLong(8, current_version);

                if (uAmministratore.executeUpdate() == 0) {
                    throw new OptimisticLockException(amministratore);
                } else {
                    amministratore.setVersion(next_version);
                }
            } else { //insert
                iAmministratore.setString(1, amministratore.getNome());
                iAmministratore.setString(2, amministratore.getCognome());
                iAmministratore.setString(3, amministratore.getEmail());
                iAmministratore.setString(4, amministratore.getPassword());
                iAmministratore.setString(5, amministratore.getTelefono());

                if (iAmministratore.executeUpdate() == 1) {
                    try ( ResultSet keys = iAmministratore.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            amministratore.setKey(key);
                            dataLayer.getCache().add(Amministratore.class, amministratore);
                        }
                    }
                }
            }

            if (amministratore instanceof DataItemProxy) {
                ((DataItemProxy) amministratore).setModified(false);
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Unable to store amministratore", ex);
        }
    }

    @Override
    public void deleteAmministratore(Amministratore amministratore) throws DataException {
        if (amministratore.getKey() == null || amministratore.getKey() <= 0) {
            return;
        }
        try {
            dAmministratore.setInt(1, amministratore.getKey());
            if (dAmministratore.executeUpdate() == 0) {
                throw new OptimisticLockException(amministratore);
            } else if (dataLayer.getCache().has(Amministratore.class, amministratore.getKey())) {
                dataLayer.getCache().delete(Amministratore.class, amministratore.getKey());
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Unable to delete amministratore", ex);
        }
    }

}
