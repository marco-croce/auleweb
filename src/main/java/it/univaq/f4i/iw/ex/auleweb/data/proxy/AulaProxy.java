package it.univaq.f4i.iw.ex.auleweb.data.proxy;

import it.univaq.f4i.iw.ex.auleweb.data.impl.AulaImpl;
import it.univaq.f4i.iw.ex.auleweb.data.model.Evento;
import it.univaq.f4i.iw.ex.auleweb.data.model.Gruppo;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.data.DataItemProxy;
import it.univaq.f4i.iw.framework.data.DataLayer;
import java.util.logging.Level;
import java.util.logging.Logger;
import it.univaq.f4i.iw.ex.auleweb.data.dao.EventoDAO;
import it.univaq.f4i.iw.ex.auleweb.data.dao.GruppoDAO;

import java.util.List;
import it.univaq.f4i.iw.ex.auleweb.data.dao.AttrezzaturaDAO;
import it.univaq.f4i.iw.ex.auleweb.data.model.Attrezzatura;

public class AulaProxy extends AulaImpl implements DataItemProxy {

    protected boolean modified;

    protected DataLayer dataLayer;

    public AulaProxy(DataLayer d) {
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
    public List<Attrezzatura> getAttrezzatura() {
        if (super.getAttrezzatura() == null) {
            try {
                super.setAttrezzatura(((AttrezzaturaDAO) dataLayer.getDAO(Attrezzatura.class)).getAttrezzatura(this));
            } catch (DataException ex) {
                Logger.getLogger(AulaProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getAttrezzatura();
    }

    @Override
    public List<Evento> getEventi() {
        if (super.getEventi() == null) {
            try {
                super.setEventi(((EventoDAO) dataLayer.getDAO(Evento.class)).getEventi(this));
            } catch (DataException ex) {
                Logger.getLogger(AulaProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getEventi();
    }

    @Override
    public List<Gruppo> getGruppi() {
        if (super.getGruppi() == null) {
            try {
                super.setGruppi(((GruppoDAO) dataLayer.getDAO(Gruppo.class)).getGruppi(this));
            } catch (DataException ex) {
                Logger.getLogger(AulaProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getGruppi();
    }

    @Override
    public void setNome(String nome) {
        super.setNome(nome);
        this.modified = true;
    }

    @Override
    public void setLuogo(String luogo) {
        super.setLuogo(luogo);
        this.modified = true;
    }

    @Override
    public void setEdificio(String edificio) {
        super.setEdificio(edificio);
        this.modified = true;
    }

    @Override
    public void setPiano(int piano) {
        super.setPiano(piano);
        this.modified = true;
    }

    @Override
    public void setCapienza(int capienza) {
        super.setCapienza(capienza);
        this.modified = true;
    }

    @Override
    public void setEmailResponsabile(String emailResponsabile) {
        super.setEmailResponsabile(emailResponsabile);
        this.modified = true;
    }

    @Override
    public void setNumeroPreseRete(int numeroPreseRete) {
        super.setNumeroPreseRete(numeroPreseRete);
        this.modified = true;
    }

    @Override
    public void setNumeroPreseElettriche(int numeroPreseElettriche) {
        super.setNumeroPreseElettriche(numeroPreseElettriche);
        this.modified = true;
    }

    @Override
    public void setNote(String note) {
        super.setNote(note);
        this.modified = true;
    }

    @Override
    public void setAttrezzatura(List<Attrezzatura> attrezzatura) {
        super.setAttrezzatura(attrezzatura);
        this.modified = true;
    }

    @Override
    public void setEventi(List<Evento> eventi) {
        super.setEventi(eventi);
        this.modified = true;
    }

    @Override
    public void setGruppi(List<Gruppo> gruppi) {
        super.setGruppi(gruppi);
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
