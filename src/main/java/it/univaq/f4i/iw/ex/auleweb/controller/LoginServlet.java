package it.univaq.f4i.iw.ex.auleweb.controller;

import it.univaq.f4i.iw.ex.auleweb.data.dao.AuleWebDataLayer;
import it.univaq.f4i.iw.ex.auleweb.data.model.Amministratore;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.result.TemplateManagerException;
import it.univaq.f4i.iw.framework.result.TemplateResult;
import it.univaq.f4i.iw.framework.security.SecurityHelpers;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginServlet extends AuleWebBaseController {

    // error = true indica che è avvenuto un errore in fase di login
    private boolean error = false;

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, TemplateManagerException {
        try {
            TemplateResult result = new TemplateResult(getServletContext());
            request.setAttribute("aule", ((AuleWebDataLayer) request.getAttribute("datalayer")).getAulaDAO().getAule());
            request.setAttribute("gruppi", ((AuleWebDataLayer) request.getAttribute("datalayer")).getGruppoDAO().getGruppi());
            request.setAttribute("corsi", ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoDAO().getCorsi());
            if (error) {
                request.setAttribute("error", "Email e/o password errati!"); // attiva finestra modale di errore mediante Freemarker
            }
            result.activate("login.ftl.html", request, response);
            // Reset attributo di errore
            error = false;
        } catch (DataException ex) {
            handleError(ex, request, response);
        }
    }

    private void action_login(HttpServletRequest request, HttpServletResponse response) throws IOException, TemplateManagerException {
        String email = request.getParameter("e");
        String password = request.getParameter("p");

        if (!email.isEmpty() && !password.isEmpty()) {
            try {

                Amministratore a = ((AuleWebDataLayer) request.getAttribute("datalayer")).getAmministratoreDAO().getAmministratoreByEmail(email);

                if (a != null && SecurityHelpers.checkHashSHA(password, a.getPassword())) {
                    TemplateResult result = new TemplateResult(getServletContext());
                    SecurityHelpers.createSession(request, email, a.getKey());

                    // Mostra un messaggio di benvenuto all'admin tramite Freemarker
                    request.setAttribute("admin", a.getNome() + " " + a.getCognome());
                    request.setAttribute("aule", ((AuleWebDataLayer) request.getAttribute("datalayer")).getAulaDAO().getAule());
                    request.setAttribute("eventi", ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoDAO().getEventi());
                    request.setAttribute("gruppi", ((AuleWebDataLayer) request.getAttribute("datalayer")).getGruppoDAO().getGruppi());
                    // Login avvenuta con successo
                    result.activate("admin.ftl.html", request, response);
                } else {
                    error = true;
                    // Login terminata con errore
                    response.sendRedirect("login");
                }
            } catch (DataException | NoSuchAlgorithmException ex) {
                handleError("Data access exception: " + ex.getMessage(), request, response);
            }
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        try {
            if (request.getParameter("login") != null) {
                action_login(request, response);
            } else {
                action_default(request, response);
            }
        } catch (IOException | TemplateManagerException ex) {
            handleError(ex, request, response);
        }

    }

}
