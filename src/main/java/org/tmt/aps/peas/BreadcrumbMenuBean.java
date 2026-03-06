package org.tmt.aps.peas;

import java.io.Serializable;
import java.util.*;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import org.jboss.logging.Logger;
import org.primefaces.model.menu.*;

@Named("breadcrumbMenuBean")
@SessionScoped
public class BreadcrumbMenuBean implements Serializable {

    private static final long serialVersionUID = 1L;

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

        while (crumbs.size() > index + 1) {
            crumbs.remove(crumbs.size() - 1);
        }

        immediateUrl = target;

        return target;
    }

    // ---- Breadcrumb operations ----

    public void addFirstItem(String name, String command) {

        System.out.println("BREADCRUMB: ADD FIRST ITEM");

        if (crumbs.size() > 1) {
            crumbs.subList(1, crumbs.size()).clear();
        }

        crumbs.add(new Crumb(name, command, ""));
        immediateUrl = null;
    }

    public void addItem(String name, String command) {

        System.out.println("BREADCRUMB: ADD ITEM");

        crumbs.add(new Crumb(name, command, ""));
        immediateUrl = null;
    }

    public void insertFirst(String name, String command) {

        System.out.println("BREADCRUMB: INSERT FIRST");

        crumbs.add(1, new Crumb(name, command, ""));
        immediateUrl = null;
    }

    public void removeTo(String value) {

        System.out.println("BREADCRUMB: REMOVE TO " + value);

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

        System.out.println("BREADCRUMB: REMOVE LAST");

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