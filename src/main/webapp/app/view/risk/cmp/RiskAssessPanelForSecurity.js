Ext.define('FHD.view.risk.cmp.RiskAssessPanelForSecurity', {
	extend : 'Ext.form.Panel',
	alias : 'widget.riskassesspanelforsecurity',
	
	initComponent : function() {
		var me = this;
		me.fieldContainer = Ext.widget('fieldcontainer',{
			layout : {
				type : 'vbox',
				align : 'stretch'
			}
		});
		Ext.applyIf(me, {
			items : [
				me.fieldContainer
			]
		});
		me.callParent(arguments);
	},
	
	reloadData(riskId,assessPlanId,riskResultScore){
		var me = this;
		var fieldsetContainer = me.fieldContainer;
		Ext.Array.each(riskResultScore,function(item,index){
			fieldsetContainer.insert(index,Ext.widget('numberfield',{
				name : 'scorenum',
				margin : '20 10 30 20',
				scoreid : item.scoreId,
				allowBlank : false,
				columnWidth : .5,
				value : item.score,
				fieldLabel : item.scoreName
			}));
		});
	},
	/**
	 *  add by jia.song@pcitc.com
	 *  date    20170614
	 *  desc    only get data don't save
	 */
	save : function(){
		var me = this;
		//check form value
		var form = me.getForm();
	    //var vobj = form.getValues();
	    if(!form.isValid()){
		     FHD.notification(FHD.locale.get('fhd.common.prompt'),'存在未通过的验证!');
		     return ;
	    }else{
	    	//get items change to rtnObj
	        var rtnArr = [];
	    	var containerItems = me.fieldContainer.items.items;
	    	Ext.Array.each(containerItems,function(item,index){
	    		 var tempObj  = {
	    				 "scoreId" :       item.scoreid,
	    				 "scoreName" : item.fieldLabel,
	    				 "score" :          item.value
	    		 };
	    		 rtnArr.push(tempObj);
	    	});
	    }
	    return rtnArr;
	}
});