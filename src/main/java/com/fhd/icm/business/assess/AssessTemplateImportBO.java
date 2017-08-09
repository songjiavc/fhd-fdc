package com.fhd.icm.business.assess;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhd.core.utils.Identities;
import com.fhd.dao.icm.assess.AssessResultDAO;
import com.fhd.entity.icm.assess.AssessResult;
import com.fhd.entity.sys.file.FileUploadEntity;
import com.fhd.fdc.utils.UserContext;
import com.fhd.fdc.utils.excel.importexcel.ReadExcel;
import com.fhd.icm.web.form.assess.TempAssessTemplate;
import com.fhd.sys.business.file.FileUploadBO;

@Service
public class AssessTemplateImportBO {
	@Autowired
	private FileUploadBO o_fileUploadBO;
	@Autowired
	private AssessResultBO o_assessResultBO;
	@Autowired
	private AssessResultDAO o_assessResultDAO;
	/**
	 * 读取excel文件数据存入表.
	 * @author 张雷
	 * @param fileId excel文件ID
	 * @param type 数据类型
	 * @param importStyle 导入方式
	 * @throws Exception
	 */
	@Transactional
	public void saveTempAssessTemplateByExcel(String fileId,String uploadFiles) throws Exception{
		FileUploadEntity fileUploadEntity = o_fileUploadBO.findById(fileId);
		if(StringUtils.isNotBlank(uploadFiles)){
			JSONArray jsonArray = JSONArray.fromObject(java.net.URLDecoder.decode(uploadFiles, "utf-8"));
	        List<TempAssessTemplate> tempAssessTemplateList = new ArrayList<TempAssessTemplate>();
			tempAssessTemplateList.addAll(saveTempAssessTemplateByPracticeTest(fileUploadEntity, jsonArray));
			tempAssessTemplateList.addAll(saveTempAssessTemplateBySampleTest(fileUploadEntity, jsonArray));
			saveBatchDataFromTempToReal(tempAssessTemplateList);
		}
		
	}
	
	/**
	 * 封装excel的穿行测试的评价结果数据到临时集合里
	 * @author 张雷
	 * @param fileUploadEntity 附件实体
	 * @param jsonArray 附件id和名称组成的json字符串
	 * @throws Exception
	 */
	public List<TempAssessTemplate> saveTempAssessTemplateByPracticeTest(FileUploadEntity fileUploadEntity, JSONArray jsonArray) throws Exception{
		
		//1.保存评价底稿临时表数据
		List<List<String>> practiceTestDataList = new ReadExcel<String>().readEspecialExcel(new ByteArrayInputStream(fileUploadEntity.getContents()), Integer.valueOf(0), 3);
		List<TempAssessTemplate> tempAssessTemplateList = new ArrayList<TempAssessTemplate>();
		for (List<String> practiceTestData : practiceTestDataList) {
			TempAssessTemplate tempAssessTemplate = new TempAssessTemplate();
			tempAssessTemplate.setSampleCode(StringUtils.isNotBlank(practiceTestData.get(4))?practiceTestData.get(4):null);
			tempAssessTemplate.setSampleName(StringUtils.isNotBlank(practiceTestData.get(5))?practiceTestData.get(5):null);
			tempAssessTemplate.setIsQualified(StringUtils.isNotBlank(practiceTestData.get(6))?practiceTestData.get(6):null);
			tempAssessTemplate.setComment(StringUtils.isNotBlank(practiceTestData.get(7))?practiceTestData.get(7):null);
			tempAssessTemplate.setPlanId(practiceTestData.get(8));
			tempAssessTemplate.setAssessResultId(practiceTestData.get(9));
			for ( int i = 0 ; i<jsonArray.size(); i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				if(StringUtils.isNotBlank(practiceTestData.get(5)) && jsonObject.get("fileName").equals(practiceTestData.get(5).trim())){
					tempAssessTemplate.setFileId(jsonObject.get("id")!=null?(String)jsonObject.get("id"):null);
				}
			}
			tempAssessTemplateList.add(tempAssessTemplate);
		}
		return tempAssessTemplateList;
	}
	
