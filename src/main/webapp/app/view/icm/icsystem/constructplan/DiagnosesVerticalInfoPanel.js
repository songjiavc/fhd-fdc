Ext.define('FHD.view.icm.icsystem.constructplan.DiagnosesVerticalInfoPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.diagnosesverticalinfopanel',
    requires: [
       'FHD.view.icm.icsystem.constructplan.form.DefectClearUpFormForView',
       'FHD.view.icm.defect.form.DefectFormForView'
    ],
	autoScroll:true,
    initParam : function(paramObj){
         var me = this;
    	 me.paramObj = paramObj;
	},
	border:false,
    initComponent: function() {
        var me = this;
        me.callParent(arguments);
    },
    getInitParams : function(){
    	var me = this;
    	FHD.ajax({
			url:__ctxPath+'/icm/icsystem/getinitparamsfordefect.f',
			params: {
				planRelaStandardId: me.planRelaStandardId,
				diagnosesId : me.diagnosesId
			},
	     	callback: function (data) {
	     		
	     		if(data.diagnosesRelaDefectId){
					me.defectform = Ext.widget('defectformforview',{defectId:data.defectId,border : false});
					me.add(me.defectform);
					me.defectform.reloadData();
					me.clearUpFormdata = Ext.widget('defectclearupformforview');
					me.clearUpFormdata.basicinfofieldset.setTitle('缺陷整理');
					me.add(me.clearUpFormdata);
					me.clearUpFormdata.initParam({
						defectId : data.diagnosesRelaDefectId
					});
					me.clearUpFormdata.reloadData();
	     		}else{
	     			me.clearUpFormdata = Ext.widget('defectclearupformforview');
					me.clearUpFormdata.basicinfofieldset.setTitle('诊断明细');
					me.add(me.clearUpFormdata);
					me.clearUpFormdata.initParam({
						diagnosesId : me.diagnosesId
					});
					me.clearUpFormdata.reloadData();
	     		}
	         }
         });
    }
});