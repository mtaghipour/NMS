package com.sazgan.helperclasses;

import org.apache.log4j.Logger;
import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;

public class SnmpConfig {
    private Snmp snmp;
    private String address;
    private ResponseListener listener;
    private String request;
    private String response;
    private Logger logger = Logger.getLogger(this.getClass().getName());
    
    public void setResponse(String response) {
        this.response = response;
    }
    
    public String getResponse() {
        return response;
    }
    
    public String getRequest() {
        return request;
    }
    
    public void setRequest(String request) {
        this.request = request;
    }
    
    public SnmpConfig(String address) {
        
        this.address = address;
        
        try {
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void stop() throws IOException {
        snmp.close();
    }
    
    /*
     * Start the Snmp session. If you forget the listen() method you will not get any answers because under the hood
     * the communication is asynchronous and the listen() method listens for answers
     * */
    private void start() throws IOException {

        /*
        TransportMapping transport = new DefaultUdpTransportMapping ();
        snmp = new Snmp ( transport );
        
        transport.listen ();
        */
        
        snmp = new Snmp(new DefaultUdpTransportMapping());
        snmp.listen();
    }
    
    /*
     * To make this as simple as possible in my client I have a simple method which takes a single OID and
     * returns the response from the agent as a String.
     * */
    public String getAsString(OID oid) throws IOException {
        
        ResponseEvent event = get(new OID[]{oid});
        return event.getResponse().toString();
    }
    
    public void getAsStringAsync(OID oids, ResponseListener listener) {
        
        try {
            snmp.send(getPDU_v1(new OID[]{oids}), getTarget(), null, listener);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private PDU getPDU_v1(OID oids[]) {
        
        PDU pdu = new PDU();
        
        for (OID oid : oids) {
            pdu.add(new VariableBinding(oid));
        }
        
        pdu.setType(PDU.GET);
        
        return pdu;
    }
    
    private ScopedPDU getPDU_v3(OID oids[]) {
        
        ScopedPDU pdu = new ScopedPDU();
        
        for (OID oid : oids) {
            pdu.add(new VariableBinding(oid));
        }
        
        pdu.setType(PDU.GET);
        
        return pdu;
    }
    
    /*
     * This method returns a Target, which contains information about where the data should be fetched and how
     * */
    private Target getTarget() {
        
        Address targetAddress = GenericAddress.parse(address);

        /*
            CommunityTarget target = new CommunityTarget ();
            target.setAddress ( targetAddress );
            target.setRetries ( 2 );
            target.setTimeout ( 1500 );
            target.setVersion ( SnmpConstants.version1 );
        */
        
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString("public"));
        target.setAddress(targetAddress);
        target.setVersion(SnmpConstants.version2c);
        
        return target;
    }
    
    /*
     * This method is more generic and is capable of handling multiple OIDs.
     * In a real application with lots of agents you would probably implement this asynchronously with a
     * ResponseListener instead to prevent your thread pool from being exhausted.
     * */
    private ResponseEvent get(OID[] oids) throws IOException {
        
        ResponseEvent response = snmp.send(getPDU_v1(oids), getTarget());
        
        if (response.getResponse() == null){
            throw new RuntimeException("GET timed out");
            
        } else{
            
            logger.info("Received response from : " + response.getPeerAddress());
            logger.info(response.getResponse().toString());
        }
        
        return response;
    }
}
