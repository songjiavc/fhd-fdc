package com.fhd.ra.business.risk.report;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import org.springframework.stereotype.Service;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Service
public class DocumentHandler {
	private Configuration configuration = null;

	public DocumentHandler() {
		configuration = new Configuration();
		configuration.setDefaultEncoding("utf-8");
	}

	@SuppressWarnings("restriction")
	public byte[] createDoc(String templatePath,String templateName,Map<String,Object> dataMap) throws Exception{
		//设置模本装置方法和路径,FreeMarker支持多种模板装载方法。可以重servlet，classpath，数据库装载，
		//这里我们的模板是放在com.fhd.ra.risk.report包下面
		configuration.setClassForTemplateLoading(this.getClass(), templatePath);
		Template t=null;
		try {
			//test.ftl为要装载的模板
			t = configuration.getTemplate(templateName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		//输出文档路径及名称
//		String docPath = "D:/temp/riskReport.doc";
//		//"E:\\FirstHuida\\workspace\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\fhd-fdc_4_5\\riskReport.doc";
//		File outFile = new File(docPath);
//		OutputStream outStream = new FileOutputStream(outFile);
		
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		Writer out = new BufferedWriter(new OutputStreamWriter(byteStream,"utf-8"));
		
		//模板处理
		t.process(dataMap, out);
		byte[] contents= byteStream.toByteArray();
		
//		//将流写入文件
//		byteStream.writeTo(outStream);
//		outStream.flush();
//		outStream.close();
		
		return contents;
	}
}
