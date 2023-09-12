package com.eternalcode.plots.plot.protection;

import com.eternalcode.plots.configuration.implementations.ProtectionConfiguration;

import java.util.Set;

public class ProtectionFactory {

    private final ProtectionConfiguration protectionConfig;

    public ProtectionFactory(ProtectionConfiguration protectionConfig) {
        this.protectionConfig = protectionConfig;
    }

    public Protection create(Set<Flag> protections) {
        return new Protection(protections);
    } // do czytania db

    public Protection createNew() {
        return new Protection(this.protectionConfig);
    } // do tworzenia nowych


}
