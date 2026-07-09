package org.tmt.aps.peas;

import java.io.Serializable;
import java.util.*;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.jboss.logging.Logger;
import org.primefaces.model.menu.*;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.session.ui.SessionController;

@Named("breadcrumbMenuBean")
@SessionScoped
public class BreadcrumbMenuBean implements Serializable {

    private static final long serialVersionUID = 1L;
    
    
    @Inject
    private SessionController sessionController;
    

    private final Logger logger = Logger.getLogger(this.getClass());

    private List<Crumb> crumbs = new ArrayList<>();

    // kept for compatibility with existing code
    private String immediateUrl;

    public BreadcrumbMenuBean() {

        crumbs.add(new Crumb(
                "Home",
                "/modules/session/sessionList.xhtml?faces-redirect=true",
                "pi pi-home"));

        crumbs.add(new Crumb(
                "Session List",
                "/modules/session/sessionList.xhtml?faces-redirect=true",
                ""));
    }

    // ---- Build PrimeFaces model ----
    public MenuModel getModel() {

        DefaultMenuModel model = new DefaultMenuModel();

        for (int i = 0; i < crumbs.size(); i++) {

            Crumb c = crumbs.get(i);

            DefaultMenuItem item = DefaultMenuItem.builder()
                    .value(c.label)
                    .command("#{breadcrumbMenuBean.navigate}")
                    .ajax(false)
                    .icon(c.icon)
                    .id("breadcrumbMenu_Item_" + i)
                    .build();

            Map<String, List<String>> params = new HashMap<>();
            params.put("crumbIndex", Collections.singletonList(String.valueOf(i)));
            params.put("target", Collections.singletonList(c.outcome));

            item.setParams(params);

            model.getElements().add(item);
        }

        return model;
    }

    // ---- Navigation handler ----
    public String navigate() {

        FacesContext ctx = FacesContext.getCurrentInstance();
        Map<String,String> params =
                ctx.getExternalContext().getRequestParameterMap();

        int index = Integer.parseInt(params.get("crumbIndex"));
        String target = params.get("target");

        // Access the crumb being navigated to
        Crumb targetCrumb = crumbs.get(index);
        
        while (crumbs.size() > index + 1) {
            crumbs.remove(crumbs.size() - 1);
        }

        immediateUrl = target;
        
        
		Session session = sessionController.getCurrentSession();

        // V3.0 re-populate model for current session based on destination
        if (target.contains("sessionDetail") && targetCrumb.label.contains(session.getSessionDateFormatted())) {
            sessionController.doViewCurrentSession();
        }

        return target;
    }

    // ---- Breadcrumb operations ----

    public void addFirstItem(String name, String command) {

        logger.debug("BREADCRUMB: ADD FIRST ITEM");

        if (crumbs.size() > 1) {
            crumbs.subList(1, crumbs.size()).clear();
        }

        crumbs.add(new Crumb(name, command, ""));
        
        immediateUrl = command;
    }

    public void addItem(String name, String command) {

    	logger.debug("BREADCRUMB: ADD ITEM, name = " + name + ", command = " + command);

        crumbs.add(new Crumb(name, command, ""));
        
        immediateUrl = command;
        
        //immediateUrl = null;
    }

    public void insertFirst(String name, String command) {

    	logger.debug("BREADCRUMB: INSERT FIRST");

        crumbs.add(1, new Crumb(name, command, ""));
        
        immediateUrl = command;
        
        //immediateUrl = null;
    }

    public void removeTo(String value) {

    	logger.debug("BREADCRUMB: REMOVE TO " + value);

        for (int i = crumbs.size() - 1; i >= 1; i--) {

            Crumb c = crumbs.get(i);

            // unchanged as requested
            if (c.label.contains(value)) {
                break;
            } else {
                crumbs.remove(i);
            }
        }
    }

    public void removeLast() {

    	logger.debug("BREADCRUMB: REMOVE LAST");

        if (crumbs.size() > 1) {
            crumbs.remove(crumbs.size() - 1);
        }
    }

    // ---- Compatibility methods ----

    public String getImmediateUrl() {
        return immediateUrl;
    }

    public boolean isInProcedure() {
        return immediateUrl != null &&
               immediateUrl.contains("procedurePerspective");
    }
    

    // ---- Navigation convenience ----

    public String goSessionList() {
        return "/modules/session/sessionList.xhtml?faces-redirect=true";
    }

    public String goProcedurePerspective() {
        System.out.println("goProcedurePerspective");
        return "/modules/procedure/procedurePerspective.xhtml?faces-redirect=true";
    }

    // ---- Internal crumb object ----

    private static class Crumb implements Serializable {

        /**
		 * 
		 */
		private static final long serialVersionUID = -7769796750493456799L;
		
		String label;
        String outcome;
        String icon;

        Crumb(String label, String outcome, String icon) {
            this.label = label;
            this.outcome = outcome;
            this.icon = icon;
        }
    }
}