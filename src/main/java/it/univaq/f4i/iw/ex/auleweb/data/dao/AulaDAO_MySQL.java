package it.univaq.f4i.iw.ex.auleweb.data.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import it.univaq.f4i.iw.ex.auleweb.data.model.Attrezzatura;
import it.univaq.f4i.iw.ex.auleweb.data.model.Aula;
import it.univaq.f4i.iw.ex.auleweb.data.model.Evento;
import it.univaq.f4i.iw.ex.auleweb.data.model.Gruppo;
import it.univaq.f4i.iw.ex.auleweb.data.proxy.AulaProxy;
import it.univaq.f4i.iw.framework.data.DAO;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.data.DataItemProxy;
import it.univaq.f4i.iw.framework.data.DataLayer;
import it.univaq.f4i.iw.framework.data.OptimisticLockException;

public class AulaDAO_MySQL extends DAO implements AulaDAO {

    private PreparedStatement sAulaById, sAula, sAulaByEvento;
    private PreparedStatement sAule, sAuleByGruppo, sAuleByNPreseRete, sAuleByNPreseElettriche,
            sAuleByCapienza, sAuleByPiano, sAuleByLuogo, sAuleByEdificio;
    private PreparedStatement sResponsabili;
    private PreparedStatement iGruppo, dGruppo;
    private PreparedStatement iAula, uAula, dAula;

    private final AttrezzaturaDAO attrezzaturaDAO;

