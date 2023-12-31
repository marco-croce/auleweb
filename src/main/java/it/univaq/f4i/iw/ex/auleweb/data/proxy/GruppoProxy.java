package it.univaq.f4i.iw.ex.auleweb.data.proxy;

import java.util.List;

import it.univaq.f4i.iw.ex.auleweb.data.impl.GruppoImpl;
import it.univaq.f4i.iw.ex.auleweb.data.model.Aula;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.data.DataItemProxy;
import it.univaq.f4i.iw.framework.data.DataLayer;
import java.util.logging.Level;
import java.util.logging.Logger;
import it.univaq.f4i.iw.ex.auleweb.data.dao.AulaDAO;

public class GruppoProxy extends GruppoImpl implements DataItemProxy {

    protected boolean modified;
    
    protected DataLayer dataLayer;

    public GruppoProxy(DataLayer d) {
        super();
        this.dataLayer = d;
        this.modified = false;
    }
    
    @Override
    public void setKey(Integer key) {
        super.setKey(key);
        this.modified = true;
    }
    
    @Override
    public List<Aula> getAule() {
        if (super.getAule() == null) {
            try {
                super.setAule(((AulaDAO) dataLayer.getDAO(Aula.class)).getAule(this));
            } catch (DataException ex) {
                Logger.getLogger(GruppoProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getAule();
    }

    @Override
    public void setNome(String nome) {
        super.setNome(nome);
        this.modified = true;
    }

    @Override
    public void setDescrizione(String descrizione) {
        super.setDescrizione(descrizione);
        this.modified = true;
    }

    @Override
    public void setAule(List<Aula> aule) {
        super.setAule(aule);
        this.modified = true;
    }

    @Override
    public void addAula(Aula aula) {
        super.addAula(aula);
        this.modified = true;
    }

    //metodi del PROXY
    @Override
    public void setModified(boolean dirty) {
        this.modified = dirty;
    }

    @Override
    public boolean isModified() {
        return modified;
    }

}
