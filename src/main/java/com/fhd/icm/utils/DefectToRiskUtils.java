package com.fhd.icm.utils;

import java.text.DecimalFormat;

import org.apache.log4j.Logger;

import com.fhd.fdc.utils.Contents;

public class DefectToRiskUtils {
	
	private static Logger logger = Logger.getLogger(DefectToRiskUtils.class);

	/**
	 * 根据缺陷样本发生率与缺陷等级转换成风险的发生可能性与影响程度.
	 * @param sampleOccurRate 0~1之间的double数据
	 * @param defectLevel 数据字典中的缺陷等级:例外，一般缺陷，重要缺陷，重大缺陷
	 * @return double[] 数组的第一个数为风险的发生可能性，第二个数为风险的影响程度
	 */
	public static double[] defectToRisk(double sampleOccurRate, String defectLevel){
		double[] ret = new double[2];
		if(sampleOccurRate < 0.0 || sampleOccurRate > 1.0 || Contents.DEFECT_LEVEL_EXCEPTION.equals(defectLevel)){
			ret[0] = 0.0;
			ret[1] = 0.0;
		}
		/*
		发生频率	缺陷等级 发生可能性 影响程度
		0.0~0.2	一般缺陷	0~1	     0~3
		0.0~0.2	重要缺陷	0~1		 4~5
		0.0~0.2	重大缺陷	0~1	     4~5
		0.2~0.4	一般缺陷	1~2		 0~2
		0.2~0.4	重要缺陷	1~2		 2~5
		0.2~0.4	重大缺陷	1~2	     2~5
		0.4~0.6	一般缺陷	2~3		 0~1
		0.4~0.6	重要缺陷	2~3		 1~4
		0.4~0.6	重大缺陷	2~3		 4~5
		0.6~0.8	一般缺陷	3~4		 0~3
		0.6~0.8	重要缺陷	3~4		 0~3
		0.6~0.8	重大缺陷	3~4		 3~5
		0.8~1.0	一般缺陷	4~5		 0~2
		0.8~1.0	重要缺陷	4~5		 0~2
		0.8~1.0	重大缺陷	4~5		 2~5
		*/
		if(0.0 <= sampleOccurRate && sampleOccurRate < 0.2){
			if(Contents.DEFECT_LEVEL_GENERAL.equals(defectLevel)){
				ret[0] = randomDouble(0, 1);
				ret[1] = randomDouble(0, 3);
			}else if(Contents.DEFECT_LEVEL_IMPORTANT.equals(defectLevel)){
				ret[0] = randomDouble(0, 1);
				ret[1] = randomDouble(4, 5);
			}else if(Contents.DEFECT_LEVEL_GREAT.equals(defectLevel)){
				ret[0] = randomDouble(0, 1);
				ret[1] = randomDouble(4, 5);
			}
		}else if(0.2 <= sampleOccurRate && sampleOccurRate < 0.4){
			if(Contents.DEFECT_LEVEL_GENERAL.equals(defectLevel)){
				ret[0] = randomDouble(1, 2);
				ret[1] = randomDouble(0, 2);
			}else if(Contents.DEFECT_LEVEL_IMPORTANT.equals(defectLevel)){
				ret[0] = randomDouble(1, 2);
				ret[1] = randomDouble(2, 5);
			}else if(Contents.DEFECT_LEVEL_GREAT.equals(defectLevel)){
				ret[0] = randomDouble(1, 2);
				ret[1] = randomDouble(2, 5);
			}
		}else if(0.4 <= sampleOccurRate && sampleOccurRate < 0.6){
			if(Contents.DEFECT_LEVEL_GENERAL.equals(defectLevel)){
				ret[0] = randomDouble(2, 3);
				ret[1] = randomDouble(0, 1);
			}else if(Contents.DEFECT_LEVEL_IMPORTANT.equals(defectLevel)){
				ret[0] = randomDouble(2, 3);
				ret[1] = randomDouble(1, 4);
			}else if(Contents.DEFECT_LEVEL_GREAT.equals(defectLevel)){
				ret[0] = randomDouble(2, 3);
				ret[1] = randomDouble(4, 5);
			}
		}else if(0.6 <= sampleOccurRate && sampleOccurRate < 0.8){
			if(Contents.DEFECT_LEVEL_GENERAL.equals(defectLevel)){
				ret[0] = randomDouble(3, 4);
				ret[1] = randomDouble(0, 3);
			}else if(Contents.DEFECT_LEVEL_IMPORTANT.equals(defectLevel)){
				ret[0] = randomDouble(3, 4);
				ret[1] = randomDouble(0, 3);
			}else if(Contents.DEFECT_LEVEL_GREAT.equals(defectLevel)){
				ret[0] = randomDouble(3, 4);
				ret[1] = randomDouble(3, 5);
			}
		}else if(0.8 <= sampleOccurRate && sampleOccurRate <= 1.0){
			if(Contents.DEFECT_LEVEL_GENERAL.equals(defectLevel)){
				ret[0] = randomDouble(4, 5);
				ret[1] = randomDouble(0, 2);
			}else if(Contents.DEFECT_LEVEL_IMPORTANT.equals(defectLevel)){
				ret[0] = randomDouble(4, 5);
				ret[1] = randomDouble(0, 2);
			}else if(Contents.DEFECT_LEVEL_GREAT.equals(defectLevel)){
				ret[0] = randomDouble(4, 5);
				ret[1] = randomDouble(2, 5);
			}
		}
		
		return ret;
	}
	/**
	 * 随机获取min和max之间的一个随机数.
	 * @param min
	 * @param max
	 * @return double
	 */
	public static double randomDouble(int min, int max){
		DecimalFormat df = new DecimalFormat("#.##");
		String ret = df.format(Math.random()*(max-min)+min);
		return Double.valueOf(ret);
	}
	public static void main(String[] args) {
		double sampleOccurRate = 1.0;
		String defectLevel = "ca_defect_level_2";
		/*
		 * ca_defect_level_0 重大
		 * ca_defect_level_1 重要
		 * ca_defect_level_2 一般
		 * ca_defect_level_3 例外
		 */
		double[] defectToRisk = defectToRisk(sampleOccurRate, defectLevel);
		for (double d : defectToRisk) {
			logger.info(d);
		}
	}
}