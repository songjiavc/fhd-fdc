Ext.define('FHD.view.kpi.cmp.kpi.memo.MemoTipPanel', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.memotippanel',
	layout:'vbox',      
	border:false,
	width: 300,
    height: 220,
    
    renderData : function(data,tooltip){
    	var me = this;
    	me.removeAll();
 		if(data!=null){
 			var str = data.split('!@#$');
	    	if(str[0]==null){
	    		str[0] = "";
	    	}
	    	if(str[1]==null){
	    		str[1] = "";
	    	}
	    	if(str[2]==null){
	    		str[2] = "";
	    	}
	    	if(str[1]=='0alarm_startus_h'){
	    		str[1]='高';
	    	}
	    	if(str[1]=='0alarm_startus_l'){
	    		str[1]='低';
	    	}
	    	if(str[1]=='0alarm_startus_n'){
	    		str[1]='正常';
	    	}
	    	var div = me;
	    	tooltip.setWidth(250);
 			tooltip.setHeight(200);
	    	var html = "<br>主题:"+str[0] + "<br/>"+"<br>重要性:"+str[1]+ "<br/>"+"<br>注释:"+str[2]+ "<br/>";
			div.el.dom.innerHTML = html;
 		}
 		if(data==null||data==""){
 			var div = me;
 			tooltip.setWidth(120);
 			tooltip.setHeight(30);
	    	var html = "添加或查看注释";
	    	div.el.dom.innerHTML = html;
 		}
	},
    
	initComponent: function(){
		var me = this;
		Ext.apply(me)
		
		me.callParent(arguments);
		
	}
});