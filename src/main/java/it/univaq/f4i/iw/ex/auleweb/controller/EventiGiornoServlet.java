package it.univaq.f4i.iw.ex.auleweb.controller;

import it.univaq.f4i.iw.framework.result.TemplateManagerException;
import it.univaq.f4i.iw.framework.result.TemplateResult;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import it.univaq.f4i.iw.ex.auleweb.data.dao.AuleWebDataLayer;
import it.univaq.f4i.iw.ex.auleweb.data.impl.EventoImpl;
import it.univaq.f4i.iw.ex.auleweb.data.model.Evento;
import it.univaq.f4i.iw.ex.auleweb.data.model.EventoRicorrente;
import it.univaq.f4i.iw.ex.auleweb.data.model.Gruppo;
import it.univaq.f4i.iw.framework.data.DataException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class EventiGiornoServlet extends AuleWebBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, TemplateManagerException {

        try {
            TemplateResult res = new TemplateResult(getServletContext());
            Gruppo gruppo = ((AuleWebDataLayer) request.getAttribute("datalayer")).getGruppoDAO().getGruppo(Integer.parseInt(request.getParameter("gruppo")));
            LocalDate giorno = LocalDate.parse(request.getParameter("giornata"));

            List<Evento> eventi = ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoDAO().getEventi(gruppo);
            List<Evento> eventiGiornata = ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoDAO().getEventi(giorno);

            List<EventoRicorrente> eventiRic = ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoRicorrenteDAO().getEventiRicorrenti(gruppo);
            List<EventoRicorrente> eventiRicGiornata = ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoRicorrenteDAO().getEventiRicorrenti(giorno);

            eventi.retainAll(eventiGiornata);
            eventiRic.retainAll(eventiRicGiornata);

            for (EventoRicorrente er : eventiRic) {
                Evento e = new EventoImpl();
                Evento ric = er.getEventoMaster();
                e.setNome(ric.getNome());
                e.setDataFine(er.getDataFine());
                e.setDataInizio(er.getDataInizio());
                e.setEmailResponsabile(ric.getEmailResponsabile());
                e.setDescrizione(ric.getDescrizione());
                e.setNomeCorso(ric.getNomeCorso());
                e.setTipologia(ric.getTipologia());
                e.setTipologiaRicorrenza(ric.getTipologiaRicorrenza());
                e.setDataFineRicorrenza(ric.getDataFineRicorrenza());
                e.setAula(ric.getAula());
                eventi.add(e);
            }

            /*
                Ordinamento degli eventi in base al nome dell'aula, nel caso
                in cui ci siano più eventi associati alla stessa aula in quella
                giornata, allora si ordina in base all'orario di inizio.
             */
            Collections.sort(eventi, (Evento e1, Evento e2) -> {
                if (e1.getAula().getNome().compareTo(e2.getAula().getNome()) != 0) {
                    return e1.getAula().getNome().compareTo(e2.getAula().getNome());
                } else {
                    return e1.getDataInizio().compareTo(e2.getDataInizio());
                }
            });

            request.setAttribute("eventi", eventi);
            
            request.setAttribute("gruppo", gruppo.getNome());

            //Mostra la giornata richiesta nel titolo del template
            request.setAttribute("giorno", giorno);

            request.setAttribute("aule", ((AuleWebDataLayer) request.getAttribute("datalayer")).getAulaDAO().getAule());
            request.setAttribute("gruppi", ((AuleWebDataLayer) request.getAttribute("datalayer")).getGruppoDAO().getGruppi());
            request.setAttribute("corsi", ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoDAO().getCorsi());
            res.activate("giorno.ftl.html", request, response);
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }

    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            action_default(request, response);
        } catch (IOException | TemplateManagerException ex) {
            handleError(ex, request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Eventi in un determinato giorno mediante servlet";
    }

}
