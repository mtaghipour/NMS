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

<%
    Logger logger = Logger.getLogger(this.getClass().getName());

    SnmpConfig snmpConfig = (SnmpConfig) request.getAttribute("snmpConfig");

    ResponseListener listener = new ResponseListener() {
        public void onResponse(ResponseEvent event) {

            /*
              Always cancel async request when response has been received
              otherwise a memory leak is created! Not canceling a request
              immediately can be useful when sending a request to a broadcast
              address.
            */
            ((Snmp) event.getSource()).cancel(event.getRequest(), this);

            PDU response = event.getResponse();
            PDU request = event.getRequest();

            if (response == null){
                System.out.println("Request " + request + " timed out");
            }else{
                snmpConfig.setRequest(request.toString());
                snmpConfig.setResponse(response.toString());
                System.out.println("Received response " + response + " on request " + request);
            }
        }
    };

    //String snmpGet = snmpConfig.getAsString(new OID("1.3.6.1.2.1.1.1.0"));
    snmpConfig.getAsStringAsync(new OID("1.3.6.1.2.1.1.1.0"), listener);

    //logger.info("In result.jsp file : " + snmpGet);

    //snmpConfig.setResponse(snmpGet);
    snmpConfig.stop();
%>

Request : ${snmpConfig.request}
Response : ${snmpConfig.response}

</body>
</html>
