
Ext.define('FHD.view.sf.index.SFMyTask',{
    extend: 'Ext.form.Panel',
    
    initComponent : function() {
    	var me = this;
    	me.html = "";
    	 FHD.ajax({
    	 	async:false,
            url:__ctxPath+ '/jbpm/processInstance/jbpmHistActinstPage.f',
            params: {assigneeId:__user.empId,endactivity:"execut1",dbversion:0,limit:100000,start:0},
            callback: function (result) {
                if (result.datas) {
                	var executionId = "";
                	var businessId = "";
                	var operate = "";
                	var businessName = "";
                	var disName = "";
                	var rate = "";
                	for(var i = 0;i<result.datas.length;i++){
                		
			    		me.html = "<tr>" +
			    				"<td class='altt1' align='center' valign=middle'>1</td>" +
			    				"<td class='altt1 'align='center' valign='middle'>执行</td>" +
			    				"<td class='altt1' align='center' valign='middle'>2013年沈飞全面风险辨识流程</td>" +
			    				"<td class='altt1' align='center' valign='middle'>风险辨识流程</td>" +
			    				"<td class='altt1' align='center' valign='middle'>60%</td>" +
								"</tr>";
						me.html += me.html;
    				}
                }
            }
        });
    	
        Ext.apply(me, {
			html:me.html
        });
        me.callParent(arguments);
    },
    execute : function (grid, ele, rowIndex){//debugger;
    	var jEle=jQuery(ele);
    	var me = this;
		var winId = "win" + Math.random()+"$ewin";
		var url = jEle.find("[name='url']").val();
		var executionId = jEle.find("[name='executionId']").val();
		var businessId = jEle.find("[name='businessId']").val()
		
		var taskPanel = Ext.create(url,{
			executionId : executionId,
			businessId : businessId,
			winId: winId
		});
		
		var window = Ext.create('FHD.ux.Window',{
			id:winId,
			title:FHD.locale.get('fhd.common.execute'),
			iconCls: 'icon-edit',//标题前的图片
			maximizable: true,
			listeners:{
				close : function(){
					var resultContainer =  Ext.ComponentQuery.query("container[reload=true]") ;
	                if(resultContainer&&resultContainer.length>0){
						 resultContainer[0].backlogGrid.store.load();
					}
				}
			}
		});
		window.show();
		window.add(taskPanel);
		taskPanel.reloadData();
	}
});
