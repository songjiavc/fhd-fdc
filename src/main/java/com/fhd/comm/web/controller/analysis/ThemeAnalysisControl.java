package com.fhd.comm.web.controller.analysis;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fhd.comm.business.analysis.ThemeAnalysisBO;
import com.fhd.comm.business.analysis.ThemePanelBO;
import com.fhd.comm.web.form.analysis.ThemeAnalysisForm;
import com.fhd.core.dao.Page;
import com.fhd.core.utils.Identities;
import com.fhd.entity.comm.analysis.ThemeAnalysis;
import com.fhd.entity.comm.analysis.ThemePanel;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.fdc.utils.Contents;
import com.fhd.fdc.utils.UserContext;

/**
 * 主题分析control.
 * @author 吴德福
 * @since 2013-9-4
 */
@Controller
public class ThemeAnalysisControl {

	@Autowired
	private ThemeAnalysisBO o_themeAnalysisBO;
	@Autowired
	private ThemePanelBO o_themePanelBO;
	
	/**
	 * 分页查询主题分析.
	 * @author 吴德福
	 * @param limit
	 * @param start
	 * @param sort
	 * @param query
	 * @param companyId
	 * @return Map<String, Object>
	 */
    @ResponseBody
    @RequestMapping("/themeAnalysis/findThemeAnalysisList.f")
    public Map<String, Object> findThemeAnalysisList(int limit, int start, String sort, String query, String companyId) {
	    Map<String, Object> map = new HashMap<String, Object>();
	    List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
	
	    if(StringUtils.isBlank(companyId)){
			companyId = UserContext.getUser().getCompanyid();
		}
	    
	    Page<ThemeAnalysis> page = new Page<ThemeAnalysis>();
	    page.setPageNo((limit == 0 ? 0 : start / limit) + 1);
	    page.setPageSize(limit);
	    page = o_themeAnalysisBO.findThemeAnalysisByPage(page, sort, query, companyId);
	    List<ThemeAnalysis> themeAnalysisList = page.getResult();
	    if (null != themeAnalysisList && themeAnalysisList.size() > 0) {
	        Map<String, Object> row = null;
	        for (ThemeAnalysis ta : themeAnalysisList) {
	            row = new HashMap<String, Object>();
	            //id
	            row.put("id", ta.getId());
	            //名称
	            row.put("name", ta.getName());
	            //对象所属列
	            row.put("desc", ta.getDesc());
	            //布局类型
	            row.put("layoutType", ta.getLayoutType());
	            //布局属性
	            row.put("attribute", ta.getAttribute());
	            //所属公司
	            row.put("companyId", ta.getCompany().getId());
	            row.put("companyName", ta.getCompany().getOrgname());
	            
	            datas.add(row);
	        }
	        map.put("datas", datas);
	        map.put("totalCount", page.getTotalItems());
	    }else {
	        map.put("datas", new Object[0]);
	        map.put("totalCount", "0");
	    }
	    return map;
	}
    /**
     * 根据form表单保存主题分析与布局.
     * @author 吴德福
     * @param form
     * @param result
     * @return Map<String, Object>
     */
	@ResponseBody
	@RequestMapping("/themeAnalysis/mergeThemeAnalysisByForm.f")
	public Map<String, Object> mergeAssessPlanByForm(ThemeAnalysisForm form, BindingResult result) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		String themeAnalysisId = "";
		
