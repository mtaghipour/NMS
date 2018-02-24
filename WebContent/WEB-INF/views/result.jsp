<%@ page import="org.snmp4j.smi.OID" %>
<%@ page import="com.sazgan.helperclasses.SnmpConfig" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="org.snmp4j.event.ResponseListener" %>
<%@ page import="org.snmp4j.event.ResponseEvent" %>
<%@ page import="org.snmp4j.Snmp" %>
<%@ page import="org.snmp4j.PDU" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <title>Get Results</title>
</head>
<body>

Request : ${snmpConfig.request}
<br><br>
Response : ${snmpConfig.response}

</body>
</html>
