package com.fhd.icm.business.tempimport;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.entity.icm.standard.Standard;
import com.fhd.entity.icm.tempimport.TempControlMeasure;
import com.fhd.entity.icm.tempimport.TempControlStandard;
import com.fhd.entity.icm.tempimport.TempPracticeTestAssessPoint;
import com.fhd.entity.icm.tempimport.TempProcess;
import com.fhd.entity.icm.tempimport.TempProcessPoint;
import com.fhd.entity.icm.tempimport.TempProcessPointRelation;
import com.fhd.entity.icm.tempimport.TempProcessStandardRiskRelation;
import com.fhd.entity.icm.tempimport.TempRiskProcessPointMeasureRelation;
import com.fhd.entity.icm.tempimport.TempSamplingTestAssessPoint;
import com.fhd.entity.process.Process;
import com.fhd.entity.sys.file.FileUploadEntity;
import com.fhd.fdc.utils.UserContext;
import com.fhd.fdc.utils.excel.importexcel.ReadExcel;
import com.fhd.icm.business.process.ProcessBO;
import com.fhd.icm.business.standard.StandardBO;
import com.fhd.sys.business.file.FileUploadBO;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * 内控导入BO.
 * @author 吴德福
 * @Date 2013-12-05 14:10:32
 */
@Service
public class IcmImportBO {

	@Autowired
	private ProcessBO o_processBO;
	@Autowired
	private StandardBO o_controlStandardBO;
	@Autowired
	private FileUploadBO o_fileUploadBO;
	@Autowired
	private TempProcessBO o_tempProcessBO;
	@Autowired
	private TempProcessPointBO o_tempProcessPointBO;
	@Autowired
	private TempProcessPointRelationBO o_tempProcessPointRelationBO;
	@Autowired
	private TempControlStandardBO o_tempControlStandardBO;
	@Autowired
	private TempProcessStandardRiskRelationBO o_tempProcessStandardRiskRelationBO;
	@Autowired
	private TempControlMeasureBO o_tempControlMeasureBO;
	@Autowired
	private TempRiskProcessPointMeasureRelationBO o_tempRiskProcessPointMeasureRelationBO;
	@Autowired
	private TempPracticeTestAssessPointBO o_tempPracticeTestAssessPointBO;
	@Autowired
	private TempSamplingTestAssessPointBO o_tempSamplingTestAssessPointBO;
	
