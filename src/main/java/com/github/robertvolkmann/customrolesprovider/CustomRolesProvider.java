package com.github.robertvolkmann.customrolesprovider;

import com.tngtech.elasticsearch.plugin.kerberosrealm.realm.RolesProvider;
import org.elasticsearch.shield.authc.RealmConfig;

public class CustomRolesProvider implements RolesProvider {

    @Override
    public void setConfig(RealmConfig realmConfig) {

    }

    @Override
    public String[] getRoles(String principal) {
        if (principal.startsWith("admin")) {
            return new String[]{"admin"};
        } else if (principal.startsWith("userWithRoles")) {
            return new String[]{"role1", "role2"};
        }
        return new String[0];
    }
}
