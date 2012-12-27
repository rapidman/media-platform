package com.mediaplatform.data.stat;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;

/**
 * User: timur
 * Date: 12/13/12
 * Time: 8:12 PM
 */
@XStreamAlias("server")
public class ServerDTO {
    @XStreamImplicit
    private List<ApplicationDTO> applications;

    public List<ApplicationDTO> getApplications() {
        if(applications == null){
            applications = new ArrayList<ApplicationDTO>();
        }
        return applications;
    }

    public void setApplications(List<ApplicationDTO> applications) {
        this.applications = applications;
    }

    public ApplicationDTO getLiveApp(){
        for(ApplicationDTO app:getApplications()){
            if("myapp".equals(app.getName())){
                return app;
            }
        }
        return null;
    }
}
