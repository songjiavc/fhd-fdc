package com.fhd.fdc.commons.exception;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import org.hibernate.ObjectNotFoundException;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

/**
 * 统一异常处理
 * @author zhengjunxiang
 *
 */
public class FHDExceptionHandler implements HandlerExceptionResolver {

	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
		Map<String, Object> model = new HashMap<String, Object>();  
        model.put("ex", ex);
        
		if (request.getHeader("x-requested-with") != null
				&& request.getHeader("x-requested-with").equalsIgnoreCase(
						"XMLHttpRequest")) { 
			response.setHeader("sessionstatus", "exception");
			response.setStatus(403);
			PrintWriter out = null;
			try {
				out = response.getWriter();
			} catch (IOException e) {
				e.printStackTrace();
			}
			JSONObject json = new JSONObject();
			json.put("title", translateException(ex));	//异常信息友好提示
			json.put("msg", formatStackTrace(ex));		//异常详细信息
			out.print(json.toString());
			out.flush();
			out.close();
			return null;
		}else { // http 超时处理
			return new ModelAndView("commons/errorpage", model);//跳转到错误页面
		}
	}
	
	/**
	 * 将后台异常信息转换成前台用户可以明白的异常信息
	 * 这里对异常进行了的分类，给用户友好的异常信息提示
	 * @return
	 */
	private String translateException(Exception ex){
		String tips = "";
		
		//根据不同错误给出不同的友好异常提示
		if(ex instanceof ObjectNotFoundException) {  
        	tips = "业务数据报错，请联系管理员！";
        }else if(ex instanceof NullPointerException) {  
        	tips = "业务数据报错，请联系管理员！";
        }else if(ex instanceof NumberFormatException) {  
        	tips = "系统程序报错,请联系管理员！";
        }else if(ex instanceof FHDException) {  
        	tips = "业务程序报错,请联系管理员！";
        }else if(ex instanceof DataAccessResourceFailureException) {  
        	tips = "数据库连接超时，请检查网络！";
        }else if(ex instanceof DataAccessException) {  
        	tips = "系统程序报错,请联系管理员！";
        }else {  
        	tips = "系统程序报错,请联系管理员！";
        }  
		return tips;
	}

	/**
	 * 获取异常信息
	 */
	private String formatStackTrace(Exception ex) {
		if(ex==null) return "";
		String rtn = ex.getStackTrace().toString();
		try {
			Writer writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);
			ex.printStackTrace(printWriter);		
			printWriter.flush();
			writer.flush();
			rtn = writer.toString();
			printWriter.close();			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		return rtn;
	}
}
