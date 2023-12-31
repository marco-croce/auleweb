package it.univaq.f4i.iw.ex.auleweb.controller;

import it.univaq.f4i.iw.ex.auleweb.data.dao.AuleWebDataLayer;
import it.univaq.f4i.iw.ex.auleweb.data.model.Aula;
import it.univaq.f4i.iw.ex.auleweb.data.model.Gruppo;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.result.CSVReader;
import it.univaq.f4i.iw.framework.result.CSVWriter;
import it.univaq.f4i.iw.framework.result.StreamResult;
import it.univaq.f4i.iw.framework.result.TemplateManagerException;
import it.univaq.f4i.iw.framework.result.TemplateResult;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

public class UploadServlet extends AuleWebBaseController {

    private void action_upload(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, TemplateManagerException {
        Part fileConf = request.getPart("fileconf");
        CSVReader csvReader = new CSVReader();
        // Contenuto del file .csv
        List<List<String>> confs = csvReader.csv_gruppi(fileConf, getServletContext().getRealPath(""));
        String edificio, luogo, piano, nomeAula, nomeGruppo;

        try {
            // Rimozione riga di intestazione
            confs.remove(0);
            // Iterazione sulle "righe" del file .csv --> coppie GRUPPO-AULA
            for (List<String> conf : confs) {
                edificio = conf.get(0);
                luogo = conf.get(1);
                piano = conf.get(2);
                nomeAula = conf.get(3);
                nomeGruppo = conf.get(4);
                Aula aula = ((AuleWebDataLayer) request.getAttribute("datalayer")).getAulaDAO().getAula(edificio, luogo, Integer.parseInt(piano), nomeAula);
                Gruppo gruppo = ((AuleWebDataLayer) request.getAttribute("datalayer")).getGruppoDAO().getGruppo(nomeGruppo);
                
                // La coppia gruppo-aula è già presente?
                boolean x = false;    
                for(Aula a: gruppo.getAule()){
                    if(a.equals(aula))
                        x = true;
                }
                if(!x)
                    ((AuleWebDataLayer) request.getAttribute("datalayer")).getAulaDAO().setGruppo(gruppo, aula);
            }
            
            // Mostra una finestra modale con un messaggio di operazione riuscita
            request.setAttribute("message", "Il caricamento della configurazione è andato a buon fine!");

        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_template(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StreamResult result = new StreamResult(getServletContext());
        URL url = getServletContext().getResource("/" + "csv" + "/" + "temp_conf.csv");
        result.setResource(url);
        result.activate(request, response);
    }

    private void action_load(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, TemplateManagerException {
        try {
            TemplateResult res = new TemplateResult(getServletContext());
            request.setAttribute("gruppi", ((AuleWebDataLayer) request.getAttribute("datalayer")).getGruppoDAO().getGruppi());
            res.activate("gruppiadmin.ftl.html", request, response);
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_download(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            StreamResult result = new StreamResult(getServletContext());
            List<Aula> aule = ((AuleWebDataLayer) request.getAttribute("datalayer")).getAulaDAO().getAule();
            List<List<String>> conf = new ArrayList();

            int i = 0;
            for (Aula a : aule) {
                for (Gruppo g : a.getGruppi()) {
                    conf.add(i, new ArrayList());
                    conf.get(i).add(a.getEdificio());
                    conf.get(i).add(a.getLuogo());
                    conf.get(i).add(Integer.toString(a.getPiano()));
                    conf.get(i).add(a.getNome());
                    conf.get(i).add(g.getNome());
                    i++;
                }

            }
            CSVWriter w = new CSVWriter();
            w.csv_gruppi(conf, getServletContext().getRealPath(""));

            URL url = getServletContext().getResource("/" + "csv" + "/" + "gruppi.csv");
            result.setResource(url);
            result.activate(request, response);
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        try {
            if (request.getParameter("template") != null) { // download template
                action_template(request, response);
                return;
            }

            if (request.getParameter("download") != null) { // download configuration
                action_download(request, response);
                return;
            }

            if (request.getPart("fileconf").getSize() != 0) { // upload configuration
                // Configurazione delle aule mediante il file .csv "caricato"
                action_upload(request, response);
                // Caricamente della pagina di gestione dei gruppi per l'admin
                action_load(request, response);
            } else {
                // Mostra una finestra modale con un messaggio di operazione non riuscita
                request.setAttribute("error", "Il caricamento della configurazione non è andato a buon fine!");
                // Caricamente della pagina di gestione dei gruppi per l'admin
                action_load(request, response);
            }

        } catch (IOException | TemplateManagerException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }

    }

    @Override
    public String getServletInfo() {
        return "Upload configuration file .csv servlet";
    }

}
