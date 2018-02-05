package com.incture.cherrywork.freshdirect.Utils;

/**
 * Created by Arun on 08-09-2016.
 */
public interface AsyncResponse {
    void processFinish(String output,int status_code,String url,int type);
    void configurationUpdated(boolean configUpdated);
}
