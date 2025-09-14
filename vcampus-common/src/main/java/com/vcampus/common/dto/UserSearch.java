package com.vcampus.common.dto;

import java.io.Serializable;

public class UserSearch implements Serializable{
    private static final long serialVersionUID = 1L;

    private String searchText;
    private String selectedRole;

    public UserSearch(String searchText, String selectedRole) {
        this.searchText = searchText;
        this.selectedRole = selectedRole;
    }

    public String getSearchText() {
        return searchText;
    }

    public String getSelectedRole() {
        return selectedRole;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public void setSelectedRole(String selectedRole) {
        this.selectedRole = selectedRole;
    }

    @Override
    public String toString() {
        return "UserSearch{" +
                "searchText='" + searchText + '\'' +
                ", selectedRole='" + selectedRole + '\'' +
                '}';
    }
}
