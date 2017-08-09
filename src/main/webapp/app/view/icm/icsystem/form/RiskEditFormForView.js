/**
 * 风险预览页面
 * 
 * @author 宋佳
 */
Ext.define('FHD.view.icm.icsystem.form.RiskEditFormForView', {
   	extend: 'Ext.form.Panel',
   	alias: 'widget.riskeditformforview',
  	requires: [
      	'FHD.view.icm.icsystem.form.MeaSureEditFormForView'	
   	],
   	frame: false,
   	border : false,
   	bodyPadding: "0 3 3 3",
   	paramObj : {
   		processId : "",
   		processRiskId : "",
   		measureId : ""
   	},
   	selectArray : [],
   	measureeditform : [],
   	autoScroll : true,
   	initParam:function(paramObj){
		var me = this;
	 	me.paramObj = paramObj;
	},
   	addComponent: function () {
    	var me = this;
    	//基本信息fieldset
       	me.riskShortView = Ext.create('FHD.view.risk.cmp.form.RiskShortFormDetail');
		me.add(me.riskShortView);
	},
	reloadData: function() {
		var me = this;
		me.riskShortView.reloadData(me.paramObj.processRiskId);
		me.load({
		    url: __ctxPath + '/processrisk/loadriskeditformdata.f',
		    params: {
		        processRiskId: me.paramObj.processRiskId
		    },
		    success: function (form, action) {
		        return true;
		    }
        });
    },
    // 初始化方法
    initComponent: function() {
        var me = this;
        Ext.applyIf(me);
        me.callParent(arguments);
        //向form表单中添加控件
	    me.addComponent();
    },
	getInitData : function(){
   	    var me = this;
   		me.measureeditform = [];
   		FHD.ajax({
			url:__ctxPath+'/processrisk/findmeasureidbyriskid.f',
			params: {
				processId: me.paramObj.processId,
				processRiskId : me.paramObj.processRiskId
			},
	     	callback: function (data) {
				me.paramObj.measureId = data.data;
				for(var i = 0;i<me.paramObj.measureId.length;i++){
					me.editform = Ext.widget('measureeditformforview',{processId:me.paramObj.processId,measureId:me.paramObj.measureId[i],num:me.measureeditform.length});
					me.measureeditform.push(me.editform);
					me.editform.initParam({
						processId: me.paramObj.processId,
						measureId : me.paramObj.measureId[i]
					});
					me.measureeditform[i].reloadData();
					me.add(me.measureeditform[i]);
	             }
	         }
         });
	   }
});