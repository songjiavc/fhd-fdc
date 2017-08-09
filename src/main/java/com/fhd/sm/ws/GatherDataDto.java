package com.fhd.sm.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**指标采集值数据传输对象
 * @author xiaozhe
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GatherData")
public class GatherDataDto {
    /**
     * 指标名称
     */
    private String name;

    /**
     * 采集值
     */
    private String value;

    /**
     * 采集值类型
     */
    private String type;

    /**
     * 采集值状态
     */
    private String status;

    /**
     * 采集时间
     */
    private String times;

    /**
     * 单位
     */
    private String unit;

    /**
     * 编号 
     */
    private String code;

    /**
     * 描述
     * 
     */
    private String desc;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

}
