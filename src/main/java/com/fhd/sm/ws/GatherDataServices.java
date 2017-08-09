package com.fhd.sm.ws;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.utils.Identities;
import com.fhd.dao.kpi.GatherDataDAO;
import com.fhd.entity.kpi.GatherData;
import com.fhd.entity.kpi.RealTimeKpi;
import com.fhd.entity.sys.dic.DictEntry;
import com.fhd.fdc.utils.Contents;
import com.fhd.sm.business.KpiErrorCode;
import com.fhd.sm.business.RealTimeKpiBO;
import com.fhd.sys.business.dic.DictBO;

/**指标采集数据对外接口
 * @author xiaozhe
 *
 */
public class GatherDataServices implements IGatherDataServices {

    /**
     * 指标采集数据对外接口数据管理接口
     */
    @Autowired
    private GatherDataDAO o_gatherDataDAO;

    @Autowired
    private DictBO o_dictBO;
    
    @Autowired
    private RealTimeKpiBO o_realTimeKpiBO;

    private static Log logger = LogFactory.getLog(GatherDataServices.class);

    /**保存指标采集数据
     * @param gatherDataDto 指标采集数据值对象
     * @return
     */
    @Transactional
    public String saveKpiGatherData(final GatherDataDto gatherDataDto) {

        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date gatherDate = null;
        try {
            gatherDate = dateformat.parse(gatherDataDto.getTimes());
        }
        catch (ParseException e) {
            e.printStackTrace();
            logger.error("解析日期格式错误:exception:" + e.toString());
            return KpiErrorCode.DATE_FORMAT_ERROR;
        }
        String dataType = this.findKpiGatherDataType(gatherDataDto.getType());
        String level = this.findKpiGatherDataStatusLevel(gatherDataDto.getStatus());
        DictEntry statusDict = null;
        if(StringUtils.isNotBlank(level)){
            statusDict = o_dictBO.findDictEntryById(level);
        }
        try {
            String name = gatherDataDto.getName();
            RealTimeKpi realTimeKpi = o_realTimeKpiBO.findRealTimeKpiByName(name, "","");
            if(null==realTimeKpi){
                //添加实时指标
                realTimeKpi = new RealTimeKpi();
                realTimeKpi.setId(Identities.uuid());
                
            }
            realTimeKpi.setName(name);
            realTimeKpi.setDeleteStatus(true);
            realTimeKpi.setStatus(statusDict);
            realTimeKpi.setDesc(gatherDataDto.getDesc());
            realTimeKpi.setCode(gatherDataDto.getCode());
            o_realTimeKpiBO.mergeRealTimeKpi(realTimeKpi);
            
            
            GatherData gatherData = new GatherData();
            gatherData.setId(Identities.uuid());
            gatherData.setName(name);
            gatherData.setType(dataType);
            gatherData.setStatus(statusDict);
            gatherData.setTimes(gatherDate);
            gatherData.setValue(NumberUtils.toDouble(gatherDataDto.getValue()));
            gatherData.setRealTimeKpi(realTimeKpi);
            o_gatherDataDAO.merge(gatherData);
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error("数据库错误:exception:" + e.toString());
            return KpiErrorCode.DB_ERROR;
        }

        return KpiErrorCode.SUCCESS;
    }

    /**根据外部传入的指标采集值类型转化为内部的类型
     * @param type指标采集值类型
     * @return
     */
    private String findKpiGatherDataType(String type) {
        String dataType = "";
        if ("目标值".equals(type)) {
            dataType = Contents.KPI_GATHER_VALUE_TARGET_TYPE;
        }
        else if ("实际值".equals(type)) {
            dataType = Contents.KPI_GATHER_VALUE_FINISH_TYPE;
        }
        else if ("评估值".equals(type)) {
            dataType = Contents.KPI_GATHER_VALUE_ASSESSMENT_TYPE;
        }
        return dataType;
    }

    /**根据外部传入的指标采集值状态转化为内部状态值
     * @param status指标采集值状态
     * @return
     */
    private String findKpiGatherDataStatusLevel(String status) {
        String dataType = "";
        if ("高".equals(status)) {
            dataType = Contents.KPI_STATUS_HIGHT_LEVEL;
        }
        else if ("中".equals(status)) {
            dataType = Contents.KPI_STATUS_MID_LEVEL;
        }
        else if ("低".equals(status)) {
            dataType = Contents.KPI_STATUS_LOW_LEVEL;
        }
        return dataType;
    }
}
