/**
 * ExcelUtil.java
 * com.fhd.fdc.utils.jxl
 *
 * Function : ENDO
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2011-7-13 		吴德福
 *
 * Copyright (c) 2011, Firsthuida All Rights Reserved.
*/

package com.fhd.fdc.utils.excel.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import jxl.Range;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * 实现写excel文件功能. 
 * @author   吴德福
 * @version  
 * @since    Ver 1.1
 * @Date	 2011-8-1		下午15:05:18
 * @see 	 
 */
@SuppressWarnings("rawtypes")
public class ExcelUtil {

	private static Logger logger = Logger.getLogger(ExcelUtil.class); 
	
	private ExcelUtil() {
	}
	
    /**
     * 生成excel文件.
     * @author 吴德福
     * @param list
     * @param fieldTitle
     * @param exportFileName
     * @param sheetName
     * @param position
     * @param request
     * @param response
     * @throws Exception
     * @since  fhd　Ver 1.1
     */
	public static void write(List list, String[] fieldTitle, String exportFileName, String sheetName, int position, 
			HttpServletRequest request,	HttpServletResponse response) throws Exception {
		
		WritableWorkbook wwbook = null;
		
		// 生成文件所在路径
		// 生成文件完整路径
		/***将临时excel文件响应客户***/
		// 设置头信息
		response.setContentType("application/msexcel;charset=utf-8");
        response.setHeader("Content-disposition", "attachment;filename= " + URLEncoder.encode(exportFileName, "utf-8")); 
    	try { 
    		// 创建工作区间
    		wwbook = jxl.Workbook.createWorkbook(response.getOutputStream()); 
    		// 创建sheet
    		WritableSheet wsheet = wwbook.createSheet(sheetName, position);

    		// 表头格式:添加带有字体颜色,带背景颜色 Formatting的对象       
		  	WritableFont wfc = new WritableFont(WritableFont.ARIAL,12,WritableFont.BOLD,false,UnderlineStyle.NO_UNDERLINE,Colour.BLUE);       
    		WritableCellFormat wcfFC = new WritableCellFormat(wfc);
    		wcfFC.setAlignment(Alignment.CENTRE);
    		//wcfFC.setBackground(Colour.BLUE);      
    		
    		// 使用合并表头
    		wsheet.mergeCells(0,0,fieldTitle.length-1,0);
    		Label cellName = new Label(0, 0, sheetName, wcfFC);
    		wsheet.addCell(cellName);
    		
    		// 列头格式:添加带有字体颜色,带背景颜色 Formatting的对象       
    		WritableFont fontcolumnhead=new WritableFont(WritableFont.ARIAL,10,WritableFont.BOLD,false,UnderlineStyle.NO_UNDERLINE,Colour.WHITE);       
    		WritableCellFormat formatcolumnhead=new WritableCellFormat(fontcolumnhead);
    		formatcolumnhead.setAlignment(Alignment.CENTRE);
    		formatcolumnhead.setBackground(Colour.GREEN);
    		
    		WritableCellFormat formatGreen=new WritableCellFormat();
    		formatGreen.setBackground(Colour.GREEN);
    		WritableCellFormat formatYellow=new WritableCellFormat();
    		formatYellow.setBackground(Colour.YELLOW);
            WritableCellFormat formatRed=new WritableCellFormat();
            formatRed.setBackground(Colour.RED);
    		
    		// Label(列号,行号 ,内容 )
    		Label label = null;      
    		for (int i=0; i<fieldTitle.length; i++){
    			//把标题放到第一行
    			label = new Label(i, 1, fieldTitle[i],formatcolumnhead);
    			wsheet.addCell(label);
    		}
    		
    		// 写入数据
    		for (int k = 0; k < list.size(); k++) {
    			Object[] obj = (Object[]) list.get(k);
    			for (int j=0; j<fieldTitle.length; j++){
    				//取消合并表头
    				//Label content = new Label(j, k + 1, (String) obj[j]);
    				//使用合并表头
    				String s = "";
    				if (obj[j] instanceof String) {
    					s = (String) obj[j];
    					
					}
    				Label content  = null;
    				if(s.startsWith("color")){
    				    if("red".equals(s.split("\\|")[1])){
    				        content = new Label(j, k + 2, "", formatRed);
    				    }else if("yellow".equals(s.split("\\|")[1])){
                            content = new Label(j, k + 2, "", formatYellow);
                        }else if("green".equals(s.split("\\|")[1])){
                            content = new Label(j, k + 2, "", formatGreen);
                        }
    				}else{
    				    content = new Label(j, k + 2, s);
    				    
    				}
					wsheet.addCell(content);
    			}
    		}

    		wwbook.write();
    		wwbook.close();
    	}catch (Exception e) { 
    		e.printStackTrace();
    		throw new Exception(e);
    	}finally {
    		response.getOutputStream().close();
    	} 
    }
	/**
	 * 多个数据源生成excel文件.
	 * @param data1
	 * @param data2
	 * @param fieldTitle
	 * @param widths
	 * @param exportFileName
	 * @param sheetName
	 * @param position
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public static void write(List data1,List data2, String[] fieldTitle,int[] widths, String exportFileName, String sheetName, int position, 
			HttpServletRequest request,	HttpServletResponse response) throws Exception{
		
		WritableWorkbook wwbook = null;
		
		response.setContentType("application/msexcel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename= " + URLEncoder.encode(exportFileName, "utf-8")); 
		
    	try { 
    		// 创建工作区间
    		wwbook = jxl.Workbook.createWorkbook(response.getOutputStream()); 
    		// 创建sheet
    		WritableSheet wsheet = wwbook.createSheet(sheetName, position);

    		// 表头格式:添加带有字体颜色,带背景颜色 Formatting的对象       
		  	WritableFont wfc = new WritableFont(WritableFont.ARIAL,12,WritableFont.BOLD,false,UnderlineStyle.NO_UNDERLINE,Colour.BLACK);       
    		WritableCellFormat wcfFC = new WritableCellFormat(wfc);
    		wcfFC.setAlignment(Alignment.CENTRE);
    		wcfFC.setBorder(Border.ALL, BorderLineStyle.THIN); // 线条
    		wcfFC.setBackground(Colour.WHITE);      
    		
    		// 使用合并表头
    		wsheet.mergeCells(0,0,fieldTitle.length-1,0);
    		Label cellName = new Label(0, 0, sheetName, wcfFC);
    		wsheet.addCell(cellName);
    		
    		//居中显示单元格
    		WritableCellFormat columntext=new WritableCellFormat();
    		columntext.setAlignment(Alignment.CENTRE);
    		columntext.setBorder(Border.ALL, BorderLineStyle.THIN); // 线条

    		//居中显示单元格
    		WritableCellFormat columnmain=new WritableCellFormat();
    		columnmain.setAlignment(Alignment.LEFT);
    		columnmain.setBorder(Border.ALL, BorderLineStyle.THIN); // 线条
    		
    		int lineIndex=0;
    		//添加计划导出信息，两列，第一列为标题，第二列为内容，合并单元格至结束
    		for(int i=0;i<data1.size();i++){
    			Object[] obj = (Object[]) data1.get(i);
    			lineIndex=i+2;
    			for (int j=0; j<obj.length; j++){
    				if(j==0){
    					Label title = new Label(j, lineIndex, (String) obj[j],columntext);
    					//wsheet.setColumnView(0,40);
            			wsheet.addCell(title);
    				}else{
    					wsheet.mergeCells(j,lineIndex,fieldTitle.length-1,0);
    		    		Label title =new Label(j, lineIndex,(String)obj[j],columnmain) ;
    		    		wsheet.addCell(title);
    				}
    			}
    		}
    		
    		// 列头格式:添加带有字体颜色,带背景颜色 Formatting的对象       
    		WritableFont fontcolumnhead=new WritableFont(WritableFont.createFont("楷体_GB2312"),10,WritableFont.BOLD );
    		WritableCellFormat formatcolumnhead=new WritableCellFormat(fontcolumnhead);
    		formatcolumnhead.setAlignment(Alignment.CENTRE);
    		formatcolumnhead.setBackground(Colour.GREEN);
    		formatcolumnhead.setBorder(Border.ALL, BorderLineStyle.THIN); // 线条
    		// Label(列号,行号 ,内容 )
    		Label label = null;      
    		for (int i=0; i<fieldTitle.length; i++){
    			//把标题放到第一行
    			label = new Label(i, lineIndex+1, fieldTitle[i],formatcolumnhead);
    			wsheet.setColumnView(i, widths[i]);
    			wsheet.addCell(label);
    		}
    		
    		// 写入数据
    		for (int k = 0; k < data2.size(); k++) {
    			Object[] obj = (Object[]) data2.get(k);
    			
    			for (int j=0; j<fieldTitle.length; j++){
    				//取消合并表头
    				//Label content = new Label(j, k + 1, (String) obj[j]);
    				//使用合并表头
				    Label content = new Label(j, lineIndex+k + 2, String.valueOf(obj[j]),columnmain);
				    wsheet.addCell(content);
    			}
    		}
//    		//表尾
//    		for(int i=0;i<data3.size();i++){
//    			Object[] obj = (Object[]) data2.get(i);
//    			for (int j=0; j<obj.length; j++){
//				    Label content = new Label(j, lineIndex+2, (String) obj[j],columnmain);
//				    wsheet.addCell(content);
//    			}
//    		}

    		wwbook.write();
    		wwbook.close();
    		
    	}catch (Exception e) { 
    		e.printStackTrace();
    		throw new Exception(e);
    	}finally {
    		response.getOutputStream().close();
    	} 
    } 
	/**
	 * 产生一个以当前服务器时间的文件名称.
	 * @author 吴德福
	 * @return String
	 * @since  fhd　Ver 1.1
	 */
    public static String getFileName() { 
    	SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmm"); 
        StringBuilder sb = new StringBuilder(); 
        sb.append(sf.format(System.currentTimeMillis())); 
        sb.append(".xls"); 
        return sb.toString(); 
    }
    /**
     * jxl读取excel中合并单元格的内容.
     * @param excelFileName
     * @throws BiffException
     * @throws IOException
     */
    public static void readExcel(String excelFileName)throws BiffException,IOException{
		//创建输入流
		InputStream stream = new FileInputStream(excelFileName);
		
		//获取Excel文件对象
		Workbook rwb = Workbook.getWorkbook(stream);
		
		//选择第一个工作表
		Sheet sheet = rwb.getSheet(0);
		for(int j=1;j<sheet.getRows();j++){
			for(int k=0;k<sheet.getColumns();k++){
				String str = null;
				str = sheet.getCell(k,j).getContents();
				Range[] ranges = sheet.getMergedCells();
				for(Range r:ranges){
					if(j > r.getTopLeft().getRow() && j <= r.getBottomRight().getRow() && k == r.getTopLeft().getColumn()){
						str = sheet.getCell(r.getTopLeft().getColumn(),r.getTopLeft().getRow()).getContents();
					}
				}
				logger.info("第"+j+"行，第"+k+"列的值："+str+"\t");		
			}
		}
	}
	/**
	 * 测试类
	 */
	public static void main(String[] args){
		try {
			ExcelUtil.readExcel("D:\\abc.xls");//文件存放地址
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}