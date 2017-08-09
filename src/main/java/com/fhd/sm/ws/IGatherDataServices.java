package com.fhd.sm.ws;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

/**指标采集数据对外接口
 * @author xiaozhe
 *
 */
@WebService
@SOAPBinding(style = Style.RPC)
public interface IGatherDataServices {

	/**保存指标采集数据
	 * @param gatherDataDto 指标采集数据值对象
	 * @return
	 */
	public String saveKpiGatherData(
			@WebParam(name = "GatherData") GatherDataDto gatherDataDto);

}
