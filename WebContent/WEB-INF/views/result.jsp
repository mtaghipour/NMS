<%@ page import="org.snmp4j.smi.OID" %>
<%@ page import="com.sazgan.helperclasses.SnmpConfig" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <title>Get Results</title>
</head>
<body>

<%
    Logger logger=Logger.getLogger (this.getClass ().getName ());

    SnmpConfig snmpConfig=(SnmpConfig) request.getAttribute ("snmpConfig");
    String snmpGet=snmpConfig.getAsString (new OID ("1.3.6.1.2.1.1.1.0"));
    logger.info ("In result.jsp file : " + snmpGet);

    snmpConfig.setResponse (snmpGet);

    snmpConfig.stop ();
%>

Get value : ${snmpConfig.response}

</body>
</html>
