package com.fhd.ra.business.assess.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fhd.comm.business.formula.FunctionCalculateBO;
import com.fhd.fdc.utils.NumberUtil;

@Service
public class RiskLevelBO {

	@Autowired
	private FunctionCalculateBO o_functionCalculateBO;
	
	/**
	 * 得到风险水平分
	 * @param scoreDicValuelist 维度公式设置
	 * @param riskLevelFormula 公式字符串
	 * @param dimAllList 维度集合
	 * @return String
	 * @author 金鹏祥
	 * */
	public String getRisklevel(List<String> scoreDicValuelist,
			String riskLevelFormula, List<String> dimAllList) {
		double result = -1;

		try {
			if (null != riskLevelFormula) {
				if (scoreDicValuelist.size() != 0) {
					int count = 0;
					for (String scoreDicValue : scoreDicValuelist) {
						try {
							String strs[] = scoreDicValue.split("--");
							String dimId = strs[0];
							String dicValue = strs[1];

							if (riskLevelFormula.indexOf(dimId) != -1) {
								riskLevelFormula = riskLevelFormula.replace(dimId, dicValue);
							}else{
								count++;
							}
						} catch (Exception e) {
							return "--设置公式未设置评估维度";
						}
					}

					if(count == scoreDicValuelist.size()){
						return "--设置公式未设置评估维度";
					}
					
					
					riskLevelFormula = riskLevelFormula.replace("<", "");
					riskLevelFormula = riskLevelFormula.replace(">", "");

					if (Pattern.compile("(?i)[a-z]").matcher(riskLevelFormula)
							.find()) {

						for (String dimId : dimAllList) {
							if (riskLevelFormula.indexOf(dimId) != -1) {
								int start = riskLevelFormula.indexOf(dimId);
								if (start == 0) {
									// 头开始
									riskLevelFormula = riskLevelFormula
											.replace(riskLevelFormula.substring(0,
													dimId.length() + start + 1), "");
								} else {
									// 其他部位
									riskLevelFormula = riskLevelFormula.replace(
											riskLevelFormula.substring(start - 1,
													dimId.length() + start), "");
								}
							}
						}
					}
					
					result = this.getRiskLevelScore(riskLevelFormula);
					result = NumberUtil.meg(result, 2);
				}
			}else{
				return "--设置公式未设置评估维度";
			}
		} catch (Exception e) {
			return "--" + e.getMessage();
		}
		
		
		return String.valueOf(result);
	}
	
	/**得到分值公式
	 *  @param scoreDicValuelist 维度公式设置
	 * @param riskLevelFormula 公式字符串
	 * @param dimAllList 维度集合
	 * @return String
	 * @author 金鹏祥
	 * */
	public String getRiskLevelFormula(ArrayList<String> scoreDicValuelist,
			String riskLevelFormula, ArrayList<String> dimAllList) {
		String resultStr = "";
		try {
			if (null != riskLevelFormula) {
				if (scoreDicValuelist.size() != 0) {
					int count = 0;
					for (String scoreDicValue : scoreDicValuelist) {
						String strs[] = scoreDicValue.split("--");
						String dimId = strs[0];
						String dicValue = strs[1];

						if (riskLevelFormula.indexOf(dimId) != -1) {
							riskLevelFormula = riskLevelFormula.replace(dimId, dicValue);
						}else{
							count++;
						}
					}

					if(count == scoreDicValuelist.size()){
						return resultStr;
					}
					
					
					riskLevelFormula = riskLevelFormula.replace("<", "");
					riskLevelFormula = riskLevelFormula.replace(">", "");

					if (Pattern.compile("(?i)[a-z]").matcher(riskLevelFormula)
							.find()) {

						for (String dimId : dimAllList) {
							if (riskLevelFormula.indexOf(dimId) != -1) {
								int start = riskLevelFormula.indexOf(dimId);
								if (start == 0) {
									// 头开始
									riskLevelFormula = riskLevelFormula
											.replace(riskLevelFormula.substring(0,
													dimId.length() + start + 1), "");
								} else {
									// 其他部位
									riskLevelFormula = riskLevelFormula.replace(
											riskLevelFormula.substring(start - 1,
													dimId.length() + start), "");
								}
							}
						}
					}
					resultStr = riskLevelFormula;
				}
			}
		} catch (Exception e) {
			return resultStr;
		}
		
		
		return resultStr;
	}
	
	/**
	 * 通过字符串公式得风险值
	 * @param riskLevelFormula 字符串公式
	 * @return double
	 * @author 金鹏祥
	 * */
	public double getRiskLevelScore(String riskLevelFormula){
		return Double.parseDouble(o_functionCalculateBO.calculate(o_functionCalculateBO.strCast(riskLevelFormula)));
	}
}
