/**
 * 
 * 风险上报标准
 * 使用card布局
 * 
 * 下级有两个组件 潜在风险上报标准、历史风险上报标准
 * 
 * @author 宋佳
 */
Ext.define('FHD.view.risk.riskedit.Quantification', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.quantification',
    requires: [
    	'FHD.ux.kpi.opt.KpiSelector'
    ],
    autoHeight: true,
    autoWidth : true,
	layout: {
        type: 'hbox',
        align : 'stretch'
    },
	initComponent: function() {
		/*定性 qualitative  定量quantification*/
		var me = this;
		me.dlPk = Ext.widget('hiddenfield', {
			name : 'dlPk', 
			value: me.key
		});
		//定量
		me.selector = Ext.widget('kpioptselector', {
			name : 'kpiIds', 
			labelWidth : 80,
			fieldValue : me.fieldValue,
			multiSelect : false,
			width:300,
			gridHeight : 25,
			btnHeight : 25
		});
		me.selector.field.on("change",me.changAlarmId);
        me.hidred = Ext.widget('hiddenfield',{
					name : 'hidred',
					value : 'false'
        			});
        me.hidyellow = Ext.widget('hiddenfield',{
    				name : 'hidyellow',
					value : 'false'
        			});
        me.hidgreen = Ext.widget('hiddenfield',{
        			name : 'hidgreen',
					value : 'false'
        			});
        me.pubred = Ext.widget('hiddenfield',{
					name : 'pubred'
        			});
        me.pubyellow = Ext.widget('hiddenfield',{
    				name : 'pubyellow'
        			});
        me.pubgreen = Ext.widget('hiddenfield',{
        				name : 'pubgreen'
        			});
        me.delQuantification = Ext.widget('label',{
			html : "<a href='javascript:void(0)' onclick='Ext.getCmp(\""+me.id+"\").delSelf()'>删除</a>"
        });
        
        me.checkBoxGroup = Ext.widget('checkboxgroup',{
        	fieldLabel : '预警区间',
        	labelWidth : 60,
        	items : [ 
        			{
	                    xtype: 'checkbox',
	                    name : 'red',
	                    boxLabel: '红',
	                    listeners : {
	                    	change : me.setHidValue
	                    }
	                },
	                {
	                    xtype: 'checkbox',
	                    name : 'yellow',
	                    boxLabel: '黄',
	                    listeners : {
	                    	change : me.setHidValue
	                    }
	                },
	                {
	                    xtype: 'checkbox',
	                    name : 'green',
	                    boxLabel: '绿',
	                    listeners : {
	                    	change : me.setHidValue
	                    }
	                }]
        ,width : 200});
        Ext.apply(me, {
            items : [
            	me.dlPk,
            	me.selector,
            	me.hidred,
            	me.hidyellow,
            	me.hidgreen,
            	me.pubred,
            	me.pubyellow,
            	me.pubgreen,
            	{
                   xtype: 'hiddenfield',
                   width : 50
               },
            	me.checkBoxGroup,
            	{
                   xtype:'tbspacer',
                   flex:1
                },
            	me.delQuantification
            	]
        });
        me.callParent(arguments);
    },
    delSelf : function(){
    	var self = this;
    	self.selector.removeAll(true);
    	self.removeAll(true);
    	upPanel = self.up('fieldset');
    	upPanel.remove(self,true);
    },
    setHidValue : function(obj){
    	var me = obj.up('quantification');
    	if(obj.name == 'red'){
    		if(obj.checked){
    			if(me.selAlarmRegions){
	    			Ext.Array.each(me.selAlarmRegions,function(selAlarmRegion){
		    			if(selAlarmRegion.alarmIconId == '0alarm_startus_h'){
		    				me.hidred.setValue(selAlarmRegion.id);
		    			}else{
		    				me.hidred.setValue("1");
		    			}
	    			});
    			}else{
    				me.hidred.setValue("1");
    			}
    		}else{
    			me.hidred.setValue("false");
    		}
    	}else if(obj.name == 'yellow'){
    		if(obj.checked){
    			if(me.selAlarmRegions){
	    			Ext.Array.each(me.selAlarmRegions,function(selAlarmRegion){
		    			if(selAlarmRegion.alarmIconId == '0alarm_startus_m'){
		    				me.hidyellow.setValue(selAlarmRegion.id);
		    			}else{
		    				me.hidyellow.setValue("1");
		    			}
	    			});
    			}
    			else{
    				me.hidyellow.setValue("1");
    			}
    		}else{
    			me.hidyellow.setValue("false");
    		}
    	}else{
    		if(obj.checked){
    			if(me.selAlarmRegions){
	    			Ext.Array.each(me.selAlarmRegions,function(selAlarmRegion){
		    			if(selAlarmRegion.alarmIconId == '0alarm_startus_l'){
		    				me.hidgreen.setValue(selAlarmRegion.id);
		    			}else{
		    				me.hidgreen.setValue("1");
		    			}
	    			});
    			}else{
    				me.hidgreen.setValue("1");
    			}
    		}else{
    			me.hidgreen.setValue("false");
    		}
    	}
    },
    /*给预警区间初始化value*/
    initValues : function(){
    	var me = this;
    	Ext.Array.each(me.alarmRegions,function(alarmRegion){
	    	if(alarmRegion.alarmIconId == '0alarm_startus_h'){
	    		me.pubred.setValue(alarmRegion.id);
	    	}else if(alarmRegion.alarmIconId == '0alarm_startus_m'){
	    		me.pubyellow.setValue(alarmRegion.id);
	    	}else{
	    		me.pubgreen.setValue(alarmRegion.id);
	    	}
    	});
    	/*给选中的节点初始化value*/
    	Ext.Array.each(me.selAlarmRegions,function(selAlarmRegion){
	    	if(selAlarmRegion.alarmIconId == '0alarm_startus_h'){
	    		me.hidred.setValue(selAlarmRegion.id);
	    		if(me.pubred.getValue() == selAlarmRegion.alarmRegionId){
	    			me.checkBoxGroup.items.items[0].setValue(true);
	    		}
	    	}
	    	if(selAlarmRegion.alarmIconId == '0alarm_startus_m'){
	    		me.hidyellow.setValue(selAlarmRegion.id);
	    		if(me.pubyellow.getValue() == selAlarmRegion.alarmRegionId){
	    			me.checkBoxGroup.items.items[1].setValue(true);
	    		}
	    	}
	    	if(selAlarmRegion.alarmIconId == '0alarm_startus_l'){
	    		me.hidgreen.setValue(selAlarmRegion.id);
	    		if(me.pubgreen.getValue() == selAlarmRegion.alarmRegionId){
	    			me.checkBoxGroup.items.items[2].setValue(true);
	    		}
	    	}
    	});
    	
    },
    selectSubmit : function(){
    	
    },
    changAlarmId : function(field,newValue,oldValue,eOpts){
    	var me = this.up('quantification');
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
				    		me.pubred.setValue(alarmRegion.id);
				    	}else if(alarmRegion.alarmIconId == '0alarm_startus_m'){
				    		me.pubyellow.setValue(alarmRegion.id);
				    	}else{
				    		me.pubgreen.setValue(alarmRegion.id);
				    	}
    				});
	     		});
	     		
	     	}
         });
    }
});