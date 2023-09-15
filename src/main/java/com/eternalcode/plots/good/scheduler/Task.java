package com.eternalcode.plots.good.scheduler;

public interface Task {

    void cancel();

    boolean isCanceled();

    boolean isAsync();

}