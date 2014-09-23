/**
 * WSVasmanagerPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.crm.service.elcom.vasman;

public interface WSVasmanagerPortType extends java.rmi.Remote {
    public com.crm.service.elcom.vasman.ProvisioningResp provisioning(com.crm.service.elcom.vasman.ProvisioningReq in0) throws java.rmi.RemoteException;
    public com.crm.service.elcom.vasman.CheckAllVasStatusResp checkAllVasStatus(com.crm.service.elcom.vasman.CheckAllVasStatusReq in0) throws java.rmi.RemoteException;
    public com.crm.service.elcom.vasman.CheckVasStatusResp checkVasStatus(com.crm.service.elcom.vasman.CheckVasStatusReq in0) throws java.rmi.RemoteException;
}
