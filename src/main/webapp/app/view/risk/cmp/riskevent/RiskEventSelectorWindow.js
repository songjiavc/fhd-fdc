/*
 * 发起（公司、部门）风险评估计划按风险添加时，去掉风险选择组件中组织、目标、流程选项卡，只保留风险选项卡
 * @2017年4月7日14:46:37 吉志强copy
 */


Ext.define('FHD.view.risk.cmp.riskevent.RiskEventSelectorWindow', {
    extend: 'Ext.window.Window',
	alias: 'widget.riskeventselectorwindow',
    
    height: 500,
    width: 1000,
    layout: {
        type: 'border'
    },
    title: '请选择风险事件',
	modal: true,
    maximizable: true,
    
    //接口
    lefttree:null,
    multiSelect:true,
    iconCls:'icon-risk',

    //内部组件
    candidategrid:null,
    selectedgrid:null,
    
    //左侧风险分类树的显示
    riskcatalogtreevisable:false,
    //左侧我的文件夹树显示
    riskmyfoldertreevisable:false,
    //右侧是否显示评估状态灯
    showLight:false,
    
    initComponent: function() {
    
        var me = this;
       // alert(me.schm);
        //列表实体定义
        me.modelFields = ['id','assessementStatus','code','name'];	
        
        //待选列表项assessementStatus
        if(me.showLight){
            me.candidateColumns = [{
            	xtype:'gridcolumn',
            	dataIndex:'assessementStatus',
            	text:'状态',
            	width:60,
            	renderer: function (v) {
                    var color = "";
                    var display = "";
                    if (v == "icon-ibm-symbol-4-sm") {
                        color = "symbol_4_sm";
                        display = FHD.locale
                            .get("fhd.alarmplan.form.hight");
                    } else if (v == "icon-ibm-symbol-6-sm") {
                        color = "symbol_6_sm";
                        display = FHD.locale
                            .get("fhd.alarmplan.form.low");
                    } else if (v == "icon-ibm-symbol-5-sm") {
                        color = "symbol_5_sm";
                        display = FHD.locale
                            .get("fhd.alarmplan.form.min");
                    } else {
                        v = "icon-ibm-underconstruction-small";
                        display = "无";
                    }
                    return "<div style='width: 32px; height: 19px; background-repeat: no-repeat;" + "background-position: center top;' data-qtitle='' " + "class='" + v + "'  data-qtip='" + display + "'>&nbsp</div>";
                }
            },{
            	xtype:'gridcolumn',
            	dataIndex:'code',
            	text:'编号',
            	width:100
            },{
            	xtype:'gridcolumn',
            	dataIndex:'name',
            	text:'名称',
            	flex:3,
            	renderer:function(value, metaData, record, rowIndex, colIndex, store){
        			metaData.tdAttr = 'data-qtip="'+value+'"';
    				return value;					
    		    }
            },{
            	xtype:'templatecolumn',
            	tpl:'<font class="icon-ibm-icon-details" style="cursor:pointer;">&nbsp&nbsp&nbsp&nbsp</font>',
            	width:80,
            	renderer:function(value, metaData, record, rowIndex, colIndex, store){
        			metaData.tdAttr = 'data-qtip="单击查看风险明细"';
        			value = '<font class="icon-ibm-icon-details" style="cursor:pointer;">&nbsp&nbsp&nbsp&nbsp</font>';
    				return value;					
    		    },
            	listeners:{
            		click:{
            			fn:function(g,d,i){
            				//click时候怎么屏蔽选择事件
            				var detailForm = Ext.create('FHD.view.risk.cmp.form.RiskRelateFormDetail', {
            		        	riskId:me.rid,//'9322cf77-a8f4-4d08-bb69-bcae3cb4fdf3',
            		        	height:360
            				});
            				var win = Ext.create('Ext.window.Window', {
                        		autoScroll:true,
                        		title:'风险事件详细信息',
                        		width:800,
                        		height:400,
                            	items:[detailForm]
                    		});
                        	win.show();
            			}
            		}
            	}
            }];
        }else{
            me.candidateColumns = [{
            	xtype:'gridcolumn',
            	dataIndex:'code',
            	text:'编号',
            	width:100
            },{
            	xtype:'gridcolumn',
            	dataIndex:'name',
            	text:'名称',
            	flex:3,
            	renderer:function(value, metaData, record, rowIndex, colIndex, store){
        			metaData.tdAttr = 'data-qtip="'+value+'"';
    				return value;					
    		    }
            },{
            	xtype:'templatecolumn',
            	tpl:'<font class="icon-ibm-icon-details" style="cursor:pointer;">&nbsp&nbsp&nbsp&nbsp</font>',
            	width:80,
            	renderer:function(value, metaData, record, rowIndex, colIndex, store){
        			metaData.tdAttr = 'data-qtip="单击查看风险明细"';
        			value = '<font class="icon-ibm-icon-details" style="cursor:pointer;">&nbsp&nbsp&nbsp&nbsp</font>';
    				return value;					
    		    },
            	listeners:{
            		click:{
            			fn:function(g,d,i){
            				//click时候怎么屏蔽选择事件
            				var detailForm = Ext.create('FHD.view.risk.cmp.form.RiskRelateFormDetail', {
            		        	riskId:me.rid,//'9322cf77-a8f4-4d08-bb69-bcae3cb4fdf3',
            		        	height:360
            				});
            				var win = Ext.create('Ext.window.Window', {
                        		autoScroll:true,
                        		title:'风险事件详细信息',
                        		width:800,
                        		height:400,
                            	items:[detailForm]
                    		});
                        	win.show();
            			}
            		}
            	}
            }];
        }
        
        //列表项
        me.gridColumns = [{
        	xtype:'gridcolumn',
        	dataIndex:'code',
        	text:'编号',
        	width:100
        },{
        	xtype:'gridcolumn',
        	dataIndex:'name',
        	text:'名称',
        	flex:3,
        	renderer:function(value, metaData, record, rowIndex, colIndex, store){
    			metaData.tdAttr = 'data-qtip="'+value+'"';
				return value;					
		    }
        },{
        	xtype:'templatecolumn',
        	tpl:'<font class="icon-del-min" style="cursor:pointer;">&nbsp&nbsp&nbsp&nbsp</font>',
        	width:70,
        	listeners:{
        		click:{
        			fn:function(g,d,i){
        				g.store.removeAt(i);
        				//更新已选择列表选中的数量
        	    		me.label.setText('(' + me.selectedgrid.store.getCount() + ')');
        			}
        		}
        	}
        }];		
        
        //2. 构建grid的model
        Ext.define('CandidateRecord', {
            extend: 'Ext.data.Model',
            fields: me.modelFields
        });
        
		var candidategridStore = Ext.create('Ext.data.Store',{
        	pageSize: 100000,
        	idProperty: 'id',
        	fields:me.modelFields,
        	proxy: {
		        type: 'ajax',
		        reader: {
		            type : 'json',
		            root : 'datas',
		            totalProperty :'totalCount'
		        }
		    }
        	
        });
        
        var selectgridStore = Ext.create('Ext.data.Store', {
			idProperty: 'id',
			fields:me.modelFields
		});
        
        //3. 构建树
//        me.lefttree = Ext.create('FHD.view.risk.cmp.riskevent.RiskEventSelectTree',{
        me.lefttree = Ext.create('FHD.view.risk.cmp.riskevent.RiskEventSelectTreeNew',{//吉志强2017年4月7日14:45:50修改
        	face:me,
			schm:me.schm,
			planType:me.planType,
			planId:me.planId,
        	riskcatalogtreevisable:me.riskcatalogtreevisable,
        	riskmyfoldertreevisable:me.riskmyfoldertreevisable,
        	split : true,
        	region:'west'
        });
        
        //待选择显示选中记录的数量
        me.candidatelabel = Ext.create("Ext.form.Label",{
        	text:'(0)',
        	padding: '0 0 2 0'
        });
        
        //只有多选时，才有全部添加和全部删除
        if(me.multiSelect) {
            me.candidategrid = Ext.create('Ext.grid.Panel',{
            	border:false,
                flex: 1,
                loadMask: true,
                store:candidategridStore,
                columns: me.candidateColumns,
                //selModel:Ext.create('Ext.selection.CheckboxModel'),
    		    tbar : [
    				'<b>待选择</b>',me.candidatelabel,'-',
    				Ext.create('Ext.button.Button', {
    					text : '全部选择',
    					handler : function() {
    		        		//添加全部
    						me.selectedgrid.store.insert(0,me.candidategrid.store.getRange(0,me.candidategrid.store.getTotalCount()-1));
    						me.selectedgrid.store.loadRecords(me.selectedgrid.store.getRange());
    			    		//更新已选择列表选中的数量
    			    		me.label.setText('(' + me.selectedgrid.store.getCount() + ')');
    					}
    				}),
    				Ext.create('Ext.button.Button', {
    					text : '全部取消',
    					handler : function() {
    						//取消全部
    						me.selectedgrid.store.remove(me.candidategrid.store.getRange(0,me.candidategrid.store.getTotalCount()-1));
            				//更新已选择列表选中的数量
            	    		me.label.setText('(' + me.selectedgrid.store.getCount() + ')');
    					}
    				}),
    				'->',
    				Ext.create('Ext.ux.form.SearchField', {
    					width : 150,
    					paramName : 'query',
    					store : candidategridStore,
    					emptyText : FHD.locale
    							.get('searchField.emptyText')
    				})]
            });
        }else{
            me.candidategrid = Ext.create('Ext.grid.Panel',{
            	border:false,
                flex: 1,
                loadMask: true,
                store:candidategridStore,
                columns: me.candidateColumns,
                tbar : [
        				'<b>待选择</b>',me.candidatelabel,
        				'->',
        				Ext.create('Ext.ux.form.SearchField', {
        					width : 150,
        					paramName : 'query',
        					store : candidategridStore,
        					emptyText : FHD.locale
        							.get('searchField.emptyText')
        				})]
            });
        }

        //显示选中记录的数量
        me.label = Ext.create("Ext.form.Label",{
        	text:'(0)',
        	padding: '0 0 2 0'
        });
        me.selectedgrid = Ext.create('Ext.grid.Panel',{
        	border:false,
            flex: 1,
            store:selectgridStore,
            columns: me.gridColumns,
            tbar:['<b>已选择</b>',me.label]
        });
        Ext.applyIf(me, {
            items: [me.lefttree,
                {
                    xtype: 'container',
                    activeItem: 0,
                    layout: {
                        type: 'border'
                    },
                    region: 'center',
                    items: [
                        {
                            xtype: 'container',
                            layout: {
                                align: 'stretch',
                                type: 'vbox'
                            },
                            region: 'center',
                            items: [me.candidategrid,me.selectedgrid]
                        }
                    ]
                }
            ]
        });
        me.buttonAlign = 'center';
        me.buttons = [
        	{
	            xtype: 'button',
	            text: $locale('fhd.common.confirm'),
	            width:70,
	            style: {
	            	marginRight: '10px'    	
	            },
	            handler:function(){
	            	me.onSubmit(me);
	            	me.close();
	            }
	        },
	        {
	            xtype: 'button',
	            text: $locale('fhd.common.close'),
	            width:70,
	            style: {
	            	marginLeft: '10px'    	
	            },
	            handler:function(){
	            	me.close();
	            }
	        }
	    ];
        me.callParent(arguments); 

        // 待选择列表
        me.candidategrid.on('select',function(t,node,i,o){
    		me.rid = node.data.id;
        	if(Ext.isEmpty(me.selectedgrid.store.getById(node.data.id))){
        		if(!me.multiSelect) {
        			me.selectedgrid.store.removeAll();
        		}
        		//3. 构建一条记录
	    	    var recordInstance = {};
        		for(var key in me.modelFields){
	    	    	recordInstance[me.modelFields[key]] = node.data[me.modelFields[key]];
	    	    }
        		var candidateRecord = new CandidateRecord(recordInstance);
	    		me.selectedgrid.store.insert(0,candidateRecord);
	    		//更新已选择列表选中的数量
	    		me.label.setText('(' + me.selectedgrid.store.getCount() + ')');
        	}
        });

    },
    setValue:function(selecteds){
    	var me = this;
    	Ext.each(selecteds,function(selected){
        	me.selectedgrid.store.insert(0,selected);
        });
    	//更新已选择列表选中的数量
		me.label.setText('(' + me.selectedgrid.store.getCount() + ')');
    },
    onSubmit:Ext.emptyFn()
});
