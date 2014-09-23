/**
 * ProvisioningReq.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.crm.service.elcom.vasman;

public class ProvisioningReq  implements java.io.Serializable {
    private java.lang.Integer NCmdID;

    private java.lang.String NVasID;

    private java.lang.String SDescription;

    private java.lang.String mdn;

    private java.lang.String pass;

    private java.lang.String user;

    public ProvisioningReq() {
    }

    public ProvisioningReq(
           java.lang.Integer NCmdID,
           java.lang.String NVasID,
           java.lang.String SDescription,
           java.lang.String mdn,
           java.lang.String pass,
           java.lang.String user) {
           this.NCmdID = NCmdID;
           this.NVasID = NVasID;
           this.SDescription = SDescription;
           this.mdn = mdn;
           this.pass = pass;
           this.user = user;
    }


    /**
     * Gets the NCmdID value for this ProvisioningReq.
     * 
     * @return NCmdID
     */
    public java.lang.Integer getNCmdID() {
        return NCmdID;
    }


    /**
     * Sets the NCmdID value for this ProvisioningReq.
     * 
     * @param NCmdID
     */
    public void setNCmdID(java.lang.Integer NCmdID) {
        this.NCmdID = NCmdID;
    }


    /**
     * Gets the NVasID value for this ProvisioningReq.
     * 
     * @return NVasID
     */
    public java.lang.String getNVasID() {
        return NVasID;
    }


    /**
     * Sets the NVasID value for this ProvisioningReq.
     * 
     * @param NVasID
     */
    public void setNVasID(java.lang.String NVasID) {
        this.NVasID = NVasID;
    }


    /**
     * Gets the SDescription value for this ProvisioningReq.
     * 
     * @return SDescription
     */
    public java.lang.String getSDescription() {
        return SDescription;
    }


    /**
     * Sets the SDescription value for this ProvisioningReq.
     * 
     * @param SDescription
     */
    public void setSDescription(java.lang.String SDescription) {
        this.SDescription = SDescription;
    }


    /**
     * Gets the mdn value for this ProvisioningReq.
     * 
     * @return mdn
     */
    public java.lang.String getMdn() {
        return mdn;
    }


    /**
     * Sets the mdn value for this ProvisioningReq.
     * 
     * @param mdn
     */
    public void setMdn(java.lang.String mdn) {
        this.mdn = mdn;
    }


    /**
     * Gets the pass value for this ProvisioningReq.
     * 
     * @return pass
     */
    public java.lang.String getPass() {
        return pass;
    }


    /**
     * Sets the pass value for this ProvisioningReq.
     * 
     * @param pass
     */
    public void setPass(java.lang.String pass) {
        this.pass = pass;
    }


    /**
     * Gets the user value for this ProvisioningReq.
     * 
     * @return user
     */
    public java.lang.String getUser() {
        return user;
    }


    /**
     * Sets the user value for this ProvisioningReq.
     * 
     * @param user
     */
    public void setUser(java.lang.String user) {
        this.user = user;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ProvisioningReq)) return false;
        ProvisioningReq other = (ProvisioningReq) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.NCmdID==null && other.getNCmdID()==null) || 
             (this.NCmdID!=null &&
              this.NCmdID.equals(other.getNCmdID()))) &&
            ((this.NVasID==null && other.getNVasID()==null) || 
             (this.NVasID!=null &&
              this.NVasID.equals(other.getNVasID()))) &&
            ((this.SDescription==null && other.getSDescription()==null) || 
             (this.SDescription!=null &&
              this.SDescription.equals(other.getSDescription()))) &&
            ((this.mdn==null && other.getMdn()==null) || 
             (this.mdn!=null &&
              this.mdn.equals(other.getMdn()))) &&
            ((this.pass==null && other.getPass()==null) || 
             (this.pass!=null &&
              this.pass.equals(other.getPass()))) &&
            ((this.user==null && other.getUser()==null) || 
             (this.user!=null &&
              this.user.equals(other.getUser())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getNCmdID() != null) {
            _hashCode += getNCmdID().hashCode();
        }
        if (getNVasID() != null) {
            _hashCode += getNVasID().hashCode();
        }
        if (getSDescription() != null) {
            _hashCode += getSDescription().hashCode();
        }
        if (getMdn() != null) {
            _hashCode += getMdn().hashCode();
        }
        if (getPass() != null) {
            _hashCode += getPass().hashCode();
        }
        if (getUser() != null) {
            _hashCode += getUser().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ProvisioningReq.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://vasman.elcom.com", "ProvisioningReq"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("NCmdID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vasman.elcom.com", "NCmdID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("NVasID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vasman.elcom.com", "NVasID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("SDescription");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vasman.elcom.com", "SDescription"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mdn");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vasman.elcom.com", "mdn"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pass");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vasman.elcom.com", "pass"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("user");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vasman.elcom.com", "user"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
