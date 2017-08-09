
Ext.define('FHD.view.sf.index.SFMyTaskGrid',{
    extend: 'FHD.ux.GridPanel',
    
    hideHeaders:false,
	checked: false,
    pagable : false,
    searchable : false,
    columnLines: true,
    rowLines:true,
    //storeGroupField:false,
    
    initComponent : function() {
    	var me = this;
    	me.jsonArray=[];
    	var value = {};
		value['property'] = "startDate";
    	value['direction'] = "desc";
    	me.jsonArray.push(value)
    	
        Ext.apply(me, {
			url:__ctxPath + '/jbpm/processInstance/jbpmHistActinstPage.f',
			extraParams:{assigneeId:__user.empId,endactivity:"execut1",dbversion:0,limit:8,sort:Ext.JSON.encode(me.jsonArray)},
			listeners:{
				afterrender:function(self,eopts){
					var elments = Ext.get("gridview-1028");//.x-grid3-hd   
					if(elments){
						elments.setStyle("overflow", 'hidden');
					}else{
						elments = Ext.get("ext-comp-1009");
						elments.setStyle("overflow", 'hidden');
					}
				}
			},
    		cols : [	
			      	{dataIndex: 'executionId', invisible:true},
					{dataIndex: 'businessId', invisible:true},
					{dataIndex: 'form', invisible:true},
					{dataIndex: 'activityName',invisible:true},
	    			{
	    				header: FHD.locale.get('fhd.common.operate'), 
	    				dataIndex: 'operate', 
	    				hidden:true,
	    				sortable: false, 
	    				flex : .2,
	    				align:'center',
	    				renderer: function(value, metaData, record, colIndex, store, view) {
							if(record.data.id){
								return "<a href=\"javascript:void(0);\" style=\"text-decoration: none;color:#2C4674;\">执行<input name='url' type='hidden' value='"+record.get("form")+"'><input name='executionId' type='hidden' value='"+record.get("executionId")+"'><input name='businessId' type='hidden' value='"+record.get("businessId")+"'></a>";
							}else{
								return value;
							}
						},
						listeners:{
		            		click:{
		            			fn:me.execute
		            		}
		            	}
					},{
						header: '工作内容', 
						dataIndex: 'businessName', 
						sortable: false,
						flex :.6,
						align:'center',
						renderer:function(value, metaData, record, rowIndex, colIndex, store){
							if(value){
								//return "<a href=\"javascript:void(0);\" style=\"text-decoration: none;color:#2C4674;\">"+ value + " ( "+record.get("activityName")+" ) <input name='url' type='hidden' value='"+record.get("form")+"'><input name='executionId' type='hidden' value='"+record.get("executionId")+"'><input name='businessId' type='hidden' value='"+record.get("businessId")+"'></a>";
								return "<a href=\"javascript:void(0);\" style=\"text-decoration: none;color:#2C4674;\">"+ 
								value + "<input name='url' type='hidden' value='"
								+record.get("form")+"'><input name='executionId' type='hidden' value='"
								+record.get("executionId")+"'><input name='businessId' type='hidden' value='"
								+record.get("businessId")+"'></a>";
							}else{
								return record.get("activityName");
							}
				   	 	},
						listeners:{
		            		click:{
		            			fn:me.execute
		            		}
		            	}
					},
			      	{
	    	            header: "工作节点",
	    	            dataIndex: 'activityName',
	    	            sortable: false,
	    	            align: 'center',
	    	            flex:.2,
	    	            renderer:function(v){
	    	            	return v ;
	    	            }
	    	       },
			      	{
	    	            header: "工作类型",
	    	            dataIndex: 'disName',
	    	            sortable: false,
	    	            align: 'center',
	    	            flex:.2,
	    	            renderer:function(v){
	    	            	return v ;
	    	            }
	    	       },
			       {
			      		header: "工作进度",
			      		dataIndex: 'rate',
			      		sortable: false,
			      		flex:.2,
			      		align:'center',
						renderer:function(value, metaData, record, rowIndex, colIndex, store){
							if(value){
								return "<div style='FONT-SIZE: 14px;height: 23px; border: 1px none #89c7ee;'>"+"<span style=\"float: left;margin: 5px 0 0 30px;\">"+value+"%"+"</span>"+"</div>";
							}else{
								return "<div style='FONT-SIZE: 14px;height: 23px; border: 1px none #89c7ee;'>"+value+"</div>";
							}
					    }
				}]
        });
        me.callParent(arguments);
    },
    execute : function (grid, ele, rowIndex){
    	var jEle=jQuery(ele);
    	var me = this;
		var winId = "win" + Math.random()+"$ewin";
		var url = jEle.find("[name='url']").val();
		if(!url)return;
		var executionId = jEle.find("[name='executionId']").val();
		var businessId = jEle.find("[name='businessId']").val()
		var taskPanel = parent.Ext.create(url,{
			executionId : executionId,
			businessId : businessId,
			winId: winId
		});
		
		var window = parent.Ext.create('FHD.ux.Window',{
			id:winId,
			title:FHD.locale.get('fhd.common.execute'),
			iconCls: 'icon-edit',//标题前的图片
			maximizable: true,
			listeners:{
				close : function(){
					grid.store.load();
				}
			}
		});
		window.show();
		window.add(taskPanel);
		taskPanel.reloadData();
	}
});
