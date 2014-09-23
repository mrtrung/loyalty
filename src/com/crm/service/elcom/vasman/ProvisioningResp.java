/**
 * ProvisioningResp.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.crm.service.elcom.vasman;

public class ProvisioningResp  implements java.io.Serializable {
    private java.lang.Integer NVasID;

    private java.lang.String errorCode;

    private java.lang.String errorDetail;

    private java.lang.String mdn;

    public ProvisioningResp() {
    }

    public ProvisioningResp(
           java.lang.Integer NVasID,
           java.lang.String errorCode,
           java.lang.String errorDetail,
           java.lang.String mdn) {
           this.NVasID = NVasID;
           this.errorCode = errorCode;
           this.errorDetail = errorDetail;
           this.mdn = mdn;
    }


    /**
     * Gets the NVasID value for this ProvisioningResp.
     * 
     * @return NVasID
     */
    public java.lang.Integer getNVasID() {
        return NVasID;
    }


    /**
     * Sets the NVasID value for this ProvisioningResp.
     * 
     * @param NVasID
     */
    public void setNVasID(java.lang.Integer NVasID) {
        this.NVasID = NVasID;
    }


    /**
     * Gets the errorCode value for this ProvisioningResp.
     * 
     * @return errorCode
     */
    public java.lang.String getErrorCode() {
        return errorCode;
    }


    /**
     * Sets the errorCode value for this ProvisioningResp.
     * 
     * @param errorCode
     */
    public void setErrorCode(java.lang.String errorCode) {
        this.errorCode = errorCode;
    }


    /**
     * Gets the errorDetail value for this ProvisioningResp.
     * 
     * @return errorDetail
     */
    public java.lang.String getErrorDetail() {
        return errorDetail;
    }


    /**
     * Sets the errorDetail value for this ProvisioningResp.
     * 
     * @param errorDetail
     */
    public void setErrorDetail(java.lang.String errorDetail) {
        this.errorDetail = errorDetail;
    }


    /**
     * Gets the mdn value for this ProvisioningResp.
     * 
     * @return mdn
     */
    public java.lang.String getMdn() {
        return mdn;
    }


    /**
     * Sets the mdn value for this ProvisioningResp.
     * 
     * @param mdn
     */
    public void setMdn(java.lang.String mdn) {
        this.mdn = mdn;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ProvisioningResp)) return false;
        ProvisioningResp other = (ProvisioningResp) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.NVasID==null && other.getNVasID()==null) || 
             (this.NVasID!=null &&
              this.NVasID.equals(other.getNVasID()))) &&
            ((this.errorCode==null && other.getErrorCode()==null) || 
             (this.errorCode!=null &&
              this.errorCode.equals(other.getErrorCode()))) &&
            ((this.errorDetail==null && other.getErrorDetail()==null) || 
             (this.errorDetail!=null &&
              this.errorDetail.equals(other.getErrorDetail()))) &&
            ((this.mdn==null && other.getMdn()==null) || 
             (this.mdn!=null &&
              this.mdn.equals(other.getMdn())));
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
        if (getNVasID() != null) {
            _hashCode += getNVasID().hashCode();
        }
        if (getErrorCode() != null) {
            _hashCode += getErrorCode().hashCode();
        }
        if (getErrorDetail() != null) {
            _hashCode += getErrorDetail().hashCode();
        }
        if (getMdn() != null) {
            _hashCode += getMdn().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ProvisioningResp.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://vasman.elcom.com", "ProvisioningResp"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("NVasID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vasman.elcom.com", "NVasID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errorCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vasman.elcom.com", "errorCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errorDetail");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vasman.elcom.com", "errorDetail"));
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
