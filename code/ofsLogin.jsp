<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ page import="weaver.integration.logging.Logger"%>
<%@ page import="weaver.integration.logging.LoggerFactory"%>
<%@page import="weaver.hrm.User"%>
<%@ page import="java.util.Map" %>
<%@ page import="weaver.file.Prop" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="weaver.integration.util.SessionUtil" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="com.alibaba.fastjson.JSON" %>
<%@ page import="com.engine.common.biz.EncryptConfigBiz" %>
<%@ page import="weaver.file.AESCoder" %>
<jsp:useBean id="rs" class="weaver.conn.RecordSet" scope="page" />
<%

	String toURL = "";

	Logger log = LoggerFactory.getLogger();
	String syscode = request.getParameter("syscode");
	String receiver = request.getParameter("receiver");
	String timestamp = request.getParameter("timestamp");
	String loginTokenFromThird = request.getParameter("loginTokenFromThird");
	String gopage = URLDecoder.decode(request.getParameter("gopage"),"utf-8");


	String loginTokenFromThird2 =  AESCoder.encrypt(receiver+timestamp, syscode+Prop.getPropValue("transferE9","secretkey"));

	log.info("=======================登陆认证开始=================================");
	log.info("syscode："+syscode);
	log.info("receiver："+receiver);
	log.info("timestamp："+timestamp);
	log.info("loginTokenFromThird ："+loginTokenFromThird);
	log.info("gopage："+gopage);
	log.info("loginTokenFromThird2："+loginTokenFromThird2);

	if(!loginTokenFromThird.equalsIgnoreCase(loginTokenFromThird2)){
		log.info("登陆失败！");
		 //response.sendRedirect("/login/Login.jsp");
		toURL = "/login/Login.jsp";

	}else {
		rs.executeQuery("select hrmtransrule from ofs_sendinfo where syscode = ?",syscode);
		rs.next();
		String hrmtransrule = rs.getString("hrmtransrule");
		if(StringUtils.isBlank(hrmtransrule)){
			hrmtransrule = "1";
		}
		log.info("hrmtransrule:"+hrmtransrule);


		User user_new = null;
		String rule = "loginid";
		if("0".equals(hrmtransrule)){
			rule = "id";
		}else if("1".equals(hrmtransrule)){
			rule = "loginid";
		}else if("2".equals(hrmtransrule)){
			rule = "workcode";
		}else if("3".equals(hrmtransrule)){
			rule = "certificatenum";
		}else if("4".equals(hrmtransrule)){
			rule = "email";
		}
		String sql = "select * from HrmResource where "+rule+" = ? and status < 4 ";
		log.info("sql："+sql);
		rs.executeQuery(sql,receiver);
		if(rs.next()){
			int userId = rs.getInt("id");
			Map<String, Object> result = (Map<String, Object>)SessionUtil.createSession(userId + "", request, response);
			log.info("登陆结果result:"+result);

			User user = (User) request.getSession(true).getAttribute("weaver_user@bean");
			log.info("登陆结果user:"+ JSON.toJSONString(user));

			String status = (String)result.get("status");
			log.info("=======================登陆认证结束=================================");
			if("1".equals(status)){
				//response.sendRedirect(gopage);
				//return;
				toURL = gopage;
			}else {
				toURL = "/login/Login.jsp";
			}
		}else{

			// 可能是数据库密文加密，调用人力接口，通过明文获取密文
			String userId = EncryptConfigBiz.getResourceIdByFieldValue(receiver, rule);

			if(StringUtils.isBlank(userId)){
				//跳转到错误页面
				//response.sendRedirect("/login/Login.jsp");
				//return;
				toURL = "/login/Login.jsp";

			}else {
				Map<String, Object> result = (Map<String, Object>)SessionUtil.createSession(userId + "", request, response);
				log.info("邮件加密登陆结果result:"+result);

				User user = (User) request.getSession(true).getAttribute("weaver_user@bean");
				log.info("邮件加密登陆结果user:"+ JSON.toJSONString(user));

				String status = (String)result.get("status");
				log.info("=======================登陆认证结束=================================");
				if("1".equals(status)){
					//response.sendRedirect(gopage);
					//return;
					toURL = gopage;
				}else {
					toURL = "/login/Login.jsp";
				}
			}

		}
	}

%>

<script type="text/javascript">

	location.replace('<%=toURL%>');

</script>
