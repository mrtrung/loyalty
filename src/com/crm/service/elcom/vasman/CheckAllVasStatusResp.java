/**
 * CheckAllVasStatusResp.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.crm.service.elcom.vasman;

public class CheckAllVasStatusResp  implements java.io.Serializable {
    private int[] NStatus;

    private int[] NVasID;

    private java.lang.String errorCode;

    private java.lang.String errorDetail;

    private java.lang.String mdn;

    public CheckAllVasStatusResp() {
    }

    public CheckAllVasStatusResp(
           int[] NStatus,
           int[] NVasID,
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
     * Gets the NStatus value for this CheckAllVasStatusResp.
     * 
     * @return NStatus
     */
    public int[] getNStatus() {
        return NStatus;
    }


    /**
     * Sets the NStatus value for this CheckAllVasStatusResp.
     * 
     * @param NStatus
     */
    public void setNStatus(int[] NStatus) {
        this.NStatus = NStatus;
    }


    /**
     * Gets the NVasID value for this CheckAllVasStatusResp.
     * 
     * @return NVasID
     */
    public int[] getNVasID() {
        return NVasID;
    }


    /**
     * Sets the NVasID value for this CheckAllVasStatusResp.
     * 
     * @param NVasID
     */
    public void setNVasID(int[] NVasID) {
        this.NVasID = NVasID;
    }


    /**
     * Gets the errorCode value for this CheckAllVasStatusResp.
     * 
     * @return errorCode
     */
    public java.lang.String getErrorCode() {
        return errorCode;
    }


    /**
     * Sets the errorCode value for this CheckAllVasStatusResp.
     * 
     * @param errorCode
     */
    public void setErrorCode(java.lang.String errorCode) {
        this.errorCode = errorCode;
    }


    /**
     * Gets the errorDetail value for this CheckAllVasStatusResp.
     * 
     * @return errorDetail
     */
    public java.lang.String getErrorDetail() {
        return errorDetail;
    }


    /**
     * Sets the errorDetail value for this CheckAllVasStatusResp.
     * 
     * @param errorDetail
     */
    public void setErrorDetail(java.lang.String errorDetail) {
        this.errorDetail = errorDetail;
    }


    /**
     * Gets the mdn value for this CheckAllVasStatusResp.
     * 
     * @return mdn
     */
    public java.lang.String getMdn() {
        return mdn;
    }


    /**
     * Sets the mdn value for this CheckAllVasStatusResp.
     * 
     * @param mdn
     */
    public void setMdn(java.lang.String mdn) {
        this.mdn = mdn;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CheckAllVasStatusResp)) return false;
        CheckAllVasStatusResp other = (CheckAllVasStatusResp) obj;
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
              java.util.Arrays.equals(this.NStatus, other.getNStatus()))) &&
            ((this.NVasID==null && other.getNVasID()==null) || 
             (this.NVasID!=null &&
              java.util.Arrays.equals(this.NVasID, other.getNVasID()))) &&
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
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getNStatus());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getNStatus(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getNVasID() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getNVasID());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getNVasID(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
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
        new org.apache.axis.description.TypeDesc(CheckAllVasStatusResp.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://vasman.elcom.com", "CheckAllVasStatusResp"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("NStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vasman.elcom.com", "NStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("http://vasman.elcom.com", "int"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("NVasID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vasman.elcom.com", "NVasID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("http://vasman.elcom.com", "int"));
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
