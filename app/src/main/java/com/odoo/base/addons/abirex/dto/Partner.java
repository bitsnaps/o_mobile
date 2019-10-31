package com.odoo.base.addons.abirex.dto;

import com.odoo.core.orm.OValues;

import org.jetbrains.annotations.NotNull;

public class Partner implements DTO {

    int serverId;
    String displayName;
    Company company;

    public Partner(int id, int serverId, String displayName) {
        this.id = id;
        this.serverId = serverId;
        this.displayName = displayName;
        this.company = company;
    }

    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @NotNull
    @Override
    public OValues toOValues() {
        return new OValues();
    }
}
