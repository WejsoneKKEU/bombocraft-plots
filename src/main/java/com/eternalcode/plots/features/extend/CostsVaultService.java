package com.eternalcode.plots.features.extend;

import com.eternalcode.plots.configuration.implementations.PluginConfiguration;
import com.eternalcode.plots.configuration.implementations.gui.models.ConfigExtend;
import com.eternalcode.plots.hook.VaultProvider;
import com.eternalcode.plots.utils.LegacyUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

class CostsVaultService implements CostsService {

    private final String noCostsMessage;
    private final ConfigExtend configExtend;
    private Economy economy = null;

    public CostsVaultService(VaultProvider vaultProvider, PluginConfiguration pluginConfiguration, ConfigExtend configExtend) {
        this.configExtend = configExtend;
        try {
            this.economy = vaultProvider.getEconomy();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        this.noCostsMessage = pluginConfiguration.plot.extend.noMoneyMessage;
    }

    @Override
    public boolean hasCosts(Player player) {
        return this.economy.has(player, this.configExtend.money);
    }

    @Override
    public void removeCosts(Player player) {
        this.economy.withdrawPlayer(player, this.configExtend.money);
    }

    @Override
    public String getCosts() {
        return String.valueOf(this.configExtend.money);
    }

    @Override
    public String noCostsMessage() {
        return LegacyUtils.color(this.noCostsMessage);
    }
}
