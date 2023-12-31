package it.univaq.f4i.iw.ex.auleweb.data.proxy;

import it.univaq.f4i.iw.ex.auleweb.data.impl.EventoImpl;
import it.univaq.f4i.iw.ex.auleweb.data.impl.TipologiaRicorrenza;
import it.univaq.f4i.iw.ex.auleweb.data.model.Aula;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.data.DataItemProxy;
import it.univaq.f4i.iw.framework.data.DataLayer;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import it.univaq.f4i.iw.ex.auleweb.data.dao.AulaDAO;
import it.univaq.f4i.iw.ex.auleweb.data.impl.TipologiaEvento;
import java.time.LocalDateTime;

public class EventoProxy extends EventoImpl implements DataItemProxy {

    protected boolean modified;
    protected int aula_key = 0;

    protected DataLayer dataLayer;

    public EventoProxy(DataLayer d) {
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
    public Aula getAula() {
        if (super.getAula() == null && aula_key > 0) {
            try {
                super.setAula(((AulaDAO) dataLayer.getDAO(Aula.class)).getAula(aula_key));
            } catch (DataException ex) {
                Logger.getLogger(EventoProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getAula();
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
    public void setEmailResponsabile(String emailResponsabile) {
        super.setEmailResponsabile(emailResponsabile);
        this.modified = true;
    }

    @Override
    public void setAula(Aula aula) {
        super.setAula(aula);
        this.aula_key = aula.getKey();
        this.modified = true;
    }

    @Override
    public void setTipologia(TipologiaEvento tipologia) {
        super.setTipologia(tipologia);
        this.modified = true;
    }

    @Override
    public void setNomeCorso(String nomeCorso) {
        super.setNomeCorso(nomeCorso);
        this.modified = true;
    }

    @Override
    public void setTipologiaRicorrenza(TipologiaRicorrenza tipologiaRicorrenza) {
        super.setTipologiaRicorrenza(tipologiaRicorrenza);
        this.modified = true;
    }

    @Override
    public void setDataFineRicorrenza(LocalDate dataFineRicorrenza) {
        super.setDataFineRicorrenza(dataFineRicorrenza);
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
