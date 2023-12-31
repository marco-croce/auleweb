package it.univaq.f4i.iw.ex.auleweb.data.dao;

import it.univaq.f4i.iw.ex.auleweb.data.impl.TipologiaEvento;
import it.univaq.f4i.iw.ex.auleweb.data.impl.TipologiaRicorrenza;
import it.univaq.f4i.iw.ex.auleweb.data.model.Aula;
import it.univaq.f4i.iw.ex.auleweb.data.model.Evento;
import it.univaq.f4i.iw.ex.auleweb.data.model.Gruppo;
import it.univaq.f4i.iw.ex.auleweb.data.proxy.EventoProxy;
import it.univaq.f4i.iw.framework.data.DAO;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.data.DataItemProxy;
import it.univaq.f4i.iw.framework.data.DataLayer;
import it.univaq.f4i.iw.framework.data.OptimisticLockException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EventoDAO_MySQL extends DAO implements EventoDAO {

    private PreparedStatement sEventoByID;
    private PreparedStatement sEventi, sEventiByAula, sEventiByCorso, sEventiByDate, sEventiByGiorno, sEventiAttuali, sEventiByGruppo;
    private PreparedStatement sCorsi, sCorsiGruppo;
    private PreparedStatement sResponsabili;
    private PreparedStatement iEvento, uEvento, dEvento;

    public EventoDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            //Precompilazione delle query
            sEventoByID = connection.prepareStatement("SELECT * FROM evento WHERE ID=?");
            sEventi = connection.prepareStatement("SELECT ID AS eventoID FROM evento WHERE DATEDIFF(DATE(data_inizio), now() ) >= 0");
            sEventiByAula = connection.prepareStatement("SELECT ID AS eventoID FROM evento WHERE ID_aula=?");
            sEventiByCorso = connection.prepareStatement("SELECT ID AS eventoID FROM evento WHERE nome_corso=?");
            sEventiByDate = connection.prepareStatement("SELECT ID AS eventoID FROM evento WHERE DATEDIFF(DATE(data_inizio),?) >= 0 AND DATEDIFF(DATE(data_inizio),?) <= 0");
            sEventiByGiorno = connection.prepareStatement("SELECT ID AS eventoID FROM evento WHERE DATEDIFF(data_inizio,?)=0");
            sEventiAttuali = connection.prepareStatement("SELECT ID AS eventoID FROM evento "
                    + "WHERE SUBTIME( DATE(data_fine),TIME( now() ) ) >= 0 AND "
                    + "SUBTIME( TIME(data_inizio), TIME( DATE_ADD( now() , INTERVAL 3 HOUR) ) ) <= 0 AND "
                    + "DATEDIFF(DATE(data_inizio), now() ) = 0");
            sEventiByGruppo = connection.prepareStatement("SELECT e.ID AS eventoID FROM evento e JOIN gruppo_aula ga "
                    + "ON e.ID_aula = ga.ID_aula WHERE ga.ID_gruppo = ?");
            sCorsi = connection.prepareStatement("SELECT DISTINCT nome_corso FROM evento WHERE nome_corso IS NOT NULL");
            sCorsiGruppo = connection.prepareStatement("SELECT DISTINCT evento.nome_corso "
                    + "FROM evento JOIN aula ON evento.ID_aula = aula.ID JOIN gruppo_aula ON aula.ID = gruppo_aula.ID_aula JOIN gruppo ON gruppo_aula.ID_gruppo = gruppo.ID "
                    + "WHERE gruppo.nome = ? AND nome_corso IS NOT NULL");
            sResponsabili = connection.prepareStatement("SELECT DISTINCT email_responsabile FROM evento");
            iEvento = connection.prepareStatement("INSERT "
                    + "INTO evento (data_inizio,data_fine,nome,descrizione,email_responsabile,ID_aula,tipologia,nome_corso,tipo_ricorrenza,data_fine_ricorrenza) "
                    + "VALUES(?,?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            uEvento = connection.prepareStatement("UPDATE "
                    + "evento SET data_inizio=?,data_fine=?,nome=?,descrizione=?,email_responsabile=?,ID_aula=?,tipologia=?,nome_corso=?,tipo_ricorrenza=?,data_fine_ricorrenza=?,version=? "
                    + "WHERE ID=? and version=?");
            dEvento = connection.prepareStatement("DELETE FROM evento WHERE ID=?");

        } catch (SQLException ex) {
            throw new DataException("Error initializing auleweb data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        //Chiusura dei prepareStatement
        try {

            sEventoByID.close();
            sEventi.close();
            sEventiByAula.close();
            sEventiByCorso.close();
            sEventiByDate.close();
            sEventiByGiorno.close();
            sEventiAttuali.close();
            sCorsi.close();
            sCorsiGruppo.close();
            sResponsabili.close();
            iEvento.close();
            uEvento.close();
            dEvento.close();

        } catch (SQLException ex) {

        }
        super.destroy();
    }

    @Override
    public Evento createEvento() {
        return new EventoProxy(getDataLayer());
    }

    private EventoProxy createEvento(ResultSet rs) throws DataException {
        EventoProxy e = (EventoProxy) createEvento();

        try {
            e.setKey(rs.getInt("ID"));
            e.setDataInizio(rs.getDate("data_inizio").toLocalDate().atTime(rs.getTime("data_inizio").toLocalTime()));
            e.setDataFine(rs.getDate("data_fine").toLocalDate().atTime(rs.getTime("data_fine").toLocalTime()));
            e.setNome(rs.getString("nome"));
            e.setDescrizione(rs.getString("descrizione"));
            e.setEmailResponsabile(rs.getString("email_responsabile"));
            e.setAulaKey(rs.getInt("ID_aula"));
            e.setTipologia(TipologiaEvento.valueOf(rs.getString("tipologia")));
            e.setNomeCorso(rs.getString("nome_corso"));
            if (rs.getString("tipo_ricorrenza") != null) {
                e.setTipologiaRicorrenza(TipologiaRicorrenza.valueOf(rs.getString("tipo_ricorrenza")));
            } else {
                e.setTipologiaRicorrenza(null);
            }
            if (rs.getDate("data_fine_ricorrenza") != null) {
                e.setDataFineRicorrenza(rs.getDate("data_fine_ricorrenza").toLocalDate());
            } else {
                e.setDataFineRicorrenza(null);
            }
            e.setVersion(rs.getLong("version"));
        } catch (SQLException ex) {
            throw new DataException("Unable to create evento object from ResultSet", ex);
        }

        return e;
    }

    @Override
    public Evento getEvento(int evento_key) throws DataException {
        Evento e = null;

        if (dataLayer.getCache().has(Evento.class, evento_key)) {
            e = dataLayer.getCache().get(Evento.class, evento_key);
        } else {
            try {
                sEventoByID.setInt(1, evento_key);
                try ( ResultSet rs = sEventoByID.executeQuery()) {
                    if (rs.next()) {
                        e = createEvento(rs);
                        dataLayer.getCache().add(Evento.class, e);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Unable to load evento by ID", ex);
            }
        }

        return e;
    }

    @Override
    public List<Evento> getEventi(Aula aula) throws DataException {
        List<Evento> result = new ArrayList();

        try {
            sEventiByAula.setInt(1, aula.getKey());
            try ( ResultSet rs = sEventiByAula.executeQuery()) {
                while (rs.next()) {
                    result.add((Evento) getEvento(rs.getInt("eventoID")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load eventi by aula", ex);
        }

        return result;
    }

    @Override
    public List<Evento> getEventi(String corso) throws DataException {
        List<Evento> result = new ArrayList();

        try {
            sEventiByCorso.setString(1, corso);
            try ( ResultSet rs = sEventiByCorso.executeQuery()) {
                while (rs.next()) {
                    result.add((Evento) getEvento(rs.getInt("eventoID")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load eventi by corso", ex);
        }
        return result;
    }

    @Override
    public List<Evento> getEventi(LocalDate inizio, LocalDate fine) throws DataException {
        List<Evento> result = new ArrayList();

        try {
            sEventiByDate.setString(1, inizio.format(DateTimeFormatter.ISO_DATE));
            sEventiByDate.setString(2, fine.format(DateTimeFormatter.ISO_DATE));
            try ( ResultSet rs = sEventiByDate.executeQuery()) {
                while (rs.next()) {
                    result.add((Evento) getEvento(rs.getInt("eventoID")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load eventi by date", ex);
        }

        return result;
    }

    @Override
    public List<Evento> getEventi(LocalDate giorno) throws DataException {
        List<Evento> result = new ArrayList();

        try {
            sEventiByGiorno.setString(1, giorno.format(DateTimeFormatter.ISO_DATE));
            try ( ResultSet rs = sEventiByGiorno.executeQuery()) {
                while (rs.next()) {
                    result.add((Evento) getEvento(rs.getInt("eventoID")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load eventi by giorno", ex);
        }

        return result;
    }

    @Override
    public List<Evento> getEventiAttuali() throws DataException {
        List<Evento> result = new ArrayList();

        try {
            try ( ResultSet rs = sEventiAttuali.executeQuery()) {
                while (rs.next()) {
                    result.add((Evento) getEvento(rs.getInt("eventoID")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load eventi attuali", ex);
        }

        return result;
    }

    @Override
    public List<Evento> getEventi(Gruppo gruppo) throws DataException {
        List<Evento> result = new ArrayList();

        try {
            sEventiByGruppo.setInt(1, gruppo.getKey());
            try ( ResultSet rs = sEventiByGruppo.executeQuery()) {
                while (rs.next()) {
                    result.add((Evento) getEvento(rs.getInt("eventoID")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load eventi by gruppo", ex);
        }

        return result;
    }

    @Override
    public List<Evento> getEventi() throws DataException {
        List<Evento> result = new ArrayList();

        try ( ResultSet rs = sEventi.executeQuery()) {
            while (rs.next()) {
                result.add(getEvento(rs.getInt("eventoID")));
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load eventi", ex);
        }
        return result;
    }

    @Override
    public List<String> getCorsi() throws DataException {
        List<String> result = new ArrayList();

        try ( ResultSet rs = sCorsi.executeQuery()) {
            while (rs.next()) {
                result.add(rs.getString("nome_corso"));
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load corsi", ex);
        }

        return result;
    }

    @Override
    public List<String> getCorsi(Gruppo gruppo) throws DataException {
        List<String> result = new ArrayList();

        try {
            sCorsiGruppo.setString(1, gruppo.getNome());
            try ( ResultSet rs = sCorsiGruppo.executeQuery()) {
                while (rs.next()) {
                    result.add(rs.getString("nome_corso"));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load corsi by gruppo", ex);
        }

        return result;
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
    public void storeEvento(Evento evento) throws DataException {
        try {
            if (evento.getKey() != null && evento.getKey() > 0) { //update
                if (evento instanceof DataItemProxy && !((DataItemProxy) evento).isModified()) {
                    return;
                }

                uEvento.setString(1, evento.getDataInizio().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                uEvento.setString(2, evento.getDataFine().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                uEvento.setString(3, evento.getNome());
                uEvento.setString(4, evento.getDescrizione());
                uEvento.setString(5, evento.getEmailResponsabile());
                if (evento.getAula() != null) {
                    uEvento.setInt(6, evento.getAula().getKey());
                } else {
                    uEvento.setNull(6, java.sql.Types.INTEGER);
                }
                uEvento.setString(7, evento.getTipologia().toString());
                if (evento.getTipologia() != TipologiaEvento.ESAME
                        && evento.getTipologia() != TipologiaEvento.PARZIALE
                        && evento.getTipologia() != TipologiaEvento.LEZIONE) {
                    uEvento.setNull(8, java.sql.Types.VARCHAR);
                } else {
                    uEvento.setString(8, evento.getNomeCorso());
                }
                if (evento.getTipologiaRicorrenza() != null) {
                    uEvento.setString(9, evento.getTipologiaRicorrenza().toString());
                    uEvento.setString(10, evento.getDataFineRicorrenza().format(DateTimeFormatter.ISO_LOCAL_DATE));
                } else {
                    uEvento.setNull(9, java.sql.Types.VARCHAR);
                    uEvento.setNull(10, java.sql.Types.DATE);
                }

                long current_version = evento.getVersion();
                long next_version = current_version + 1;

                uEvento.setLong(11, next_version);
                uEvento.setInt(12, evento.getKey());
                uEvento.setLong(13, current_version);

                if (uEvento.executeUpdate() == 0) {
                    throw new OptimisticLockException(evento);
                } else {
                    evento.setVersion(next_version);
                }
            } else { //insert
                iEvento.setString(1, evento.getDataInizio().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                iEvento.setString(2, evento.getDataFine().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                iEvento.setString(3, evento.getNome());
                iEvento.setString(4, evento.getDescrizione());
                iEvento.setString(5, evento.getEmailResponsabile());
                if (evento.getAula() != null) {
                    iEvento.setInt(6, evento.getAula().getKey());
                } else {
                    iEvento.setNull(6, java.sql.Types.INTEGER);
                }
                iEvento.setString(7, evento.getTipologia().toString());
                if (evento.getTipologia() != TipologiaEvento.ESAME
                        && evento.getTipologia() != TipologiaEvento.PARZIALE
                        && evento.getTipologia() != TipologiaEvento.LEZIONE) {
                    iEvento.setNull(8, java.sql.Types.VARCHAR);
                } else {
                    iEvento.setString(8, evento.getNomeCorso());
                }
                if (evento.getTipologiaRicorrenza() != null) {
                    iEvento.setString(9, evento.getTipologiaRicorrenza().toString());
                    iEvento.setString(10, evento.getDataFineRicorrenza().format(DateTimeFormatter.ISO_LOCAL_DATE));
                } else {
                    iEvento.setNull(9, java.sql.Types.VARCHAR);
                    iEvento.setNull(10, java.sql.Types.DATE);
                }

                if (iEvento.executeUpdate() == 1) {
                    try ( ResultSet keys = iEvento.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            evento.setKey(key);
                            dataLayer.getCache().add(Evento.class, evento);
                        }
                    }
                }
            }

            if (evento instanceof DataItemProxy) {
                ((DataItemProxy) evento).setModified(false);
            } else {
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Unable to store evento", ex);
        }
    }

    @Override
    public void deleteEvento(Evento evento) throws DataException {
        if (evento.getKey() == null || evento.getKey() <= 0) {
            return;
        }

        try {
            dEvento.setInt(1, evento.getKey());
            if (dEvento.executeUpdate() == 0) {
                throw new OptimisticLockException(evento);
            } else if (dataLayer.getCache().has(Evento.class, evento.getKey())) {
                dataLayer.getCache().delete(Evento.class, evento.getKey());
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Unable to delete evento", ex);
        }
    }

}
