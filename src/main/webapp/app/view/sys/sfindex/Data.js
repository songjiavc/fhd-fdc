Data={
	// Basic Data Model definition
	dataModel: [
            {id:"1", stt:"1", col:"0", row:"0", leaf: true, text: "我的待办", comp:"FHD.view.sys.sfindex.MyTask"},//text与portalPanel中页面title对应；id与portalPanel中item名称对应
            {id:"2", stt:"1", col:"0", row:"0", leaf: true, text: "风险监控报表", comp:"FHD.view.sys.sfindex.RiskKpiReportMainPanel"},
            {id:"3", stt:"1", col:"0", row:"0", leaf: true, text: "部门指标", comp:"FHD.view.sys.sfindex.MyKpiGrid"},
            {id:"4", stt:"1", col:"0", row:"0", leaf: true, text: "我的风险", comp:"FHD.view.sys.sfindex.IRiskEventGrid"},
            {id:"5", stt:"1", col:"0", row:"0", leaf: true, text: "我的待办2", comp:"FHD.ux.GridPanel"},//text与portalPanel中页面title对应；id与portalPanel中item名称对应
            {id:"6", stt:"1", col:"0", row:"0", leaf: true, text: "公司重大风险2", comp:"FHD.ux.GridPanel"},
            {id:"7", stt:"1", col:"0", row:"0", leaf: true, text: "监控预警2", comp:"FHD.ux.GridPanel"},
            {id:"8", stt:"1", col:"0", row:"0", leaf: true, text: "我的风险2", comp:"FHD.ux.GridPanel"}
        ],

	initDataModelSTT : function() {
		for (var x=0; x<this.dataModel.length; x++) {
			this.dataModel[x].stt = "1";
		}
	},
	setDataModelSTT : function(fid) {
		for (var x=0; x<this.dataModel.length; x++) {
			if (fid == this.dataModel[x].id) {
				this.dataModel[x].stt = "0";
				break;
			}
		}
	},
	tmpSortArray : [[0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0]],
	initTmpSortArray : function() {
		for (var x=0; x<3; x++) {
			for (var y=0; y<9; y++) {
				this.tmpSortArray[x][y] = 0;
			}
		}
	},
	// NO NEED
	portalModel:[{
            id: "col-1",
            flex: 1,
            items: []
        },{
            id: "col-2",
            flex: 1,
            items: []
        },{
            id: "col-3",
            flex: 1,
            items: []
        }],
	cookieData:[
		[],
		[],
		[]
	],
	initCookieData: function(){
		for(var i=0;i<this.cookieData.length;i++){
			var cd = this.cookieData[i];
			cd.splice(0,cd.length);
		}
	},
	initPortalModelItems : function() {
		for (var x=0; x<this.portalModel.length; x++) {
			this.portalModel[x].items = {};
		}
	},

	makeTreeNodes:function(){ // Directorily
		return this.dataModel;
	},
	makePortalItems:function(){ // return [{id,flex,items[{},..]},..]
		this.initTmpSortArray();
		for (var ix=0;ix<this.dataModel.length;ix++) {
			var dt=this.dataModel[ix];
			if (dt.stt=="0") { // 0-被使用，不可用； 1-未使用，可用	
				var c = dt.col;
				var r = dt.row;
				this.tmpSortArray[c][r] = dt.id;
			}
		} // EOF this.dataModel
		
		for(var c=0; c<3; c++) {
			for(var r=0; r<9; r++) {
				var id = this.tmpSortArray[c][r];
				if (id > "0") {
					for(var m=0;m<this.dataModel.length;m++) {
						var dm=this.dataModel[m];
						if (dm.id == id) {
							this.portalModel[c].items[r] = {"title":dm.text, "comp":dm.comp, "tools":"", "items":"", "listeners":"","id":dm.id  };
							break;
						}
					}
				}
			} // EOF row
		} // EOF col
	},
	updateDataModel:function(summary) { // summary=[{id,col,row},..]
		
	},
	getComp:function(fid) { 
		var comp="";
		for(var i=0;i<this.dataModel.length;i++){
			var dm=this.dataModel[i];
			if(fid==dm.id){
				comp=dm.comp;
				break;
			}
		}
		return comp;
	},
	checkStatusById:function(fid){
		var stt = "1";
		for(var i=0;i<this.dataModel.length;i++){
			var dm=this.dataModel[i];
			if(dm.id==fid){
				stt = dm.stt;
				break;
			}
		}
		return stt;
	},

	loadDataFromCookie:function(){
		// load data from cookie and save to dataModel。
		// format of cookie-data =[{id,col,row},..]
		var fromCookieData = this.loadCookieData();
		var items="";
		if(""==fromCookieData){
			//items=[[{id:"1", c:0, r:0},{id:"2", c:0, r:1}],[{id:"3", c:1, r:0}],[{id:"4", c:2, r:0},{id:"5", c:2, r:1},{id:"6", c:2, r:2},{id:"7", c:2, r:3},{id:"8", c:2, r:4}]];
			items=[[{id:"1"},{id:"2"}],[{id:"3"}],[{id:"4"},{id:"5"},{id:"6"},{id:"7"},{id:"8"}]];

		}
		else{
			items=Ext.decode(fromCookieData);
		}

		for( var x=0;x<items.length;x++ ) {
			var it=items[x];
			for(var m=0;m<it.length;m++){
				for (var y=0; y<this.dataModel.length; y++) {
					var dt =this.dataModel[y];
					if (dt.id == it[m].id) {
						dt.stt = 0; // 0-被使用，不可用； 1-未使用，可用
						dt.row = m;
						dt.col = x;
						break;
					}
				}
			} // EOF this.dataModel
		} // EOF items
	},
	saveDataToCookie:function() { 
		//debugger;
		 var empId= __user.empId;
		 var str=Ext.encode(this.cookieData);
        //设置cookie 为永久有效
        document.cookie=empId+ "=" +escape(str)+"; expires=Fri, 31 Dec 9999 23:59:59 GMT";
	},
	loadCookieData: function(){

        var empId= __user.empId;

        if (document.cookie.length>0)
        {
            c_start=document.cookie.indexOf(empId + "=");

            if (c_start!=-1){

                c_start=c_start + empId.length+1 ;

                c_end=document.cookie.indexOf(";",c_start);

                if (c_end==-1){

                    c_end=document.cookie.length;
                }
                return unescape(document.cookie.substring(c_start,c_end));
            }
        }
        return "";
	}
	
}