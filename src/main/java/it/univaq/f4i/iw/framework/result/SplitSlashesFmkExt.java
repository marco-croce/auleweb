/*
 * Implementando l'interfaccia TemplateMethodModelEx e' possibile creare funzioni
 * richiamabili all'interno dei template di Freemarker. Si veda la classe MakeArticle per
 * un esempio di uso.
 */
package it.univaq.f4i.iw.framework.result;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import it.univaq.f4i.iw.framework.security.SecurityHelpers;
import java.util.List;

public class SplitSlashesFmkExt implements TemplateMethodModelEx {

    @Override
    public Object exec(List list) throws TemplateModelException {
        //la lista contiene i parametri passati alla funzione nel template
        if (!list.isEmpty()) {
            return SecurityHelpers.stripSlashes(list.get(0).toString());
        } else {
            //e' possibile ritornare qualsiasi tipo che sia gestibile da Freemarker (scalari, hash, liste)
            return "";
        }
    }
}