	/**
	 * 封装excel里的抽样结果的数据到临时集合里
	 * @author 张雷
	 * @param fileUploadEntity 附件实体
	 * @param jsonArray 附件id和名称组成的json字符串
	 * @throws Exception
	 */
	public List<TempAssessTemplate> saveTempAssessTemplateBySampleTest(FileUploadEntity fileUploadEntity, JSONArray jsonArray) throws Exception{
		//1.保存评价底稿临时表数据
		List<List<String>> sampleTestDataList = new ReadExcel<String>().readEspecialExcel(new ByteArrayInputStream(fileUploadEntity.getContents()), Integer.valueOf(1), 3);
		List<TempAssessTemplate> tempAssessTemplateList = new ArrayList<TempAssessTemplate>();
		for (List<String> sampleTestData : sampleTestDataList) {
			TempAssessTemplate tempAssessTemplate = new TempAssessTemplate();
			tempAssessTemplate.setSampleCode(StringUtils.isNotBlank(sampleTestData.get(4))?sampleTestData.get(4):null);
			tempAssessTemplate.setSampleName(StringUtils.isNotBlank(sampleTestData.get(5))?sampleTestData.get(5):null);
			tempAssessTemplate.setIsQualified(StringUtils.isNotBlank(sampleTestData.get(6))?sampleTestData.get(6):null);
			tempAssessTemplate.setComment(StringUtils.isNotBlank(sampleTestData.get(7))?sampleTestData.get(7):null);
			tempAssessTemplate.setPlanId(sampleTestData.get(8));
			tempAssessTemplate.setAssessResultId(sampleTestData.get(9));
			for ( int i = 0 ; i<jsonArray.size(); i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				if(StringUtils.isNotBlank(sampleTestData.get(5)) && jsonObject.get("fileName").equals(sampleTestData.get(5).trim())){
					tempAssessTemplate.setFileId(jsonObject.get("id")!=null?(String)jsonObject.get("id"):null);
				}
			}
			tempAssessTemplateList.add(tempAssessTemplate);
		}
		return tempAssessTemplateList;
	}
	
	
	/**
	 * 验证临时表数据.
	 * @author 张雷
	 * @param fileId excel文件ID
	 * @param uploadFileNames 附件文件名称，多个以“:”隔开
	 * @return Map<String,Object>
	 * @throws Exception
	 */
	@Transactional
	public Map<String, Object> validateData(String fileId,String uploadFileNames) throws Exception{
		Map<String, Object> map = new TreeMap<String, Object>();
		FileUploadEntity fileUploadEntity = o_fileUploadBO.findById(fileId);
		List<List<String>> practiceTestDataList = new ReadExcel<String>().readEspecialExcel(new ByteArrayInputStream(fileUploadEntity.getContents()), Integer.valueOf(0), 3);
		StringBuffer sheet1E = new StringBuffer();
		StringBuffer sheet1F = new StringBuffer();
		StringBuffer sheet1G = new StringBuffer();
		StringBuffer sheet1H = new StringBuffer();
		StringBuffer sheet2E = new StringBuffer();
		StringBuffer sheet2F = new StringBuffer();
		StringBuffer sheet2G = new StringBuffer();
		StringBuffer sheet2H = new StringBuffer();
		boolean flag = true;//附件是否有问题
		List<String> uploadFileNameList = new ArrayList<String>(); 
		if(StringUtils.isNotBlank(uploadFileNames)){
			String[] uploadFileNameArray = java.net.URLDecoder.decode(uploadFileNames, "utf-8").split(":");
			for (int i = 0; i < uploadFileNameArray.length; i++) {
				uploadFileNameList.add(uploadFileNameArray[i]);
			}
		}
		for (int i = 0; i < practiceTestDataList.size(); i++) {
			if(StringUtils.isBlank(practiceTestDataList.get(i).get(4).trim())){
				sheet1E.append("E").append(i+4).append("; ");
			}
			if(StringUtils.isBlank(practiceTestDataList.get(i).get(6).trim())){
				sheet1G.append("G").append(i+4).append("; ");
			}else{
				if(!"合格".equals(practiceTestDataList.get(i).get(6).trim())){
					if(StringUtils.isBlank(practiceTestDataList.get(i).get(7).trim())){
						sheet1H.append("H").append(i+4).append("; ");
					}
					if("不合格".equals(practiceTestDataList.get(i).get(6).trim())){
						if(uploadFileNameList.size()<1){
							flag = false;
						}else if(!uploadFileNameList.contains(practiceTestDataList.get(i).get(5).trim())){
							sheet1F.append("F").append(i+4).append("; ");
							flag = false;
						}
					}
				}
			}
		}
		List<List<String>> sampleTestDataList = new ReadExcel<String>().readEspecialExcel(new ByteArrayInputStream(fileUploadEntity.getContents()), Integer.valueOf(1), 3);
		for (int j = 0; j < sampleTestDataList.size(); j++) {
			if(StringUtils.isBlank(sampleTestDataList.get(j).get(4).trim())){
				sheet2E.append("E").append(j+4).append("; ");
			}
			if(StringUtils.isBlank(sampleTestDataList.get(j).get(6).trim())){
				sheet2G.append("G").append(j+4).append("; ");
			}else{
				if(!"合格".equals(sampleTestDataList.get(j).get(6).trim())){
					if(StringUtils.isBlank(sampleTestDataList.get(j).get(7).trim())){
						sheet2H.append("H").append(j+4).append("; ");
					}
					if("不合格".equals(sampleTestDataList.get(j).get(6).trim())){
						if(uploadFileNameList.size()<1){
							flag = false;
						}else if(!uploadFileNameList.contains(sampleTestDataList.get(j).get(5).trim())){
							sheet2F.append("F").append(j+4).append("; ");
							flag = false;
						}
					}
				}
			}
		}
		if(StringUtils.isBlank(sheet1E) && StringUtils.isBlank(sheet1G) && StringUtils.isBlank(sheet1H)
				&& StringUtils.isBlank(sheet2E) && StringUtils.isBlank(sheet2G)&& StringUtils.isBlank(sheet2H)){
			if(!flag){
				map.put("success", false);
				if(StringUtils.isBlank(sheet1F) && StringUtils.isBlank(sheet2F)){
					map.put("sheetfileproblem", true);
				}else{
					map.put("sheet1fileproblem", sheet1F);
					map.put("sheet2fileproblem", sheet2F);
				}
			}else{
				map.put("success", true);
			}
		}else{
			map.put("success", false);
			if(!flag){
				if(StringUtils.isBlank(sheet1F) && StringUtils.isBlank(sheet2F)){
					map.put("sheetfileproblem", true);
				}else{
					map.put("sheet1fileproblem", sheet1F);
					map.put("sheet2fileproblem", sheet2F);
				}
			}else{
				map.put("sheet1blankproblem", sheet1E.append(sheet1G).append(sheet1H));
				map.put("sheet2blankproblem", sheet2E.append(sheet2G).append(sheet2H));
			}
		}
		return map;
	}
	
