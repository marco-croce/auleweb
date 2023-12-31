package it.univaq.f4i.iw.ex.auleweb.controller;

import it.univaq.f4i.iw.ex.auleweb.data.dao.AuleWebDataLayer;
import it.univaq.f4i.iw.ex.auleweb.data.impl.EventoImpl;
import it.univaq.f4i.iw.ex.auleweb.data.model.Aula;
import it.univaq.f4i.iw.ex.auleweb.data.model.Evento;
import it.univaq.f4i.iw.ex.auleweb.data.model.EventoRicorrente;
import it.univaq.f4i.iw.ex.auleweb.data.model.Gruppo;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.result.CSVWriter;
import it.univaq.f4i.iw.framework.result.StreamResult;
import it.univaq.f4i.iw.framework.result.TemplateManagerException;
import it.univaq.f4i.iw.framework.result.TemplateResult;
import java.io.IOException;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HomeServlet extends AuleWebBaseController {

    //Si usa nel caso in cui l'utente richieda il download degli eventi relativi al corso
    private String latest_corso_requested = null;

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, TemplateManagerException {
        try {
            TemplateResult res = new TemplateResult(getServletContext());

            //Mostra il tasto di download per la lista degli eventi in un intervallo nel template
            request.setAttribute("download_home", "download_home");
            //Nasconde il tasto di download per la lista degli eventi del corso nel template
            request.setAttribute("download_corso", null);

            request.setAttribute("gruppi", ((AuleWebDataLayer) request.getAttribute("datalayer")).getGruppoDAO().getGruppi());

            List<Evento> eventi;
            List<EventoRicorrente> eventiRic;

            // il gruppo è stato selezionato dall'utente
            if (request.getParameter("gruppo") != null) {
                Gruppo gruppo = ((AuleWebDataLayer) request.getAttribute("datalayer")).getGruppoDAO().getGruppo(Integer.parseInt(request.getParameter("gruppo")));
                request.setAttribute("aule", ((AuleWebDataLayer) request.getAttribute("datalayer")).getAulaDAO().getAule(gruppo));
                request.setAttribute("corsi", ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoDAO().getCorsi(gruppo));
                request.setAttribute("gruppo", gruppo.getNome());
                eventi = ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoDAO().getEventi(gruppo);
                eventiRic = ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoRicorrenteDAO().getEventiRicorrenti(gruppo);
            } else { // il gruppo non è stato ancora selezionato dall'utente
                eventi = ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoDAO().getEventi();
                eventiRic = ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoRicorrenteDAO().getEventiRicorrenti();
            }

            for (EventoRicorrente er : eventiRic) {
                Evento ric = er.getEventoMaster();
                Evento e = new EventoImpl();
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
            res.activate("eventi.ftl.html", request, response);
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_corso(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, TemplateManagerException {
        try {
            TemplateResult res = new TemplateResult(getServletContext());
            //Mostra il tasto di download per la lista degli eventi del corso nel template
            request.setAttribute("download_corso", "corso");

            // parametro gruppo
            Gruppo gruppo = ((AuleWebDataLayer) request.getAttribute("datalayer")).getGruppoDAO().getGruppo(Integer.parseInt(request.getParameter("gruppo")));
            
            request.setAttribute("aule", ((AuleWebDataLayer) request.getAttribute("datalayer")).getAulaDAO().getAule(gruppo));
            request.setAttribute("gruppi", ((AuleWebDataLayer) request.getAttribute("datalayer")).getGruppoDAO().getGruppi());
            request.setAttribute("corsi", ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoDAO().getCorsi(gruppo));

            //Memorizza l'ultimo corso richiesto nel caso in cui l'utente voglia richiedere poi un elenco in CSV
            latest_corso_requested = request.getParameter("corso");

            List<Evento> eventi = ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoDAO().getEventi(latest_corso_requested);
            List<EventoRicorrente> eventiRic = ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoRicorrenteDAO().getEventiRicorrenti(latest_corso_requested);

            // Parametri anno e numero della settimana 
            int year = Integer.parseInt(request.getParameter("year"));
            int weekNumber = Integer.parseInt(request.getParameter("week"));

            /*  
                Costruzione della settimana settando anche l'anno, così, nel caso
                in cui l'utente richieda la visualizzazione in una settimana 
                dell'anno successivo a quello in cui ci si trova attualmente, non
                ci saranno errori. 
             */
            LocalDate week = LocalDate.now().withYear(year).with(ChronoField.ALIGNED_WEEK_OF_YEAR, weekNumber);

            // Lunedì
            LocalDate first = week.with(DayOfWeek.MONDAY);
            // Domenica
            LocalDate last = first.plusDays(6);

            List<Evento> eventiWeek = ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoDAO().getEventi(first, last);
            List<EventoRicorrente> eventiRicWeek = ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoRicorrenteDAO().getEventiRicorrenti(first, last);
            eventi.retainAll(eventiWeek);
            eventiRic.retainAll(eventiRicWeek);

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

            // Su Freemarker vengono usate nel titolo della tabella
            request.setAttribute("gruppo", gruppo.getNome());
            request.setAttribute("corso", latest_corso_requested);

            request.setAttribute("eventi", eventi);
            res.activate("eventi.ftl.html", request, response);
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_aula(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, TemplateManagerException {
        try {
            TemplateResult res = new TemplateResult(getServletContext());
            //Nasconde il tasto di download per la lista del corso nel template
            request.setAttribute("download_corso", null);

            // parametro gruppo
            Gruppo gruppo = ((AuleWebDataLayer) request.getAttribute("datalayer")).getGruppoDAO().getGruppo(Integer.parseInt(request.getParameter("gruppo")));
            
            request.setAttribute("aule", ((AuleWebDataLayer) request.getAttribute("datalayer")).getAulaDAO().getAule(gruppo));
            request.setAttribute("gruppi", ((AuleWebDataLayer) request.getAttribute("datalayer")).getGruppoDAO().getGruppi());
            request.setAttribute("corsi", ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoDAO().getCorsi(gruppo));

            Aula aula = ((AuleWebDataLayer) request.getAttribute("datalayer")).getAulaDAO().getAula(Integer.parseInt(request.getParameter("aula")));
            List<Evento> eventi = ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoDAO().getEventi(aula);
            List<EventoRicorrente> eventiRic = ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoRicorrenteDAO().getEventiRicorrenti(aula);

            // Parametri anno e numero della settimana 
            int year = Integer.parseInt(request.getParameter("year"));
            int weekNumber = Integer.parseInt(request.getParameter("week"));

            /*  
                Costruzione della settimana settando anche l'anno, così, nel caso
                in cui l'utente richieda la visualizzazione in una settimana 
                dell'anno successivo a quello in cui ci si trova attualmente, non
                ci saranno errori. 
             */
            LocalDate week = LocalDate.now().withYear(year).with(ChronoField.ALIGNED_WEEK_OF_YEAR, weekNumber);

            // Lunedì
            LocalDate first = week.with(DayOfWeek.MONDAY);
            // Domenica
            LocalDate last = first.plusDays(6);

            List<Evento> eventiWeek = ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoDAO().getEventi(first, last);
            List<EventoRicorrente> eventiRicWeek = ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoRicorrenteDAO().getEventiRicorrenti(first, last);
            eventi.retainAll(eventiWeek);
            eventiRic.retainAll(eventiRicWeek);

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

            // Su Freemarker vengono usate nel titolo della tabella
            request.setAttribute("gruppo", ((AuleWebDataLayer) request.getAttribute("datalayer")).getGruppoDAO().getGruppo(Integer.parseInt(request.getParameter("gruppo"))).getNome());
            request.setAttribute("aula", aula.getNome() + " - edificio " + aula.getEdificio());

            request.setAttribute("eventi", eventi);
            res.activate("eventi.ftl.html", request, response);
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    // Download lista degli eventi associati a un determinato corso
    private void action_download(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            StreamResult result = new StreamResult(getServletContext());
            //Tutti gli eventi associati all'ultimo corso selezionato
            List<Evento> eventi = ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoDAO().getEventi(latest_corso_requested);
            List<EventoRicorrente> eventiRic = ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoRicorrenteDAO().getEventiRicorrenti(latest_corso_requested);
            
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
            
            CSVWriter w = new CSVWriter();
            w.csv_corso(eventi, getServletContext().getRealPath(""));

            URL url = getServletContext().getResource("/" + "csv" + "/" + "eventi_corso.csv");
            result.setResource(url);
            result.activate(request, response);
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    // Download lista degli eventi pianificati in un determinato intervallo di tempo
    private void action_download2(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            StreamResult result = new StreamResult(getServletContext());

            // first --> Inizio intervallo di tempo / last --> Fine intervallo di tempo
            LocalDate first = LocalDate.parse(request.getParameter("first"));
            LocalDate last = LocalDate.parse(request.getParameter("last"));

            //Tutti gli eventi pianificati in quel determinato intervallo di tempo
            List<Evento> eventi = ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoDAO().getEventi(first, last);
            List<EventoRicorrente> eventiRic = ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoRicorrenteDAO().getEventiRicorrenti(first, last);
           
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
            
            CSVWriter w = new CSVWriter();
            w.csv_time(eventi, getServletContext().getRealPath(""));

            URL url = getServletContext().getResource("/" + "csv" + "/" + "eventi_time.csv");
            result.setResource(url);
            result.activate(request, response);
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {

            // Richiesta download eventi corso
            if (request.getParameter("csv") != null) {
                action_download(request, response);
                return;
            }
            // Richiesta download eventi in un intervallo di tempo
            if (request.getParameter("csv2") != null) {
                action_download2(request, response);
                return;
            }

            if (request.getParameter("gruppo") != null) {
                if (request.getParameter("aula") != null || request.getParameter("corso") != null) {

                    if (request.getParameter("corso") != null) {
                        action_corso(request, response);
                        return;
                    } else {
                        action_aula(request, response);
                        return;
                    }

                }
            }

            action_default(request, response);

        } catch (IOException | TemplateManagerException ex) {
            handleError(ex, request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Main auleweb servlet";
    }

}
