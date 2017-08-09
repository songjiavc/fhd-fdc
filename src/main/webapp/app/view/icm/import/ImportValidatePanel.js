Ext.define('FHD.view.icm.import.ImportValidatePanel', {
	extend: 'Ext.tab.Panel',
	alias: 'widget.importvalidatepanel',

	layout:{
		type:'vbox',
		align:'stretch'
	},
	
	// 初始化方法
	initComponent: function() {
		var me = this;
		
		Ext.applyIf(me, {
			viewConfig : {forceFit : true},
			deferredRender: false,
			activeTab: 0,
			plain: true,
			overflowX:'hidden',
			overflowY:'auto'
	    });
		
	    me.callParent(arguments);
	    
	    //添加tabPanel
	    me.reloadData();
	},
	setFileId:function(fileId, type){
		var me=this;
		
		me.fileId = fileId;
		me.type = type;
	},
	addTabItems:function (panelName, title, url, allCount, correctCount, errorCount){
		var me = this;
		
		me.showItem = Ext.create(panelName,{
			title:title,
			url:url,
			extraParams:{
				fileId:me.fileId
			}
//			tbarItems: [
//	            Ext.create('Ext.form.Label' ,{ text: '导入总数'+':'+allCount+FHD.locale.get('fhd.common.units.ge')}),
//	            '-',
//	            Ext.create('Ext.form.Label' ,{ text: '正确个数'+':'+correctCount+FHD.locale.get('fhd.common.units.ge')}),
//	            '-',
//	            Ext.create('Ext.form.Label' ,{ text: '错误个数'+':'+errorCount+FHD.locale.get('fhd.common.units.ge')})
//	        ]
		});
		me.add(me.showItem);
    },
	reloadData:function(){
		var me=this;
		
		me.removeAll(true);
		
    	if('all' == me.type){
    		//全部
    		if(me.countMap){
    			Ext.each(me.countMap, function (item, index){
    	    		for(var key in item){
    	    			if('S1processMap' == key){
    	    				var panelName = 'FHD.view.icm.import.ProcessImportPreviewGrid';
    	    				var title = 'S1流程';
    	    				var url = __ctxPath + '/icm/process/import/findProcessPreviewListBySome.f';
    	    				var allCount = item[key].allCount;
    	    				var correctCount = item[key].correctCount;
    	    				var errorCount = item[key].errorCount;
    	    				me.addTabItems(panelName, title, url, allCount, correctCount, errorCount)
    	    			}
    	    			if('S2processPointMap' == key){
    	    				var panelName = 'FHD.view.icm.import.ProcessPointImportPreviewGrid';
    	    				var title = 'S2流程节点';
    	    				var url = __ctxPath + '/icm/process/import/findProcessPointPreviewListBySome.f';
    	    				var allCount = item[key].allCount;
    	    				var correctCount = item[key].correctCount;
    	    				var errorCount = item[key].errorCount;
    	    				me.addTabItems(panelName, title, url, allCount, correctCount, errorCount)
    	    			}
    	    			if('S3processPointRelationMap' == key){
    	    				var panelName = 'FHD.view.icm.import.ProcessPointRelationImportPreviewGrid';
    	    				var title = 'S3流程节点关系';
    	    				var url = __ctxPath + '/icm/process/import/findProcessPointRelationPreviewListBySome.f';
    	    				var allCount = item[key].allCount;
    	    				var correctCount = item[key].correctCount;
    	    				var errorCount = item[key].errorCount;
    	    				me.addTabItems(panelName, title, url, allCount, correctCount, errorCount)
    	    			}
    	    			if('S4controlStandardMap' == key){
    	    				var panelName = 'FHD.view.icm.import.ControlStandardImportPreviewGrid';
    	    				var title = 'S4控制标准(要求)';
    	    				var url = __ctxPath + '/icm/process/import/findControlStandardPreviewListBySome.f';
    	    				var allCount = item[key].allCount;
    	    				var correctCount = item[key].correctCount;
    	    				var errorCount = item[key].errorCount;
    	    				me.addTabItems(panelName, title, url, allCount, correctCount, errorCount)
    	    			}
    	    			if('S5processStandardRiskRelationMap' == key){
    	    				var panelName = 'FHD.view.icm.import.ProcessStandardRiskRelationImportPreviewGrid';
    	    				var title = 'S5流程--控制标准(要求)--风险';
    	    				var url = __ctxPath + '/icm/process/import/findProcessStandardRiskRelationPreviewListBySome.f';
    	    				var allCount = item[key].allCount;
    	    				var correctCount = item[key].correctCount;
    	    				var errorCount = item[key].errorCount;
    	    				me.addTabItems(panelName, title, url, allCount, correctCount, errorCount)
    	    			}
    	    			if('S6controlMeasureMap' == key){
    	    				var panelName = 'FHD.view.icm.import.ControlMeasureImportPreviewGrid';
    	    				var title = 'S6控制措施';
    	    				var url = __ctxPath + '/icm/process/import/findControlMeasurePreviewListBySome.f';
    	    				var allCount = item[key].allCount;
    	    				var correctCount = item[key].correctCount;
    	    				var errorCount = item[key].errorCount;
    	    				me.addTabItems(panelName, title, url, allCount, correctCount, errorCount)
    	    			}
    	    			if('S7riskProcessPointMeasureRelationMap' == key){
    	    				var panelName = 'FHD.view.icm.import.RiskProcessPointMeasureRelationImportPreviewGrid';
    	    				var title = 'S7风险-流程-流程节点-控制措施';
    	    				var url = __ctxPath + '/icm/process/import/findRiskProcessPointMeasureRelationPreviewListBySome.f';
    	    				var allCount = item[key].allCount;
    	    				var correctCount = item[key].correctCount;
    	    				var errorCount = item[key].errorCount;
    	    				me.addTabItems(panelName, title, url, allCount, correctCount, errorCount)
    	    			}
    	    			if('S8practiceTestAssessPointMap' == key){
    	    				var panelName = 'FHD.view.icm.import.PracticeTestAssessPointImportPreviewGrid';
    	    				var title = 'S8穿行测试评价点';
    	    				var url = __ctxPath + '/icm/process/import/findPracticeTestAssessPointPreviewListBySome.f';
    	    				var allCount = item[key].allCount;
    	    				var correctCount = item[key].correctCount;
    	    				var errorCount = item[key].errorCount;
    	    				me.addTabItems(panelName, title, url, allCount, correctCount, errorCount)
    	    			}
    	    			if('S9samplingTestAssessPointMap' == key){
    	    				var panelName = 'FHD.view.icm.import.SamplingTestAssessPointImportPreviewGrid';
    	    				var title = 'S9抽样测试评价点';
    	    				var url = __ctxPath + '/icm/process/import/findSamplingTestAssessPointPreviewListBySome.f';
    	    				var allCount = item[key].allCount;
    	    				var correctCount = item[key].correctCount;
    	    				var errorCount = item[key].errorCount;
    	    				me.addTabItems(panelName, title, url, allCount, correctCount, errorCount)
    	    			}
    	    		}
    	        });
    		}
    	}else if('0' == me.type){
    		//流程
    		if(me.countMap){
    			Ext.each(me.countMap, function (item, index){
    	    		for(var key in item){
    	    			if('S1processMap' == key){
    	    				var panelName = 'FHD.view.icm.import.ProcessImportPreviewGrid';
    	    				var title = 'S1流程';
    	    				var url = __ctxPath + '/icm/process/import/findProcessPreviewListBySome.f';
    	    				var allCount = item[key].allCount;
    	    				var correctCount = item[key].correctCount;
    	    				var errorCount = item[key].errorCount;
    	    				me.addTabItems(panelName, title, url, allCount, correctCount, errorCount)
    	    			}
    	    		}
    	        });
    		}
    	}else if('1' == me.type){
    		//流程节点
    		if(me.countMap){
    			Ext.each(me.countMap, function (item, index){
    	    		for(var key in item){  
    	    			if('S2processPointMap' == key){
    	    				var panelName = 'FHD.view.icm.import.ProcessPointImportPreviewGrid';
    	    				var title = 'S2流程节点';
    	    				var url = __ctxPath + '/icm/process/import/findProcessPointPreviewListBySome.f';
    	    				var allCount = item[key].allCount;
    	    				var correctCount = item[key].correctCount;
    	    				var errorCount = item[key].errorCount;
    	    				me.addTabItems(panelName, title, url, allCount, correctCount, errorCount)
    	    			}
    	    		}
    	        });
    		}
    	}else if('2' == me.type){
    		//流程节点关系
    		if(me.countMap){
    			Ext.each(me.countMap, function (item, index){
    	    		for(var key in item){
    	    			if('S3processPointRelationMap' == key){
    	    				var panelName = 'FHD.view.icm.import.ProcessPointRelationImportPreviewGrid';
    	    				var title = 'S3流程节点关系';
    	    				var url = __ctxPath + '/icm/process/import/findProcessPointRelationPreviewListBySome.f';
    	    				var allCount = item[key].allCount;
    	    				var correctCount = item[key].correctCount;
    	    				var errorCount = item[key].errorCount;
    	    				me.addTabItems(panelName, title, url, allCount, correctCount, errorCount)
    	    			}
    	    		}
    	        });
    		}
    	}else if('3' == me.type){
    		//控制标准(要求)
    		if(me.countMap){
    			Ext.each(me.countMap, function (item, index){
    	    		for(var key in item){
    	    			if('S4controlStandardMap' == key){
    	    				var panelName = 'FHD.view.icm.import.ControlStandardImportPreviewGrid';
    	    				var title = 'S4控制标准(要求)';
    	    				var url = __ctxPath + '/icm/process/import/findControlStandardPreviewListBySome.f';
    	    				var allCount = item[key].allCount;
    	    				var correctCount = item[key].correctCount;
    	    				var errorCount = item[key].errorCount;
    	    				me.addTabItems(panelName, title, url, allCount, correctCount, errorCount)
    	    			}
    	    		}
    	        });
    		}
    	}else if('4' == me.type){
    		//控制标准-流程-风险
    		if(me.countMap){
    			Ext.each(me.countMap, function (item, index){
    	    		for(var key in item){
    	    			if('S5processStandardRiskRelationMap' == key){
    	    				var panelName = 'FHD.view.icm.import.ProcessStandardRiskRelationImportPreviewGrid';
    	    				var title = 'S5流程--控制标准(要求)--风险';
    	    				var url = __ctxPath + '/icm/process/import/findProcessStandardRiskRelationPreviewListBySome.f';
    	    				var allCount = item[key].allCount;
    	    				var correctCount = item[key].correctCount;
    	    				var errorCount = item[key].errorCount;
    	    				me.addTabItems(panelName, title, url, allCount, correctCount, errorCount)
    	    			}
    	    		}
    	        });
    		}
    	}else if('5' == me.type){
    		//控制措施
    		if(me.countMap){
    			Ext.each(me.countMap, function (item, index){
    	    		for(var key in item){
    	    			if('S6controlMeasureMap' == key){
    	    				var panelName = 'FHD.view.icm.import.ControlMeasureImportPreviewGrid';
    	    				var title = 'S6控制措施';
    	    				var url = __ctxPath + '/icm/process/import/findControlMeasurePreviewListBySome.f';
    	    				var allCount = item[key].allCount;
    	    				var correctCount = item[key].correctCount;
    	    				var errorCount = item[key].errorCount;
    	    				me.addTabItems(panelName, title, url, allCount, correctCount, errorCount)
    	    			}
    	    		}
    	        });
    		}
    	}else if('6' == me.type){
    		//风险-流程-流程节点-控制措施
    		if(me.countMap){
    			Ext.each(me.countMap, function (item, index){
    	    		for(var key in item){
    	    			if('S7riskProcessPointMeasureRelationMap' == key){
    	    				var panelName = 'FHD.view.icm.import.RiskProcessPointMeasureRelationImportPreviewGrid';
    	    				var title = 'S7风险-流程-流程节点-控制措施';
    	    				var url = __ctxPath + '/icm/process/import/findRiskProcessPointMeasureRelationPreviewListBySome.f';
    	    				var allCount = item[key].allCount;
    	    				var correctCount = item[key].correctCount;
    	    				var errorCount = item[key].errorCount;
    	    				me.addTabItems(panelName, title, url, allCount, correctCount, errorCount)
    	    			}
    	    		}
    	        });
    		}
    	}else if('7' == me.type){
    		//穿行测试评价点
    		if(me.countMap){
    			Ext.each(me.countMap, function (item, index){
    	    		for(var key in item){
    	    			if('S8practiceTestAssessPointMap' == key){
    	    				var panelName = 'FHD.view.icm.import.PracticeTestAssessPointImportPreviewGrid';
    	    				var title = 'S8穿行测试评价点';
    	    				var url = __ctxPath + '/icm/process/import/findPracticeTestAssessPointPreviewListBySome.f';
    	    				var allCount = item[key].allCount;
    	    				var correctCount = item[key].correctCount;
    	    				var errorCount = item[key].errorCount;
    	    				me.addTabItems(panelName, title, url, allCount, correctCount, errorCount)
    	    			}
    	    		}
    	        });
    		}
    	}else if('8' == me.type){
    		//抽样测试评价点
    		if(me.countMap){
    			Ext.each(me.countMap, function (item, index){
    	    		for(var key in item){
    	    			if('S9samplingTestAssessPointMap' == key){
    	    				var panelName = 'FHD.view.icm.import.SamplingTestAssessPointImportPreviewGrid';
    	    				var title = 'S9抽样测试评价点';
    	    				var url = __ctxPath + '/icm/process/import/findSamplingTestAssessPointPreviewListBySome.f';
    	    				var allCount = item[key].allCount;
    	    				var correctCount = item[key].correctCount;
    	    				var errorCount = item[key].errorCount;
    	    				me.addTabItems(panelName, title, url, allCount, correctCount, errorCount)
    	    			}
    	    		}
    	        });
    		}
    	}
	}
});