package it.univaq.f4i.iw.ex.auleweb.data.proxy;

import java.util.logging.Level;
import java.util.logging.Logger;
import it.univaq.f4i.iw.ex.auleweb.data.impl.EventoRicorrenteImpl;
import it.univaq.f4i.iw.ex.auleweb.data.model.Evento;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.data.DataItemProxy;
import it.univaq.f4i.iw.framework.data.DataLayer;
import it.univaq.f4i.iw.ex.auleweb.data.dao.EventoDAO;
import java.time.LocalDateTime;

public class EventoRicorrenteProxy extends EventoRicorrenteImpl implements DataItemProxy {

    protected boolean modified;
    protected int evento_master_key = 0;
    
    protected DataLayer dataLayer;

    public EventoRicorrenteProxy(DataLayer d) {
        super();
        this.evento_master_key = 0;
        this.dataLayer = d;
        this.modified = false;
    }
    
    @Override
    public void setKey(Integer key) {
        super.setKey(key);
        this.modified = true;
    }

    @Override
    public Evento getEventoMaster() {
        if (super.getEventoMaster() == null && evento_master_key > 0) {
            try {
                super.setEventoMaster(((EventoDAO) dataLayer.getDAO(Evento.class)).getEvento(evento_master_key));
            } catch (DataException ex) {
                Logger.getLogger(EventoRicorrenteProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getEventoMaster();
    }
    
    @Override
    public void setDataInizio(LocalDateTime dataInizio) {
        super.setDataInizio(dataInizio);
        this.modified = true;
    }

    @Override
    public void setDataFine(LocalDateTime dataFine) {
        super.setDataFine(dataFine);
        this.modified = true;
    }

    @Override
    public void setEventoMaster(Evento evento) {
        super.setEventoMaster(evento);
        this.evento_master_key = evento.getKey();
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

    public void setEventoMasterKey(int evento_master_key) {
        this.evento_master_key = evento_master_key;
        super.setEventoMaster(null);
    }

}
