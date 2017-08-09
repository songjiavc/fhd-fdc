-- @date     2017-6-6 11hour
-- @author  jia.song@pcitc.com
-- @desc     delete t_rm_risks risk_socre columns

ALTER TABLE `ermis45_demo`.`t_rm_risks` DROP COLUMN `RISK_SOCRE`;
-- @date 2017--6-8 增加过程记录风险分类综合得分
-- @author jia.song@pcitc.com
CREATE TABLE `temp_sync_riskrbs_score` (
  `RISK_ID` varchar(255) NOT NULL,
  `RISK_SCORE` double DEFAULT NULL,
  `ASSESS_PLAN_ID` varchar(45) NOT NULL,
  PRIMARY KEY (`RISK_ID`,`ASSESS_PLAN_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- @date 2017-06-09 
-- @author jia.song@pcitc.com
-- @desc t_rm_risks add columns ASSESS_PLAN_ID 
ALTER TABLE `ermis45_demo`.`t_rm_risks` 
ADD COLUMN `ASSESS_PLAN_ID` VARCHAR(45) NULL AFTER `RISK_SCORE`;


--@date     2017-06-09
--@author  jia.song@pcitc.com
--@desc     

CREATE TABLE `ermis45_demo`.`t_rm_risks_dimension_value` (
  `ID` VARCHAR(45) NOT NULL,
  `RISK_ID` VARCHAR(45) NULL,
  `SCORE_DIM_ID` VARCHAR(45) NULL,
  `SCORE_DIM_VALUE` VARCHAR(45) NULL,
  `SCORE` DOUBLE NULL,
  PRIMARY KEY (`ID`));
  
  
  
  --@date 		2017-7-10
  --@author 	jia.song@pcitc.com
  --@desc   	业务表，提供给考核追溯管理机关和人员使用
  
  CREATE TABLE `ermis45_demo`.`t_rm_check_bussiness` (
  `ID` VARCHAR(44) NOT NULL,
  `NAME` VARCHAR(45) NULL COMMENT '业务名称数据字典目前提供给考核使用',
  PRIMARY KEY (`ID`));

  
  --@date 		2017-7-10
  --@author 	jia.song@pcitc.com
  --@desc   	针对于业务的人员机关管理关系表
  
  CREATE TABLE `t_rm_check_bussiness_org_rela_emp` (
  `ID` varchar(45) NOT NULL,
  `MANAGE_ORG_ID` varchar(45) DEFAULT NULL COMMENT '管理机关id',
  `MANAGED_ORG_ID` varchar(45) DEFAULT NULL COMMENT '被管理机关id',
  `BUSSINESS_ID` varchar(45) DEFAULT NULL COMMENT '跟业务对应的机关和人员对应关系',
  `EMP_ID` varchar(45) DEFAULT NULL COMMENT '管理人员id',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


	--'0db55169-8ef8-4894-8bf1-bd7ef92cba9d', 'ALL', 'ALL_CHECK', '督办考核', NULL, 'M', '8', '2', '.ALL.0db55169-8ef8-4894-8bf1-bd7ef92cba9d.', '0', NULL
	-- '7226ec29-94da-4ca1-a9b7-21a9d16b2c34', '0db55169-8ef8-4894-8bf1-bd7ef92cba9d', 'ALL_CHECK_CONFIG', '基础配置', 'FHD.view.compoments.orgrelaleaderorgandemp.OrgRelaLeaderMainPanel', 'M', '1', '3', '.ALL.0db55169-8ef8-4894-8bf1-bd7ef92cba9d.7226ec29-94da-4ca1-a9b7-21a9d16b2c34.', '1', NULL
	-- 修改风险库字段   增加风险应对内容字段存放应对信息
	
	ALTER TABLE `ermis45_demo`.`t_rm_risks` 
	CHANGE COLUMN `RESERVED8` `RESPONSE_CONTENT` TEXT NULL DEFAULT NULL ;
	
	
  --@date 		2017-7-20
  --@author 	Perry Guo
  --@desc   	考评项目管理表
  
  CREATE TABLE `t_rm_check_rule_project` (
  `ID` varchar(100) NOT NULL DEFAULT '' COMMENT '考评项目ID',
  `NAME` varchar(255) DEFAULT NULL COMMENT '考评项目名称',
  `TOTAL_SCORE` int(11) DEFAULT NULL COMMENT '总分',
  `PROJECT_ORDER` int(11) DEFAULT NULL COMMENT '排序',
  `IS_USERD` char(1) DEFAULT '0' COMMENT '是否启用（0未启用，1启用）默认为零',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


  --@date 		2017-7-20
  --@author 	Perry Guo
  --@desc   	考评项目管理表
  --@url   考评项目管理  FHD.view.check.rule.CheckProjectMainPanel

  CREATE TABLE `t_rm_check_rule_project` (
  `ID` varchar(100) NOT NULL DEFAULT '' COMMENT '考评项目ID',
  `NAME` varchar(255) DEFAULT NULL COMMENT '考评项目名称',
  `TOTAL_SCORE` int(11) DEFAULT NULL COMMENT '总分',
  `PROJECT_ORDER` int(11) DEFAULT NULL COMMENT '排序',
  `IS_USERD` char(1) DEFAULT '0' COMMENT '是否启用（0未启用，1启用）默认为零',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--@date 		2017-7-20
  --@author 	Perry Guo
  --@desc   	考评内容管理表
  --@url    考核内容管理  FHD.view.check.rule.CheckCommentMainPanel

 CREATE TABLE `t_rm_check_rule_comment` (
  `ID` varchar(100) NOT NULL DEFAULT '' COMMENT '考核内容ID',
  `NAME` varchar(255) DEFAULT NULL COMMENT '考核内容名称',
  `PROJECT_ID` varchar(100) DEFAULT NULL COMMENT '考评项目ID',
  `COMMENT_ORDER` int(11) DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



	-- 宋佳relation表中增加字段emp_id
	-- date 2017-7-27  11:23
	ALTER TABLE `ermis45_demo`.`t_rm_risk_org_relation_temp` 
	ADD COLUMN `EMP_ID` VARCHAR(100) NULL DEFAULT NULL AFTER `RISK_ID`;

--@date 		2017-7-20
  --@author 	Perry Guo
  --@desc   	考评细则管理表
  --@url    考核内容管理  FHD.view.check.rule.CheckDetailMainPanel


DROP TABLE IF EXISTS `t_rm_check_rule_detail`;
CREATE TABLE `t_rm_check_rule_detail` (
  `ID` varchar(100) NOT NULL COMMENT '评分细则ID',
  `NAME` varchar(100) DEFAULT NULL COMMENT '评分细则名称',
  `DETAIL_STANDARD` varchar(3000) DEFAULT NULL COMMENT '评分标准',
  `DETAIL_SCORE` int(11) DEFAULT NULL COMMENT '分值',
  `COMMENT_ID` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;




	-- @date  2017年8月3日17:20:54
  	-- @author 	吉志强
  	-- @desc   	重大风险应对相关表   begin
-- ----------------------------
-- Table structure for `t_rm_response_counter`
-- ----------------------------
DROP TABLE IF EXISTS `t_rm_response_counter`;
CREATE TABLE `t_rm_response_counter` (
  `ID` varchar(64) NOT NULL,
  `DESCRIPTION` text COMMENT '措施描述',
  `START_TIME` timestamp NULL DEFAULT NULL COMMENT '实施时间',
  `FINISH_TIME` timestamp NULL DEFAULT NULL COMMENT '完成时间',
  `TARGET` text COMMENT '管理目标',
  `COMPLETE_SIGN` text COMMENT '完成标志',
  `TYPE` varchar(2) DEFAULT NULL COMMENT '1：汇总 0：非汇总',
  `CREATE_USER` varchar(64) DEFAULT NULL COMMENT '创建人ID',
  `CREATE_ORG` varchar(64) DEFAULT NULL COMMENT '创建部门ID',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_rm_response_counter
-- ----------------------------

-- ----------------------------
-- Table structure for `t_rm_response_counter_exe`
-- ----------------------------
DROP TABLE IF EXISTS `t_rm_response_counter_exe`;
CREATE TABLE `t_rm_response_counter_exe` (
  `ID` varchar(64) NOT NULL,
  `ITEM_EXECUTE_ID` varchar(64) DEFAULT NULL COMMENT '风险事项执行ID',
  `COUNTER_ID` varchar(64) DEFAULT NULL COMMENT '措施ID',
  `EXE_USER` varchar(64) DEFAULT NULL COMMENT '执行人',
  `EXE_ORG` varchar(64) DEFAULT NULL COMMENT '执行部门',
  `TYPE` varchar(2) DEFAULT NULL COMMENT '1：汇总 0：非汇总',
  `START_TIME` timestamp NULL DEFAULT NULL COMMENT '开始实施时间',
  `FINISH_TIME` timestamp NULL DEFAULT NULL COMMENT '完成时间',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_rm_response_counter_exe
-- ----------------------------

-- ----------------------------
-- Table structure for `t_rm_response_execute`
-- ----------------------------
DROP TABLE IF EXISTS `t_rm_response_execute`;
CREATE TABLE `t_rm_response_execute` (
  `ID` varchar(64) NOT NULL,
  `SCHEME_ID` varchar(64) DEFAULT NULL COMMENT '方案模板ID',
  `CREATE_USER` varchar(64) DEFAULT NULL COMMENT '创建人ID',
  `CREATE_ORG` varchar(64) DEFAULT NULL COMMENT '创建部门ID',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_rm_response_execute
-- ----------------------------

-- ----------------------------
-- Table structure for `t_rm_response_item`
-- ----------------------------
DROP TABLE IF EXISTS `t_rm_response_item`;
CREATE TABLE `t_rm_response_item` (
  `ID` varchar(64) NOT NULL,
  `DESCRIPTION` text COMMENT '风险事项描述',
  `FLOW` text COMMENT '相关流程',
  `REASON` text COMMENT '产生动因',
  `TYPE` varchar(2) DEFAULT NULL COMMENT '1：汇总 0：非汇总',
  `CREATE_USER` varchar(64) DEFAULT NULL COMMENT '创建人ID',
  `CREATE_ORG` varchar(64) DEFAULT NULL COMMENT '创建部门ID',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_rm_response_item
-- ----------------------------

-- ----------------------------
-- Table structure for `t_rm_response_item_counter_rela`
-- ----------------------------
DROP TABLE IF EXISTS `t_rm_response_item_counter_rela`;
CREATE TABLE `t_rm_response_item_counter_rela` (
  `ID` varchar(64) NOT NULL,
  `ITEM_ID` varchar(64) DEFAULT NULL COMMENT '风险事项ID',
  `COUNTER_ID` varchar(64) DEFAULT NULL COMMENT '措施ID',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_rm_response_item_counter_rela
-- ----------------------------

-- ----------------------------
-- Table structure for `t_rm_response_item_exe`
-- ----------------------------
DROP TABLE IF EXISTS `t_rm_response_item_exe`;
CREATE TABLE `t_rm_response_item_exe` (
  `ID` varchar(64) NOT NULL,
  `EXECUTE_ID` varchar(64) DEFAULT NULL COMMENT '方案执行ID',
  `ITEM_ID` varchar(64) DEFAULT NULL COMMENT '风险事项ID',
  `EXE_USER` varchar(64) DEFAULT NULL COMMENT '执行人',
  `EXE_ORG` varchar(64) DEFAULT NULL COMMENT '执行部门',
  `TYPE` varchar(2) DEFAULT NULL COMMENT '1：汇总 0：非汇总',
  `DESCRIPTION` text COMMENT '本阶段工作情况说明',
  `PRE_RESULT` text COMMENT '初步成效',
  `REQUESTS` text COMMENT '主要问题及难点',
  `SUGGEST` text COMMENT '调整建议',
  `PLAN` text COMMENT '下阶段工作计划',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_rm_response_item_exe
-- ----------------------------

-- ----------------------------
-- Table structure for `t_rm_response_plan_risk_rela`
-- ----------------------------
DROP TABLE IF EXISTS `t_rm_response_plan_risk_rela`;
CREATE TABLE `t_rm_response_plan_risk_rela` (
  `ID` varchar(64) NOT NULL,
  `PLAN_ID` varchar(64) DEFAULT NULL,
  `RISK_ID` varchar(64) DEFAULT NULL,
  `DEPT_ID` varchar(64) DEFAULT NULL,
  `DEPT_TYPE` varchar(2) DEFAULT NULL,
  `UPDATE_USER` varchar(64) DEFAULT NULL,
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



-- ----------------------------
-- Table structure for `t_rm_response_risk_scheme_rela`
-- ----------------------------
DROP TABLE IF EXISTS `t_rm_response_risk_scheme_rela`;
CREATE TABLE `t_rm_response_risk_scheme_rela` (
  `ID` varchar(64) NOT NULL,
  `TASK_EXECUTION_ID` varchar(64) DEFAULT NULL COMMENT '重大风险ID',
  `SCHEME_ID` varchar(64) DEFAULT NULL COMMENT '方案模板ID',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_rm_response_risk_scheme_rela
-- ----------------------------

-- ----------------------------
-- Table structure for `t_rm_response_scheme`
-- ----------------------------
DROP TABLE IF EXISTS `t_rm_response_scheme`;
CREATE TABLE `t_rm_response_scheme` (
  `ID` varchar(64) NOT NULL,
  `DESCRIPTION` text COMMENT '风险简述',
  `ANALYSIS` text COMMENT '风险分析',
  `TARGET` text COMMENT '管理目标',
  `STRATEGY` text COMMENT '应对策略',
  `START_TIME` timestamp NULL DEFAULT NULL COMMENT '实施时间',
  `FINISH_TIME` timestamp NULL DEFAULT NULL COMMENT '完成时间',
  `TYPE` varchar(2) DEFAULT NULL COMMENT '1：汇总 0：非汇总',
  `CREATE_USER` varchar(64) DEFAULT NULL COMMENT '创建人ID',
  `CREATE_ORG` varchar(64) DEFAULT NULL COMMENT '创建部门ID',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



-- ----------------------------
-- Table structure for `t_rm_response_scheme_item_rela`
-- ----------------------------
DROP TABLE IF EXISTS `t_rm_response_scheme_item_rela`;
CREATE TABLE `t_rm_response_scheme_item_rela` (
  `ID` varchar(64) NOT NULL,
  `SCHEME_ID` varchar(64) DEFAULT NULL COMMENT '方案模板ID',
  `ITEM_ID` varchar(64) DEFAULT NULL COMMENT '风险事项ID',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_rm_response_scheme_item_rela
-- ----------------------------

-- ----------------------------
-- Table structure for `t_rm_response_task_execution`
-- ----------------------------
DROP TABLE IF EXISTS `t_rm_response_task_execution`;
CREATE TABLE `t_rm_response_task_execution` (
  `ID` varchar(64) NOT NULL,
  `PLAN_RISK_RELA_ID` varchar(64) DEFAULT NULL,
  `EMP_ID` varchar(64) DEFAULT NULL,
  `EMP_TYPE` varchar(2) DEFAULT NULL COMMENT '1:普通员工 2：汇总人员',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

	-- @date  2017年8月3日17:20:54
  	-- @author 	吉志强
  	-- @desc   	重大风险应对相关表  end


	-- @date  2017年8月3日17:20:54
  	-- @author 	郭鹏
  	-- @desc   	年度考核计划表       菜单位置：FHD.view.check.yearcheck.plan.YearCheckMainPanel
  	
-- ----------------------------
-- Table structure for `t_rm_check_year_plan`
-- ----------------------------
DROP TABLE IF EXISTS `t_rm_check_year_plan`;
CREATE TABLE `t_rm_check_year_plan` (
  `ID` varchar(100) NOT NULL COMMENT '计划ID',
  `NAME` varchar(200) DEFAULT NULL COMMENT '计划名称',
  `BEGIN_DATE` datetime DEFAULT NULL COMMENT '计划开始时间',
  `END_DATE` datetime DEFAULT NULL COMMENT '计划结束时间',
  `CONTACT_PERSON` varchar(100) DEFAULT NULL COMMENT '联系人',
  `RESPONSIBLE_PERSON` varchar(100) DEFAULT NULL COMMENT '负责人',
  `STATUS` varchar(2) DEFAULT NULL COMMENT '计划状态',
  `CREATTIME` datetime DEFAULT NULL COMMENT '计划创建时间',
  `CREATE_ORG` varchar(100) DEFAULT NULL COMMENT '计划创建部门',
  `CREATE_EMP` varchar(100) DEFAULT NULL COMMENT '计划创建员工',
  `PLAN_CODE` varchar(255) DEFAULT NULL COMMENT '计划编号',
  `DEAL_STATUS` varchar(2) DEFAULT NULL COMMENT '计划处理状态',
  `WORK_TARG` varchar(2000) DEFAULT NULL COMMENT '工作目标',
  `RANGE_REQUIRE` varchar(2000) DEFAULT NULL COMMENT '范围要求',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------

	-- @date  2017年8月3日17:20:54
  	-- @author 	郭鹏
  	-- @desc   	年度考核计划表部门关系表
-- ----------------------------
-- Table structure for `t_rm_check_year_plan_org`
-- ----------------------------
DROP TABLE IF EXISTS `t_rm_check_year_plan_org`;
CREATE TABLE `t_rm_check_year_plan_org` (
  `ID` varchar(100) NOT NULL COMMENT 'ID',
  `PLAN_ID` varchar(100) DEFAULT NULL COMMENT '年度计划表ID',
  `ORG_ID` varchar(100) DEFAULT NULL COMMENT '被管理部门ID',
  `EMP_ID` varchar(100) DEFAULT NULL COMMENT '负责人ID',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------

	-- @date  2017年8月3日17:20:54
  	-- @author 	郭鹏
  	-- @desc   	年度考核得分部门表
-- ----------------------------
-- ----------------------------
-- Table structure for `t_rm_check_year_score_org`
-- ----------------------------
DROP TABLE IF EXISTS `t_rm_check_year_score_org`;
CREATE TABLE `t_rm_check_year_score_org` (
  `ID` varchar(100) NOT NULL COMMENT '主键ID',
  `OWEN_SCORE` int(11) DEFAULT NULL COMMENT '自评得分',
  `PLAN_ORG_ID` varchar(100) DEFAULT NULL COMMENT '部门关系表ID',
  `CHECK_PROJECT_NAME` varchar(200) DEFAULT NULL COMMENT '考评项目名称',
  `CHECK_PROJECT_SCORE` int(11) DEFAULT NULL COMMENT '考评项目标准分',
  `CHECK_COMMENT_NAME` varchar(200) DEFAULT NULL COMMENT '考核内容名称',
  `CHECK_DETAIL_NAME` varchar(200) DEFAULT NULL COMMENT '考评细则名称',
  `CHECK_DETAIL_DESCRIBE` varchar(2500) DEFAULT NULL COMMENT '考评标准',
  `CHECK_DETAIL_SCORE` int(11) DEFAULT NULL COMMENT '标准分',
  `CHECK_DETAIL_AUTO_SUB` int(11) DEFAULT '0' COMMENT '自动扣减分',
  `RISK_SCORE` int(11) DEFAULT NULL COMMENT '风险办得分',
  `AUDIT_SCORE` int(11) DEFAULT NULL COMMENT '审计处得分',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------