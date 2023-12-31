package it.univaq.f4i.iw.ex.auleweb.controller;

import it.univaq.f4i.iw.ex.auleweb.data.dao.AuleWebDataLayer;
import it.univaq.f4i.iw.ex.auleweb.data.model.Aula;
import it.univaq.f4i.iw.ex.auleweb.data.model.Gruppo;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.result.TemplateManagerException;
import it.univaq.f4i.iw.framework.result.TemplateResult;
import it.univaq.f4i.iw.framework.security.SecurityHelpers;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GruppiAdminServlet extends AuleWebBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, TemplateManagerException {
        try {
            TemplateResult res = new TemplateResult(getServletContext());
            List<Gruppo> gruppi = ((AuleWebDataLayer) request.getAttribute("datalayer")).getGruppoDAO().getGruppi();
            request.setAttribute("gruppi", gruppi);
            Collections.sort(gruppi, (Gruppo g1, Gruppo g2) -> g1.getNome().compareTo(g2.getNome()));
            res.activate("gruppiadmin.ftl.html", request, response);
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_manage(HttpServletRequest request, HttpServletResponse response, int gruppo_key) throws IOException, ServletException, TemplateManagerException {
        try {
            TemplateResult res = new TemplateResult(getServletContext());
            if (gruppo_key > 0) {
                Gruppo gruppo = ((AuleWebDataLayer) request.getAttribute("datalayer")).getGruppoDAO().getGruppo(gruppo_key);
                if (gruppo != null) {
                    request.setAttribute("gruppo", gruppo);
                    List<Aula> unassigned = ((AuleWebDataLayer) request.getAttribute("datalayer")).getAulaDAO().getAule();
                    List<Aula> assigned = ((AuleWebDataLayer) request.getAttribute("datalayer")).getAulaDAO().getAule(gruppo);
                    if (!assigned.isEmpty()) {
                        unassigned.removeAll(assigned);
                    }
                    request.setAttribute("unassigned_aule", unassigned);
                    request.setAttribute("assigned_aule", assigned);

                    res.activate("gestioneGruppo.ftl.html", request, response);
                } else {
                    handleError("Undefined gruppo", request, response);
                }
            } else {
                Gruppo gruppo = ((AuleWebDataLayer) request.getAttribute("datalayer")).getGruppoDAO().createGruppo();
                request.setAttribute("gruppo", gruppo);
                request.setAttribute("unassigned_aule", Collections.EMPTY_LIST);
                request.setAttribute("assigned_aule", Collections.EMPTY_LIST);
                res.activate("gestioneGruppo.ftl.html", request, response);
            }
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_update(HttpServletRequest request, HttpServletResponse response, int gruppo_key) throws IOException, ServletException, TemplateManagerException {
        try {
            Gruppo gruppo;
            if (gruppo_key > 0) {
                gruppo = ((AuleWebDataLayer) request.getAttribute("datalayer")).getGruppoDAO().getGruppo(gruppo_key);
            } else {
                gruppo = ((AuleWebDataLayer) request.getAttribute("datalayer")).getGruppoDAO().createGruppo();
            }
            if (gruppo != null && request.getParameter("nome") != null) {
                gruppo.setNome(request.getParameter("nome"));
                if (request.getParameter("descrizione") != null) {
                    gruppo.setDescrizione(request.getParameter("descrizione"));
                }
                ((AuleWebDataLayer) request.getAttribute("datalayer")).getGruppoDAO().storeGruppo(gruppo);
                action_default(request, response);
            } else {
                handleError("Cannot update gruppo: insufficient parameters", request, response);
            }
        } catch (DataException ex) {
            // Mostra una finestra modale con il messaggio di errore di MySQL
            request.setAttribute("error", ex.getMessage());
            action_default(request, response);
        }
    }

    private void action_add_aula(HttpServletRequest request, HttpServletResponse response, int gruppo_key) throws IOException, ServletException, TemplateManagerException {
        try {
            Gruppo gruppo = ((AuleWebDataLayer) request.getAttribute("datalayer")).getGruppoDAO().getGruppo(gruppo_key);

            if (gruppo != null && request.getParameter("addaula") != null) {
                Aula aula = ((AuleWebDataLayer) request.getAttribute("datalayer")).getAulaDAO().getAula(Integer.parseInt(request.getParameter("addaula")));
                if (aula != null) {
                    ((AuleWebDataLayer) request.getAttribute("datalayer")).getAulaDAO().setGruppo(gruppo, aula);
                    action_manage(request, response, gruppo_key);
                } else {
                    handleError("Cannot add undefined aula", request, response);
                }
            } else {
                handleError("Cannot add aula: insufficient parameters", request, response);
            }
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_remove_aula(HttpServletRequest request, HttpServletResponse response, int gruppo_key) throws IOException, ServletException, TemplateManagerException {
        try {
            Gruppo gruppo = ((AuleWebDataLayer) request.getAttribute("datalayer")).getGruppoDAO().getGruppo(gruppo_key);

            if (gruppo != null && request.getParameter("removeaula") != null) {
                Aula aula = ((AuleWebDataLayer) request.getAttribute("datalayer")).getAulaDAO().getAula(Integer.parseInt(request.getParameter("removeaula")));
                if (aula != null) {
                    if (((AuleWebDataLayer) request.getAttribute("datalayer")).getAulaDAO().getAule(gruppo).contains(aula)) {
                        ((AuleWebDataLayer) request.getAttribute("datalayer")).getAulaDAO().removeFromGruppo(gruppo, aula);
                    }
                    action_manage(request, response, gruppo_key);
                } else {
                    handleError("Cannot remove undefined aula", request, response);
                }
            } else {
                handleError("Cannot remove aula: insufficient parameters", request, response);
            }
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_delete(HttpServletRequest request, HttpServletResponse response, int gruppo_key) throws IOException, ServletException, TemplateManagerException {
        try {
            Gruppo gruppo = ((AuleWebDataLayer) request.getAttribute("datalayer")).getGruppoDAO().getGruppo(gruppo_key);
            if (gruppo != null && request.getParameter("delete") != null) {
                ((AuleWebDataLayer) request.getAttribute("datalayer")).getGruppoDAO().deleteGruppo(gruppo);
                action_default(request, response);
            } else {
                handleError("Undefined gruppo", request, response);
            }
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        int gruppo_key;
        try {
            if (request.getParameter("n") != null) {
                gruppo_key = SecurityHelpers.checkNumeric(request.getParameter("n"));

                if (request.getParameter("adda") != null) {
                    action_add_aula(request, response, gruppo_key);
                } else if (request.getParameter("removea") != null) {
                    action_remove_aula(request, response, gruppo_key);
                } else if (request.getParameter("update") != null) {
                    action_update(request, response, gruppo_key);
                } else if (request.getParameter("delete") != null) {
                    action_delete(request, response, gruppo_key);
                } else {
                    action_manage(request, response, gruppo_key);
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
        return "Gestione gruppi da parte dell'admin mediante servlet";
    }

}
