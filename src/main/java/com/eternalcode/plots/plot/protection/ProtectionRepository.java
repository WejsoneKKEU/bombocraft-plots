package com.eternalcode.plots.plot.protection;

import com.eternalcode.plots.plot.Plot;

public interface ProtectionRepository {

    void saveProtection(Plot plot, Protection protection);
}
