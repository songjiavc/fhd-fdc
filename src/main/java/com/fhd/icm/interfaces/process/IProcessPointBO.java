package com.fhd.icm.interfaces.process;

import com.fhd.entity.process.ProcessPoint;

/**
 * 
 * 增加、修改、删除、查询流程对象
 *
 * @author   李克东
 * @since    fhd Ver 4.5
 * @Date	 
 *
 * @see
 */
public interface IProcessPointBO{

    /**
     * <pre>
     * 根据流程ID获得流程节点
     * </pre>
     * 
     * @author 宋佳
     * @param id 流程ID
     * @return
     * @since  fhd　Ver 1.1
    */
    public ProcessPoint findProcessPointById(String processpointId);
}



 

