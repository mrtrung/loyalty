/**
 * RegInfoRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.fss.oom3.service;

public class RegInfoRequest  implements java.io.Serializable {
    private java.lang.String IDCard;

    private java.lang.String address;

    private java.lang.String customerType;

    private java.util.Calendar dateOfIssue;

    private java.util.Calendar dob;

    private java.lang.String firstName;

    private java.lang.String fullName;

    private java.lang.String info;

    private java.lang.String lastName;

    private com.fss.oom3.entity.Mdn mdn;

    private java.lang.String placeOfIssue;

    private java.lang.String serial;

    public RegInfoRequest() {
    }

    public RegInfoRequest(
           java.lang.String IDCard,
           java.lang.String address,
           java.lang.String customerType,
           java.util.Calendar dateOfIssue,
           java.util.Calendar dob,
           java.lang.String firstName,
           java.lang.String fullName,
           java.lang.String info,
           java.lang.String lastName,
           com.fss.oom3.entity.Mdn mdn,
           java.lang.String placeOfIssue,
           java.lang.String serial) {
           this.IDCard = IDCard;
           this.address = address;
           this.customerType = customerType;
           this.dateOfIssue = dateOfIssue;
           this.dob = dob;
           this.firstName = firstName;
           this.fullName = fullName;
           this.info = info;
           this.lastName = lastName;
           this.mdn = mdn;
           this.placeOfIssue = placeOfIssue;
           this.serial = serial;
    }


    /**
     * Gets the IDCard value for this RegInfoRequest.
     * 
     * @return IDCard
     */
    public java.lang.String getIDCard() {
        return IDCard;
    }


    /**
     * Sets the IDCard value for this RegInfoRequest.
     * 
     * @param IDCard
     */
    public void setIDCard(java.lang.String IDCard) {
        this.IDCard = IDCard;
    }


    /**
     * Gets the address value for this RegInfoRequest.
     * 
     * @return address
     */
    public java.lang.String getAddress() {
        return address;
    }


    /**
     * Sets the address value for this RegInfoRequest.
     * 
     * @param address
     */
    public void setAddress(java.lang.String address) {
        this.address = address;
    }


    /**
     * Gets the customerType value for this RegInfoRequest.
     * 
     * @return customerType
     */
    public java.lang.String getCustomerType() {
        return customerType;
    }


    /**
     * Sets the customerType value for this RegInfoRequest.
     * 
     * @param customerType
     */
    public void setCustomerType(java.lang.String customerType) {
        this.customerType = customerType;
    }


    /**
     * Gets the dateOfIssue value for this RegInfoRequest.
     * 
     * @return dateOfIssue
     */
    public java.util.Calendar getDateOfIssue() {
        return dateOfIssue;
    }


    /**
     * Sets the dateOfIssue value for this RegInfoRequest.
     * 
     * @param dateOfIssue
     */
    public void setDateOfIssue(java.util.Calendar dateOfIssue) {
        this.dateOfIssue = dateOfIssue;
    }


    /**
     * Gets the dob value for this RegInfoRequest.
     * 
     * @return dob
     */
    public java.util.Calendar getDob() {
        return dob;
    }


    /**
     * Sets the dob value for this RegInfoRequest.
     * 
     * @param dob
     */
    public void setDob(java.util.Calendar dob) {
        this.dob = dob;
    }


    /**
     * Gets the firstName value for this RegInfoRequest.
     * 
     * @return firstName
     */
    public java.lang.String getFirstName() {
        return firstName;
    }


    /**
     * Sets the firstName value for this RegInfoRequest.
     * 
     * @param firstName
     */
    public void setFirstName(java.lang.String firstName) {
        this.firstName = firstName;
    }


    /**
     * Gets the fullName value for this RegInfoRequest.
     * 
     * @return fullName
     */
    public java.lang.String getFullName() {
        return fullName;
    }


    /**
     * Sets the fullName value for this RegInfoRequest.
     * 
     * @param fullName
     */
    public void setFullName(java.lang.String fullName) {
        this.fullName = fullName;
    }


    /**
     * Gets the info value for this RegInfoRequest.
     * 
     * @return info
     */
    public java.lang.String getInfo() {
        return info;
    }


    /**
     * Sets the info value for this RegInfoRequest.
     * 
     * @param info
     */
    public void setInfo(java.lang.String info) {
        this.info = info;
    }


    /**
     * Gets the lastName value for this RegInfoRequest.
     * 
     * @return lastName
     */
    public java.lang.String getLastName() {
        return lastName;
    }


    /**
     * Sets the lastName value for this RegInfoRequest.
     * 
     * @param lastName
     */
    public void setLastName(java.lang.String lastName) {
        this.lastName = lastName;
    }


    /**
     * Gets the mdn value for this RegInfoRequest.
     * 
     * @return mdn
     */
    public com.fss.oom3.entity.Mdn getMdn() {
        return mdn;
    }


    /**
     * Sets the mdn value for this RegInfoRequest.
     * 
     * @param mdn
     */
    public void setMdn(com.fss.oom3.entity.Mdn mdn) {
        this.mdn = mdn;
    }


    /**
     * Gets the placeOfIssue value for this RegInfoRequest.
     * 
     * @return placeOfIssue
     */
    public java.lang.String getPlaceOfIssue() {
        return placeOfIssue;
    }


    /**
     * Sets the placeOfIssue value for this RegInfoRequest.
     * 
     * @param placeOfIssue
     */
    public void setPlaceOfIssue(java.lang.String placeOfIssue) {
        this.placeOfIssue = placeOfIssue;
    }


    /**
     * Gets the serial value for this RegInfoRequest.
     * 
     * @return serial
     */
    public java.lang.String getSerial() {
        return serial;
    }


    /**
     * Sets the serial value for this RegInfoRequest.
     * 
     * @param serial
     */
    public void setSerial(java.lang.String serial) {
        this.serial = serial;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RegInfoRequest)) return false;
        RegInfoRequest other = (RegInfoRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.IDCard==null && other.getIDCard()==null) || 
             (this.IDCard!=null &&
              this.IDCard.equals(other.getIDCard()))) &&
            ((this.address==null && other.getAddress()==null) || 
             (this.address!=null &&
              this.address.equals(other.getAddress()))) &&
            ((this.customerType==null && other.getCustomerType()==null) || 
             (this.customerType!=null &&
              this.customerType.equals(other.getCustomerType()))) &&
            ((this.dateOfIssue==null && other.getDateOfIssue()==null) || 
             (this.dateOfIssue!=null &&
              this.dateOfIssue.equals(other.getDateOfIssue()))) &&
            ((this.dob==null && other.getDob()==null) || 
             (this.dob!=null &&
              this.dob.equals(other.getDob()))) &&
            ((this.firstName==null && other.getFirstName()==null) || 
             (this.firstName!=null &&
              this.firstName.equals(other.getFirstName()))) &&
            ((this.fullName==null && other.getFullName()==null) || 
             (this.fullName!=null &&
              this.fullName.equals(other.getFullName()))) &&
            ((this.info==null && other.getInfo()==null) || 
             (this.info!=null &&
              this.info.equals(other.getInfo()))) &&
            ((this.lastName==null && other.getLastName()==null) || 
             (this.lastName!=null &&
              this.lastName.equals(other.getLastName()))) &&
            ((this.mdn==null && other.getMdn()==null) || 
             (this.mdn!=null &&
              this.mdn.equals(other.getMdn()))) &&
            ((this.placeOfIssue==null && other.getPlaceOfIssue()==null) || 
             (this.placeOfIssue!=null &&
              this.placeOfIssue.equals(other.getPlaceOfIssue()))) &&
            ((this.serial==null && other.getSerial()==null) || 
             (this.serial!=null &&
              this.serial.equals(other.getSerial())));
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
        if (getIDCard() != null) {
            _hashCode += getIDCard().hashCode();
        }
        if (getAddress() != null) {
            _hashCode += getAddress().hashCode();
        }
        if (getCustomerType() != null) {
            _hashCode += getCustomerType().hashCode();
        }
        if (getDateOfIssue() != null) {
            _hashCode += getDateOfIssue().hashCode();
        }
        if (getDob() != null) {
            _hashCode += getDob().hashCode();
        }
        if (getFirstName() != null) {
            _hashCode += getFirstName().hashCode();
        }
        if (getFullName() != null) {
            _hashCode += getFullName().hashCode();
        }
        if (getInfo() != null) {
            _hashCode += getInfo().hashCode();
        }
        if (getLastName() != null) {
            _hashCode += getLastName().hashCode();
        }
        if (getMdn() != null) {
            _hashCode += getMdn().hashCode();
        }
        if (getPlaceOfIssue() != null) {
            _hashCode += getPlaceOfIssue().hashCode();
        }
        if (getSerial() != null) {
            _hashCode += getSerial().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RegInfoRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://service.oom3.fss.com", "RegInfoRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("IDCard");
        elemField.setXmlName(new javax.xml.namespace.QName("", "IDCard"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("address");
        elemField.setXmlName(new javax.xml.namespace.QName("", "address"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customerType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "customerType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dateOfIssue");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dateOfIssue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dob");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dob"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("firstName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "firstName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fullName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "fullName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("info");
        elemField.setXmlName(new javax.xml.namespace.QName("", "info"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "lastName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mdn");
        elemField.setXmlName(new javax.xml.namespace.QName("", "mdn"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://entity.oom3.fss.com", "Mdn"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("placeOfIssue");
        elemField.setXmlName(new javax.xml.namespace.QName("", "placeOfIssue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serial");
        elemField.setXmlName(new javax.xml.namespace.QName("", "serial"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
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
