package com.cityfix.citifix.application.port.in;

import com.cityfix.citifix.domain.model.GlobalStats;

public interface GetGlobalStatsInputPort {
    GlobalStats execute();
}