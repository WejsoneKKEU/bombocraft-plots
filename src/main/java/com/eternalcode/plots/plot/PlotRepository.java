package com.eternalcode.plots.plot;

import panda.std.reactive.Completable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlotRepository {

    CompletableFuture<Plot> loadPlot(UUID plotUUID);

    Completable<List<Plot>> loadAllPlot();

    void savePlot(Plot plot);

    void deletePlot(Plot plot);
}
