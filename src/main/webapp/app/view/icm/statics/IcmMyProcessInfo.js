/**
 * 我的数据
 * 我的流程
 * @author 邓广义
 */
Ext.define('FHD.view.icm.statics.IcmMyProcessInfo', {
    alias: 'widget.icmmyprocessinfo',
 	extend: 'Ext.container.Container',
 	overflowX: 'hidden',
	overflowY: 'auto',
	layout: {
        type: 'vbox',
        align: 'stretch'
    },
    displayChart:true,
    initParam:function(extraParams){
	   	var me = this;
	   	me.extraParams = extraParams;
    },
    requires: [
        'FHD.ux.GridPanel',
        'FHD.view.icm.statics.ProcessCountChart',
        'FHD.view.icm.statics.AssessResultCountChart',
        'FHD.view.icm.statics.DefectCountChart',
        'FHD.view.icm.icsystem.bpm.PlanProcessEditTabPanelForView',
        'FHD.view.icm.statics.AssessResultCountChart',
        'FHD.ux.Window'
    ],
    // 初始化方法
    initComponent: function() {
    	 var me = this;
    	 me.callParent(arguments);
		 me.gird = Ext.widget('fhdgrid', {
		 	flex:1,
			cols: [
				{dataIndex:'id',invisible:true},
				{dataIndex:'orgId',invisible:true},
				{dataIndex:'empId',invisible:true},
				{
				 	header: '统计',
				    xtype:'actioncolumn',
				    dataIndex: '',
				    align: 'center',
				    width: 70,
				    items: [{
				        iconCls: 'icon-table',
				        tooltip: '评价统计',
				        handler: function(grid, rowIndex, colIndex) {
		                    var rec = grid.getStore().getAt(rowIndex);
		                    me.showAssessResultStatics(rec.get('processName'),rec.get('id'));
		                }
				    },'-',{
				    	iconCls: 'icon-chart-bar',
				        tooltip: '缺陷统计',
				        handler: function(grid, rowIndex, colIndex) {
		                    var rec = grid.getStore().getAt(rowIndex);
		                    me.showDefectStatics(rec.get('processName'),rec.get('id'));
		                }
				    }/*,'-',{
				    	icon: __ctxPath + '/images/icons/icon_browse_dis.gif',
				    	tooltip: '',
				        handler: me.showSampleTestList
				    },'-',{
				        icon: __ctxPath + '/images/icons/icon_browse_r.gif',
				        tooltip: '',
				        handler: me.showSampleTestAllList
				    }*/]
				},
				{
					header:'缺陷状态',
					dataIndex: 'defectStatus',
					flex: .4,
					sortable:false,
					renderer: function (v) {
				        var color = "";
				        var display = "";
				        if (v == "ca_defect_level_0") {
				            color = "icon-ibm-symbol-4-sm";
				            display = FHD.locale.get("fhd.alarmplan.form.hight");
				        } else if (v == "ca_defect_level_2") {
				            color = "icon-ibm-symbol-6-sm";
				            display = FHD.locale.get("fhd.alarmplan.form.low");
				        } else if (v == "ca_defect_level_1") {
				            color = "icon-ibm-symbol-5-sm";
				            display = FHD.locale.get("fhd.alarmplan.form.min");
				        } else {
				            v = "icon-ibm-underconstruction-small";
				            display = FHD.locale.get('fhd.common.none');
				        }
				        return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;" +
				            "background-position: center top;' data-qtitle='' " +
				            "class='" + color + "'  data-qtip='" + display + "'>&nbsp</div>";
				    }
				},
				{
					header:'风险状态',
					dataIndex: 'riskStatus',
					flex: .4,
					sortable:false,
					renderer: function (v) {
				        var display = "";
				        if (v == "icon-ibm-symbol-4-sm") {
				            display = FHD.locale.get("fhd.alarmplan.form.hight");
				        } else if (v == "icon-ibm-symbol-6-sm") {
				            display = FHD.locale.get("fhd.alarmplan.form.low");
				        } else if (v == "icon-ibm-symbol-5-sm") {
				            display = FHD.locale.get("fhd.alarmplan.form.min");
				        } else {
				            v = "icon-ibm-underconstruction-small";
				            display = FHD.locale.get('fhd.common.none');
				        }
				        return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;" +
				            "background-position: center top;' data-qtitle='' " +
				            "class='" + v + "'  data-qtip='" + display + "'>&nbsp</div>";
				    }
				},
				{ header: '流程编号',  dataIndex: 'processCode' ,flex: 1 ,
					renderer:function(value,metaData,record,colIndex,store,view) {
							metaData.tdAttr = 'data-qtip="'+value+'" '; 
							return value; 
					}
				},
				{ header: '流程名称', dataIndex: 'processName', flex: 2 ,
					renderer:function(value,metaData,record,colIndex,store,view) {
							metaData.tdAttr = 'data-qtip="'+value+'" '; 
							return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showProcessView('" + record.data.id + "')\" >" + value + "</a>"; 
					}
				},
				{ header: '流程分类', dataIndex: 'parentName', flex: 1,
					renderer:function(value,metaData,record,colIndex,store,view) {
							metaData.tdAttr = 'data-qtip="'+value+'" '; 
							return value; 
					}
				},
				{ header: '发生频率', dataIndex: 'frequency', flex: .3},
				{ header: '责任部门', dataIndex: 'orgName', flex: .6},
				{ header: '责任人', dataIndex: 'empName', flex: .5},
				{ header: '更新日期', dataIndex: 'updateDate', width: 90}
				/*{ header: '操作', dataIndex: '', width: 50 , hideable: false,
					renderer:function(value,metaData,record,colIndex,store,view) {
							return "<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('"+me.id+"').showDefectStatics('" + record.get("id") + "')\"><div class='TipDiv'>aaaaa</div></a>"; 
					}
				},*/
				
			],
			url: __ctxPath+'/icm/statics/findprocessbysome.f',
			tbarItems: [
				{iconCls : 'icon-ibm-action-export-to-excel',text:'导出到Excel',tooltip: '把当前列表导出到Excel',handler :me.exportChart,scope : this}
			],
			extraParams:me.extraParams,
			checked:false,
			searchable:true,
			pagable : true
		});			 
		
		if(me.displayChart){
			me.processcountchart = Ext.widget('processcountchart',{
				flex:1,
				extraParams:me.extraParams,
				toolRegion:'west'
			});
        	me.add(me.processcountchart);
        }
        me.add(me.gird);
    },
    showDefectStatics: function(processName,processId){
    	var me=this;
		var centerPanel = Ext.getCmp('center-panel');
		var tab = centerPanel.getComponent('FHD.view.icm.statics.DefectCountChart'+processId);
		if(tab){
			centerPanel.setActiveTab(tab);
		}else{
			var p = centerPanel.add(Ext.widget('panel',{
				id:'FHD.view.icm.statics.DefectCountChart'+processId,
				title:processName+'-缺陷统计',
				flex:1,
				closable:true,
				layout:'fit',
				items:[
					Ext.widget('defectcountchart',{
			    		//grid列表url参数--可选
			    		extraParams:me.extraParams
			    	})
				]
			}));
			centerPanel.setActiveTab(p);
		}
    },
    showAssessResultStatics: function(processName,processId){
    	var me=this;
		var centerPanel = Ext.getCmp('center-panel');
		var tab = centerPanel.getComponent('FHD.view.icm.statics.AssessResultCountChart'+processId);
		if(tab){
			centerPanel.setActiveTab(tab);
		}else{
			
			var p = centerPanel.add(Ext.widget('panel',{
				id:'FHD.view.icm.statics.AssessResultCountChart'+processId,
				title:processName+'-评价统计',
				flex:1,
				closable:true,
				layout:'fit',
				items:[
					Ext.widget('assessresultcountchart',{
			    		//grid列表url参数--可选
			    		extraParams:me.extraParams
			    	})
				]
			}));
			centerPanel.setActiveTab(p);
		}
    },
    showProcessView:function(processid){
    	var me = this;
    	var grid = Ext.widget('planprocessedittabpanelforview',{paramObj:{processId:processid},readOnly:true});;
    	grid.reloadData();
    	me.win=Ext.widget('fhdwindow',{
			title : '详细查看',
			flex:1,
			autoHeight:true,
			autoScroll:true,
			collapsible : true,
			modal : true,
			maximizable : true,
			listeners:{
				close : function(){
				}
			}
		}).show();
		me.win.add(grid);
    },
    reloadData:function(){
    	var me=this;
    	me.gird.store.proxy.extraParams = me.extraParams;
    	me.gird.store.load();
    	if(me.displayChart){
    		me.processcountchart.extraParams = me.extraParams;
    		me.processcountchart.reloadData();
    	}
    },
    //导出grid列表
    exportChart:function(item, pressed){
    	var me=this;
    	if(me.gird.getStore().getCount()>0){
    		FHD.exportExcel(me.gird,'exportexcel','流程数据');
    	}else{
    		Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'),'没有要导出的数据!');
    	}
    }
});