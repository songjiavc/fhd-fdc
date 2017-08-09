package com.fhd.sm.interfaces;

import com.fhd.entity.kpi.KpiDataSource;

public interface IKpiDataSource {
    public abstract void deleteById(String id);
    public abstract void delete(KpiDataSource kpiDataSource);
    public abstract void save(KpiDataSource kpiDataSource);

}
