/**
 * 
 * 风险上报标准
 * 使用card布局
 * 
 * 下级有两个组件 潜在风险上报标准、历史风险上报标准
 * 
 * @author 宋佳
 */
Ext.define('FHD.view.risk.riskedit.ConfidenceKpi', {
    extend: 'Ext.container.Container',
    alias: 'widget.confidencekpi',
    requires: [
    	'FHD.ux.kpi.opt.KpiSelector',
    	'FHD.view.risk.riskedit.ConfidenceField'
    ],
	autoHeight: true,
    autoWidth : true,
	layout: {
        type: 'hbox',
        align : 'stretch'
    },
    activeOptItem : {},
    kpiname : '',
	initComponent: function() {
		//定量
		var me = this;
		me.confidence = Ext.widget('kpioptselector', {
			name : 'conKpiId', 
			labelWidth : 80,
			multiSelect : false,
			width:300,
			gridHeight : 25,
			btnHeight : 25
		});
		me.confidence.field.on("change",me.changAlarmId);
	    me.hidConred = Ext.widget('hiddenfield',{
					name : 'hidConred',
					value : 'false'
        			});
        me.hidConyellow = Ext.widget('hiddenfield',{
    				name : 'hidConyellow',
					value : 'false'
        			});
        me.hidCongreen = Ext.widget('hiddenfield',{
        			name : 'hidCongreen',
					value : 'false'
        			});
        me.pubConred = Ext.widget('hiddenfield',{
					name : 'pubConred'
        			});
        me.pubConyellow = Ext.widget('hiddenfield',{
    				name : 'pubConyellow'
        			});
        me.pubCongreen = Ext.widget('hiddenfield',{
        				name : 'pubCongreen'
        			});
        me.delConfidence = Ext.widget('label',{
			html : "<a href='javascript:void(0)' onclick='Ext.getCmp(\""+me.id+"\").delSelf()'>删除</a>"
        });
        me.addConfidence = Ext.widget('label',{
			html : "<a href='javascript:void(0)' onclick='Ext.getCmp(\""+me.id+"\").addConfidenceField()'>增加置信度/</a>"
        });
        me.confidenceCheckBoxGroup = Ext.widget('checkboxgroup',{
        	fieldLabel : '预警区间',
        	labelWidth : 60,
        	items : [ {
	                    xtype: 'checkboxfield',
	                    boxLabel: '红',
	                    name : 'red',
	                    listeners : {
	                    	change : me.setHidValue
	                    }
	                },
	                {
	                    xtype: 'checkboxfield',
	                    boxLabel: '黄',
	                    name : 'yellow',
	                    listeners : {
	                    	change : me.setHidValue
	                    }
	                },
	                {
	                    xtype: 'checkboxfield',
	                    boxLabel: '绿',
	                    name : 'green',
	                    listeners : {
	                    	change : me.setHidValue
	                    }
	                }]
        ,width : 200});
        me.region = Ext.widget('textfield',{
        	value : '',
        	width : 50
        });
        Ext.apply(me, {
            items: [
               me.confidence,
               me.hidConred,
	           me.hidConyellow,
	           me.hidCongreen,
	           me.pubConred,
	           me.pubConyellow,
	           me.pubCongreen,
               {
                   xtype: 'hiddenfield',
                   width : 50
               },
               me.confidenceCheckBoxGroup,
               {
                   xtype:'tbspacer',
                   flex : .5
                },
				me.addConfidence,
                me.delConfidence
            ]
        });

        me.callParent(arguments);
    },
    delSelf : function(){
    	var self = this;
    	upPanel = self.up('fieldset');
    	upPanel.remove(self);
    },
    addConfidenceField : function(){
    	var me = this;
    	var confidence = me.up('confidence');
    	if(me.activeOptItem.checked){
    		if(me.activeOptItem.name == 'red'){
    			var confidenceField = Ext.widget('confidencefield',{
    				conKpiId : me.confidence.getFieldValue(),
    				conColor : me.pubConred
    				});
    			confidence.confidencedatacardpanel.redFieldContainer.add(confidenceField);
    		}else if(me.activeOptItem.name == 'yellow'){
    			var confidenceField = Ext.widget('confidencefield',{
    				conKpiId : me.confidence.getFieldValue(),
    				conColor : me.pubConyellow
    				});
    			confidence.confidencedatacardpanel.yellowFieldContainer.add(confidenceField);
    		}else{
    			var confidenceField = Ext.widget('confidencefield',{
    				conKpiId : me.confidence.getFieldValue(),
    				conColor : me.pubCongreen
    				});
    			confidence.confidencedatacardpanel.greenFieldContainer.add(confidenceField);
    		}
    	}else{
    		Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'), "请选择一个预警区间进行添加置信度！");
    		return false;
    	}
    },
    setHidValue : function(obj){
    	var me = obj.up('confidencekpi');
    	if(obj.name == 'red'){
    		/*对预警情况的判断处理*/
    		if(obj.checked){
    			if(me.selAlarmRegions){
	    			Ext.Array.each(me.selAlarmRegions,function(selAlarmRegion){
		    			if(selAlarmRegion.alarmIconId == '0alarm_startus_h'){
		    				me.hidConred.setValue(selAlarmRegion.id);
		    			}else{
		    				me.hidConred.setValue("1");
		    			}
	    			});
    			}else{
    				me.hidConred.setValue("1");
    			}
    		}else{
    			me.hidConred.setValue("false");
    		}
    		/*控制显示置信区间的内容*/
    		var confidence = me.up('confidence');
    		confidence.confidencedatacardpanel.setActiveItem(0);
    	}else if(obj.name == 'yellow'){
    		/*对预警情况的判断处理*/
    		if(obj.checked){
    			if(me.selAlarmRegions){
	    			Ext.Array.each(me.selAlarmRegions,function(selAlarmRegion){
		    			if(selAlarmRegion.alarmIconId == '0alarm_startus_m'){
		    				me.hidConyellow.setValue(selAlarmRegion.id);
		    			}else{
		    				me.hidConyellow.setValue("1");
		    			}
	    			});
    			}
    			else{
    				me.hidConyellow.setValue("1");
    			}
    		}else{
    			me.hidConyellow.setValue("false");
    		}
    		/*控制显示置信区间的内容*/
    		var confidence = me.up('confidence');
    		confidence.confidencedatacardpanel.setActiveItem(1);
    	}else{
    		if(obj.checked){
    			if(me.selAlarmRegions){
	    			Ext.Array.each(me.selAlarmRegions,function(selAlarmRegion){
		    			if(selAlarmRegion.alarmIconId == '0alarm_startus_l'){
		    				me.hidCongreen.setValue(selAlarmRegion.id);
		    			}else{
		    				me.hidCongreen.setValue("1");
		    			}
	    			});
    			}else{
    				me.hidCongreen.setValue("1");
    			}
    		}else{
    			me.hidCongreen.setValue("false");
    		}
    		/*控制显示置信区间的内容*/
    		var confidence = me.up('confidence');
    		confidence.confidencedatacardpanel.setActiveItem(2);
    	}
    	me.activeOptItem = obj;
    },
    changAlarmId : function(field,newValue,oldValue,eOpts){
    	var me = this.up('confidencekpi');
    	FHD.ajax({
			url: __ctxPath+'/chf/reportingstandardcontrol/addquantificationinitvalue.f',
			params: {
				kpis : newValue
			},
	     	callback: function (data) {
	     		Ext.Array.each(data.quantificationArray,function(quantificationItem){
 					var alarmRegions = quantificationItem.alarmRegions;
 					Ext.Array.each(alarmRegions,function(alarmRegion){
				    	if(alarmRegion.alarmIconId == '0alarm_startus_h'){
				    		me.pubConred.setValue(alarmRegion.id);
				    	}else if(alarmRegion.alarmIconId == '0alarm_startus_m'){
				    		me.pubConyellow.setValue(alarmRegion.id);
				    	}else{
				    		me.pubCongreen.setValue(alarmRegion.id);
				    	}
    				});
	     		});
	     		
	     	}
         });
    }
});