package it.univaq.f4i.iw.framework.result;

import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public interface DataModelFiller {
    //request e context POSSONO ESSERE NULL
    public void fillDataModel(Map datamodel, HttpServletRequest request, ServletContext context);
}
