package com.betmansmall.server.accouting;

import com.badlogic.gdx.utils.StringBuilder;

public class UserAccount {
    public String loginName;
    public String factionName;
    public String accountID;

    public UserAccount(String loginName, String factionName, String accountID) {
        this.loginName = loginName;
        this.factionName = factionName;
        this.accountID = accountID;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("UserAccount[");
        sb.append("loginName:" + loginName);
        sb.append(",factionName:" + factionName);
        sb.append(",accountID:" + accountID);
        sb.append("]");
        return sb.toString();
    }
}
