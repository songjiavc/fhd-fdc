package com.fhd.ra.business.assess.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.fhd.comm.business.formula.StatisticFunctionCalculateBO;
import com.fhd.entity.assess.riskTidy.KpiAdjustHistory;
import com.fhd.fdc.utils.NumberUtil;

@Service
public class CalculateUtil {
	
	public static void main(String[] args) {
		// String riskLevelFormula =
		// "asd111<*>46e75071-9946-4b37-b407-aff729219a7b<*>707752d7-70bb-48d2-af1d-8270e6e62a1d";
		// ArrayList<String> scoreDicValuelist = new ArrayList<String>();
		// scoreDicValuelist.add("46e75071-9946-4b37-b407-aff729219a7b--2");
		// scoreDicValuelist.add("707752d7-70bb-48d2-af1d-8270e6e62a1d--3");
		// ArrayList<String> dimAllList = new ArrayList<String>();
		// dimAllList.add("46e75071-9946-4b37-b407-aff729219a7b");
		// dimAllList.add("707752d7-70bb-48d2-af1d-8270e6e62a1d");
		// dimAllList.add("asd111");
		// System.out.println(getRisklevel(scoreDicValuelist, riskLevelFormula,
		// dimAllList));

		// String riskLevelFormula =
		// "46e75071-9946-4b37-b407-aff729219a7b<*>707752d7-70bb-48d2-af1d-8270e6e62a1d<*>asd111";
		// ArrayList<String> scoreDicValuelist = new ArrayList<String>();
		// scoreDicValuelist.add("46e75071-9946-4b37-b407-aff729219a7b--2");
		// scoreDicValuelist.add("707752d7-70bb-48d2-af1d-8270e6e62a1d--3");
		// ArrayList<String> dimAllList = new ArrayList<String>();
		// dimAllList.add("46e75071-9946-4b37-b407-aff729219a7b");
		// dimAllList.add("707752d7-70bb-48d2-af1d-8270e6e62a1d");
		// dimAllList.add("asd111");
		// System.out.println(getRisklevel(scoreDicValuelist, riskLevelFormula,
		// dimAllList));

//		String riskLevelFormula = "46e75071-9946-4b37-b407-aff729219a7b<*>asd111<*>707752d7-70bb-48d2-af1d-8270e6e62a1d";
//		ArrayList<String> scoreDicValuelist = new ArrayList<String>();
//		// scoreDicValuelist.add("46e75071-9946-4b37-b407-aff729219a7b--2");
//		// scoreDicValuelist.add("707752d7-70bb-48d2-af1d-8270e6e62a1d--3");
//		ArrayList<String> dimAllList = new ArrayList<String>();
//		dimAllList.add("46e75071-9946-4b37-b407-aff729219a7b");
//		dimAllList.add("707752d7-70bb-48d2-af1d-8270e6e62a1d");
//		dimAllList.add("asd111");
//		System.out.println(getRisklevel(scoreDicValuelist, riskLevelFormula,
//				dimAllList));
		
//		SqrtFunctionBO sqrtFunctionBO = new SqrtFunctionBO();
//		sqrtFunctionBO.c
//		System.out.println(MathEval.eval("3.0^2"));
//		
//		o_functionCalculateBO.calculate(o_functionCalculateBO.strCast(formula));

	}

	/**
	 * 判断是否是数字
	 * @param str 字符串
	 * @return static boolean
	 * @author 金鹏祥
	 * */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	/**
	 * 最大值存储
	 * @param scoreValueList 分值集合
	 * @return static double
	 * @author 金鹏祥
	 * */
	public static double getMaxValue(List<Double> scoreValueList) {
		double scoreValueFinal = 0l;
		double[] scoreValues = new double[scoreValueList.size()];

		for (int j = 0; j < scoreValueList.size(); j++) {
			scoreValues[j] = scoreValueList.get(j);
		}
		scoreValueFinal = StatisticFunctionCalculateBO.max(scoreValues);

		if ("NaN".equalsIgnoreCase(String.valueOf(scoreValueFinal))) {
			scoreValueFinal = 0;
		}
		scoreValueFinal = NumberUtil.meg(scoreValueFinal, 2);
		return scoreValueFinal;
	}

