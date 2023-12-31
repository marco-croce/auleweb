package it.univaq.f4i.iw.ex.auleweb.controller;

import it.univaq.f4i.iw.ex.auleweb.data.dao.AuleWebDataLayer;
import it.univaq.f4i.iw.ex.auleweb.data.impl.TipologiaEvento;
import it.univaq.f4i.iw.ex.auleweb.data.impl.TipologiaRicorrenza;
import it.univaq.f4i.iw.ex.auleweb.data.model.Evento;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.result.TemplateManagerException;
import it.univaq.f4i.iw.framework.result.TemplateResult;
import it.univaq.f4i.iw.framework.security.SecurityHelpers;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EventiAdminServlet extends AuleWebBaseController {

    private final DateTimeFormatter formatter1 = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private final DateTimeFormatter formatter2 = DateTimeFormatter.ISO_DATE;

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, TemplateManagerException {
        try {
            TemplateResult res = new TemplateResult(getServletContext());
            List<Evento> eventi = ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoDAO().getEventi();
            Collections.sort(eventi, (Evento e1, Evento e2) -> e1.getDataInizio().compareTo(e2.getDataInizio()));
            request.setAttribute("eventi", eventi);
            res.activate("eventiadmin.ftl.html", request, response);
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_manage(HttpServletRequest request, HttpServletResponse response, int evento_key) throws IOException, ServletException, TemplateManagerException {
        try {
            TemplateResult res = new TemplateResult(getServletContext());
            if (evento_key > 0) {
                Evento evento = ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoDAO().getEvento(evento_key);
                if (evento != null) {
                    request.setAttribute("evento", evento);
                    request.setAttribute("responsabili", ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoDAO().getResponsabili());
                    request.setAttribute("aule", ((AuleWebDataLayer) request.getAttribute("datalayer")).getAulaDAO().getAule());
                    request.setAttribute("tipologie", TipologiaEvento.values());
                    request.setAttribute("ricorrenze", TipologiaRicorrenza.values());
                    request.setAttribute("corsi", ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoDAO().getCorsi());

                    res.activate("gestioneEvento.ftl.html", request, response);
                } else {
                    handleError("Undefined evento", request, response);
                }
            } else {
                Evento evento = ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoDAO().createEvento();
                request.setAttribute("evento", evento);
                request.setAttribute("responsabili", ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoDAO().getResponsabili());
                request.setAttribute("aule", ((AuleWebDataLayer) request.getAttribute("datalayer")).getAulaDAO().getAule());
                request.setAttribute("tipologie", TipologiaEvento.values());
                request.setAttribute("corsi", ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoDAO().getCorsi());
                request.setAttribute("ricorrenze", TipologiaRicorrenza.values());

                res.activate("gestioneEvento.ftl.html", request, response);
            }
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_update(HttpServletRequest request, HttpServletResponse response, int evento_key) throws IOException, ServletException, TemplateManagerException {
        try {
            Evento evento;
            if (evento_key > 0) {
                evento = ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoDAO().getEvento(evento_key);
            } else {
                evento = ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoDAO().createEvento();
            }
            if (evento != null
                    && request.getParameter("nome") != null
                    && request.getParameter("data") != null
                    && request.getParameter("ora_inizio") != null
                    && request.getParameter("ora_fine") != null
                    && request.getParameter("emailResponsabile") != null
                    && request.getParameter("descrizione") != null
                    && request.getParameter("aula") != null
                    && request.getParameter("tipologia") != null) {
                evento.setNome(request.getParameter("nome"));
                String dataInizio = request.getParameter("data").concat("T").concat(request.getParameter("ora_inizio"));
                evento.setDataInizio(LocalDateTime.parse(dataInizio, formatter1));
                String dataFine = request.getParameter("data").concat("T").concat(request.getParameter("ora_fine"));
                evento.setDataFine(LocalDateTime.parse(dataFine, formatter1));
                evento.setEmailResponsabile(request.getParameter("emailResponsabile"));
                evento.setDescrizione(request.getParameter("descrizione"));
                evento.setAula(((AuleWebDataLayer) request.getAttribute("datalayer")).getAulaDAO().getAula(Integer.parseInt(request.getParameter("aula"))));
                evento.setTipologia(TipologiaEvento.valueOf(request.getParameter("tipologia")));

                if (evento.getTipologia() == TipologiaEvento.ESAME
                        || evento.getTipologia() == TipologiaEvento.PARZIALE
                        || evento.getTipologia() == TipologiaEvento.LEZIONE) {
                    evento.setNomeCorso(request.getParameter("corso"));
                }
                if (!request.getParameter("tipologiaRicorrenza").equals("")) {
                    evento.setTipologiaRicorrenza(TipologiaRicorrenza.valueOf(request.getParameter("tipologiaRicorrenza")));
                    evento.setDataFineRicorrenza(LocalDate.parse(request.getParameter("data_fine_ricorrenza"), formatter2));
                }
                ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoDAO().storeEvento(evento);
                action_default(request, response);
            } else {
                handleError("Cannot update evento: insufficient parameters", request, response);
            }
        } catch (DataException ex) {
            // Mostra una finestra modale con il messaggio di errore di MySQL
            request.setAttribute("error", ex.getMessage());
            action_default(request, response);
        }
    }

    private void action_delete(HttpServletRequest request, HttpServletResponse response, int evento_key) throws IOException, ServletException, TemplateManagerException {
        try {
            Evento evento = ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoDAO().getEvento(evento_key);
            if (evento != null && request.getParameter("delete") != null) {
                ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoDAO().deleteEvento(evento);
                action_default(request, response);
            } else {
                handleError("Undefined evento", request, response);
            }
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        int evento_key;

        try {
            if (request.getParameter("n") != null) {
                evento_key = SecurityHelpers.checkNumeric(request.getParameter("n"));

                if (request.getParameter("update") != null || request.getParameter("delete") != null) {
                    if (request.getParameter("update") != null) {
                        action_update(request, response, evento_key);
                    } else {
                        action_delete(request, response, evento_key);
                    }
                } else {
                    action_manage(request, response, evento_key);
                }
            } else {
                action_default(request, response);
            }
        } catch (IOException | TemplateManagerException ex) {
            handleError(ex, request, response);
        }

    }

    @Override
    public String getServletInfo() {
        return "Gestione eventi da parte dell'admin mediante servlet";
    }

}
