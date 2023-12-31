package it.univaq.f4i.iw.ex.auleweb.data.proxy;

import it.univaq.f4i.iw.ex.auleweb.data.dao.AulaDAO;
import it.univaq.f4i.iw.ex.auleweb.data.impl.AttrezzaturaImpl;
import it.univaq.f4i.iw.ex.auleweb.data.model.Aula;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.data.DataItemProxy;
import it.univaq.f4i.iw.framework.data.DataLayer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AttrezzaturaProxy extends AttrezzaturaImpl implements DataItemProxy {

    protected boolean modified;
    protected int aula_key = 0;

    protected DataLayer dataLayer;

    public AttrezzaturaProxy(DataLayer d) {
        super();
        //dependency injection
        this.dataLayer = d;
        this.modified = false;
        this.aula_key = 0;
    }

    @Override
    public void setKey(Integer key) {
        super.setKey(key);
        this.modified = true;
    }

    @Override
    public Aula getAula() {
        if (super.getAula() == null && aula_key > 0) {
            try {
                super.setAula(((AulaDAO) dataLayer.getDAO(Aula.class)).getAula(aula_key));
            } catch (DataException ex) {
                Logger.getLogger(AttrezzaturaProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getAula();
    }

    @Override
    public void setNumeroSeriale(String numeroSeriale) {
        super.setNumeroSeriale(numeroSeriale);
        this.modified = true;
    }

    @Override
    public void setDescrizione(String descrizione) {
        super.setDescrizione(descrizione);
        this.modified = true;
    }

    @Override
    public void setAula(Aula aula) {
        super.setAula(aula);
        if (aula != null) {
            this.aula_key = aula.getKey();
        } else {
            this.aula_key = 0;
        }
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

    public void setAulaKey(int aula_key) {
        this.aula_key = aula_key;
        super.setAula(null);
    }

}
