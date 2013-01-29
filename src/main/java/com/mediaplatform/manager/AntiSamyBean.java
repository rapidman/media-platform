package com.mediaplatform.manager;

import com.mediaplatform.model.AbstractContent;
import com.mediaplatform.model.Content;
import com.mediaplatform.model.HtmlContainer;
import org.jboss.solder.logging.Logger;
import org.owasp.validator.html.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * User: timur
 * Date: 1/29/13
 * Time: 10:13 PM
 */
@ApplicationScoped
@Named
public class AntiSamyBean implements Serializable {
    @Inject
    protected Logger log;
    private  Policy policy;

    public String cleanHtml(String dirtyInput) {
        try {
            if(policy == null) policy = Policy.getInstance(Thread.currentThread().getContextClassLoader().getResourceAsStream("antisamy-1.4.1.xml"));
            AntiSamy as = new AntiSamy();
            CleanResults cr = as.scan(dirtyInput, policy);
            return cr.getCleanHTML();
        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    public void prepare(HtmlContainer content) {
        content.setTitle(HtmlTextHelper.htmlToPlain(HtmlTextHelper.truncateTitle(content.getTitle())));
        content.setDescription(cleanHtml(content.getDescription()));
    }
}
