﻿Ext.define('FHD.demo.layout.GridPanelDemo', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.gridpaneldemo',
    
    /**
     * 初始化页面组件
     */
    initComponent: function () {
        var me = this;

        var cols = [
		{
			dataIndex:'id',
			hidden:true
		},
        {
            header: "编号",
            dataIndex: 'code',
            sortable: true,
            width: 80
        },
        {
            header: "名称",
            dataIndex: 'name',
            sortable: true,
            width:40,
            flex:1
        }
        ];
        
        var grid = Ext.create("FHD.ux.GridPanel",{
        	region:'center',
        	url : __ctxPath + "/pages/demo/layout/list.json",
            extraParams:{
            	riskId:1
            },
        	cols:cols,
        	btnIndex:2,
        	tbarItems:[{
        			btype:'add',
        			handler:function(){
        				alert('add');
        			}
    			},{
        			btype:'edit',
        			handler:function(){
        				alert('edit');
        			}
    			},{
        			btype:'delete',
        			handler:function(){
        				alert('del');
        			}
    			},{
        			btype:'export',
        			handler:function(){
        				alert('export');
        			}
    			},{
    				btype:'op',
        			tooltip: '操作1',
				    text:'操作1',
		            iconCls: 'icon-del',
				    handler: function() {
				        
				    }
    			},{
        			tooltip: '自定义按钮',
				    text:'自定义按钮',
		            iconCls: 'icon-save',
				    handler: function() {
				        
				    }
    			}],
        	title:'列表',
		    border: true,
		    checked: true
        });
        
        Ext.applyIf(me,{
        	layout:'border',
        	items:[grid]
        });
        
        me.callParent(arguments);
      
    }
});