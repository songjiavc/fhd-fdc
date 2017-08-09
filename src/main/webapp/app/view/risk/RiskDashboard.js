Ext.define('FHD.view.risk.RiskDashboard', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.riskdashboard',
    
    bodyPadding: '3 3 3 3',
    border : false,
    orgId:__user.companyId,
    
    initComponent: function() {
        var me = this;
        
        me.finishRateXml = '<chart lowerLimit="0" upperLimit="100" gaugeScaleAngle="360"\n\
        	minorTMNumber="0" majorTMNumber="0" gaugeOuterRadius="45" gaugeInnerRadius="35"\n\
        	placeValuesInside="0"  pivotRadius="1" showGaugeBorder="0"  majorTMColor="ffffff"\n\
        	showBorder="0" chartLeftMargin="10" chartTopMargin="30" chartBottomMargin="30"\n\
        	placeValuesInside="1" displayValueDistance="999" basefontColor="000000"\n\
        	toolTipBgColor="FFFFFF"  majorTMHeight="10" showShadow="0" numberSuffix="%"\n\
        	bgColor="FFFFFF">\n\
        	<colorRange>\n\
        		<color minValue="0" maxValue="78.52" code="339900"/>\n\
        		<color minValue="78.52" maxValue="100" code="FDEBE3"/>\n\
        	</colorRange>\n\
        	<dials>\n\
        		<dial value="78.52" showValue="0" valueY="45" baseWidth="4" radius="20" topWidth="1"\n\
        			animation="1" borderAlpha="0" />\n\
        	</dials>\n\
        	</chart>';
        
        me.annualRiskAssessmentChart = Ext.create('FHD.ux.FusionChartPanel',{
    		id:'annual_risk_asessment_finish_rate',
    		chartType:'AngularGauge',
			flex:4,
			width:120,
			height:150,
			border:false,
			//title:'计划完成率',
    		xmlData:me.finishRateXml
		});
        
        me.tbspacer = {
            xtype:'tbspacer',
            flex:1
        };
        
        me.widthSpace = {
            xtype:'tbspacer',
            width:3
        };
        
        me.heightSpace = {
    		xtype:'tbspacer',
    		height:10
        };
        
        me.finishRate=88;
        if(me.finishRate<50){
        	me.finishRateTitle = '计划完成率&nbsp;&nbsp;<font color="FF6600" size="5px">'+me.finishRate+'%</font>';
        }else if(me.finishRate>=50 && me.finishRate<75){
        	me.finishRateTitle = '计划完成率&nbsp;&nbsp;<font color="CC9900" size="5px">'+me.finishRate+'%</font>';
        }else{
        	me.finishRateTitle = '计划完成率&nbsp;&nbsp;<font color="339900" size="5px">'+me.finishRate+'%</font>';
        }
        /*
        me.annualRiskAssessmentChart=Ext.create('FHD.view.risk.chart.AnnualRiskAssessmentChart',{
        	toolRegion:'west',
        	flex:4,
        	extraParams:{
        		orgId:me.orgId
        	}
        });
        */
        me.riskClassChart=Ext.create('FHD.view.risk.chart.RiskClassCountChart',{
        	toolRegion:'west',
        	flex:4,
        	extraParams:{
        		orgId:me.orgId
        	}
        });
        
        me.topOrgRiskChart=Ext.create('FHD.view.risk.chart.TopOrgRiskChart',{
        	toolRegion:'north',
        	flex:4,
        	extraParams:{
        		orgId:me.orgId
        	}
        });
        me.topKpiRiskChart=Ext.create('FHD.view.risk.chart.TopKpiRiskChart',{
        	toolRegion:'north',
        	flex:4,
        	extraParams:{
        		orgId:me.orgId
        	}
        });
        me.topProcessRiskChart=Ext.create('FHD.view.risk.chart.TopProcessRiskChart',{
        	toolRegion:'north',
        	flex:4,
        	extraParams:{
        		orgId:me.orgId
        	}
        });
        
        me.newRiskList=Ext.create('FHD.view.risk.chart.NewRiskList',{
        	flex:4,
        	extraParams:{
        		orgId:me.orgId
        	}
        });
        me.riskDistributionXml='<chart palette="3" caption="风险图谱分布" yAxisName="" xAxisName="" showLegend="0" showNames="1" xAxisMaxValue="9" xAxisMinValue="0" submitDataAsXML="0" showFormBtn="0">\n\
        	<categories>\n\
	        	<category name="1" x="1" showVerticalLine="1"/>\n\
	        	<category name="2" x="2" showVerticalLine="1"/>\n\
	        	<category name="3" x="3" showVerticalLine="1"/>\n\
	        	<category name="4" x="4" showVerticalLine="1"/>\n\
	        	<category name="5" x="5" showVerticalLine="1"/>\n\
	        	<category name="6" x="6" showVerticalLine="1"/>\n\
	        	<category name="7" x="7" showVerticalLine="1"/>\n\
	        	<category name="8" x="8" showVerticalLine="1"/>\n\
	        	<category name="9" x="9" showVerticalLine="1"/>\n\
        	</categories>\n\
        	<dataSet id="DS1" seriesName="风险" color="0372AB" plotBorderThickness="0" showPlotBorder="1" anchorSides="3">\n\
	        	<set id="INVEQ324_1" x="4.2" y="3.2" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_2" x="2.8" y="3.6" tooltext="发生可能性：2.8,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_3" x="6.2" y="4.8" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_4" x="1" y="4" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_5" x="1.2" y="3.4" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_6" x="4.4" y="4.4" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_7" x="8.5" y="3" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_8" x="6.9" y="1.8" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_9" x="8.9" y="4.1" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_10" x="0.9" y="3" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_11" x="8.8" y="4.8" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_12" x="3.2" y="2.4" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_13" x="1.1" y="3.6" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_14" x="4.8" y="2.8" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_15" x="5.8" y="7.4" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_16" x="3.5" y="2.5" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_17" x="2.9" y="3.1" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_18" x="0.8" y="1.4" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_19" x="8.9" y="4.7" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_20" x="0.9" y="2" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_21" x="5.3" y="4.4" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_22" x="1.4" y="2.4" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_23" x="8.1" y="3.7" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_24" x="7.8" y="4.8" tooltext="发生可能性：4.2,影响程度：3.2"/>\n\
	        	<set id="INVEQ324_25" x="8.8" y="4.4" tooltext="发生可能性：4.2,影响程度：3.24%"/>\n\
        	</dataSet>\n\
        	<vTrendLines>\n\
	        	<line startValue="0" endValue="3" displayValue="安全" isTrendZone="1" color="006600" />\n\
	        	<line startValue="3" endValue="6" displayValue="关注" isTrendZone="1" color="cc9900" />\n\
	        	<line startValue="6" endValue="9" displayValue="预警" isTrendZone="1" color="990000" />\n\
        	</vTrendLines>\n\
        	<styles>\n\
        		<definition>\n\
        			<style name="myCaptionFont" type="font" font="Arial" size="14" bold="1" underline="1"/>\n\
        		</definition>\n\
	        	<application>\n\
	        		<apply toObject="Caption" styles="myCaptionFont"/>\n\
	        	</application>\n\
        	</styles>\n\
        	</chart>';
        me.riskDistributionChart = Ext.create('FHD.ux.FusionChartPanel',{
    		chartType:'SelectScatter',
			flex:4,
			border:false,
    		xmlData:me.riskDistributionXml
		});
        
        Ext.applyIf(me, {
        	layout: {
				type: 'hbox',
	        	align:'stretch'
	        },
        	items:[
        		Ext.create('Ext.container.Container',{
		        	layout: {
						type: 'vbox',
			        	align:'stretch'
			        },
			        border:false,
		            flex:33,
		        	items:[
		        	    Ext.create('Ext.container.Container',{
				        	layout: {
								type: 'hbox',
					        	align:'stretch'
					        },
					        border:false,
				            flex:4,
				        	items:[
				        	    //me.annualRiskAssessmentChart,
				        	    me.tbspacer,
				        	    me.encapsulateChart(me.annualRiskAssessmentChart,me.finishRateTitle,'反映当前年度风险评估的完成情况'),
				        	    me.tbspacer,
				        	    me.riskClassChart
				        	]
						}),
						Ext.create('Ext.container.Container',{
				        	layout: {
								type: 'hbox',
					        	align:'stretch'
					        },
					        border:false,
				            flex:4,
				        	items:[
					        	me.topOrgRiskChart,
					        	me.topKpiRiskChart,
					        	me.topProcessRiskChart
				        	]
						}),
						Ext.create('Ext.container.Container',{
				        	layout: {
								type: 'hbox',
					        	align:'stretch'
					        },
					        border:false,
				            flex:4,
				        	items:[
				        	    me.newRiskList,
				        	    me.riskDistributionChart
				        	]
						})
		        	]
				}),
				me.widthSpace,
				Ext.create('Ext.container.Container',{
		        	layout: {
						type: 'vbox',
			        	align:'stretch'
			        },
			        border:false,
		            flex:1,
		        	items:[
		        	    me.heightSpace,
			        	{
				            xtype: 'button',
				            icon: __ctxPath + '/images/icons/icon_diagram_32.gif',
				            scale : 'large',
				            tooltip: '新增风险',
				            listeners:{
				      			click:function(split,event){
				      				alert('新增风险');
				      			}
				      		}
				        },
				        me.heightSpace,
				        {
				            xtype: 'button',
				            icon: __ctxPath + '/images/icons/icon_project_32.gif',
				            scale : 'large',
				            tooltip: '新增历史事件',
				            listeners:{
				      			click:function(split,event){
				      				alert('新增历史事件');
				      			}
				      		}
				        },
				        me.heightSpace,
				        {
				            xtype: 'button',
				            icon: __ctxPath + '/images/icons/icon_metrictype_32.gif',
				            scale : 'large',
				            tooltip: '风险树维护',
				            listeners:{
				      			click:function(split,event){
				      				alert('风险树维护');
				      			}
				      		}
				        },
				        me.heightSpace,
				        {
				            xtype: 'button',
				            icon: __ctxPath + '/images/icons/icon_completed_32.gif',
				            scale : 'large',
				            tooltip: '图谱查看',
				            listeners:{
				      			click:function(split,event){
				      				alert('图谱查看');
				      			}
				      		}
				        },
				        me.heightSpace,
				        {
				            xtype: 'button',
				            icon: __ctxPath + '/images/icons/icon_late_32.gif',
				            scale : 'large',
				            tooltip: '评价标准',
				            listeners:{
				      			click:function(split,event){
				      				alert('评价标准');
				      			}
				      		}
				        },
				        me.heightSpace,
				        {
				            xtype: 'button',
				            icon: __ctxPath + '/images/icons/icon_not_started_32.gif',
				            scale : 'large',
				            tooltip: '预防方案',
				            listeners:{
				      			click:function(split,event){
				      				alert('预防方案');
				      			}
				      		}
				        },
				        me.heightSpace,
				        {
				            xtype: 'button',
				            icon: __ctxPath + '/images/icons/symbol_4_med.gif',
				            scale : 'large',
				            tooltip: '风险辨识',
				            listeners:{
				      			click:function(split,event){
				      				alert('风险辨识');
				      			}
				      		}
				        },
				        me.heightSpace,
				        {
				            xtype: 'button',
				            icon: __ctxPath + '/images/icons/symbol_5_med.gif',
				            scale : 'large',
				            tooltip: '风险评估',
				            listeners:{
				      			click:function(split,event){
				      				alert('风险评估');
				      			}
				      		}
				        },
				        me.heightSpace,
				        {
				            xtype: 'button',
				            icon: __ctxPath + '/images/icons/symbol_6_med.gif',
				            scale : 'large',
				            tooltip: '风险查看',
				            listeners:{
				      			click:function(split,event){
				      				alert('风险查看');
				      			}
				      		}
				        },
				        me.heightSpace,
				        {
				            xtype: 'button',
				            icon: __ctxPath + '/images/icons/icon_warning.gif',
				            scale : 'large',
				            tooltip: '计划跟踪',
				            listeners:{
				      			click:function(split,event){
				      				alert('计划跟踪');
				      			}
				      		}
				        },
				        me.heightSpace,
				        Ext.create('Ext.container.Container',{
				        	layout: {
								type: 'hbox',
					        	align:'stretch'
					        },
					        border:false,
				        	items:[
								{
								    xtype: 'button',
								    icon: __ctxPath + '/images/icons/icon_trend_rising_null.gif',
								    tooltip: '点击按钮向上滚动',
								    listeners:{
										click:function(split,event){
											alert('向上滚动');
										}
									}
								},
								{
						            xtype: 'button',
						            icon: __ctxPath + '/images/icons/icon_trend_falling_null.gif',
						            tooltip: '点击按钮向下滚动',
						            listeners:{
						      			click:function(split,event){
						      				alert('向下滚动');
						      			}
						      		}
						        }
				        	]
				        })
		        	]
				})
			]
        });

        me.callParent(arguments);
    },
    encapsulateChart:function(singleChart,title,subtitle){
    	var me=this;
    	var encapsulateChart = Ext.widget('panel',{
			width: 348,
			height: 80,
			layout: {
                type: 'table',
                columns: 2
            },
            border:false,
            defaults: {frame:true},
			items:[
				singleChart,
				Ext.create('Ext.container.Container',{
					layout:'column',
					defaults:{
						columnWidth: 1
					},
					height: 80,
					items:[
						Ext.widget('label',{
							margin : '13 0 0 10',
							html: '<font size="3">'+title+'</font>'
						}),
						Ext.widget('label',{
							margin : '5 0 0 10',
							html: '<font color="#7E8877">'+subtitle+'</font>'
						})
					]
				})
			]
		});
    	
    	return encapsulateChart;
    },
    reloadData:function(){
    	var me=this;
    	
    }
});