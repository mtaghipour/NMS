package com.sazgan.controllers;

import com.sazgan.helperclasses.SnmpConfig;
import org.apache.log4j.Logger;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.smi.OID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * SnmpController uses SnmpConfig class to get parameters and
 * return the desired value on web page.
 * @author Maziar
 */
@Controller
@RequestMapping("/nms")
public class SnmpController {
    /**
     * This is a logger4j object to manage logs in the class.
     */
    private Logger logger = Logger.getLogger(this.getClass().getName());
    /**
     * Initializing SNMP SnmpConfig class for specified
     * ip and port and protocol.
     */
    final private SnmpConfig snmpConfig = new SnmpConfig("udp:127.0.0.1/161");
    /**
     * @param model ..... Model to handle SnmpConfig attribute.
     * @return ..... Transfer us to result jps file.
     */
    @RequestMapping("/getDemo")
    public String configSnmp(final Model model) {
        logger.info("configSnmp() ...");
        ResponseListener listener = new ResponseListener() {
            public void onResponse(final ResponseEvent event) {

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
                    snmpConfig.setRequest("Time out (Request)");
                    snmpConfig.setResponse("Time out (Response)");
                } else{
                    snmpConfig.setRequest(request.toString());
                    snmpConfig.setResponse(response.toString());
                }
            }
        };
        //String snmpGet = snmpConfig.getAsString(new OID("1.3.6.1.2.1.1.1.0"));
        //logger.info("In result.jsp file : " + snmpGet);
        snmpConfig.getAsStringAsync(new OID("1.3.6.1.2.1.1.1.0"), listener);
       /* try{
            snmpConfig.stop();
        }catch (IOException e){
            e.printStackTrace();
        }*/
        model.addAttribute("snmpConfig", snmpConfig);
        return "result";
    }
}