		ThemeAnalysis themeAnalysis = null;
		if(null != form){
			if(StringUtils.isNotBlank(form.getId())){
				themeAnalysis = o_themeAnalysisBO.findThemeAnalysisById(form.getId());
				themeAnalysis.setId(form.getId());
			}else{
				themeAnalysis = new ThemeAnalysis();
				themeAnalysis.setId(Identities.uuid2());
				//删除状态
				themeAnalysis.setDeleteStatus(Contents.DELETE_STATUS_USEFUL);
				//公司
				SysOrganization company = new SysOrganization();
				company.setId(UserContext.getUser().getCompanyid());
				themeAnalysis.setCompany(company);
			}
			if(StringUtils.isNotBlank(form.getName())){
				themeAnalysis.setName(form.getName());
			}
			if(StringUtils.isNotBlank(form.getDesc())){
				themeAnalysis.setDesc(form.getDesc());
			}
			if(StringUtils.isNotBlank(form.getLayoutType())){
				themeAnalysis.setLayoutType(form.getLayoutType());
				themeAnalysis.setAttribute(form.getAttribute());
				if(StringUtils.isNotBlank(form.getId())){
					o_themeAnalysisBO.mergeThemeAnalysis(themeAnalysis);
				}else{
					o_themeAnalysisBO.saveThemeAnalysis(themeAnalysis);
				}
				//保存布局信息 -- 布局中的宽度比、高度比或绝对高度.
				if("layout0".equals(form.getLayoutType())){
					//布局A：11
					ThemePanel themePanel = new ThemePanel();
					themePanel.setId(Identities.uuid2());
					
					//相对
					if("relative".equals(form.getAttribute())){
						//宽度比
						themePanel.setWidthRatio("1");
						//高度比
						themePanel.setHeightRatio("1");
						themePanel.setPosition("A");
					}else if("absolute".equals(form.getAttribute())){
						//绝对
						themePanel.setWidth(form.getOneWidth());
						themePanel.setHeight(form.getOneHeight());
						themePanel.setPosition("A");
					}
					
					themePanel.setThemeAnalysis(themeAnalysis);
					o_themePanelBO.mergeThemePanel(themePanel);
				}else if("layout1".equals(form.getLayoutType())){
					//布局A：11、21、22
					ThemePanel themePanel = null;
					for (int i = 0; i < 3; i++) {
						themePanel = new ThemePanel();
						themePanel.setId(Identities.uuid2());
						
						//相对
						if("relative".equals(form.getAttribute())){
							//第二行宽度比
							String twoWidthRatio = form.getTwoWidthRatio();
							String[] twoWidthRatioArray = twoWidthRatio.split(":");
							//高度比
							String heightRatio = form.getHeightRatio();
							String[] heightRatioArray = heightRatio.split(":");
							if(i==0){
								themePanel.setWidthRatio("1");
								themePanel.setHeightRatio(heightRatioArray[0]);
								themePanel.setPosition("A");
							}else if(i==1){
								themePanel.setWidthRatio(twoWidthRatioArray[0]);
								themePanel.setHeightRatio(heightRatioArray[1]);
								themePanel.setPosition("B");
							}else if(i==2){
								themePanel.setWidthRatio(twoWidthRatioArray[1]);
								themePanel.setHeightRatio(heightRatioArray[1]);
								themePanel.setPosition("C");
							}
						}else if("absolute".equals(form.getAttribute())){
							//绝对
							if(i==0){
								themePanel.setWidth(form.getOneWidth());
								themePanel.setHeight(form.getOneHeight());
								themePanel.setPosition("A");
							}else if(i==1){
								themePanel.setWidth(form.getTwoWidth());
								themePanel.setHeight(form.getTwoHeight());
								themePanel.setPosition("B");
							}else if(i==2){
								themePanel.setWidth(form.getThreeWidth());
								themePanel.setHeight(form.getThreeHeight());
								themePanel.setPosition("C");
							}
						}
						
						themePanel.setThemeAnalysis(themeAnalysis);
						o_themePanelBO.mergeThemePanel(themePanel);
					}
				}else if("layout2".equals(form.getLayoutType())){
					//布局B：11、12、21、22
					ThemePanel themePanel = null;
					for (int i = 0; i < 4; i++) {
						themePanel = new ThemePanel();
						themePanel.setId(Identities.uuid2());
						
						//相对
						if("relative".equals(form.getAttribute())){
							//第一行宽度比
							String oneWidthRatio = form.getOneWidthRatio();
							String[] oneWidthRatioArray = oneWidthRatio.split(":");
							//第二行宽度比
							String twoWidthRatio = form.getTwoWidthRatio();
							String[] twoWidthRatioArray = twoWidthRatio.split(":");
							//高度比
							String heightRatio = form.getHeightRatio();
							String[] heightRatioArray = heightRatio.split(":");
							if(i==0){
								themePanel.setWidthRatio(oneWidthRatioArray[0]);
								themePanel.setHeightRatio(heightRatioArray[0]);
								themePanel.setPosition("A");
							}else if(i==1){
								themePanel.setWidthRatio(oneWidthRatioArray[1]);
								themePanel.setHeightRatio(heightRatioArray[0]);
								themePanel.setPosition("B");
							}else if(i==2){
								themePanel.setWidthRatio(twoWidthRatioArray[0]);
								themePanel.setHeightRatio(heightRatioArray[1]);
								themePanel.setPosition("C");
							}else if(i==3){
								themePanel.setWidthRatio(twoWidthRatioArray[1]);
								themePanel.setHeightRatio(heightRatioArray[1]);
								themePanel.setPosition("D");
							}
						}else if("absolute".equals(form.getAttribute())){
							//绝对
							if(i==0){
								themePanel.setWidth(form.getOneWidth());
								themePanel.setHeight(form.getOneHeight());
								themePanel.setPosition("A");
							}else if(i==1){
								themePanel.setWidth(form.getTwoWidth());
								themePanel.setHeight(form.getTwoHeight());
								themePanel.setPosition("B");
							}else if(i==2){
								themePanel.setWidth(form.getThreeWidth());
								themePanel.setHeight(form.getThreeHeight());
								themePanel.setPosition("C");
							}else if(i==3){
								themePanel.setWidth(form.getFourWidth());
								themePanel.setHeight(form.getFourHeight());
								themePanel.setPosition("D");
							}
						}
						
						themePanel.setThemeAnalysis(themeAnalysis);
						o_themePanelBO.mergeThemePanel(themePanel);
					}
				}
			}
			themeAnalysisId = themeAnalysis.getId();
		}
		map.put("success", true);
		map.put("themeAnalysisId", themeAnalysisId);
		return map;
	}
	/**
	 * 根据id集合批量删除主题分析.
	 * @author 吴德福
	 * @param themeAnalysisIds 对象id集合
	 * @param response
	 * @throws IOException 
	 */
    @RequestMapping("/themeAnalysis/removeThemeAnalysisByIds.f")
    public void removeThemeAnalysisByIds(String themeAnalysisIds, HttpServletResponse response) throws IOException{
    	PrintWriter out = response.getWriter();
    	try {
    		if(StringUtils.isNotBlank(themeAnalysisIds)){
    			o_themeAnalysisBO.removeThemeAnalysisByIds(themeAnalysisIds);
    		}
			out.print("true");
    	} catch (Exception e) {
    		out.print("false");
			e.printStackTrace();
		} finally{
			if(null != out){
				out.close();
			}
		}
    }
    /**
     * 验证主题分析名称是否重复.
     * @param themeAnalysisId
     * @param name
     * @return Map<String, Object>
     */
    @ResponseBody
	@RequestMapping("/themeAnalysis/validateThemeAnalysisForm.f")
	public Map<String, Object> validateThemeAnalysisForm(String themeAnalysisId, String name) {

    	boolean flag = false;
		// 修改数据时验证
		if (StringUtils.isNotEmpty(themeAnalysisId)) {
			List<ThemeAnalysis> themeAnalysisList = o_themeAnalysisBO.findThemeAnalysisBySome(name);
			if (null != themeAnalysisList && themeAnalysisList.size() > 0) {
				if(themeAnalysisList.size() == 1){
					if (!themeAnalysisList.get(0).getId().equals(themeAnalysisId)) {
						flag = true;
					}
				}else{
					flag = true;
				}
			}
		} else {// 添加数据时验证
			List<ThemeAnalysis> themeAnalysisList = o_themeAnalysisBO.findThemeAnalysisBySome(name);
			if (null != themeAnalysisList && themeAnalysisList.size() > 0) {
				flag = true;
			}
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("success", flag);
		return result;
	}
    /**
     * 加载布局panel.
     * @param themeAnalysisId
     * @return Map<String, Object>
     */
    @ResponseBody
	@RequestMapping("/themeAnalysis/findThemeAnalysisById.f")
	public Map<String, Object> findThemeAnalysisById(String themeAnalysisId) {
    	Map<String, Object> map=new HashMap<String,Object>();
    	if(StringUtils.isNotBlank(themeAnalysisId)){
    		ThemeAnalysis themeAnalysis = o_themeAnalysisBO.findThemeAnalysisById(themeAnalysisId);
    		Map<String, Object> formMap = new HashMap<String, Object>();
    		//id
    		formMap.put("id", themeAnalysis.getId());
    		//名称
    		formMap.put("name", themeAnalysis.getName());
    		//描述
    		formMap.put("desc", themeAnalysis.getDesc());
    		//布局类型
    		formMap.put("layoutType", themeAnalysis.getLayoutType());
    		//布局属性
    		formMap.put("attribute", themeAnalysis.getAttribute());
    		//布局属性值
    		if("relative".equals(themeAnalysis.getAttribute())){
    			if("layout0".equals(themeAnalysis.getLayoutType())){
    				//布局0
    				String widthRatio = "";
    				String heightRatio = "";
    				String onePanelId = "";
    				String onePanelName = "A";
    				Set<ThemePanel> themePanels = themeAnalysis.getThemePanels();
    				int i=0;
        			for (ThemePanel themePanel : themePanels) {
        				if(i==0){
	    					widthRatio = "1:1";
	    					heightRatio = "1:1";
	    					onePanelId = themePanel.getId();
	    					onePanelName = themePanel.getPanelName();
        				}
        				i++;
    				}
        			//宽度比
    				formMap.put("widthRatio", widthRatio);
    				//高度比
    				formMap.put("heightRatio", heightRatio);
    				//第一个面板id
    				formMap.put("onePanelId", onePanelId);
    				formMap.put("onePanelName", onePanelName);
    			}else if("layout1".equals(themeAnalysis.getLayoutType())){
    				//布局1
    				String twoWidthRatio = "";
    				String heightRatio = "";
    				String onePanelId = "";
    				String onePanelName = "A";
    				String twoPanelId = "";
    				String twoPanelName = "B";
    				String threePanelId = "";
    				String threePanelName = "C";
    				Set<ThemePanel> themePanels = themeAnalysis.getThemePanels();
    				int i=0;
        			for (ThemePanel themePanel : themePanels) {
        				if(i==0){
        					heightRatio += themePanel.getHeightRatio();
        					
        					onePanelId = themePanel.getId();
        					onePanelName = themePanel.getPanelName();
        				}else if(i==1){
        					heightRatio += ":";
        					heightRatio += themePanel.getHeightRatio();
        					
        					twoWidthRatio += themePanel.getWidthRatio();
        					
        					twoPanelId = themePanel.getId();
        					twoPanelName = themePanel.getPanelName();
        				}else if(i==2){
        					twoWidthRatio += ":";
        					twoWidthRatio += themePanel.getWidthRatio();
        					
        					threePanelId = themePanel.getId();
        					threePanelName = themePanel.getPanelName();
        				}
        				i++;
    				}
        			//第二行宽度比
    				formMap.put("twoWidthRatio", twoWidthRatio);
    				//高度比
    				formMap.put("heightRatio", heightRatio);
    				//第一个面板id
    				formMap.put("onePanelId", onePanelId);
    				formMap.put("onePanelName", onePanelName);
    				//第二个面板id
    				formMap.put("twoPanelId", twoPanelId);
    				formMap.put("twoPanelName", twoPanelName);
    				//第三个面板id
    				formMap.put("threePanelId", threePanelId);
    				formMap.put("threePanelName", threePanelName);
    			}else if("layout2".equals(themeAnalysis.getLayoutType())){
    				//布局2
    				String oneWidthRatio = "";
    				String twoWidthRatio = "";
    				String heightRatio = "";
    				String onePanelId = "";
    				String onePanelName = "";
    				String twoPanelId = "";
    				String twoPanelName = "";
    				String threePanelId = "";
    				String threePanelName = "";
    				String fourPanelId = "";
    				String fourPanelName = "";
    				Set<ThemePanel> themePanels = themeAnalysis.getThemePanels();
    				int i=0;
        			for (ThemePanel themePanel : themePanels) {
        				if(i==0){
        					heightRatio += themePanel.getHeightRatio();
        					
        					oneWidthRatio += themePanel.getWidthRatio();
        					
        					onePanelId = themePanel.getId();
        					onePanelName = themePanel.getPanelName();
        				}else if(i==1){
        					oneWidthRatio += ":";
        					oneWidthRatio += themePanel.getWidthRatio();
        					
        					twoPanelId = themePanel.getId();
        					twoPanelName = themePanel.getPanelName();
        				}else if(i==2){
        					heightRatio += ":";
        					heightRatio += themePanel.getHeightRatio();
        					
        					twoWidthRatio += themePanel.getWidthRatio();
        					
        					threePanelId = themePanel.getId();
        					threePanelName = themePanel.getPanelName();
        				}else if(i==3){
        					twoWidthRatio += ":";
        					twoWidthRatio += themePanel.getWidthRatio();
        					
        					fourPanelId = themePanel.getId();
        					fourPanelName = themePanel.getPanelName();
        				}
        				i++;
    				}
        			//第一行宽度比
        			formMap.put("oneWidthRatio", oneWidthRatio);
        			//第二行宽度比
    				formMap.put("twoWidthRatio", twoWidthRatio);
    				//高度比
    				formMap.put("heightRatio", heightRatio);
    				//第一个面板id
    				formMap.put("onePanelId", onePanelId);
    				formMap.put("onePanelName", onePanelName);
    				//第二个面板id
    				formMap.put("twoPanelId", twoPanelId);
    				formMap.put("twoPanelName", twoPanelName);
    				//第三个面板id
    				formMap.put("threePanelId", threePanelId);
    				formMap.put("threePanelName", threePanelName);
    				//第四个面板id
    				formMap.put("fourPanelId", fourPanelId);
    				formMap.put("fourPanelName", fourPanelName);
    			}
    		}else if("absolute".equals(themeAnalysis.getAttribute())){
    			if("layout0".equals(themeAnalysis.getLayoutType())){
    				//布局0
    				String oneWidth = "";
    				String oneHeight = "";
    				String onePanelId = "";
    				String onePanelName = "";
    				Set<ThemePanel> themePanels = themeAnalysis.getThemePanels();
    				int i=0;
        			for (ThemePanel themePanel : themePanels) {
        				if(i==0){
        					oneWidth = themePanel.getWidth();
        					oneHeight = themePanel.getHeight();
        					onePanelId = themePanel.getId();
        					onePanelName = themePanel.getPanelName();
        				}
        				i++;
    				}
        			//第一行宽度
        			formMap.put("oneWidth", oneWidth);
        			//第一行高度
    				formMap.put("oneHeight", oneHeight);
    				//第一个面板id
    				formMap.put("onePanelId", onePanelId);
    				formMap.put("onePanelName", onePanelName);
    			}else if("layout1".equals(themeAnalysis.getLayoutType())){
    				//布局1
    				String oneWidth = "";
    				String twoWidth = "";
    				String threeWidth = "";
    				String oneHeight = "";
    				String twoHeight = "";
    				String threeHeight = "";
    				String onePanelId = "";
    				String onePanelName = "";
    				String twoPanelId = "";
    				String twoPanelName = "";
    				String threePanelId = "";
    				String threePanelName = "";
    				Set<ThemePanel> themePanels = themeAnalysis.getThemePanels();
    				int i=0;
        			for (ThemePanel themePanel : themePanels) {
        				if(i==0){
        					oneWidth = themePanel.getWidth();
        					oneHeight = themePanel.getHeight();
        					onePanelId = themePanel.getId();
        					onePanelName = themePanel.getPanelName();
        				}else if(i==1){
        					twoWidth = themePanel.getWidth();
        					twoHeight = themePanel.getHeight();
        					twoPanelId = themePanel.getId();
        					twoPanelName = themePanel.getPanelName();
        				}else if(i==2){
        					threeWidth = themePanel.getWidth();
        					threeHeight = themePanel.getHeight();
        					threePanelId = themePanel.getId();
        					threePanelName = themePanel.getPanelName();
        				}
        				i++;
    				}
        			//第一行宽度
        			formMap.put("oneWidth", oneWidth);
        			//第一行高度
    				formMap.put("oneHeight", oneHeight);
    				//第二行宽度
        			formMap.put("twoWidth", twoWidth);
        			//第二行高度
    				formMap.put("twoHeight", twoHeight);
    				//第三行宽度
        			formMap.put("threeWidth", threeWidth);
        			//第三行高度
    				formMap.put("threeHeight", threeHeight);
    				//第一个面板id
    				formMap.put("onePanelId", onePanelId);
    				formMap.put("onePanelName", onePanelName);
    				//第二个面板id
    				formMap.put("twoPanelId", twoPanelId);
    				formMap.put("twoPanelName", twoPanelName);
    				//第三个面板id
    				formMap.put("threePanelId", threePanelId);
    				formMap.put("threePanelName", threePanelName);
    			}else if("layout2".equals(themeAnalysis.getLayoutType())){
    				//布局2
    				String oneWidth = "";
    				String twoWidth = "";
    				String threeWidth = "";
    				String fourWidth = "";
    				String oneHeight = "";
    				String twoHeight = "";
    				String threeHeight = "";
    				String fourHeight = "";
    				String onePanelId = "";
    				String onePanelName = "";
    				String twoPanelId = "";
    				String twoPanelName = "";
    				String threePanelId = "";
    				String threePanelName = "";
    				String fourPanelId = "";
    				String fourPanelName = "";
    				Set<ThemePanel> themePanels = themeAnalysis.getThemePanels();
    				int i=0;
        			for (ThemePanel themePanel : themePanels) {
        				if(i==0){
        					oneWidth = themePanel.getWidth();
        					oneHeight = themePanel.getHeight();
        					onePanelId = themePanel.getId();
        					onePanelName = themePanel.getPanelName();
        				}else if(i==1){
        					twoWidth = themePanel.getWidth();
        					twoHeight = themePanel.getHeight();
        					twoPanelId = themePanel.getId();
        					twoPanelName = themePanel.getPanelName();
        				}else if(i==2){
        					threeWidth = themePanel.getWidth();
        					threeHeight = themePanel.getHeight();
        					threePanelId = themePanel.getId();
        					threePanelName = themePanel.getPanelName();
        				}else if(i==3){
        					fourWidth = themePanel.getWidth();
        					fourHeight = themePanel.getHeight();
        					fourPanelId = themePanel.getId();
        					fourPanelName = themePanel.getPanelName();
        				}
        				i++;
    				}
        			//第一行宽度
        			formMap.put("oneWidth", oneWidth);
        			//第一行高度
    				formMap.put("oneHeight", oneHeight);
    				//第二行宽度
        			formMap.put("twoWidth", twoWidth);
        			//第二行高度
    				formMap.put("twoHeight", twoHeight);
    				//第三行宽度
        			formMap.put("threeWidth", threeWidth);
        			//第三行高度
    				formMap.put("threeHeight", threeHeight);
    				//第四行宽度
        			formMap.put("fourWidth", fourWidth);
        			//第四行高度
    				formMap.put("fourHeight", fourHeight);
    				//第一个面板id
    				formMap.put("onePanelId", onePanelId);
    				formMap.put("onePanelName", onePanelName);
    				//第二个面板id
    				formMap.put("twoPanelId", twoPanelId);
    				formMap.put("twoPanelName", twoPanelName);
    				//第三个面板id
    				formMap.put("threePanelId", threePanelId);
    				formMap.put("threePanelName", threePanelName);
    				//第四个面板id
    				formMap.put("fourPanelId", fourPanelId);
    				formMap.put("fourPanelName", fourPanelName);
    			}
    		}
    		
    		map.put("data", formMap);
    		map.put("success", true);
    	}else{
    		map.put("success", false);
    	}
		return map;
	}
}