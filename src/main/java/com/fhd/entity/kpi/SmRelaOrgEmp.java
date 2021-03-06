package com.fhd.entity.kpi;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fhd.entity.base.IdEntity;
import com.fhd.entity.sys.orgstructure.SysEmployee;
import com.fhd.entity.sys.orgstructure.SysOrganization;

/**
 * 
 * 战略目标相关部门人员
 *
 * @author   胡迪新
 * @version  
 * @since    Ver 1.1
 * @Date	 2012	2012-9-17		上午10:22:24
 *
 * @see
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Cacheable(true)
@Table(name = "t_kpi_sm_rela_org_emp")
public class SmRelaOrgEmp extends IdEntity implements java.io.Serializable, Comparable<SmRelaOrgEmp>
{

    /**
     * 
     * @author 胡迪新
     * @since  fhd　Ver 1.1
     */

    private static final long serialVersionUID = 1L;

    // Fields

    /**
     * 战略目标
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STRATEGY_MAP_ID")
    private StrategyMap strategyMap;

    /**
     * 类型  B所属部门；R报告部门；V查看部门
     */
    @Column(name = "ETYPE")
    private String type;

    /**
     * 部门
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORG_ID")
    private SysOrganization org;

    /**
     * 员工
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMP_ID")
    private SysEmployee emp;

    // Constructors

    /** default constructor */
    public SmRelaOrgEmp()
    {
    }

    /** minimal constructor */
    public SmRelaOrgEmp(String id)
    {
        setId(id);
    }

    // Property accessors

    public StrategyMap getStrategyMap()
    {
        return strategyMap;
    }

    public void setStrategyMap(StrategyMap strategyMap)
    {
        this.strategyMap = strategyMap;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public SysOrganization getOrg()
    {
        return org;
    }

    public void setOrg(SysOrganization org)
    {
        this.org = org;
    }

    public SysEmployee getEmp()
    {
        return emp;
    }

    public void setEmp(SysEmployee emp)
    {
        this.emp = emp;
    }

    @Override
    public int compareTo(SmRelaOrgEmp o)
    {
        return this.getId().compareTo(o.getId());
    }

}