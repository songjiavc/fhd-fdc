package com.fhd.comm.utils;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import org.springframework.stereotype.Service;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * ftl模板变量内容替换.
 * @author 郑军祥
 * @modify 吴德福
 * @Date 2013-11-07 14:46:34
 */
@Service
public class FreeMarkerXml {

	/**
	 * 根据需要替换的变量map值集合替换ftl模板中的变量.
	 * @param templatePath ftl模板路径
	 * @param templateName ftl模板名称
	 * @param dataMap ftl模板中对应的变量集合
	 * @return byte[] 返回替换变量后的内容
	 * @throws Exception
	 */
	public static byte[] createDoc(String templatePath, String templateName, Map<String,Object> dataMap) throws Exception{
		//设置模本装置方法和路径,FreeMarker支持多种模板装载方法。可以重servlet，classpath，数据库装载
		Configuration configuration = new Configuration();
		configuration.setDefaultEncoding("utf-8");
		
		configuration.setClassForTemplateLoading(FreeMarkerXml.class, templatePath);
		Template t=null;
		try {
			t = configuration.getTemplate(templateName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		Writer out = new BufferedWriter(new OutputStreamWriter(byteStream,"utf-8"));
		
		//模板处理
		t.process(dataMap, out);
		byte[] contents= byteStream.toByteArray();
		
		return contents;
	}
}