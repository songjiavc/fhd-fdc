/*
 * 十大风险分类列表
 * zhengjunxiang
 * */

Ext.define('FHD.view.report.risk.Top10RiskGridGroupPanel', {
    extend: 'FHD.ux.GridPanel',
	alias: 'widget.top10riskgridgrouppanel',
	
	/**
	 * 常量区
	 */
	saveUrl:'/riskhistoryversion/createRiskVersion',		 //创建风险版本url
	listUrl:'/riskhistoryversion/findCurrentRiskStorage.f',  //查询url
	exportUrl:'/riskhistoryversion/findCurrentRiskStorageExport.f',  //查询url
	
	/**
	 * 变量
	 */
	companyId:undefined,	//公司id，在集团的十大风险模块，会传入值
	
	initParams:function(companyId){
		var me = this;
		me.companyId = companyId;
	},
	
	initComponent: function () {
        var me = this;
        
        var cols = [{
            dataIndex: 'id',
            invisible: true
        }, {
            dataIndex: 'riskId',
            invisible: true
        }, {
            dataIndex: 'adjustHistoryId',
            invisible: true
        }, {
            dataIndex: 'templateId',
            invisible: true
        }, {
            dataIndex: 'adjustType',
            invisible: true
        }, {
            dataIndex: 'num',
            header: '排名',
            sortable:false,
            width:40
        }, {
            dataIndex: 'name',
            header: '风险名称',
            sortable:false,
            flex:2,
            renderer: function (value, metaData, record, colIndex, store, view) {
                metaData.tdAttr = 'data-qtip="' + value + '"';
                var id = record.data['riskId'];
                return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('" + me.id + "').showRiskEventDetailWindow('" + id + "')\" >" + value + "</a>";
            }
        }, {
            dataIndex: 'parentName',
            sortable:false,
            header:'上级风险'
        }, {
            dataIndex: 'dutyDepartment',
            sortable:false,
            flex:1,
            header:'责任部门'
        }, {
            dataIndex: 'relativeDepartment',
            sortable:false,
            flex:1,
            header:'相关部门'
        }, {
            dataIndex: 'probability',
            sortable:false,
            header: '发生可能性',
            renderer:function(value){
            	if(value!=''){
            		//可以四舍五入
            		var num = Number(value);
            		return num.toFixed(2);
            	}
            }
        }, {
            dataIndex: 'influenceDegree',
            sortable:false,
            header: '影响程度',
            renderer:function(value){
            	if(value!=''){
            		//可以四舍五入
            		var num = Number(value);
            		return num.toFixed(2);
            	}
            }
        }, {
            dataIndex: 'riskScore',
            header: '风险值',
            sortable:false,
            renderer:function(value){
            	if(value!=''){
            		//可以四舍五入
            		var num = Number(value);
            		return num.toFixed(2);
            	}
            }
        }, {
            dataIndex: 'riskStatus',
            header: '风险水平',
            renderer:function(v){
                var display = "";
                if (v == "icon-ibm-symbol-4-sm") {
                    display = FHD.locale
                        .get("fhd.alarmplan.form.hight");
                } else if (v == "icon-ibm-symbol-6-sm") {
                    display = FHD.locale
                        .get("fhd.alarmplan.form.low");
                } else if (v == "icon-ibm-symbol-5-sm") {
                    display = FHD.locale
                        .get("fhd.alarmplan.form.min");
                } else {
                    v = "icon-ibm-underconstruction-small";
                    display = "无";
                }
                return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;" + "background-position: center top;' data-qtitle='' " + "class='" + v + "'  data-qtip='" + display + "'>&nbsp</div>";
            }
        }];
        
        me.versionTxt = Ext.widget('label', {
            xtype: 'label',
            text: '公司名称：',
            margin: '0 0 0 10'
        });
        me.versionLabel = Ext.widget('label', {
            xtype: 'label',
            text: __user.companyName
        });
        
		Ext.apply(me, {
			border : false,
        	checked:false,
            border: false,
            columnLines: true,
            url: __ctxPath + me.listUrl,
            extraParams:{
            	level:2
            },
            cols: cols,
            tbarItems:[me.versionTxt,me.versionLabel,{
            	btype: 'add',
            	text:'创建新版本',
            	name:'btnCreate',
            	handler:function(){
            		var me = this;
            		if(!me.window){
            			me.txt = Ext.create("Ext.form.field.Text",{
                        	xtype:'textfield',
                          	name:'versionName',
                        	fieldLabel:'版本名称',
                        	value:'',
                        	labelWidth : 60,
                        	width : '98%',
                        	labelAlign : 'right',
                        	margin : '10 3 10 3'
                        });
//                        me.form = Ext.create("Ext.form.Panel",{
//                        	items:[me.txt]
//                        });
            			me.window = Ext.create('Ext.window.Window', {
            				id:'zjx',
	                        title: '创建风险版本',
	                        maximizable: true,
	                        modal: true,
	                        closeAction:'hide',
	                        width: 400,
	                        height: 200,
	                        collapsible: true,
	                        autoScroll: true,
	                        layout: 'anchor',
	                        items: [me.txt],
	                        buttons: [{
	                            text: '保存',
	                            handler: function () {
	                            	var btn = this;
	                            	btn.setDisabled(true);
	                            	me.window.body.mask("提交中...","x-mask-loading");
	                            	FHD.ajax({
	                            		//async:false,//添加这句话mask不好使
	                    				url : __ctxPath + me.saveUrl,
	                    				params : {
	                    					companyId:me.companyId,	//公司id
	                    					versionName : me.txt.getValue() // 风险还是风险事件
	                    				},
	                    				callback : function(data) {
	                    					me.window.body.unmask();
	                    					btn.setDisabled(false);
	                    					//关闭window
	                    					me.window.close();
	                    					FHD.notification("创建新版本成功","操作提示");
	                    				}
	                    			});
	                            }
	                        }, {
	                            text: '关闭',
	                            handler: function () {
	                                me.window.close();
	                            }
	                        }]
	                    })
            		}
            		me.txt.setValue('');
					me.window.show();
            	}
            }, {
                btype: 'edit',
                name: 'btnEdit',
                text:'修改值',
                disabled:true,
                handler: function () {
                	var me = this;
                    if (me.getSelectionModel().getSelection().length > 0) {
                    	var record = me.getSelectionModel().getSelection()[0];
                    	if(record.data.riskScore == null || record.data.riskScore == ''){
                    		//新增
	                        me.riskAssessPanel = Ext.create('FHD.view.risk.cmp.RiskAssessPanel', {
	                            isEdit: false,
	                            type: 'risk',
	                            callback: function (data) {
	                                me.reloadData();
	                                me.assesswindow.close();
	                                if (me.callback) {
	                                    me.callback(data);
	                                }
	                            }
	                        });
	                        me.riskAssessPanel.reloadData(record.data.riskId);
                    	}else{
                    		me.riskAssessPanel = Ext.create('FHD.view.risk.cmp.RiskAssessPanel', {
	                            isEdit: true,
	                            type: 'risk',
	                            callback: function (data) {
	                                me.reloadData();
	                                me.assesswindow.close();
	                                if (me.callback) {
	                                    me.callback(data);
	                                }
	                            }
	                        });
	                        me.riskAssessPanel.reloadData(record.data.riskId, record.data.templateId, record.data.adjustHistoryId,record.data.adjustType);
                    	}
                    	
                        me.assesswindow = Ext.create('FHD.ux.Window', {
                            title: '评估记录修改',
                            maximizable: true,
                            modal: true,
                            width: 600,
                            height: 550,
                            collapsible: true,
                            autoScroll: true,
                            items: me.riskAssessPanel,
                            buttons: [{
                                text: '保存',
                                handler: function () {
                                    me.riskAssessPanel.save();
                                }
                            }, {
                                text: '关闭',
                                handler: function () {
                                    me.assesswindow.close();
                                }
                            }]
                        }).show();
                    }
                }
            },{
            	text : '导出excel',
    			tooltip: '导出excel',
    			iconCls : 'icon-ibm-action-export-to-excel',
    			scope:me,
    			handler:function(){
    				var me = this;
    				me.exportChart();
    			}
            },{
            	iconCls : 'icon-style-go',text: '高级查询',tooltip: '高级查询',handler :me.showSeniorQuery,scope : me
            }]
        });
    		
    	me.callParent(arguments);
    	
    	//控制按钮状态
    	me.on("itemclick",function(){
    		var btnEdit = Ext.ComponentQuery.query('button[name=btnEdit]',me)[0];
    		btnEdit.setDisabled(false);
    	});
	},
	
	showSeniorQuery : function(){
		var me=this;
		if(!me.searchWin){
			me.winform = Ext.create('FHD.view.report.risk.Top10RiskSeniorQueryGroupForm',{
				owner:me
			});
	    	me.searchWin = Ext.create('FHD.ux.Window',{
					title:'高级查询主窗口',
					height : 300,
					collapsible:false,
					items:[me.winform],
					closeAction : 'hide',	//不关闭
					maximizable:true//（是否增加最大化，默认没有）
		    });
		}
	    me.searchWin.show();
	},
	
	/**
	 * 查询，按照风险版本，级别，组织条件进行查询
	 */
	reloadData : function(version,level,orgIds,query){
		var me = this;
		if(version != undefined){
			me.version = version;
		}
		if(level != undefined){
			me.level = level;
		}
		if(orgIds != undefined){
			me.orgIds = orgIds;
		}
		if(query != undefined){
			me.querycontent = query;	//me.query报错，因为名称冲突
		}
		me.store.proxy.url = __ctxPath + me.listUrl;
		if(me.companyId){
			me.store.proxy.extraParams.companyId = me.companyId;
		}
		me.store.proxy.extraParams.version = me.version;
		me.store.proxy.extraParams.level = me.level;
		me.store.proxy.extraParams.orgIds = me.orgIds;
		me.store.proxy.extraParams.query = me.querycontent || '';
        me.store.load();
	},
	
	showRiskEventDetailWindow:function(riskId){
    	var me = this;
    	var detailForm = Ext.create('FHD.view.risk.cmp.form.RiskRelateFormDetail', {
        	riskId:riskId
		});
		var win = Ext.create('Ext.window.Window', {
    		autoScroll:true,
    		title:'风险详细信息',
    		width:800,
    		height:400,
    		layout:'fit'
		}).show();
		win.add(detailForm);
    },
    
    /**
     * 列表导出
     */
    exportChart : function(){
		var me=this;
    	me.headerDatas = [];
    	var items = me.columns;
		Ext.each(items,function(item){
			if(!item.hidden && item.dataIndex != ''){
				var value = {};
				value['dataIndex'] = item.dataIndex;
	        	value['text'] = item.text;
	        	me.headerDatas.push(value);
			}
		});
		var companyId = me.companyId || "";
		var version = me.version || "";
		var level = me.level || "2";
		var orgIds = me.orgIds || "";
		var query = me.querycontent || "";
		window.location.href = __ctxPath + me.exportUrl + '?version='+version+'&level='+level+'&companyId='+companyId
								+'&query='+query+'&orgIds='+orgIds+'&headerData='+Ext.encode(me.headerDatas);
	}
});