	/**
	 * 加权平均存储
	 * @param scoreValueList 分值集合
	 * @return static double
	 * @author 金鹏祥
	 * */
	public static double getSunMaValue(List<Double> scoreValueList) {
		// 加权平均
		double scoreValueSun = 0l;
		double scoreValueFinal = 0l;

		for (Double scoreValue : scoreValueList) {
			scoreValueSun += scoreValue;
		}

		for (Double scoreValue : scoreValueList) {
			scoreValueFinal += (scoreValue * scoreValue) / scoreValueSun;
		}

		if ("NaN".equalsIgnoreCase(String.valueOf(scoreValueFinal))) {
			scoreValueFinal = 0;
		}
		scoreValueFinal = NumberUtil.meg(scoreValueFinal, 2);
		return scoreValueFinal;
	}

	/**
	 * 得到平均值
	 * @param scoreValueList 分值集合
	 * @return static double
	 * @author 金鹏祥
	 * */
	public static double getMaValue(List<Double> riskValueList) {
		double scoreValueFinal = 0l;
		double[] riskValue = new double[riskValueList.size()];

		for (int i = 0; i < riskValue.length; i++) {
			riskValue[i] = riskValueList.get(i);
		}

		scoreValueFinal = StatisticFunctionCalculateBO.ma(riskValue);

		if ("NaN".equalsIgnoreCase(String.valueOf(scoreValueFinal))) {
			scoreValueFinal = 0;
		}
		scoreValueFinal = NumberUtil.meg(scoreValueFinal, 2);
		return scoreValueFinal;
	}

	/**
	 * 得到加权平均值(指标下的权重,目标计算专用)
	 * @param list 指标集合
	 * @param KpiAdjustHistoryByIdAndAssessPlanIdMapAll 指标记录通过ID,MAP集合
	 * @return static double
	 * @author 金鹏祥
	 * */
	public static double getEweightMaValue(List<Object> list, Map<String, KpiAdjustHistory> kpiAdjustHistoryByIdAndAssessPlanIdMapAll) {
		double eweightSun = 0l;
		double eweightFinal = 0l;
		for (Object object : list) {
			String strs[] = object.toString().split("--");
			// strs[0];//指标
			// strs[1];//权重
			eweightSun += Double.parseDouble(strs[1].toString());
		}

		for (Object object : list) {
			String strs[] = object.toString().split("--");
			// strs[0];//指标
			// strs[1];//权重
			eweightFinal += 
					kpiAdjustHistoryByIdAndAssessPlanIdMapAll.get(strs[0].toString()).getRiskStatus() * (Double.parseDouble(strs[1].toString()) /eweightSun);
		}

		if ("NaN".equalsIgnoreCase(String.valueOf(eweightFinal))) {
			eweightFinal = 0;
		}
		eweightFinal = NumberUtil.meg(eweightFinal, 2);
		return eweightFinal;
	}

	/**
	 * 取最大数
	 * @param arr[] 数组集合
	 * @return static long
	 * @author 金鹏祥
	 * */
	public static long getMax(long arr[]) {
		// 定义初始化变量值
		long max = arr[0];
		// 对数组元素进行遍历
		for (int x = 1; x < arr.length; x++) {
			// 将遍历到的元素和数组中存储的元素进行比较，大的值存到变量中
			if (arr[x] > max) {
				max = arr[x];
			}
		}
		return max;
	}
	
	/**
	 * 取最小数
	 * @param arr[] 数组集合
	 * @return static long
	 * @author 金鹏祥
	 * */
	public static long getMin(long arr[]) {
		// 定义初始化变量值
		long min = arr[0];
		// 对数组元素进行遍历
		for (int x = 1; x < arr.length; x++) {
			// 将遍历到的元素和数组中存储的元素进行比较，大的值存到变量中
			if (arr[x] < min) {
				min = arr[x];
			}
		}
		return min;
	}

	/**
	 * 得到秒级的当前时间
	 * @param time 当前时间
	 * @return static String
	 * @author 金鹏祥
	 */
	public static String getTimestamp(String time) {
		java.text.SimpleDateFormat formater = new SimpleDateFormat(time);
		return formater.format(new Date());
	}
	
	/**将FRI(TIME类型)强转yyyy-MM-dd HH:mm:ss类型
	 * @param adjustTime TIME类型
	 * @return static String
	 * @author 金鹏祥
	 * */
	@SuppressWarnings({ "static-access", "deprecation" })
	public static String getTime(String adjustTime){
		if(adjustTime.toString().indexOf("Fri") != -1 || adjustTime.toString().indexOf("CST") != -1){
			String s = adjustTime.toString();
			Date f = new Date();
			f.parse(s);
			SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String strs = format1.format(f);
			adjustTime = strs;
		}
		
		return adjustTime;
	}
}