	/**
	 * 验证临时表数据.
	 * @author 吴德福
	 * @param fileId
	 * @param type
	 * @return Map<String,Object>
	 * @throws Exception
	 */
	@Transactional
	public Map<String,Object> validateData(String fileId, String type){
		Map<String,Object> map = new TreeMap<String,Object>();
		
		int errorAllCount = 0;
		int correctAllCount = 0;
		if("all".equals(type)){
			//1.验证流程信息
			Map<String,Object> processMap = o_tempProcessBO.validateProcessData(fileId);
			map.put("S1processMap", processMap);
			correctAllCount += Integer.valueOf(String.valueOf(processMap.get("correctCount")));
			errorAllCount += Integer.valueOf(String.valueOf(processMap.get("errorCount")));
			
			//2.验证流程节点信息
			Map<String,Object> processPointMap = o_tempProcessPointBO.validateProcessPointData(fileId);
			map.put("S2processPointMap", processPointMap);
			correctAllCount += Integer.valueOf(String.valueOf(processPointMap.get("correctCount")));
			errorAllCount += Integer.valueOf(String.valueOf(processPointMap.get("errorCount")));
			
			//3.验证流程节点关系信息
			Map<String,Object> processPointRelationMap = o_tempProcessPointRelationBO.validateProcessPointRelationData(fileId);
			map.put("S3processPointRelationMap", processPointRelationMap);
			correctAllCount += Integer.valueOf(String.valueOf(processPointRelationMap.get("correctCount")));
			errorAllCount += Integer.valueOf(String.valueOf(processPointRelationMap.get("errorCount")));
			
			//4.控制标准(要求)信息
			Map<String,Object> controlStandardMap = o_tempControlStandardBO.validateControlStandardData(fileId);
			map.put("S4controlStandardMap", controlStandardMap);
			correctAllCount += Integer.valueOf(String.valueOf(controlStandardMap.get("correctCount")));
			errorAllCount += Integer.valueOf(String.valueOf(controlStandardMap.get("errorCount")));
			
			//5.流程--控制标准(要求)--风险信息
			Map<String,Object> processStandardRiskRelationMap = o_tempProcessStandardRiskRelationBO.validateProcessStandardRiskRelationData(fileId);
			map.put("S5processStandardRiskRelationMap", processStandardRiskRelationMap);
			correctAllCount += Integer.valueOf(String.valueOf(processStandardRiskRelationMap.get("correctCount")));
			errorAllCount += Integer.valueOf(String.valueOf(processStandardRiskRelationMap.get("errorCount")));
			
			//6.控制措施信息
			Map<String,Object> controlMeasureMap = o_tempControlMeasureBO.validateControlMeasureData(fileId);
			map.put("S6controlMeasureMap", controlMeasureMap);
			correctAllCount += Integer.valueOf(String.valueOf(controlMeasureMap.get("correctCount")));
			errorAllCount += Integer.valueOf(String.valueOf(controlMeasureMap.get("errorCount")));
			
			//7.风险--流程--流程节点--控制措施信息
			Map<String,Object> riskProcessPointMeasureRelationMap = o_tempRiskProcessPointMeasureRelationBO.validateRiskProcessPointMeasureRelationData(fileId);
			map.put("S7riskProcessPointMeasureRelationMap", riskProcessPointMeasureRelationMap);
			correctAllCount += Integer.valueOf(String.valueOf(riskProcessPointMeasureRelationMap.get("correctCount")));
			errorAllCount += Integer.valueOf(String.valueOf(riskProcessPointMeasureRelationMap.get("errorCount")));
			
			//8.穿行测试评价点信息
			Map<String,Object> practiceTestAssessPointMap = o_tempPracticeTestAssessPointBO.validatePracticeTestAssessPointData(fileId);
			map.put("S8practiceTestAssessPointMap", practiceTestAssessPointMap);
			correctAllCount += Integer.valueOf(String.valueOf(practiceTestAssessPointMap.get("correctCount")));
			errorAllCount += Integer.valueOf(String.valueOf(practiceTestAssessPointMap.get("errorCount")));

			//9.抽样测试评价点信息
			Map<String,Object> samplingTestAssessPointMap = o_tempSamplingTestAssessPointBO.validateSamplingTestAssessPointData(fileId);
			map.put("S9samplingTestAssessPointMap", samplingTestAssessPointMap);
			correctAllCount += Integer.valueOf(String.valueOf(samplingTestAssessPointMap.get("correctCount")));
			errorAllCount += Integer.valueOf(String.valueOf(samplingTestAssessPointMap.get("errorCount")));
		}else if("0".equals(type)){
			//1.验证流程信息
			Map<String,Object> processMap = o_tempProcessBO.validateProcessData(fileId);
			map.put("S1processMap", processMap);
			correctAllCount = Integer.valueOf(String.valueOf(processMap.get("correctCount")));
			errorAllCount = Integer.valueOf(String.valueOf(processMap.get("errorCount")));
		}else if("1".equals(type)){
			//2.验证流程节点信息
			Map<String,Object> processPointMap = o_tempProcessPointBO.validateProcessPointData(fileId);
			map.put("S2processPointMap", processPointMap);
			correctAllCount = Integer.valueOf(String.valueOf(processPointMap.get("correctCount")));
			errorAllCount = Integer.valueOf(String.valueOf(processPointMap.get("errorCount")));
		}else if("2".equals(type)){
			//3.验证流程节点关系信息
			Map<String,Object> processPointRelationMap = o_tempProcessPointRelationBO.validateProcessPointRelationData(fileId);
			map.put("S3processPointRelationMap", processPointRelationMap);
			correctAllCount = Integer.valueOf(String.valueOf(processPointRelationMap.get("correctCount")));
			errorAllCount = Integer.valueOf(String.valueOf(processPointRelationMap.get("errorCount")));
		}else if("3".equals(type)){
			//4.控制标准(要求)信息
			Map<String,Object> controlStandardMap = o_tempControlStandardBO.validateControlStandardData(fileId);
			map.put("S4controlStandardMap", controlStandardMap);
			correctAllCount = Integer.valueOf(String.valueOf(controlStandardMap.get("correctCount")));
			errorAllCount = Integer.valueOf(String.valueOf(controlStandardMap.get("errorCount")));
		}else if("4".equals(type)){
			//5.流程--控制标准(要求)--风险信息
			Map<String,Object> processStandardRiskRelationMap = o_tempProcessStandardRiskRelationBO.validateProcessStandardRiskRelationData(fileId);
			map.put("S5processStandardRiskRelationMap", processStandardRiskRelationMap);
			correctAllCount = Integer.valueOf(String.valueOf(processStandardRiskRelationMap.get("correctCount")));
			errorAllCount = Integer.valueOf(String.valueOf(processStandardRiskRelationMap.get("errorCount")));
		}else if("5".equals(type)){
			//6.控制措施信息
			Map<String,Object> controlMeasureMap = o_tempControlMeasureBO.validateControlMeasureData(fileId);
			map.put("S6controlMeasureMap", controlMeasureMap);
			correctAllCount = Integer.valueOf(String.valueOf(controlMeasureMap.get("correctCount")));
			errorAllCount = Integer.valueOf(String.valueOf(controlMeasureMap.get("errorCount")));
		}else if("6".equals(type)){
			//7.风险--流程--流程节点--控制措施信息
			Map<String,Object> riskProcessPointMeasureRelationMap = o_tempRiskProcessPointMeasureRelationBO.validateRiskProcessPointMeasureRelationData(fileId);
			map.put("S7riskProcessPointMeasureRelationMap", riskProcessPointMeasureRelationMap);
			correctAllCount = Integer.valueOf(String.valueOf(riskProcessPointMeasureRelationMap.get("correctCount")));
			errorAllCount = Integer.valueOf(String.valueOf(riskProcessPointMeasureRelationMap.get("errorCount")));
		}else if("7".equals(type)){
			//8.穿行测试评价点信息
			Map<String,Object> practiceTestAssessPointMap = o_tempPracticeTestAssessPointBO.validatePracticeTestAssessPointData(fileId);
			map.put("S8practiceTestAssessPointMap", practiceTestAssessPointMap);
			correctAllCount = Integer.valueOf(String.valueOf(practiceTestAssessPointMap.get("correctCount")));
			errorAllCount = Integer.valueOf(String.valueOf(practiceTestAssessPointMap.get("errorCount")));
		}else if("8".equals(type)){
			//9.抽样测试评价点信息
			Map<String,Object> samplingTestAssessPointMap = o_tempSamplingTestAssessPointBO.validateSamplingTestAssessPointData(fileId);
			map.put("S9samplingTestAssessPointMap", samplingTestAssessPointMap);
			correctAllCount = Integer.valueOf(String.valueOf(samplingTestAssessPointMap.get("correctCount")));
			errorAllCount = Integer.valueOf(String.valueOf(samplingTestAssessPointMap.get("errorCount")));
		}
		map.put("errorAllCount", errorAllCount);
		map.put("correctAllCount", correctAllCount);
		return map;
	}
	/**
	 * 读取excel文件数据存入临时表.
	 * @author 吴德福
	 * @param fileId
	 * @param type 数据类型
	 * @param importStyle 导入方式
	 * @throws Exception
	 */
	@Transactional
	public void saveTempProcessByExcel(byte[] contents, String type, String importStyle) throws Exception{
		//FileUploadEntity fileUploadEntity = o_fileUploadBO.findById(fileId);
		//删除临时表数据
		this.removeTempData(type);
		
		/**
		 * 声明fileUploadEntity为空，临时表不存fileId
		 * 前台excel导入直接浏览，而不是附件上传 修改者：郑军祥
		 */
		FileUploadEntity fileUploadEntity = null;
		
		if("all".equals(type)){
			//1.保存流程临时表数据,readEspecialExcel每个占用0.8s的时间
			List<List<String>> processDataList = new ReadExcel<String>().readEspecialExcel(new ByteArrayInputStream(contents), Integer.valueOf(0), 2);

			//流程list
			List<Process> processList = o_processBO.findProcessListByCompanyId(UserContext.getUser().getCompanyid());
			o_tempProcessBO.saveTempDataByDataListAndType(processList, processDataList, fileUploadEntity);
			//2.保存流程节点临时表数据
			List<List<String>> processPointDataList = new ReadExcel<String>().readEspecialExcel(new ByteArrayInputStream(contents), Integer.valueOf(1), 2);
			o_tempProcessPointBO.saveTempProcessPoint(processPointDataList, fileUploadEntity);
			//3.保存流程节点关系临时表数据
			List<List<String>> processPointRelationDataList = new ReadExcel<String>().readEspecialExcel(new ByteArrayInputStream(contents), Integer.valueOf(2), 2);
			o_tempProcessPointRelationBO.saveTempProcessPointRelation(processPointRelationDataList, fileUploadEntity);
			//4.控制标准(要求)临时表数据
			List<List<String>> controlStandardDataList = new ReadExcel<String>().readEspecialExcel(new ByteArrayInputStream(contents), Integer.valueOf(3), 2);

			//控制标准(要求)list
			List<Standard> controlStandardList = o_controlStandardBO.findControlStandardListByCompanyId(UserContext.getUser().getCompanyid());
			o_tempControlStandardBO.saveTempDataByDataListAndType(controlStandardList, controlStandardDataList, fileUploadEntity);
			//5.流程--控制标准(要求)--风险临时表数据
			List<List<String>> processStandardRiskDataList = new ReadExcel<String>().readEspecialExcel(new ByteArrayInputStream(contents), Integer.valueOf(4), 2);
			o_tempProcessStandardRiskRelationBO.saveTempProcessStandardRiskRelation(processStandardRiskDataList, fileUploadEntity);
			//6.控制措施临时表数据
			List<List<String>> controlMeasureDataList = new ReadExcel<String>().readEspecialExcel(new ByteArrayInputStream(contents), Integer.valueOf(5), 2);
			o_tempControlMeasureBO.saveTempControlMeasure(controlMeasureDataList, fileUploadEntity);
			//7.风险--流程--流程节点--控制措施临时表数据
			List<List<String>> riskProcessPointMeasureDataList = new ReadExcel<String>().readEspecialExcel(new ByteArrayInputStream(contents), Integer.valueOf(6), 2);
			o_tempRiskProcessPointMeasureRelationBO.saveTempProcessStandardRiskRelation(riskProcessPointMeasureDataList, fileUploadEntity);
			//8.穿行测试评价点临时表数据
			List<List<String>> practiceTestAssessPointDataList = new ReadExcel<String>().readEspecialExcel(new ByteArrayInputStream(contents), Integer.valueOf(7), 2);
			o_tempPracticeTestAssessPointBO.saveTempPracticeTestAssessPoint(practiceTestAssessPointDataList, fileUploadEntity);
			//9.抽样测试评价点临时表数据
			List<List<String>> samplingTestAssessPointDataList = new ReadExcel<String>().readEspecialExcel(new ByteArrayInputStream(contents), Integer.valueOf(8), 2);
			o_tempSamplingTestAssessPointBO.saveTempSamplingTestAssessPoint(samplingTestAssessPointDataList, fileUploadEntity);
		}else {
			//保存临时表数据
			List<List<String>> dataList = new ReadExcel<String>().readEspecialExcel(new ByteArrayInputStream(contents), Integer.valueOf(type), 2);
			if("0".equals(type)){
				//流程list
				List<Process> processList = o_processBO.findProcessListByCompanyId(UserContext.getUser().getCompanyid());
				o_tempProcessBO.saveTempDataByDataListAndType(processList, dataList, fileUploadEntity);
			}else if("1".equals(type)){
				//保存流程节点临时表数据
				o_tempProcessPointBO.saveTempProcessPoint(dataList, fileUploadEntity);
			}else if("2".equals(type)){
				//保存流程节点关系临时表数据
				o_tempProcessPointRelationBO.saveTempProcessPointRelation(dataList, fileUploadEntity);
			}else if("3".equals(type)){
				//控制标准(要求)list
				List<Standard> controlStandardList = o_controlStandardBO.findControlStandardListByCompanyId(UserContext.getUser().getCompanyid());
				o_tempControlStandardBO.saveTempDataByDataListAndType(controlStandardList, dataList, fileUploadEntity);
			}else if("4".equals(type)){
				//流程--控制标准(要求)--风险临时表数据
				o_tempProcessStandardRiskRelationBO.saveTempProcessStandardRiskRelation(dataList, fileUploadEntity);
			}else if("5".equals(type)){
				//控制措施临时表数据
				o_tempControlMeasureBO.saveTempControlMeasure(dataList, fileUploadEntity);
			}else if("6".equals(type)){
				//风险--流程--流程节点--控制措施临时表数据
				o_tempRiskProcessPointMeasureRelationBO.saveTempProcessStandardRiskRelation(dataList, fileUploadEntity);
			}else if("7".equals(type)){
				//穿行测试评价点临时表数据
				o_tempPracticeTestAssessPointBO.saveTempPracticeTestAssessPoint(dataList, fileUploadEntity);
			}else if("8".equals(type)){
				//抽样测试评价点临时表数据
				o_tempSamplingTestAssessPointBO.saveTempSamplingTestAssessPoint(dataList, fileUploadEntity);
			}
		}
	}
	/**
	 * 根据fileId删除对应的临时表数据.
	 * @author 吴德福
	 * @param fileId
	 * @type
	 */
	@Transactional
	public void removeTempData(String type){
		if("all".equals(type)){
			//1.流程临时表
			o_tempProcessBO.removeBatchTempProcessByHql();
			//2.流程节点临时表
			o_tempProcessPointBO.removeBatchTempProcessPointByHql();
			//3.流程节点临时表
			o_tempProcessPointRelationBO.removeBatchTempProcessPointRelationByHql();
			//4.控制标准(要求)临时表
			o_tempControlStandardBO.removeBatchTempControlStandardByHql();
			//5.流程--控制标准(要求)--风险临时表
			o_tempProcessStandardRiskRelationBO.removeBatchTempProcessStandardRiskRelationByHql();
			//6.控制措施临时表
			o_tempControlMeasureBO.removeBatchTempControlMeasureByHql();
			//7.风险--流程--流程节点--控制措施临时表
			o_tempRiskProcessPointMeasureRelationBO.removeBatchTempRiskProcessPointMeasureRelationByHql();
			//8.穿行测试评价点临时表
			o_tempPracticeTestAssessPointBO.removeBatchTempPracticeTestAssessPointByHql();
			//9.抽样测试评价点临时表
			o_tempSamplingTestAssessPointBO.removeBatchTempSamplingTestAssessPointByHql();
		}else if("0".equals(type)){
			//流程临时表
			o_tempProcessBO.removeBatchTempProcessByHql();
		}else if("1".equals(type)){
			//流程节点临时表
			o_tempProcessPointBO.removeBatchTempProcessPointByHql();
		}else if("2".equals(type)){
			//流程节点临时表
			o_tempProcessPointRelationBO.removeBatchTempProcessPointRelationByHql();
		}else if("3".equals(type)){
			//控制标准(要求)临时表
			o_tempControlStandardBO.removeBatchTempControlStandardByHql();
		}else if("4".equals(type)){
			//流程--控制标准(要求)--风险临时表
			o_tempProcessStandardRiskRelationBO.removeBatchTempProcessStandardRiskRelationByHql();
		}else if("5".equals(type)){
			//控制措施临时表
			o_tempControlMeasureBO.removeBatchTempControlMeasureByHql();
		}else if("6".equals(type)){
			//风险--流程--流程节点--控制措施临时表数据
			o_tempRiskProcessPointMeasureRelationBO.removeBatchTempRiskProcessPointMeasureRelationByHql();
		}else if("7".equals(type)){
			//穿行测试评价点临时表
			o_tempPracticeTestAssessPointBO.removeBatchTempPracticeTestAssessPointByHql();
		}else if("8".equals(type)){
			//抽样测试评价点临时表
			o_tempSamplingTestAssessPointBO.removeBatchTempSamplingTestAssessPointByHql();
		}
	}
	/**
	 * 导入临时表数据.
	 * @param fileId
	 * @param type
	 */
	@Transactional
	public void importData(String fileId, String type){
		Map<String,Object> map = new HashMap<String,Object>();

		if("all".equals(type)){
			//1.流程信息
			List<TempProcess> tempProcessList = o_tempProcessBO.findProcessPreviewListBySome("", fileId);
			map.put("tempProcessList", tempProcessList);
			
			//2.流程节点信息
			List<TempProcessPoint> tempProcessPointList = o_tempProcessPointBO.findProcessPointPreviewListBySome("", fileId);
			map.put("tempProcessPointList", tempProcessPointList);
			
			//3.流程节点关系信息
			List<TempProcessPointRelation> tempProcessPointRelationList = o_tempProcessPointRelationBO.findProcessPointRelationPreviewListBySome("", fileId);
			map.put("tempProcessPointRelationList", tempProcessPointRelationList);
			
			//4.控制标准(要求)信息
			List<TempControlStandard> tempControlStandardList = o_tempControlStandardBO.findControlStandardPreviewListBySome("", fileId);
			map.put("tempControlStandardList", tempControlStandardList);
			
			//5.流程--控制标准(要求)--风险关系信息
			List<TempProcessStandardRiskRelation> tempProcessStandardRiskRelationList = o_tempProcessStandardRiskRelationBO.findProcessStandardRiskRelationPreviewListBySome("", fileId);
			map.put("tempProcessStandardRiskRelationList", tempProcessStandardRiskRelationList);
			
			//6.控制措施信息
			List<TempControlMeasure> tempControlMeasureList = o_tempControlMeasureBO.findControlMeasurePreviewListBySome("", fileId);
			map.put("tempControlMeasureList", tempControlMeasureList);
			
			//7.风险--流程--流程节点--控制措施关系信息
			List<TempRiskProcessPointMeasureRelation> tempRiskProcessPointMeasureRelationList = o_tempRiskProcessPointMeasureRelationBO.findRiskProcessPointMeasureRelationPreviewListBySome("", fileId);
			map.put("tempRiskProcessPointMeasureRelationList", tempRiskProcessPointMeasureRelationList);
			
			//8.穿行测试评价点信息
			List<TempPracticeTestAssessPoint> tempPracticeTestAssessPointList = o_tempPracticeTestAssessPointBO.findPracticeTestAssessPointPreviewListBySome("", fileId);
			map.put("tempPracticeTestAssessPointList", tempPracticeTestAssessPointList);

			//9.抽样测试评价点信息
			List<TempSamplingTestAssessPoint> tempSamplingTestAssessPointList = o_tempSamplingTestAssessPointBO.findSamplingTestAssessPointPreviewListBySome("", fileId);
			map.put("tempSamplingTestAssessPointList", tempSamplingTestAssessPointList);
		}else if("0".equals(type)){
			//1.流程信息
			List<TempProcess> tempProcessList = o_tempProcessBO.findProcessPreviewListBySome("", fileId);
			map.put("tempProcessList", tempProcessList);
		}else if("1".equals(type)){
			//2.流程节点信息
			List<TempProcessPoint> tempProcessPointList = o_tempProcessPointBO.findProcessPointPreviewListBySome("", fileId);
			map.put("tempProcessPointList", tempProcessPointList);
		}else if("2".equals(type)){
			//3.流程节点关系信息
			List<TempProcessPointRelation> tempProcessPointRelationList = o_tempProcessPointRelationBO.findProcessPointRelationPreviewListBySome("", fileId);
			map.put("tempProcessPointRelationList", tempProcessPointRelationList);
		}else if("3".equals(type)){
			//4.控制标准(要求)信息
			List<TempControlStandard> tempControlStandardList = o_tempControlStandardBO.findControlStandardPreviewListBySome("", fileId);
			map.put("tempControlStandardList", tempControlStandardList);
		}else if("4".equals(type)){
			//5.流程--控制标准(要求)--风险关系信息
			List<TempProcessStandardRiskRelation> tempProcessStandardRiskRelationList = o_tempProcessStandardRiskRelationBO.findProcessStandardRiskRelationPreviewListBySome("", fileId);
			map.put("tempProcessStandardRiskRelationList", tempProcessStandardRiskRelationList);
		}else if("5".equals(type)){
			//6.控制措施信息
			List<TempControlMeasure> tempControlMeasureList = o_tempControlMeasureBO.findControlMeasurePreviewListBySome("", fileId);
			map.put("tempControlMeasureList", tempControlMeasureList);
		}else if("6".equals(type)){
			//7.风险--流程--流程节点--控制措施关系信息
			List<TempRiskProcessPointMeasureRelation> tempRiskProcessPointMeasureRelationList = o_tempRiskProcessPointMeasureRelationBO.findRiskProcessPointMeasureRelationPreviewListBySome("", fileId);
			map.put("tempRiskProcessPointMeasureRelationList", tempRiskProcessPointMeasureRelationList);
		}else if("7".equals(type)){
			//8.穿行测试评价点信息
			List<TempPracticeTestAssessPoint> tempPracticeTestAssessPointList = o_tempPracticeTestAssessPointBO.findPracticeTestAssessPointPreviewListBySome("", fileId);
			map.put("tempPracticeTestAssessPointList", tempPracticeTestAssessPointList);
		}else if("8".equals(type)){
			//9.抽样测试评价点信息
			List<TempSamplingTestAssessPoint> tempSamplingTestAssessPointList = o_tempSamplingTestAssessPointBO.findSamplingTestAssessPointPreviewListBySome("", fileId);
			map.put("tempSamplingTestAssessPointList", tempSamplingTestAssessPointList);
		}
		
		this.saveBatchDataFromTempToReal(map, type);
		
		//最后，删除临时表数据
		this.removeTempData(type);
	}
	/**
	 * 将流程临时表信息导入真实流程表.
	 * @author 吴德福
	 * @param map 临时对象map
	 * @param type
	 */
	@Transactional
	private void saveBatchDataFromTempToReal(final Map<String,Object> map, final String type){
		if("all".equals(type)){
			//郑军祥：多个批处理导入，会有多个事务，这块暂时不做单个事务控制，以后发现问题确实要补充再加
			//1.导入流程信息
			o_tempProcessBO.saveBatchDataFromTempToReal(map);
			//2.导入流程节点信息
			o_tempProcessPointBO.saveBatchDataFromTempToReal(map);
			//3.导入流程节点关系信息
			o_tempProcessPointRelationBO.saveBatchDataFromTempToReal(map);
			//4.导入内控标准(要求)信息
			o_tempControlStandardBO.saveBatchDataFromTempToReal(map);
			//5.导入流程--控制标准(要求)--风险信息
			o_tempProcessStandardRiskRelationBO.saveBatchDataFromTempToReal(map);
			//6.导入控制措施信息
			o_tempControlMeasureBO.saveBatchDataFromTempToReal(map);
			//7.导入风险--流程--流程节点--控制措施关系信息
			o_tempRiskProcessPointMeasureRelationBO.saveBatchDataFromTempToReal(map);
			//8.穿行测试评价点信息
			o_tempPracticeTestAssessPointBO.saveBatchDataFromTempToReal(map);
			//9.抽样测试评价点信息
			o_tempSamplingTestAssessPointBO.saveBatchDataFromTempToReal(map);
		}else if("0".equals(type)){
			//导入流程信息
			o_tempProcessBO.saveBatchDataFromTempToReal(map);
		}else if("1".equals(type)){
			//导入流程节点信息
			o_tempProcessPointBO.saveBatchDataFromTempToReal(map);
		}else if("2".equals(type)){
			//导入流程节点关系信息
			o_tempProcessPointRelationBO.saveBatchDataFromTempToReal(map);
		}else if("3".equals(type)){
			//导入内控标准(要求)信息
			o_tempControlStandardBO.saveBatchDataFromTempToReal(map);
		}else if("4".equals(type)){
			//导入流程--控制标准(要求)--风险信息
			o_tempProcessStandardRiskRelationBO.saveBatchDataFromTempToReal(map);
		}else if("5".equals(type)){
			//导入控制措施信息
			o_tempControlMeasureBO.saveBatchDataFromTempToReal(map);
		}else if("6".equals(type)){
			//导入风险--流程--流程节点--控制措施关系信息
			o_tempRiskProcessPointMeasureRelationBO.saveBatchDataFromTempToReal(map);
		}else if("7".equals(type)){
			//穿行测试评价点信息
			o_tempPracticeTestAssessPointBO.saveBatchDataFromTempToReal(map);
		}else if("8".equals(type)){
			//抽样测试评价点信息
			o_tempSamplingTestAssessPointBO.saveBatchDataFromTempToReal(map);
		}
	}
}
