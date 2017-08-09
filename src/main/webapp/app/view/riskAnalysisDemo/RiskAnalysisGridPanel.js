/**
 * 
 * 风险分析列表demo
 */

Ext.define('FHD.view.riskAnalysisDemo.RiskAnalysisGridPanel', {
    extend: 'FHD.ux.layout.GridPanel',
    alias: 'widget.riskanalysisgridpanel',
 	requires: [
 		'FHD.view.risk.assess.utils.GridCells',
 		'FHD.view.riskAnalysisDemo.RiskRegressionAnalyMainPanel'
	],
	queryUtl : 'risk/analysis/findriskanalysis.f',
	beforestart : 0,
	
    edit : function(isAdd){
    	var me = this;
    	var selections = me.getSelectionModel().getSelection();
    	if(isAdd){
	    	Ext.getCmp('metriccentercardpanel').showRiskEventAddContainer();
    	}else{
    		var riskid = selections[0].get('id');
	    	riskid = riskid.split('_')[1];//风险
    		Ext.getCmp('metriccentercardpanel').showRiskEventAddContainer(riskid);
    	}
    },
    
    //回归分析
    regressionFun: function(){
    	var me = this;
    	me.riskRegressionAnalyMainPanel = Ext.widget('riskRegressionAnalyMainPanel');
		me.preWin = Ext.create('FHD.ux.Window', {
			title:'回归分析',
   		 	height: 600,
    		width: 940,
   			layout: 'fit',
    		items: [me.riskRegressionAnalyMainPanel]
   			/*fbar: [
   					{ xtype: 'button', text: '确定', handler:function(){me.preWin.hide();}}
				  ]*/
		}).show();
    },
    //风险承受度
    toleranceFun: function(){
    	var me = this;
    	me.kpiRiskAnalyse = Ext.create('FHD.view.risk.analyse.KpiRiskAnalyse');
    	me.toleWin = Ext.create('FHD.ux.Window', {
			title:'回归分析',
   		 	height: 600,
    		width: 900,
   			layout: 'fit',
    		items: [me.kpiRiskAnalyse]
		}).show();
    },
    //风险关联
    risksSelect:function(){
    	var me = this;
    	var selections = me.getSelectionModel().getSelection();
        if (selections.length == 1) {
	    	var kpiid = selections[0].get('id');
	    	kpiid = kpiid.split('_')[0];//影响指标
	        FHD.ajax({
	            url: 'risk/analysis/findinflukpi.f',
	            params: {
	                kpiid : kpiid,
	                smid : Ext.getCmp('strategyobjectivetab').paramObj.smid
	            },
	            callback: function(data) {
	            	Ext.define('Risk', {
						extend: 'Ext.data.Model',
						fields:['id', 'code', 'name']
					});
	            	me.win = Ext.create('FHD.view.risk.cmp.riskevent.RiskEventSelectorWindow',{//风险选择组件
						multiSelect:true,
			    		modal: true,
					   	onSubmit:function(win){
					   		var selectedgridstores =  win.selectedgrid.store;
					   		var riskids = [];
					   		Ext.each(selectedgridstores.data.items,function(item){
					   			riskids.push(item.data.id);
					        });
					        FHD.ajax({
					            url: 'risk/analysis/savekpirelarisk.f',
					            params: {
					                riskids : riskids.join(','),
					                kpiid: kpiid,
					                smid : Ext.getCmp('strategyobjectivetab').paramObj.smid
					            },
					            callback: function(data) {
					            	Ext.ux.Toast.msg(FHD.locale.get('fhd.common.prompt'), FHD.locale.get('fhd.common.operateSuccess'));
					            	Ext.getCmp('strategyobjectivetab').riskanalysisgridpanel.store.load();
					            }
					        });
			    		}
					}).show();
					for(var i=0;i<data.datas.length;i++){
						var riskent = new Risk({
							id : data.datas[i].id,
							code : data.datas[i].code,
							name : data.datas[i].name
						});
						me.win.setValue(riskent);
					}
	            	
//	            	
//	                if (data) {
//	                    me.store.load();
//	                    if(enable=='0yn_y'){
//	                    	Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'), '启用成功!');
//	                    }else{
//	                    	Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'), '停用成功!');
//	                    }
//	                    
//	                }
	            }
	        });
        }else {
            Ext.ux.Toast.msg(FHD.locale.get('fhd.common.prompt'), '请选择一条目标指标.');
            return;
        }
    },
    
    setstatus : function(me){//设置按钮可用状态
    	Ext.getCmp('riskanalysisgrid_editbtnId').setDisabled(me.getSelectionModel().getSelection().length != 1);
    },
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        var cols = [
			{
				header: "id",
				dataIndex:'id',
				hidden:true
			},
	        {
	            header: "目标指标",
	            dataIndex: 'aimTarget',
	            sortable: false,
	            flex: .8,
	            renderer: function (value, metaData, record, colIndex, store, view) {
	            	metaData.tdAttr = 'data-qtip="'+value+'"';
	                return value;
	            }
	        },{
	            header: "风险名称",
	            dataIndex: 'riskname',
	            sortable: false,
	            flex: 2,
	            renderer:function(value, metaData, record, colIndex, store, view) {
	            	metaData.tdAttr = 'data-qtip="'+value+'"';
	            	var id = record.data['id'];
	            	id = id.split('_')[1];
    				return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('metriccentercardpanel').showRiskEventDetailContainer('" + id + "')\" >" + value +"</a>";
    			}
	        },{
	            header: "风险指标",
	            dataIndex: 'riskMeasure',
	            sortable: false,
	            flex: 1,
	            renderer: function (value, metaData, record, colIndex, store, view) {
	            	metaData.tdAttr = 'data-qtip="'+value+'"';
	                return value;
	            }
	        },{
	            header: "状态",
	            dataIndex: 'assessementStatus',
	            sortable: false,
	            flex: .2,
	            renderer: function (v) {
	                var color = "";
	                var display = "";
	                if (v == "icon-ibm-symbol-4-sm") {
	                    color = "symbol_4_sm";
	                    display = FHD.locale.get("fhd.alarmplan.form.hight");
	                } else if (v == "icon-ibm-symbol-6-sm") {
	                    color = "symbol_6_sm";
	                    display = FHD.locale.get("fhd.alarmplan.form.low");
	                } else if (v == "icon-ibm-symbol-5-sm") {
	                    color = "symbol_5_sm";
	                    display = FHD.locale.get("fhd.alarmplan.form.min");
	                } else if(v=="icon-ibm-symbol-safe-sm"){
	                	 display = "安全";
	                } 
	                else {
	                    v = "icon-ibm-underconstruction-small";
	                    display = "无";
	                }
	                return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;" +
	                    "background-position: center top;' data-qtitle='' " +
	                    "class='" + v + "'  data-qtip='" + display + "'>&nbsp</div>";
	            }
        }
        ];
        
        var btns = [{
        			btype:'add',
        			handler:function(){
        				me.edit(true);
        			}
    			},{
        			btype:'edit',
        			disabled:true,
        			id : 'riskanalysisgrid_editbtnId',
        			handler:function(){
        				me.edit(false);
        			}
    			},{
				    text:'关联风险',
		            iconCls: 'icon-plugin-add',
				    handler: function() {
				        me.risksSelect();
				    }
    			},{
				    text:'回归分析',
		            iconCls: 'icon-linechart',
				    handler: function() {
				        me.regressionFun();
				    }
    			},{
				    text:'风险承受度',
		            iconCls: 'icon-ibm-icon-metrics-16',
				    handler: function() {
				        me.toleranceFun();
				    }
    			}];
       
        Ext.apply(me,{
        	cols:cols,
        	btns: btns,
		    border: false,
		    checked : true,
		    pagable : true,
 		    rowlines: true,
 		    columnLines: true
        });
       
        me.on('selectionchange',function(){me.setstatus(me)});
        me.callParent(arguments);
        
        me.on('resize',function(p){
    		me.setHeight(FHD.getCenterPanelHeight() - 35);
    	});
    	me.store.on('load',function(){
        	Ext.widget('gridCells').mergeCells(me, [3]);
        });
        me.store.on('beforeload',function(){ 
        	if(me.beforestart == me.store.currentPage){
        		me.beforestart = 0;
        		me.store.proxy.extraParams.start = 0;
				me.store.loadPage(1);
			}else{
				me.beforestart = me.store.currentPage;
				me.store.proxy.extraParams.start = (me.store.currentPage-1) * me.store.pageSize;
			}
        });
    },
    
    reLoadData : function(smid){
    	var me = this;
    	me.smid = smid;
    	me.store.proxy.url = me.queryUtl;
    	me.store.proxy.extraParams.smid = smid;
    	me.store.load();
    }

});