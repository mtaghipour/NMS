package com.sazgan.helperclasses;

import org.apache.log4j.Logger;
import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;

/**
 * To Config , manage , create the Snmp objects.
 *
 * @author Maziar
 */
public class SnmpConfig {
    /**
     * Snmp object should be configured.
     */
    private Snmp snmp;
    /**
     * Address of remote target.
     */
    private String address;
    /**
     * listener to get Data from Snmp object asynchronously.
     */
    private ResponseListener listener;
    /**
     * Show request object.
     */
    private String request;
    /**
     * Show response object.
     */
    private String response;
    /**
     * Log4j object to log some important data.
     */
    private Logger logger = Logger.getLogger(this.getClass().getName());
    /**
     * @param response ..... Sets response for us in output.
     */
    public void setResponse(String response) {
        this.response = response;
    }
    /**
     * @return ..... Gets response to show in output.
     */
    public String getResponse() {
        return response;
    }
    /**
     * @return ..... Gets request to show in output.
     */
    public String getRequest() {
        return request;
    }
    /**
     * @param request ..... Sets request for us in output.
     */
    public void setRequest(String request) {
        this.request = request;
    }
    /**
     * @param address .... Address of the target system we want to connect to.
     */
    public SnmpConfig(String address) {
        this.address = address;
        try {
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * @throws IOException
     */
    public void stop() throws IOException {
        snmp.close();
    }
    /**
     * Start the Snmp session. If you forget the listen() method you will not get any answers because under the hood
     * the communication is asynchronous and the listen() method listens for answers.
     *
     * @throws IOException
     */
    private void start() throws IOException {

        /*
        TransportMapping transport = new DefaultUdpTransportMapping ();
        snmp = new Snmp ( transport );
        
        transport.listen ();
        */
        
        snmp = new Snmp(new DefaultUdpTransportMapping());
        snmp.listen();
    }
    /**
     * To make this as simple as possible in my client I have a simple method which takes a single OID and
     * returns the response from the agent as a String.
     *
     * @param oid
     * @return
     * @throws IOException
     */
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
    
    /**
     * Create PDU version 1.
     *
     * @param oids
     * @return PDU
     */
    private PDU getPDU_v1(OID oids[]) {
        PDU pdu = new PDU();
        for (OID oid : oids) {
            pdu.add(new VariableBinding(oid));
        }
        pdu.setType(PDU.GET);
        return pdu;
    }
    
    /**
     * Creates PDU version 3.
     *
     * @param oids
     * @return
     */
    private ScopedPDU getPDU_v3(OID oids[]) {
        ScopedPDU pdu = new ScopedPDU();
        for (OID oid : oids) {
            pdu.add(new VariableBinding(oid));
        }
        pdu.setType(PDU.GET);
        return pdu;
    }
    /**
     * This method returns a Target, which contains information about where the data should be fetched and how
     * it should be done.
     *
     * @return
     */
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
    /**
     * This method is more generic and is capable of handling multiple OIDs.
     * In a real application with lots of agents you would probably implement this asynchronously with a
     * ResponseListener instead to prevent your thread pool from being exhausted.
     *
     * @param oids
     * @return
     * @throws IOException
     */
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
