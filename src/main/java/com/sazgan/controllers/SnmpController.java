package com.sazgan.controllers;

import com.sazgan.helperclasses.SnmpConfig;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/nms")
public class SnmpController{
    
    private Logger logger=Logger.getLogger(this.getClass().getName());
    
    @RequestMapping("/getDemo")
    public String configSnmp(Model model){
        
        logger.info("in configSnmp() method");
        
        SnmpConfig snmpConfig=new SnmpConfig("udp:127.0.0.1/161");
        model.addAttribute("snmpConfig", snmpConfig);
        
        return "result";
        
    }
}