    public AulaDAO_MySQL(DataLayer d) {
        super(d);
        attrezzaturaDAO = (AttrezzaturaDAO) d.getDAO(Attrezzatura.class);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            //Precompilazione delle query
            sAulaById = connection.prepareStatement("SELECT * FROM aula WHERE ID=?");
            sAula = connection.prepareStatement("SELECT * FROM aula WHERE nome=? AND edificio=? AND luogo=? AND piano=?");
            sAulaByEvento = connection.prepareStatement("SELECT a.* FROM evento e JOIN aula a ON e.ID_aula = a.ID WHERE e.ID=?");
            sAule = connection.prepareStatement("SELECT ID AS aulaID FROM aula");
            sAuleByGruppo = connection.prepareStatement("SELECT ID AS aulaID FROM aula JOIN gruppo_aula ON aula.ID = gruppo_aula.ID_aula WHERE ID_gruppo=?");
            sAuleByNPreseRete = connection.prepareStatement("SELECT ID AS aulaID FROM aula WHERE numero_prese_rete >=?");
            sAuleByNPreseElettriche = connection.prepareStatement("SELECT ID AS aulaID FROM aula WHERE numero_prese_elettriche >=?");
            sAuleByCapienza = connection.prepareStatement("SELECT ID AS aulaID FROM aula WHERE capienza >=?");
            sAuleByPiano = connection.prepareStatement("SELECT ID AS aulaID FROM aula WHERE piano =?");
            sAuleByLuogo = connection.prepareStatement("SELECT ID AS aulaID FROM aula WHERE luogo =?");
            sAuleByEdificio = connection.prepareStatement("SELECT ID AS aulaID FROM aula WHERE edificio =?");
            sResponsabili = connection.prepareStatement("SELECT DISTINCT email_responsabile FROM aula");

            iGruppo = connection.prepareStatement("INSERT INTO gruppo_aula (ID_aula, ID_gruppo) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            dGruppo = connection.prepareStatement("DELETE FROM gruppo_aula WHERE ID_aula=? AND ID_gruppo=?");

            iAula = connection.prepareStatement("INSERT INTO aula (nome, luogo, edificio, piano, capienza, email_responsabile, numero_prese_rete, numero_prese_elettriche, note)"
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            uAula = connection.prepareStatement("UPDATE aula"
                    + " SET nome = ?, luogo = ?, edificio = ?, piano = ?, capienza = ?, email_responsabile = ?, numero_prese_rete = ?, numero_prese_elettriche = ?, note = ?, version=?"
                    + " WHERE ID = ? AND version=?");
            dAula = connection.prepareStatement("DELETE FROM aula WHERE ID=?");

        } catch (SQLException ex) {
            throw new DataException("Error initializing auleweb data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        //Chiusura dei prepareStatement
        try {

            sAula.close();
            sAulaByEvento.close();
            sAulaById.close();
            sAule.close();
            sAuleByGruppo.close();
            sAuleByNPreseRete.close();
            sAuleByNPreseElettriche.close();
            sAuleByCapienza.close();
            sAuleByPiano.close();
            sAuleByLuogo.close();
            sAuleByEdificio.close();
            sResponsabili.close();
            iGruppo.close();
            dGruppo.close();
            iAula.close();
            uAula.close();
            dAula.close();

        } catch (SQLException ex) {

        }
        super.destroy();
    }

    @Override
    public Aula createAula() {
        return new AulaProxy(getDataLayer());
    }

    private AulaProxy createAula(ResultSet rs) throws DataException {
        AulaProxy a = (AulaProxy) createAula();

        try {
            a.setKey(rs.getInt("ID"));
            a.setNome(rs.getString("nome"));
            a.setCapienza(rs.getInt("capienza"));
            a.setEdificio(rs.getString("edificio"));
            a.setEmailResponsabile(rs.getString("email_responsabile"));
            a.setLuogo(rs.getString("luogo"));
            a.setNote(rs.getString("note"));
            a.setNumeroPreseElettriche(rs.getInt("numero_prese_elettriche"));
            a.setNumeroPreseRete(rs.getInt("numero_prese_rete"));
            a.setPiano(rs.getInt("piano"));
            a.setVersion(rs.getLong("version"));
            List<Attrezzatura> attr = attrezzaturaDAO.getAttrezzatura(a);
            a.setAttrezzatura(attr);

        } catch (SQLException ex) {
            throw new DataException("Unable to create aula object from ResultSet", ex);
        }

        return a;
    }

    @Override
    public Aula getAula(int aula_key) throws DataException {
        Aula a = null;

        if (dataLayer.getCache().has(Aula.class, aula_key)) {
            a = dataLayer.getCache().get(Aula.class, aula_key);
        } else {
            try {
                sAulaById.setInt(1, aula_key);
                try ( ResultSet rs = sAulaById.executeQuery()) {
                    if (rs.next()) {
                        a = createAula(rs);
                        dataLayer.getCache().add(Aula.class, a);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Unable to load aula by ID", ex);
            }
        }

        return a;
    }

    @Override
    public Aula getAula(String edificio, String luogo, int piano, String nome) throws DataException {
        Aula a = null;

        try {
            sAula.setString(1, nome);
            sAula.setString(2, edificio);
            sAula.setString(3, luogo);
            sAula.setInt(4, piano);

            try ( ResultSet rs = sAula.executeQuery()) {
                if (rs.next()) {
                    a = createAula(rs);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load aula", ex);
        }

        return a;
    }

    @Override
    public Aula getAula(Evento evento) throws DataException {
        Aula a = null;

        try {
            sAulaByEvento.setInt(1, evento.getKey());

            try ( ResultSet rs = sAulaByEvento.executeQuery()) {
                if (rs.next()) {
                    a = createAula(rs);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load aula by evento", ex);
        }

        return a;
    }

    @Override
    public List<Aula> getAule() throws DataException {
        List<Aula> aule = new ArrayList<>();

        try ( ResultSet rs = sAule.executeQuery()) {
            while (rs.next()) {
                aule.add((Aula) getAula(rs.getInt("aulaID")));
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load aule", ex);
        }

        return aule;
    }

    @Override
    public List<Aula> getAule(Gruppo gruppo) throws DataException {
        List<Aula> aule = new ArrayList<>();

        try {
            sAuleByGruppo.setInt(1, gruppo.getKey());
            try ( ResultSet rs = sAuleByGruppo.executeQuery()) {
                while (rs.next()) {
                    aule.add((Aula) getAula(rs.getInt("aulaID")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load aule by gruppo", ex);
        }

        return aule;
    }

    @Override
    public List<Aula> getAuleByPreseRete(int numeroPreseRete) throws DataException {
        List<Aula> aule = new ArrayList<>();

        try {
            sAuleByNPreseRete.setInt(1, numeroPreseRete);
            try ( ResultSet rs = sAuleByNPreseRete.executeQuery()) {
                while (rs.next()) {
                    aule.add((Aula) getAula(rs.getInt("aulaID")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load aule by numero di prese di rete", ex);
        }

        return aule;
    }

    @Override
    public List<Aula> getAuleByPreseElettriche(int numeroPreseElettriche) throws DataException {
        List<Aula> aule = new ArrayList<>();

        try {
            sAuleByNPreseElettriche.setInt(1, numeroPreseElettriche);
            try ( ResultSet rs = sAuleByNPreseElettriche.executeQuery()) {
                while (rs.next()) {
                    aule.add((Aula) getAula(rs.getInt("aulaID")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load aule by numero di prese elettriche", ex);
        }

        return aule;
    }

    @Override
    public List<Aula> getAuleByCapienza(int capienza) throws DataException {
        List<Aula> aule = new ArrayList<>();

        try {
            sAuleByCapienza.setInt(1, capienza);
            try ( ResultSet rs = sAuleByCapienza.executeQuery()) {
                while (rs.next()) {
                    aule.add((Aula) getAula(rs.getInt("aulaID")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load aule by capienza", ex);
        }

        return aule;
    }

    @Override
    public List<Aula> getAuleByPiano(int piano) throws DataException {
        List<Aula> aule = new ArrayList<>();

        try {
            sAuleByPiano.setInt(1, piano);
            try ( ResultSet rs = sAuleByPiano.executeQuery()) {
                while (rs.next()) {
                    aule.add((Aula) getAula(rs.getInt("aulaID")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load aule by piano", ex);
        }

        return aule;
    }

    @Override
    public List<Aula> getAuleByLuogo(String luogo) throws DataException {
        List<Aula> aule = new ArrayList<>();

        try {
            sAuleByLuogo.setString(1, luogo);
            try ( ResultSet rs = sAuleByLuogo.executeQuery()) {
                while (rs.next()) {
                    aule.add((Aula) getAula(rs.getInt("aulaID")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load aule by luogo", ex);
        }

        return aule;
    }

    @Override
    public List<Aula> getAuleByEdificio(String edificio) throws DataException {
        List<Aula> aule = new ArrayList<>();

        try {
            sAuleByEdificio.setString(1, edificio);
            try ( ResultSet rs = sAuleByEdificio.executeQuery()) {
                while (rs.next()) {
                    aule.add((Aula) getAula(rs.getInt("aulaID")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load aule by edificio", ex);
        }

        return aule;
    }

    @Override
    public List<String> getResponsabili() throws DataException {
        List<String> resp = new ArrayList<>();

        try {
            try ( ResultSet rs = sResponsabili.executeQuery()) {
                while (rs.next()) {
                    resp.add((String) rs.getString("email_responsabile"));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load responsabili", ex);
        }

        return resp;
    }

    @Override
    public void setGruppo(Gruppo gruppo, Aula aula) throws DataException {
        try {
            iGruppo.setInt(1, aula.getKey());
            iGruppo.setInt(2, gruppo.getKey());

            if (iGruppo.executeUpdate() == 1) {
                try ( ResultSet keys = iAula.getGeneratedKeys()) {
                    if (keys.next()) {
                        aula.addGruppo(gruppo);
                        gruppo.addAula(aula);
                        dataLayer.getCache().add(Aula.class, aula);
                        dataLayer.getCache().add(Gruppo.class, gruppo);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to set gruppo", ex);
        }
    }

    @Override
    public void removeFromGruppo(Gruppo gruppo, Aula aula) throws DataException {
        if (aula.getKey() == null || aula.getKey() <= 0
                || gruppo.getKey() == null || gruppo.getKey() <= 0) {
            return;
        }

        try {
            dGruppo.setInt(1, aula.getKey());
            dGruppo.setInt(2, gruppo.getKey());
            if (dGruppo.executeUpdate() == 0) {
                throw new OptimisticLockException(aula);
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Unable to remove aula from gruppo", ex);
        }
    }

    @Override
    public void storeAula(Aula aula) throws DataException {
        try {
            if (aula.getKey() != null && aula.getKey() > 0) { //update
                if (aula instanceof DataItemProxy && !((DataItemProxy) aula).isModified()) {
                    return;
                }
                uAula.setString(1, aula.getNome());
                uAula.setString(2, aula.getLuogo());
                uAula.setString(3, aula.getEdificio());
                uAula.setInt(4, aula.getPiano());
                uAula.setInt(5, aula.getCapienza());
                uAula.setString(6, aula.getEmailResponsabile());
                uAula.setInt(7, aula.getNumeroPreseRete());
                uAula.setInt(8, aula.getNumeroPreseElettriche());
                uAula.setString(9, aula.getNote());
                long current_version = aula.getVersion();
                long next_version = current_version + 1;

                uAula.setLong(10, next_version);
                uAula.setInt(11, aula.getKey());
                uAula.setLong(12, current_version);

                if (uAula.executeUpdate() == 0) {
                    throw new OptimisticLockException(aula);
                } else {
                    aula.setVersion(next_version);
                }
            } else { //insert
                iAula.setString(1, aula.getNome());
                iAula.setString(2, aula.getLuogo());
                iAula.setString(3, aula.getEdificio());
                iAula.setInt(4, aula.getPiano());
                iAula.setInt(5, aula.getCapienza());
                iAula.setString(6, aula.getEmailResponsabile());
                iAula.setInt(7, aula.getNumeroPreseRete());
                iAula.setInt(8, aula.getNumeroPreseElettriche());
                iAula.setString(9, aula.getNote());

                if (iAula.executeUpdate() == 1) {
                    try ( ResultSet keys = iAula.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            aula.setKey(key);
                            dataLayer.getCache().add(Aula.class, aula);
                        }
                    }
                }
            }

            if (aula instanceof DataItemProxy) {
                ((DataItemProxy) aula).setModified(false);
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Unable to store aula", ex);
        }
    }

    @Override
    public void deleteAula(Aula aula) throws DataException {
        if (aula.getKey() == null || aula.getKey() <= 0) {
            return;
        }

        try {
            dAula.setInt(1, aula.getKey());
            if (dAula.executeUpdate() == 0) {
                throw new OptimisticLockException(aula);
            } else if (dataLayer.getCache().has(Aula.class, aula.getKey())) {
                dataLayer.getCache().delete(Aula.class, aula.getKey());
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Unable to delete aula", ex);
        }
    }

}
