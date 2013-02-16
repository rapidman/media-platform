package com.mediaplatform.jsf;

import org.jboss.seam.faces.view.SeamViewHandler;

import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

/**
 * User: timur
 * Date: 2/16/13
 * Time: 10:59 PM
 */
public class CustomViewHandler extends SeamViewHandler {
    private ViewHandler delegate;

    public CustomViewHandler(ViewHandler delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    @Override
    public UIViewRoot restoreView(FacesContext facesContext, String viewId) {
        /**
         * {@link javax.faces.application.ViewExpiredException}. This happens only  when we try to logout from timed out pages.
         */
        UIViewRoot root = delegate.restoreView(facesContext, viewId);
        if(root == null) {
            root = createView(facesContext, viewId);
        }
        return root;
    }

    @Override
    public ViewHandler getWrapped() {
        return delegate;
    }

}
