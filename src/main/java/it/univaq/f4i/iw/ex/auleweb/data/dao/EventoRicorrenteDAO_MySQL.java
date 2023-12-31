package it.univaq.f4i.iw.ex.auleweb.data.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.univaq.f4i.iw.ex.auleweb.data.model.Aula;
import it.univaq.f4i.iw.ex.auleweb.data.model.Evento;
import it.univaq.f4i.iw.ex.auleweb.data.model.EventoRicorrente;
import it.univaq.f4i.iw.ex.auleweb.data.model.Gruppo;
import it.univaq.f4i.iw.ex.auleweb.data.proxy.EventoRicorrenteProxy;
import it.univaq.f4i.iw.framework.data.DAO;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.data.DataLayer;
import it.univaq.f4i.iw.framework.data.OptimisticLockException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EventoRicorrenteDAO_MySQL extends DAO implements EventoRicorrenteDAO {

    private PreparedStatement sEventoRicorrenteById, sEventoMaster, sEventiRicorrenti, sEventiRicorrentiByEvento;
    private PreparedStatement sEventiRicorrentiByAula, sEventiRicorrentiByCorso, sEventiRicorrentiByDate, sEventiRicorrentiByGiorno, sEventiRicorrentiAttuali, sEventiRicorrentiByGruppo;
    private PreparedStatement dEventoRicorrente;

    private final EventoDAO eventoDAO;

    public EventoRicorrenteDAO_MySQL(DataLayer d) {
        super(d);
        eventoDAO = (EventoDAO) d.getDAO(Evento.class);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            //Precompilazione delle query
            sEventoRicorrenteById = connection.prepareStatement("SELECT * FROM evento_ricorrente WHERE ID=?");
            sEventoMaster = connection.prepareStatement("SELECT ID_master FROM evento_ricorrente WHERE ID=?");
            sEventiRicorrenti = connection.prepareStatement("SELECT ID AS eventoRicorrenteID FROM evento_ricorrente");
            sEventiRicorrentiByEvento = connection.prepareStatement("SELECT ID AS eventoRicorrenteID FROM evento_ricorrente WHERE ID_master=?");
            sEventiRicorrentiByAula = connection.prepareStatement("SELECT er.ID AS eventoRicorrenteID FROM evento e JOIN evento_ricorrente er ON er.ID_master = e.ID WHERE e.ID_aula=?");
            sEventiRicorrentiByCorso = connection.prepareStatement("SELECT er.ID AS eventoRicorrenteID FROM evento e JOIN evento_ricorrente er ON er.ID_master = e.ID WHERE e.nome_corso=?");
            sEventiRicorrentiByDate = connection.prepareStatement("SELECT ID AS eventoRicorrenteID FROM evento_ricorrente WHERE DATEDIFF(DATE(data_inizio),?) >= 0 AND DATEDIFF(DATE(data_inizio),?) <= 0");
            sEventiRicorrentiByGiorno = connection.prepareStatement("SELECT ID AS eventoRicorrenteID FROM evento_ricorrente WHERE DATEDIFF(data_inizio,?)=0");
            sEventiRicorrentiAttuali = connection.prepareStatement("SELECT ID AS eventoRicorrenteID FROM evento_ricorrente "
                    + "WHERE SUBTIME( DATE(data_fine),TIME( now() ) ) >= 0 AND "
                    + "SUBTIME( TIME(data_inizio), TIME( DATE_ADD( now() , INTERVAL 3 HOUR) ) ) <= 0 AND "
                    + "DATEDIFF(DATE(data_inizio), now() ) = 0");
            sEventiRicorrentiByGruppo = connection.prepareStatement("SELECT er.ID AS eventoRicorrenteID FROM evento e JOIN evento_ricorrente er ON er.ID_master = e.ID "
                    + "JOIN gruppo_aula ga ON ga.ID_aula = e.ID_aula WHERE ga.ID_gruppo=?");
            dEventoRicorrente = connection.prepareStatement("DELETE FROM evento_ricorrente WHERE ID=?");

        } catch (SQLException ex) {
            throw new DataException("Error initializing auleweb data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        //Chiusura dei prepareStatement
        try {

            sEventoRicorrenteById.close();
            sEventoMaster.close();
            sEventiRicorrenti.close();
            sEventiRicorrentiByEvento.close();
            sEventiRicorrentiByAula.close();
            sEventiRicorrentiByCorso.close();
            sEventiRicorrentiByDate.close();
            sEventiRicorrentiByGiorno.close();
            sEventiRicorrentiByGruppo.close();

            dEventoRicorrente.close();

        } catch (SQLException ex) {

        }
        super.destroy();
    }

    @Override
    public EventoRicorrente createEventoRicorrente() {
        return new EventoRicorrenteProxy(getDataLayer());
    }

    private EventoRicorrenteProxy createEventoRicorrente(ResultSet rs) throws DataException {
        EventoRicorrenteProxy e = (EventoRicorrenteProxy) createEventoRicorrente();

        try {
            e.setKey(rs.getInt("ID"));
            e.setDataInizio(rs.getDate("data_inizio").toLocalDate().atTime(rs.getTime("data_inizio").toLocalTime()));
            e.setDataFine(rs.getDate("data_fine").toLocalDate().atTime(rs.getTime("data_fine").toLocalTime()));
            e.setEventoMaster(eventoDAO.getEvento(rs.getInt("ID_master")));
            e.setEventoMasterKey(rs.getInt("ID_master"));
            e.setVersion(rs.getLong("version"));
        } catch (SQLException ex) {
            throw new DataException("Unable to create evento ricorrente object from ResultSet", ex);
        }

        return e;
    }

    @Override
    public EventoRicorrente getEventoRicorrente(int evento_ricorrente_key) throws DataException {
        EventoRicorrente e = null;

        if (dataLayer.getCache().has(EventoRicorrente.class, evento_ricorrente_key)) {
            e = dataLayer.getCache().get(EventoRicorrente.class, evento_ricorrente_key);
        } else {
            try {
                sEventoRicorrenteById.setInt(1, evento_ricorrente_key);
                try ( ResultSet rs = sEventoRicorrenteById.executeQuery()) {
                    if (rs.next()) {
                        e = createEventoRicorrente(rs);
                        dataLayer.getCache().add(EventoRicorrente.class, e);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Unable to load evento ricorrente by ID", ex);
            }
        }

        return e;
    }

    @Override
    public Evento getEventoMaster(int evento_ricorrente_key) throws DataException {

        Evento e = null;

        try {
            sEventoMaster.setInt(1, evento_ricorrente_key);

            try ( ResultSet rs = sEventoMaster.executeQuery()) {
                if (rs.next()) {
                    e = eventoDAO.getEvento(rs.getInt("ID_master"));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load evento master", ex);
        }

        return e;
    }

    @Override
    public List<EventoRicorrente> getEventiRicorrenti() throws DataException {
        List<EventoRicorrente> result = new ArrayList();

        try ( ResultSet rs = sEventiRicorrenti.executeQuery()) {
            while (rs.next()) {
                result.add(getEventoRicorrente(rs.getInt("eventoRicorrenteID")));
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load eventi ricorrenti", ex);
        }

        return result;
    }

    @Override
    public List<EventoRicorrente> getEventiRicorrenti(Evento evento) throws DataException {
        List<EventoRicorrente> result = new ArrayList();

        try {
            sEventiRicorrentiByEvento.setInt(1, evento.getKey());
            try ( ResultSet rs = sEventiRicorrentiByEvento.executeQuery()) {
                while (rs.next()) {
                    result.add((EventoRicorrente) getEventoRicorrente(rs.getInt("eventoRicorrenteID")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load eventi ricorrenti by evento", ex);
        }

        return result;
    }

    @Override
    public List<EventoRicorrente> getEventiRicorrenti(Aula aula) throws DataException {
        List<EventoRicorrente> result = new ArrayList();

        try {
            sEventiRicorrentiByAula.setInt(1, aula.getKey());
            try ( ResultSet rs = sEventiRicorrentiByAula.executeQuery()) {
                while (rs.next()) {
                    result.add((EventoRicorrente) getEventoRicorrente(rs.getInt("eventoRicorrenteID")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load eventi ricorrenti by aula", ex);
        }

        return result;
    }

    @Override
    public List<EventoRicorrente> getEventiRicorrenti(String corso) throws DataException {
        List<EventoRicorrente> result = new ArrayList();

        try {
            sEventiRicorrentiByCorso.setString(1, corso);
            try ( ResultSet rs = sEventiRicorrentiByCorso.executeQuery()) {
                while (rs.next()) {
                    result.add((EventoRicorrente) getEventoRicorrente(rs.getInt("eventoRicorrenteID")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load eventi ricorrenti by corso", ex);
        }

        return result;
    }

    @Override
    public List<EventoRicorrente> getEventiRicorrenti(LocalDate inizio, LocalDate fine) throws DataException {
        List<EventoRicorrente> result = new ArrayList();

        try {
            sEventiRicorrentiByDate.setString(1, inizio.format(DateTimeFormatter.ISO_DATE));
            sEventiRicorrentiByDate.setString(2, fine.format(DateTimeFormatter.ISO_DATE));
            try ( ResultSet rs = sEventiRicorrentiByDate.executeQuery()) {
                while (rs.next()) {
                    result.add((EventoRicorrente) getEventoRicorrente(rs.getInt("eventoRicorrenteID")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load eventi ricorrenti by date", ex);
        }

        return result;
    }

    @Override
    public List<EventoRicorrente> getEventiRicorrenti(LocalDate giorno) throws DataException {
        List<EventoRicorrente> result = new ArrayList();

        try {
            sEventiRicorrentiByGiorno.setString(1, giorno.format(DateTimeFormatter.ISO_DATE));
            try ( ResultSet rs = sEventiRicorrentiByGiorno.executeQuery()) {
                while (rs.next()) {
                    result.add((EventoRicorrente) getEventoRicorrente(rs.getInt("eventoRicorrenteID")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load eventi ricorrenti by giorno", ex);
        }

        return result;
    }

    @Override
    public List<EventoRicorrente> getEventiRicorrentiAttuali() throws DataException {
        List<EventoRicorrente> result = new ArrayList();

        try {
            try ( ResultSet rs = sEventiRicorrentiAttuali.executeQuery()) {
                while (rs.next()) {
                    result.add((EventoRicorrente) getEventoRicorrente(rs.getInt("eventoRicorrenteID")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load eventi ricorrenti attuali", ex);
        }

        return result;
    }

    @Override
    public List<EventoRicorrente> getEventiRicorrenti(Gruppo gruppo) throws DataException {
        List<EventoRicorrente> result = new ArrayList();

        try {
            sEventiRicorrentiByGruppo.setInt(1, gruppo.getKey());
            try ( ResultSet rs = sEventiRicorrentiByGruppo.executeQuery()) {
                while (rs.next()) {
                    result.add((EventoRicorrente) getEventoRicorrente(rs.getInt("eventoRicorrenteID")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load eventi ricorrenti by gruppo", ex);
        }

        return result;
    }

    @Override
    public void deleteEventoRicorrente(EventoRicorrente evento) throws DataException {
        if (evento.getKey() == null || evento.getKey() <= 0) {
            return;
        }
        try {
            dEventoRicorrente.setInt(1, evento.getKey());
            if (dEventoRicorrente.executeUpdate() == 0) {
                throw new OptimisticLockException(evento);
            } else if (dataLayer.getCache().has(EventoRicorrente.class, evento.getKey())) {
                dataLayer.getCache().delete(EventoRicorrente.class, evento.getKey());
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Unable to delete evento ricorrente", ex);
        }
    }

}
