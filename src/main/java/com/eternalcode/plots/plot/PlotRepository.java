package com.eternalcode.plots.plot;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlotRepository {

    CompletableFuture<Plot> loadPlot(UUID plotUUID);

    CompletableFuture<List<Plot>> loadAllPlot();

    void savePlot(Plot plot);

    void deletePlot(Plot plot);
}
