package it.univaq.f4i.iw.ex.auleweb.controller;

import it.univaq.f4i.iw.framework.result.TemplateManagerException;
import it.univaq.f4i.iw.framework.result.TemplateResult;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import it.univaq.f4i.iw.ex.auleweb.data.dao.AuleWebDataLayer;
import it.univaq.f4i.iw.ex.auleweb.data.model.Attrezzatura;
import it.univaq.f4i.iw.ex.auleweb.data.model.Aula;
import it.univaq.f4i.iw.ex.auleweb.data.model.Gruppo;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.security.SecurityHelpers;
import java.util.Collections;
import java.util.List;

public class AuleAdminServlet extends AuleWebBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, TemplateManagerException {
        try {
            TemplateResult res = new TemplateResult(getServletContext());
            request.setAttribute("aule", ((AuleWebDataLayer) request.getAttribute("datalayer")).getAulaDAO().getAule());
            res.activate("auleadmin.ftl.html", request, response);
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_manage(HttpServletRequest request, HttpServletResponse response, int aula_key) throws IOException, ServletException, TemplateManagerException {
        try {
            TemplateResult res = new TemplateResult(getServletContext());
            if (aula_key > 0) {
                Aula aula = ((AuleWebDataLayer) request.getAttribute("datalayer")).getAulaDAO().getAula(aula_key);
                if (aula != null) {
                    request.setAttribute("aula", aula);
                    List<Gruppo> unassigned1 = ((AuleWebDataLayer) request.getAttribute("datalayer")).getGruppoDAO().getGruppi();
                    List<Gruppo> assigned1 = ((AuleWebDataLayer) request.getAttribute("datalayer")).getGruppoDAO().getGruppi(aula);
                    if (!assigned1.isEmpty()) {
                        unassigned1.removeAll(assigned1);
                    }
                    List<Attrezzatura> unassigned2 = ((AuleWebDataLayer) request.getAttribute("datalayer")).getAttrezzaturaDAO().getAttrezzatura();
                    List<Attrezzatura> assigned2 = ((AuleWebDataLayer) request.getAttribute("datalayer")).getAttrezzaturaDAO().getAttrezzatura(aula);
                    if (!assigned2.isEmpty()) {
                        unassigned2.removeAll(assigned2);
                    }
                    request.setAttribute("unassigned_gruppi", unassigned1);
                    request.setAttribute("assigned_gruppi", assigned1);
                    request.setAttribute("unassigned_attrezzatura", unassigned2);
                    request.setAttribute("assigned_attrezzatura", assigned2);
                    request.setAttribute("responsabili", ((AuleWebDataLayer) request.getAttribute("datalayer")).getAulaDAO().getResponsabili());

                    res.activate("gestioneAula.ftl.html", request, response);
                } else {
                    handleError("Undefined aula", request, response);
                }
            } else {
                Aula aula = ((AuleWebDataLayer) request.getAttribute("datalayer")).getAulaDAO().createAula();
                request.setAttribute("aula", aula);
                request.setAttribute("unassigned_gruppi", Collections.EMPTY_LIST);
                request.setAttribute("assigned_gruppi", Collections.EMPTY_LIST);
                request.setAttribute("unassigned_attrezzatura", Collections.EMPTY_LIST);
                request.setAttribute("assigned_attrezzatura", Collections.EMPTY_LIST);
                request.setAttribute("responsabili", ((AuleWebDataLayer) request.getAttribute("datalayer")).getAulaDAO().getResponsabili());
                res.activate("gestioneAula.ftl.html", request, response);
            }
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_update(HttpServletRequest request, HttpServletResponse response, int aula_key) throws IOException, ServletException, TemplateManagerException {
        try {
            Aula aula;
            if (aula_key > 0) {
                aula = ((AuleWebDataLayer) request.getAttribute("datalayer")).getAulaDAO().getAula(aula_key);
            } else {
                aula = ((AuleWebDataLayer) request.getAttribute("datalayer")).getAulaDAO().createAula();
            }
            if (aula != null
                    && request.getParameter("nome") != null
                    && request.getParameter("luogo") != null
                    && request.getParameter("edificio") != null
                    && request.getParameter("emailResponsabile") != null
                    && request.getParameter("piano") != null
                    && request.getParameter("capienza") != null
                    && request.getParameter("rete") != null
                    && request.getParameter("elettriche") != null
                    && request.getParameter("note") != null) {
                aula.setNome(request.getParameter("nome"));
                aula.setLuogo(request.getParameter("luogo"));
                aula.setEdificio(request.getParameter("edificio"));
                aula.setPiano(Integer.parseInt(request.getParameter("piano")));
                aula.setCapienza(Integer.parseInt(request.getParameter("capienza")));
                aula.setEmailResponsabile(request.getParameter("emailResponsabile"));
                aula.setNumeroPreseRete(Integer.parseInt(request.getParameter("rete")));
                aula.setNumeroPreseElettriche(Integer.parseInt(request.getParameter("elettriche")));
                aula.setNote(request.getParameter("note"));
                ((AuleWebDataLayer) request.getAttribute("datalayer")).getAulaDAO().storeAula(aula);
                action_default(request, response);
            } else {
                handleError("Cannot update aula: insufficient parameters", request, response);
            }
        } catch (DataException ex) {
            // Mostra una finestra modale con il messaggio di errore di MySQL
            request.setAttribute("error", ex.getMessage());
            action_default(request, response);
        }
    }

    private void action_add_gruppo(HttpServletRequest request, HttpServletResponse response, int aula_key) throws IOException, ServletException, TemplateManagerException {
        try {

            Aula aula = ((AuleWebDataLayer) request.getAttribute("datalayer")).getAulaDAO().getAula(aula_key);

            if (aula != null && request.getParameter("addgruppo") != null) {
                Gruppo gruppo = ((AuleWebDataLayer) request.getAttribute("datalayer")).getGruppoDAO().getGruppo(Integer.parseInt(request.getParameter("addgruppo")));
                if (gruppo != null) {
                    ((AuleWebDataLayer) request.getAttribute("datalayer")).getAulaDAO().setGruppo(gruppo, aula);
                    action_manage(request, response, aula_key);
                } else {
                    handleError("Cannot add undefined gruppo", request, response);
                }
            } else {
                handleError("Cannot add gruppo: insufficient parameters", request, response);
            }
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_remove_gruppo(HttpServletRequest request, HttpServletResponse response, int aula_key) throws IOException, ServletException, TemplateManagerException {
        try {

            Aula aula = ((AuleWebDataLayer) request.getAttribute("datalayer")).getAulaDAO().getAula(aula_key);

            if (aula != null && request.getParameter("removegruppo") != null) {
                Gruppo gruppo = ((AuleWebDataLayer) request.getAttribute("datalayer")).getGruppoDAO().getGruppo(Integer.parseInt(request.getParameter("removegruppo")));
                if (gruppo != null) {
                    if (((AuleWebDataLayer) request.getAttribute("datalayer")).getGruppoDAO().getGruppi(aula).contains(gruppo)) {
                        ((AuleWebDataLayer) request.getAttribute("datalayer")).getAulaDAO().removeFromGruppo(gruppo, aula);
                    }
                    action_manage(request, response, aula_key);
                } else {
                    handleError("Cannot remove undefined gruppo", request, response);
                }
            } else {
                handleError("Cannot remove gruppo: insufficient parameters", request, response);
            }
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_add_attrezzatura(HttpServletRequest request, HttpServletResponse response, int aula_key) throws IOException, ServletException, TemplateManagerException {
        try {

            Aula aula = ((AuleWebDataLayer) request.getAttribute("datalayer")).getAulaDAO().getAula(aula_key);

            if (aula != null && request.getParameter("addattrezzatura") != null) {
                Attrezzatura attrezzatura = ((AuleWebDataLayer) request.getAttribute("datalayer")).getAttrezzaturaDAO().getAttrezzatura(Integer.parseInt(request.getParameter("addattrezzatura")));
                if (attrezzatura != null) {
                    ((AuleWebDataLayer) request.getAttribute("datalayer")).getAttrezzaturaDAO().setAula(aula, attrezzatura);
                    action_manage(request, response, aula_key);
                } else {
                    handleError("Cannot add undefined attrezzatura", request, response);
                }
            } else {
                handleError("Cannot add attrezzatura: insufficient parameters", request, response);
            }
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_remove_attrezzatura(HttpServletRequest request, HttpServletResponse response, int aula_key) throws IOException, ServletException, TemplateManagerException {
        try {

            Aula aula = ((AuleWebDataLayer) request.getAttribute("datalayer")).getAulaDAO().getAula(aula_key);

            if (aula != null && request.getParameter("removeattrezzatura") != null) {
                Attrezzatura attrezzatura = ((AuleWebDataLayer) request.getAttribute("datalayer")).getAttrezzaturaDAO().getAttrezzatura(Integer.parseInt(request.getParameter("removeattrezzatura")));
                if (attrezzatura != null) {
                    if (((AuleWebDataLayer) request.getAttribute("datalayer")).getAttrezzaturaDAO().getAttrezzatura(aula).contains(attrezzatura)) {
                        ((AuleWebDataLayer) request.getAttribute("datalayer")).getAttrezzaturaDAO().removeFromAula(aula, attrezzatura);
                    }
                    action_manage(request, response, aula_key);
                } else {
                    handleError("Cannot remove undefined attrezzatura", request, response);
                }
            } else {
                handleError("Cannot remove attrezzatura: insufficient parameters", request, response);
            }
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        int aula_key;

        try {
            if (request.getParameter("n") != null) {
                aula_key = SecurityHelpers.checkNumeric(request.getParameter("n"));

                if (request.getParameter("addg") != null) {
                    action_add_gruppo(request, response, aula_key);
                } else if (request.getParameter("removeg") != null) {
                    action_remove_gruppo(request, response, aula_key);
                } else if (request.getParameter("update") != null) {
                    action_update(request, response, aula_key);
                } else if (request.getParameter("adda") != null) {
                    action_add_attrezzatura(request, response, aula_key);
                } else if (request.getParameter("removea") != null) {
                    action_remove_attrezzatura(request, response, aula_key);
                } else {
                    action_manage(request, response, aula_key);
                }
            } else {
                action_default(request, response);
            }
        } catch (NumberFormatException ex) {
            handleError("Invalid number submitted", request, response);
        } catch (IOException | TemplateManagerException ex) {
            handleError(ex, request, response);
        }

    }

    @Override
    public String getServletInfo() {
        return "Gestione aule da parte dell'admin mediante servlet";
    }

}
