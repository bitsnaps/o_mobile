package com.ehealthinformatics.core.orm;

import com.ehealthinformatics.core.rpc.helper.utils.gson.OdooResult;
import java.util.List;

public class OdooServerException extends Exception {

    private Double code;
    private String debug;
    private String name;
    private String message;
    private String exception_type;
    private List arguments;

    public static final String TAG = OdooServerException.class.getSimpleName();

    public OdooServerException(String message) {
        super(message);
        this.message = message;
    }

    public OdooServerException(OdooResult error) {
        super(error.has("data") && error.getMap("data").has("name") ? error.getMap("data").getString("name") : error.toString());
        OdooResult errorData = error.getMap("data");
        if(error.has("code"))code = errorData.getDouble("code");
        if(error.has("name"))name = errorData.getString("name");
        if(error.has("debug"))debug = errorData.getString("debug");
        if(error.has("message"))message = errorData.getString("message");
        if(error.has("exception_type"))exception_type = errorData.getString("exception_type");
        if(error.has("arguments"))arguments = error.getArray("arguments");
    }

    public Double getCode() {
        return code;
    }

    public String getDebug() {
        return debug;
    }

    public String getName() {
        return name;
    }

    public String getException_type() {
        return exception_type;
    }

    public List getArguments() {
        return arguments;
    }

    @Override
    public String getMessage() {
        return message;
    }


}
