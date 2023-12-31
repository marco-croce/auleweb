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
import java.util.Collections;
import java.util.List;

public class EventiAttualiServlet extends AuleWebBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, TemplateManagerException {

        try {
            TemplateResult res = new TemplateResult(getServletContext());
            Gruppo gruppo = ((AuleWebDataLayer) request.getAttribute("datalayer")).getGruppoDAO().getGruppo(Integer.parseInt(request.getParameter("gruppo")));
            List<Evento> eventi = ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoDAO().getEventi(gruppo);
            List<Evento> eventiAttuali = ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoDAO().getEventiAttuali();
            
            List<EventoRicorrente> eventiRicAttuali = ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoRicorrenteDAO().getEventiRicorrentiAttuali();
            List<EventoRicorrente> eventiRic = ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoRicorrenteDAO().getEventiRicorrenti(gruppo);

            eventi.retainAll(eventiAttuali);
            eventiRic.retainAll(eventiRicAttuali);

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
            
            Collections.sort(eventi, (Evento e1, Evento e2) -> e1.getDataInizio().compareTo(e2.getDataInizio()));

            request.setAttribute("eventi", eventi);
            request.setAttribute("gruppo", gruppo.getNome());

            request.setAttribute("aule", ((AuleWebDataLayer) request.getAttribute("datalayer")).getAulaDAO().getAule());
            request.setAttribute("gruppi", ((AuleWebDataLayer) request.getAttribute("datalayer")).getGruppoDAO().getGruppi());
            request.setAttribute("corsi", ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoDAO().getCorsi());
            res.activate("attuali.ftl.html", request, response);
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
        return "Eventi attuali mediante servlet";
    }

}
