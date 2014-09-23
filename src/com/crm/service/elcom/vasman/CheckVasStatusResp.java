/**
 * CheckVasStatusResp.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.crm.service.elcom.vasman;

public class CheckVasStatusResp  implements java.io.Serializable {
    private java.lang.Integer NStatus;

    private java.lang.Long NVasID;

    private java.lang.String errorCode;

    private java.lang.String errorDetail;

    private java.lang.String mdn;

    public CheckVasStatusResp() {
    }

    public CheckVasStatusResp(
           java.lang.Integer NStatus,
           java.lang.Long NVasID,
           java.lang.String errorCode,
           java.lang.String errorDetail,
           java.lang.String mdn) {
           this.NStatus = NStatus;
           this.NVasID = NVasID;
           this.errorCode = errorCode;
           this.errorDetail = errorDetail;
           this.mdn = mdn;
    }


    /**
     * Gets the NStatus value for this CheckVasStatusResp.
     * 
     * @return NStatus
     */
    public java.lang.Integer getNStatus() {
        return NStatus;
    }


    /**
     * Sets the NStatus value for this CheckVasStatusResp.
     * 
     * @param NStatus
     */
    public void setNStatus(java.lang.Integer NStatus) {
        this.NStatus = NStatus;
    }


    /**
     * Gets the NVasID value for this CheckVasStatusResp.
     * 
     * @return NVasID
     */
    public java.lang.Long getNVasID() {
        return NVasID;
    }


    /**
     * Sets the NVasID value for this CheckVasStatusResp.
     * 
     * @param NVasID
     */
    public void setNVasID(java.lang.Long NVasID) {
        this.NVasID = NVasID;
    }


    /**
     * Gets the errorCode value for this CheckVasStatusResp.
     * 
     * @return errorCode
     */
    public java.lang.String getErrorCode() {
        return errorCode;
    }


    /**
     * Sets the errorCode value for this CheckVasStatusResp.
     * 
     * @param errorCode
     */
    public void setErrorCode(java.lang.String errorCode) {
        this.errorCode = errorCode;
    }


    /**
     * Gets the errorDetail value for this CheckVasStatusResp.
     * 
     * @return errorDetail
     */
    public java.lang.String getErrorDetail() {
        return errorDetail;
    }


    /**
     * Sets the errorDetail value for this CheckVasStatusResp.
     * 
     * @param errorDetail
     */
    public void setErrorDetail(java.lang.String errorDetail) {
        this.errorDetail = errorDetail;
    }


    /**
     * Gets the mdn value for this CheckVasStatusResp.
     * 
     * @return mdn
     */
    public java.lang.String getMdn() {
        return mdn;
    }


    /**
     * Sets the mdn value for this CheckVasStatusResp.
     * 
     * @param mdn
     */
    public void setMdn(java.lang.String mdn) {
        this.mdn = mdn;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CheckVasStatusResp)) return false;
        CheckVasStatusResp other = (CheckVasStatusResp) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.NStatus==null && other.getNStatus()==null) || 
             (this.NStatus!=null &&
              this.NStatus.equals(other.getNStatus()))) &&
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
        if (getNStatus() != null) {
            _hashCode += getNStatus().hashCode();
        }
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
        new org.apache.axis.description.TypeDesc(CheckVasStatusResp.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://vasman.elcom.com", "CheckVasStatusResp"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("NStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vasman.elcom.com", "NStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("NVasID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vasman.elcom.com", "NVasID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
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
