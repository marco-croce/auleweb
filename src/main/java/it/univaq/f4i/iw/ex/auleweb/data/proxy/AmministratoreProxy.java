package it.univaq.f4i.iw.ex.auleweb.data.proxy;

import it.univaq.f4i.iw.ex.auleweb.data.impl.AmministratoreImpl;
import it.univaq.f4i.iw.framework.data.DataItemProxy;
import it.univaq.f4i.iw.framework.data.DataLayer;

public class AmministratoreProxy extends AmministratoreImpl implements DataItemProxy {

    protected boolean modified;
    
    protected DataLayer dataLayer;

    public AmministratoreProxy(DataLayer d) {
        super();
        this.dataLayer = d;
        this.modified = false;
    }

    @Override
    public void setNome(String nome) {
        super.setNome(nome);
        this.modified = true;
    }

    @Override
    public void setCognome(String cognome) {
        super.setCognome(cognome);
        this.modified = true;
    }

    @Override
    public void setEmail(String email) {
        super.setEmail(email);
        this.modified = true;
    }

    @Override
    public void setPassword(String password) {
        super.setPassword(password);
        this.modified = true;
    }

    @Override
    public void setTelefono(String telefono) {
        super.setTelefono(telefono);
        this.modified = true;
    }

    @Override
    public boolean isModified() {
        return modified;
    }

    @Override
    public void setModified(boolean dirty) {
        this.modified = dirty;
    }

}
