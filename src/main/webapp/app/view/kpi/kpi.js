/**
 * 首页目标对象钻取
 * @param id 目标ID
 */

function analyseStrategyMap(id) {
    var smname = "";
    FHD.ajax({
        async: false,
        url: __ctxPath + '/kpi/kpistrategymap/findparentbyid.f',
        params: {
            id: id
        },
        callback: function (data) {
            smname = data.name;
        }
    });
    var url = 'FHD.view.kpi.homepage.SmDetailAnalysis';
    var idurl = url+"_"+id;
    var centerPanel = Ext.getCmp('center-panel');
    var tab = centerPanel.getComponent(idurl);
    if(tab){
		centerPanel.setActiveTab(tab);
	}else{
		if(url.startWith('FHD')){
			var param = {
			        smid: id //目标ID
			    }
		    var smDetailAnalysis = Ext.create(url, {
		        closable: true,
		        id:idurl,
		        paramObj: param,
		        title: smname
		    });
		    var p = centerPanel.add(smDetailAnalysis);
		    centerPanel.setActiveTab(p);
		}
	}
    
}
/**
 * 首页记分卡对象钻取
 * @param id 记分卡ID
 */

function analyseScorecard(id) {
	var scname = "";
    FHD.ajax({
        async: false,
        url: __ctxPath + '/kpi/category/findparentbyid.f',
        params: {
            id: id
        },
        callback: function (data) {
        	scname = data.name;
        }
    });
 
	var url = 'FHD.view.kpi.homepage.ScDetailAnalysis';
    var idurl = url+"_"+id;
    var centerPanel = Ext.getCmp('center-panel');
    var tab = centerPanel.getComponent(idurl);
    if(tab){
		centerPanel.setActiveTab(tab);
	}else{
		if(url.startWith('FHD')){
			var param = {
			        scid: id //记分卡ID
			    }
		    var scDetailAnalysis = Ext.create(url, {
		        closable: true,
		        id:idurl,
		        paramObj: param,
		        title: scname
		    });
		    var p = centerPanel.add(scDetailAnalysis);
		    centerPanel.setActiveTab(p);
		}
	}
}

/**
 * 首页指标对象钻取
 * @param id 指标ID
 */

function analyseKpi(id) {
	var kpiname = '';
	FHD.ajax({
        async: false,
        url: __ctxPath + '/kpi/kpi/findkpiobjectbyid.f',
        params: {
            kpiid: id
        },
        callback: function (data) {
        	if(data){
        		kpiname = data.kpiname;
        	}
        }
    });
	var idurl = url+"_"+id;
	var url = 'FHD.view.kpi.cmp.kpi.result.MainPanel';
	PARAM.kpiname = kpiname;
	PARAM.type = 'myfolder';
	PARAM.kgrid= '';
	PARAM.name = kpiname;
	PARAM.navId = id;
	PARAM.memoTitle = '';
	PARAM.treeId = '';
	PARAM.kpiid = id;
	var centerPanel = Ext.getCmp('center-panel');
    var tab = centerPanel.getComponent(idurl);
    if(tab){
		centerPanel.setActiveTab(tab);
	}else{
		if(url.startWith('FHD')){
		    		
			var kpipanel = Ext.create('FHD.view.kpi.cmp.kpi.result.MainPanel',{
				//id:idurl,
				//title:kpiname,
				//closable: true
    		});
    		kpipanel.load(PARAM);
			var kpipanelContainer = Ext.create('FHD.ux.CardPanel',{
				items:[kpipanel],
				title:kpiname,
				id:idurl,
				closable: true
			});
			
    		var p = centerPanel.add(kpipanelContainer);
			centerPanel.setActiveTab(p);
		}
	}
}