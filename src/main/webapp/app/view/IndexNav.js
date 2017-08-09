/**
 * 
 * 首页导航面板
 */
Ext.define('FHD.view.IndexNav', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.indexNav',
    layout:'fit',
    onMenuClick : function(url,title,businessId){
		var url = url;
		var text = title;//FHD.titleJs[url];
		var centerPanel = Ext.getCmp('center-panel');
		var tab = centerPanel.getComponent(url);
		
		if(tab){
			centerPanel.setActiveTab(tab);
		}else{
			if(url.startWith('FHD')){
				var p = centerPanel.add(Ext.create(url,{
					id:url,
					businessId:businessId,
					title: text,
					tabTip: text,
					closable:true
				}));
				centerPanel.setActiveTab(p);
			} else if (url.startWith('/pages')){
				var p = centerPanel.add({
					id:url,
					title: text,
					tabTip: text,
					layout:'fit',
					autoWidth:true,
					border:false,
					//iconCls: 'tabs',
					closable:true,
					autoLoad :{ url: __ctxPath+url,scripts: true}
				});
				centerPanel.setActiveTab(p);
			}else{
				var p = centerPanel.add({
					id:url,
					title: menu.text,
					tabTip:menu.text,
					layout:'fit',
					autoWidth:true,
					border:false,
					//iconCls: 'tabs',
					closable:true,
					html : '<iframe width=\'100%\' height=\'100%\' frameborder=\'0\' src=\''+__ctxPath+url+'\'></iframe>'
					//autoLoad :{ url: 'pages/icon.jsp',scripts: true}
					//items:[{xtype:'dictTypelist'}]
				});
				centerPanel.setActiveTab(p);
			}
		}
	},
    
    onMouseMoveFun : function(){
    	$(document).ready(function(){
        	$(".wrap div").hover(function() {
        		$(this).animate({"top": "-80px"}, 300, "swing");
        	},function() {
        		$(this).stop(true,false).animate({"top": "0px"}, 300, "swing");
        	});

        	});
    },
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        
        me.id ='indexNavId'
        
        Ext.apply(me, {
        	border : true,
        	bodyPadding:'20 150 20 150',
        	html:   '<div>' + 
        	
			        '<a href="javascript:Ext.getCmp(\'indexNavId\').onMenuClick(\'FHD.view.myallfolder.MyAllFolderMain\',\'个人工作台\');">' + 
			        '<div class="frame" onMouseMove="Ext.getCmp(\'indexNavId\').onMouseMoveFun();">' + 
			        '<div class="wrap">' + 
			        '<img src=\'images/homepage/super_mono_3d_28.png\' border="0" style="width:78px; height:78px; padding:5px"/>' + 
			        '<div>' + 
			        '<span>' +  
			        '<h1>个人工作台</h1>' +    
			        '<p>个人工作平台包括个人待办工作和个人的风险、指标、流程等相关信息。</p>' +        
			        '</span>' + 
			        '</div>' + 
			        '</div>' + 
			        '</div>' + 
			        '</a>' + 

        			'<a href="javascript:Ext.getCmp(\'indexNavId\').onMenuClick(\'FHD.view.icm.icsystem.FlowMainManage\',\'环境信息\');">' + 
			        '<div class="frame" onMouseMove="Ext.getCmp(\'indexNavId\').onMouseMoveFun();">' + 
			        '<div class="wrap">' + 
			        '<img src=\'images/homepage/super_mono_3d_part2_08.png\' border="0" style="width:78px; height:78px; padding:5px"/>' + 
			        '<div>' + 
			        '<span> ' + 
			        '<h1>环境信息</h1>  ' +  
			        '<p>梳理和完善管理制度的分类归档。多角度可视公司风险，优化风险管理责任和分工的落实。</p>  ' +     
			        '</span>' + 
			        '</div>' + 
			        '</div>' + 
			        '</div>' + 
			        '</a>' + 
        	
					'<a href="javascript:Ext.getCmp(\'indexNavId\').onMenuClick(\'FHD.view.risk.analyse.AssessAnalyseMainPanel\',\'评估结果\');">' + 
			        '<div class="frame" onMouseMove="Ext.getCmp(\'indexNavId\').onMouseMoveFun();">' + 
			        '<div class="wrap">' + 
			        '<img src=\'images/homepage/super_mono_3d_part2_28.png\' border="0" style="width:78px; height:78px; padding:5px"/>' + 
			        '<div>' + 
			        '<span> ' + 
			        '<h1>风险评估</h1>   ' + 
			        '<p>通过持续的风险辨识、分析和评价工作，对新发生或需持续关注的风险行为进行跟踪。</p>  ' + 
			        '</span>' + 
			        '</div>' + 
			        '</div>' + 
			        '</div>' + 
			        '</a>' + 
			        
			        '<a href="javascript:Ext.getCmp(\'indexNavId\').onMenuClick(\'FHD.view.icm.statics.IcmMyDatas\',\'风险控制\');">' + 
			        '<div class="frame" onMouseMove="Ext.getCmp(\'indexNavId\').onMouseMoveFun();">' + 
			        '<div class="wrap">' + 
			        '<img src=\'images/homepage/super_mono_3d_85.png\' border="0" style="width:78px; height:78px; padding:5px"/>' + 
			        '<div>' + 
			        '<span> ' + 
			        '<h1>风险控制</h1>   ' + 
			        '<p>明确重大风险管理策略，以统一公司的风险管理行为，动态监控风险和风险管理的效果。</p>  ' + 
			        '</span>' + 
			        '</div>' + 
			        '</div>' + 
			        '</div>' + 
			        '</a>' + 
			        
        			'<a href="javascript:Ext.getCmp(\'indexNavId\').onMenuClick(\'FHD.view.kpi.homepage.mainhome\',\'我的关注\');">' + 
			        '<div class="frame" onMouseMove="Ext.getCmp(\'indexNavId\').onMouseMoveFun();">' + 
			        '<div class="wrap">' + 
			        '<img src=\'images/homepage/super_mono_3d_part2_67.png\' border="0" style="width:78px; height:78px; padding:5px"/>' + 
			        '<div >' + 
			        '<span> ' + 
			        '<h1>监控预警</h1>   ' + 
			        '<p>前瞻性理念，改变传统监控预警思路，分析过去和现在，突出对未来不确定性的趋势预警。</p>  ' + 
			        '</span>' + 
			        '</div>' + 
			        '</div>' + 
			        '</div>' + 
			        '</a>' + 
			        
			        
			        '<a href="javascript:Ext.getCmp(\'indexNavId\').onMenuClick(\'FHD.view.kpi.kpimonitor.KpiMonitorMain\',\'考核发布\');">' +
			        '<div class="frame" onMouseMove="Ext.getCmp(\'indexNavId\').onMouseMoveFun();">' + 
			        '<div class="wrap">' + 
			        '<img src=\'images/homepage/super_mono_3d_part2_76.png\' border="0" style="width:78px; height:78px; padding:5px"/>' + 
			        '<div>' + 
			        '<span> ' + 
			        '<h1>考核评价</h1>   ' + 
			        '<p>对全面风险管理与内部控制制度的健全性、合理性和有效性进行监督检查与评审。</p>  	' + 
			        '</span>' + 
			        '</div>' + 
			        '</div>' + 
			        '</div>' + 
					'</a>' + 
					
			        '</div>' + 
			        
			        '<div>' + 
					
			        '<a href="javascript:Ext.getCmp(\'indexNavId\').onMenuClick(\'FHD.view.risk.assess.report.ReportMainPanel\',\'报告\');">' + 
			        '<div class="frame" onMouseMove="Ext.getCmp(\'indexNavId\').onMouseMoveFun();">' + 
			        '<div class="wrap">' + 
			        '<img src=\'images/homepage/super_mono_3d_part2_36.png\' border="0" style="width:78px; height:78px; padding:5px"/>' + 
			        '<div>' + 
			        '<span> ' + 
			        '<h1>信息沟通</h1>' + 
			        '<p>支持多级的风险管理报告模板，定期的公司风险评估报告，满足管理层和监管机构的管理要求。</p>  	' + 	
			        '</span>' + 
			        '</div>' + 
			        '</div>' + 
			        '</div>' + 
			        '</a>' + 
			        
			        '<a href="javascript:Ext.getCmp(\'indexNavId\').onMenuClick(\'FHD.view.sys.organization.ManPanel\',\'机构管理\');">' + 
			        '<div class="frame" onMouseMove="Ext.getCmp(\'indexNavId\').onMouseMoveFun();">' + 
			        '<div class="wrap">' + 
			        '<img src=\'images/homepage/super_mono_3d_part2_66.png\' border="0" style="width:78px; height:78px; padding:5px"/>' + 
			        '<div>' + 
			        '<span>' + 
			        '<h1>系统管理</h1>' + 
			        '<p>管理系统基础信息包括：组织机构、用户、访问权限等。</p>' + 
			        '</span>' + 
			        '</div>' + 
			        '</div>' + 
			        '</div>' + 
			        '</a>' + 
			        
			        '<a href="javascript:Ext.getCmp(\'indexNavId\').onMenuClick(\'FHD.view.risk.planconform.PlanConformMain\',\'计划管理\');">' +
			        '<div class="frame" onMouseMove="Ext.getCmp(\'indexNavId\').onMouseMoveFun();">' + 
			        '<div class="wrap">' + 
			        '<img src=\'images/homepage/super_mono_3d_19.png\' border="0" style="width:78px; height:78px; padding:5px"/>' + 
			        '<div>' + 
			        '<span> ' + 
			        '<h1>计划管理</h1>   ' + 
			        '<p>制定风险评估，风险辨识，风险应对，应急预案等计划，并实时监控这些计划。</p>  	' + 
			        '</span>' + 
			        '</div>' + 
			        '</div>' + 
			        '</div>' + 
					'</a>' + 
					
					'<a href="javascript:Ext.getCmp(\'indexNavId\').onMenuClick(\'FHD.view.risk.riskstorage.RiskStorageMainPanel\',\'风险库维护\');">' +
			        '<div class="frame" onMouseMove="Ext.getCmp(\'indexNavId\').onMouseMoveFun();">' + 
			        '<div class="wrap">' + 
			        '<img src=\'images/homepage/super_mono_3d_86.png\' border="0" style="width:78px; height:78px; padding:5px"/>' + 
			        '<div>' + 
			        '<span> ' + 
			        '<h1>风险维护</h1>   ' + 
			        '<p>全面管理和维护风险大类基础数据。</p>  	' + 
			        '</span>' + 
			        '</div>' + 
			        '</div>' + 
			        '</div>' + 
					'</a>' + 
			        
					'<a href="javascript:Ext.getCmp(\'indexNavId\').onMenuClick(\'FHD.view.risk.hisevent.HistoryEventMainPanel\',\'历史事件\');">' +
			        '<div class="frame" onMouseMove="Ext.getCmp(\'indexNavId\').onMouseMoveFun();">' + 
			        '<div class="wrap">' + 
			        '<img src=\'images/homepage/super_mono_3d_07.png\' border="0" style="width:78px; height:78px; padding:5px"/>' + 
			        '<div>' + 
			        '<span> ' + 
			        '<h1>历史事件</h1>   ' + 
			        '<p>管理并维护全部的历史事件信息。</p>  	' + 
			        '</span>' + 
			        '</div>' + 
			        '</div>' + 
			        '</div>' + 
					'</a>' + 
					
					'<a href="javascript:Ext.getCmp(\'indexNavId\').onMenuClick(\'FHD.view.report.risk.OrgTop10RiskPanel\',\'风险版本管理\');">' +
			        '<div class="frame" onMouseMove="Ext.getCmp(\'indexNavId\').onMouseMoveFun();">' + 
			        '<div class="wrap">' + 
			        '<img src=\'images/homepage/super_mono_3d_78.png\' border="0" style="width:78px; height:78px; padding:5px"/>' + 
			        '<div>' + 
			        '<span> ' + 
			        '<h1>风险版本</h1>   ' + 
			        '<p>整合全部风险，创建风险版本，对历史风险进行归类管理和维护。</p>  	' + 
			        '</span>' + 
			        '</div>' + 
			        '</div>' + 
			        '</div>' + 
					'</a>' + 
			        
					'</div>'
        });
        me.callParent(arguments);
        
    }

});