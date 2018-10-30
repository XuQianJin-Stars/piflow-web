package com.nature.third.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nature.base.util.HttpUtils;
import com.nature.base.util.LoggerUtil;
import com.nature.common.constant.SysParamsCache;
import com.nature.component.workFlow.model.FlowInfoDb;
import com.nature.mapper.FlowInfoDbMapper;
import com.nature.third.inf.IGetFlowInfo;
import com.nature.third.inf.IGetFlowProgress;
import com.nature.third.vo.flowInfo.FlowInfo;
import com.nature.third.vo.flowInfo.FlowInfoStopVo;

@Component
public class GetFlowInfoImpl implements IGetFlowInfo {

	Logger logger = LoggerUtil.getLogger();
	
	@Autowired
	private FlowInfoDbMapper flowInfoDbMapper;
	@Autowired
	private IGetFlowProgress iGetFlowProgress;
	

	/**
	 * @Title 发送 post请求
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public FlowInfo getFlowInfo(String appid) {
		FlowInfo jb = null;
		Map<String, String> map = new HashMap<String, String>();
		map.put("appID", appid);
		String doGet = HttpUtils.doGet(SysParamsCache.FLOW_INFO_URL(), map);
		if (StringUtils.isNotBlank(doGet)) {
			// 同样先将json字符串转换为json对象，再将json对象转换为java对象，如下所示。
			JSONObject obj = JSONObject.fromObject(doGet);// 将json字符串转换为json对象
			// 当FlowInfo中有List时需要
			Map<String, Class> classMap = new HashMap<String, Class>();
			// key为FlowInfo中的List的name,value为list的泛型的class
			classMap.put("stops", FlowInfoStopVo.class);
			// 将json对象转换为java对象
			jb = (FlowInfo) JSONObject.toBean(obj, FlowInfo.class, classMap);
		}
		return jb;
	}

	/**
	 * 通过appId调接口返回flowInfo并保存数据库
	 */
	@Override
	public FlowInfoDb AddFlowInfo(String appId) {
		FlowInfo startFlow2 = getFlowInfo(appId);
		String progress = iGetFlowProgress.getFlowInfo(appId);
		logger.info("测试返回信息：" + startFlow2);
		if ( null != startFlow2 && null!= startFlow2.getFlow()) {
			FlowInfoDb flowInfoDb = flowInfoDbMapper.flowInfoDb(startFlow2.getFlow().getId());
			//如果数据库不为空则更新接口返回来的数据
			if (null != flowInfoDb) {
				FlowInfoDb up = new FlowInfoDb();
				up.setId(flowInfoDb.getId());
				up.setName(startFlow2.getFlow().getName());
				up.setState(startFlow2.getFlow().getState());
				up.setEndTime(startFlow2.getFlow().getEndTime());
				up.setStartTime(startFlow2.getFlow().getStartTime());
				up.setProgress(progress);
				up.setLastUpdateUser("王栋栋");
				up.setLastUpdateDttm(new Date());
				int updateFlowInfo = flowInfoDbMapper.updateFlowInfo(up);
				if (updateFlowInfo > 0) {
					return up;
				} 
			}else{
				//接口返回不为空的话,数据库保存
				FlowInfoDb add = new FlowInfoDb();
				add.setId(startFlow2.getFlow().getId());
				add.setName(startFlow2.getFlow().getName());
				add.setState(startFlow2.getFlow().getState());
				add.setEndTime(startFlow2.getFlow().getEndTime());
				add.setStartTime(startFlow2.getFlow().getStartTime());
				add.setProgress(progress);
				add.setCrtDttm(new Date());
				add.setCrtUser("wdd");
				add.setVersion(0L);
				add.setEnableFlag(true);
				add.setLastUpdateUser("王栋栋");
				add.setLastUpdateDttm(new Date());
				int addFlowInfo = flowInfoDbMapper.addFlowInfo(add);
				if (addFlowInfo > 0) {
					return add;
				} 
			}
		}
			FlowInfoDb kong = new FlowInfoDb();
			//flowInfo接口返回为空的情况
			kong = new FlowInfoDb();
			kong.setId(appId);
			kong.setCrtDttm(new Date());
			kong.setCrtUser("wdd");
			kong.setVersion(0L);
			kong.setEnableFlag(true);
			kong.setLastUpdateUser("王栋栋");
			kong.setLastUpdateDttm(new Date());
			return kong;
	}

}