	/**
	 * 批量从临时表插入到真实表
	 * @author 吴德福
	 * @param map
	 */
	@Transactional
	public void saveBatchDataFromTempToReal(final List<TempAssessTemplate> tempAssessTemplateList){
		o_assessResultDAO.getSession().doWork(new Work() {
			
			public void execute(Connection connection) throws SQLException {
				connection.setAutoCommit(false);
				
				//临时表中的评价结果id集合
				Map<String,Boolean> assessResultMap = new HashMap<String,Boolean>();
				
				for (TempAssessTemplate tempAssessTemplate : tempAssessTemplateList) {
					if("不合格".equals(tempAssessTemplate.getIsQualified())){
						assessResultMap.put(tempAssessTemplate.getAssessResultId(), false);
					}else{
						if(!assessResultMap.containsKey(tempAssessTemplate.getAssessResultId())){
							assessResultMap.put(tempAssessTemplate.getAssessResultId(), true);
						}
					}
				}
				
				//评价结果list
				List<AssessResult> assessResultList = o_assessResultBO.findAssessResultListByCompanyId(UserContext.getUser().getCompanyid());
				for (AssessResult assessResult : assessResultList) {
					if(assessResultMap.containsKey(assessResult.getId())){
						//评价结果在临时表中存在
						assessResult.setHasDefect(assessResultMap.get(assessResult.getId()));
						o_assessResultBO.mergeAssessResult(assessResult);
					}
				}
				
				//评价样本sql--8个字段
				String assessSampleSql = "insert into t_ca_sample(id,plan_id,assessment_point_id,sample_code,sample_name,eresult,ecomment,etype) values(?,?,?,?,?,?,?,?)";
				//评价样本相关附件sql--3个字段
				String assessSampleRelaFileSql = "insert into t_ca_sample_rela_file(id,sample_id,file_id) values(?,?,?)";
				
				PreparedStatement assessSamplePst = connection.prepareStatement(assessSampleSql);
				PreparedStatement assessSampleRelaFilePst = connection.prepareStatement(assessSampleRelaFileSql);
				
				for (TempAssessTemplate tempAssessTemplate : tempAssessTemplateList) {
					/*
					 * 评价样本信息
					 */
					String assessSampleId = Identities.uuid();
					assessSamplePst.setString(1, assessSampleId);
					assessSamplePst.setString(2, tempAssessTemplate.getPlanId()); //评价计划id
					assessSamplePst.setString(3, tempAssessTemplate.getAssessResultId()); //评价结果id
					assessSamplePst.setString(4, tempAssessTemplate.getSampleCode()); //样本编号
					assessSamplePst.setString(5, tempAssessTemplate.getSampleName()); //样本名称
					if(StringUtils.isNotBlank(tempAssessTemplate.getIsQualified())){ //Y--合格;N--不合格
						if("合格".equals(tempAssessTemplate.getIsQualified().trim())){
							assessSamplePst.setString(6, "Y");
						}else if("不合格".equals(tempAssessTemplate.getIsQualified().trim())){
							assessSamplePst.setString(6, "N");
						}
					}
					assessSamplePst.setString(7, tempAssessTemplate.getComment()); //说明
					assessSamplePst.setString(8, "自动"); //类型：自动
					assessSamplePst.addBatch();
					
					/*
					 * 评价样本相关附件
					 */
					if(StringUtils.isNotBlank(tempAssessTemplate.getFileId())){
						assessSampleRelaFilePst.setString(1, Identities.uuid());
						assessSampleRelaFilePst.setString(2, assessSampleId);
						assessSampleRelaFilePst.setString(3, tempAssessTemplate.getFileId());
						assessSampleRelaFilePst.addBatch();
					}
				}
				
				assessSamplePst.executeBatch();
				assessSampleRelaFilePst.executeBatch();
				
				connection.commit();
				connection.setAutoCommit(true);
			}
		});
	}
}
