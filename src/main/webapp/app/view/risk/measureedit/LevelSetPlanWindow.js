Ext.define('FHD.view.risk.measureedit.LevelSetPlanWindow', {
	extend : 'Ext.window.Window',
	alias : 'widget.levelsetplanwindow',
	
	height : 500,
	width : 720,
	layout : {
		type : 'border'
	},
	type : '',
	selectId : '',
	title: '设定方案',
	modal: true,
    maximizable: true,
    
	initComponent : function(){
		var me = this;
		me.planStore = Ext.create('Ext.data.Store',{
        	storeId:'planStore',
        	pageSize: 100000,
        	idProperty: 'id',
        	fields:['id', 'name', 'desc'],
		    proxy: {
		        type : 'ajax',
		        url : 'chf/risk/measure/getplanlist',
		        extraParams:{
		        		type : me.type
		        	},
		        reader: {
		            type : 'json',
		            root : 'datas',
		            totalProperty :'totalCount'
		        }
		    },
		    autoLoad:true
        });
		
		me.plangrid = Ext.create('Ext.grid.Panel',{
            flex: 1,
            store:me.planStore,
            columns: [
            	{
                    xtype: 'gridcolumn',
                    dataIndex: 'id',
					filterable: false,
                    flex: 1,
                    text: 'id',
                    hidden : true
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'name',
					filterable: false,
                    flex: 1,
                    text: '方案名称',
                    renderer:function(value,metaData,record,colIndex,store,view) { 
						return "<a href='javascript:void(0)' onclick=\"Ext.getCmp('"+me.id+"').showPlanViewList('" + record.data.id + "')\" >" + value + "</a>"; 
					}
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'desc',
					filterable: false,
                    flex: 1,
                    text: '方案描述'
                }
            ],
            tbar : ['->',
				Ext.create('Ext.ux.form.SearchField', {
					width : 150,
					paramName:'query',
					store:me.planStore,
					emptyText : '方案名称'
			})]
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
                            items: [me.plangrid]
                        }
                    ]
                }
            ]
        });
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
	},
	
	showPlanViewList : function(){
	
	},
	
	onSubmit:Ext.emptyFn()
});