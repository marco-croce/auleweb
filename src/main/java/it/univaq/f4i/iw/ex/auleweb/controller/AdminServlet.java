package it.univaq.f4i.iw.ex.auleweb.controller;

import it.univaq.f4i.iw.ex.auleweb.data.dao.AuleWebDataLayer;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.result.TemplateManagerException;
import it.univaq.f4i.iw.framework.result.TemplateResult;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AdminServlet extends AuleWebBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, TemplateManagerException {
        TemplateResult result = new TemplateResult(getServletContext());
        try {
            request.setAttribute("aule", ((AuleWebDataLayer) request.getAttribute("datalayer")).getAulaDAO().getAule());
            request.setAttribute("eventi", ((AuleWebDataLayer) request.getAttribute("datalayer")).getEventoDAO().getEventi());
            request.setAttribute("gruppi", ((AuleWebDataLayer) request.getAttribute("datalayer")).getGruppoDAO().getGruppi());
        } catch (DataException ex) {
            handleError(ex, request, response);
        }
        result.activate("admin.ftl.html", request, response);
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
        return "Gestione homepage admin servlet";
    }

}
