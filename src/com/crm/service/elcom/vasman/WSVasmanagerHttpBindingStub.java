/**
 * WSVasmanagerHttpBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.crm.service.elcom.vasman;

public class WSVasmanagerHttpBindingStub extends org.apache.axis.client.Stub implements com.crm.service.elcom.vasman.WSVasmanagerPortType {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[3];
        _initOperationDesc1();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("Provisioning");
//        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://vasman.elcom.com", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://vasman.elcom.com", "ProvisioningReq"), com.crm.service.elcom.vasman.ProvisioningReq.class, false, false);
//        ((Object) param).setNillable(true);
//        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://vasman.elcom.com", "ProvisioningResp"));
        oper.setReturnClass(com.crm.service.elcom.vasman.ProvisioningResp.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://vasman.elcom.com", "out"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("CheckAllVasStatus");
//        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://vasman.elcom.com", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://vasman.elcom.com", "CheckAllVasStatusReq"), com.crm.service.elcom.vasman.CheckAllVasStatusReq.class, false, false);
//        param.setNillable(true);
//        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://vasman.elcom.com", "CheckAllVasStatusResp"));
        oper.setReturnClass(com.crm.service.elcom.vasman.CheckAllVasStatusResp.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://vasman.elcom.com", "out"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("CheckVasStatus");
//        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://vasman.elcom.com", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://vasman.elcom.com", "CheckVasStatusReq"), com.crm.service.elcom.vasman.CheckVasStatusReq.class, false, false);
//        param.setNillable(true);
//        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://vasman.elcom.com", "CheckVasStatusResp"));
        oper.setReturnClass(com.crm.service.elcom.vasman.CheckVasStatusResp.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://vasman.elcom.com", "out"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[2] = oper;

    }

    public WSVasmanagerHttpBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public WSVasmanagerHttpBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public WSVasmanagerHttpBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://vasman.elcom.com", "ArrayOfInt");
            cachedSerQNames.add(qName);
            cls = int[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int");
            qName2 = new javax.xml.namespace.QName("http://vasman.elcom.com", "int");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://vasman.elcom.com", "CheckAllVasStatusReq");
            cachedSerQNames.add(qName);
            cls = com.crm.service.elcom.vasman.CheckAllVasStatusReq.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://vasman.elcom.com", "CheckAllVasStatusResp");
            cachedSerQNames.add(qName);
            cls = com.crm.service.elcom.vasman.CheckAllVasStatusResp.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://vasman.elcom.com", "CheckVasStatusReq");
            cachedSerQNames.add(qName);
            cls = com.crm.service.elcom.vasman.CheckVasStatusReq.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://vasman.elcom.com", "CheckVasStatusResp");
            cachedSerQNames.add(qName);
            cls = com.crm.service.elcom.vasman.CheckVasStatusResp.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://vasman.elcom.com", "ProvisioningReq");
            cachedSerQNames.add(qName);
            cls = com.crm.service.elcom.vasman.ProvisioningReq.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://vasman.elcom.com", "ProvisioningResp");
            cachedSerQNames.add(qName);
            cls = com.crm.service.elcom.vasman.ProvisioningResp.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setEncodingStyle(null);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public com.crm.service.elcom.vasman.ProvisioningResp provisioning(com.crm.service.elcom.vasman.ProvisioningReq in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://vasman.elcom.com", "Provisioning"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {in0});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.crm.service.elcom.vasman.ProvisioningResp) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.crm.service.elcom.vasman.ProvisioningResp) org.apache.axis.utils.JavaUtils.convert(_resp, com.crm.service.elcom.vasman.ProvisioningResp.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.crm.service.elcom.vasman.CheckAllVasStatusResp checkAllVasStatus(com.crm.service.elcom.vasman.CheckAllVasStatusReq in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://vasman.elcom.com", "CheckAllVasStatus"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {in0});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.crm.service.elcom.vasman.CheckAllVasStatusResp) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.crm.service.elcom.vasman.CheckAllVasStatusResp) org.apache.axis.utils.JavaUtils.convert(_resp, com.crm.service.elcom.vasman.CheckAllVasStatusResp.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.crm.service.elcom.vasman.CheckVasStatusResp checkVasStatus(com.crm.service.elcom.vasman.CheckVasStatusReq in0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://vasman.elcom.com", "CheckVasStatus"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {in0});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.crm.service.elcom.vasman.CheckVasStatusResp) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.crm.service.elcom.vasman.CheckVasStatusResp) org.apache.axis.utils.JavaUtils.convert(_resp, com.crm.service.elcom.vasman.CheckVasStatusResp.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
