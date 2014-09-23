package com.crm.service.elcom.vasman;

public class WSVasmanagerPortTypeProxy implements com.crm.service.elcom.vasman.WSVasmanagerPortType {
  private String _endpoint = null;
  private com.crm.service.elcom.vasman.WSVasmanagerPortType wSVasmanagerPortType = null;
  
  public WSVasmanagerPortTypeProxy() {
    _initWSVasmanagerPortTypeProxy();
  }
  
  public WSVasmanagerPortTypeProxy(String endpoint) {
    _endpoint = endpoint;
    _initWSVasmanagerPortTypeProxy();
  }
  
  private void _initWSVasmanagerPortTypeProxy() {
    try {
      wSVasmanagerPortType = (new com.crm.service.elcom.vasman.WSVasmanagerLocator()).getWSVasmanagerHttpPort();
      if (wSVasmanagerPortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)wSVasmanagerPortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)wSVasmanagerPortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (wSVasmanagerPortType != null)
      ((javax.xml.rpc.Stub)wSVasmanagerPortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.crm.service.elcom.vasman.WSVasmanagerPortType getWSVasmanagerPortType() {
    if (wSVasmanagerPortType == null)
      _initWSVasmanagerPortTypeProxy();
    return wSVasmanagerPortType;
  }
  
  public com.crm.service.elcom.vasman.ProvisioningResp provisioning(com.crm.service.elcom.vasman.ProvisioningReq in0) throws java.rmi.RemoteException{
    if (wSVasmanagerPortType == null)
      _initWSVasmanagerPortTypeProxy();
    return wSVasmanagerPortType.provisioning(in0);
  }
  
  public com.crm.service.elcom.vasman.CheckAllVasStatusResp checkAllVasStatus(com.crm.service.elcom.vasman.CheckAllVasStatusReq in0) throws java.rmi.RemoteException{
    if (wSVasmanagerPortType == null)
      _initWSVasmanagerPortTypeProxy();
    return wSVasmanagerPortType.checkAllVasStatus(in0);
  }
  
  public com.crm.service.elcom.vasman.CheckVasStatusResp checkVasStatus(com.crm.service.elcom.vasman.CheckVasStatusReq in0) throws java.rmi.RemoteException{
    if (wSVasmanagerPortType == null)
      _initWSVasmanagerPortTypeProxy();
    return wSVasmanagerPortType.checkVasStatus(in0);
  }
  
  
}