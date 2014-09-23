/**
 * TpUICollectCriteria_Helper.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Aug 08, 2005 (11:49:10 PDT) WSDL2Java emitter.
 */

package org.csapi.www.ui_data.schema;

public class TpUICollectCriteria_Helper {
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TpUICollectCriteria.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.csapi.org/ui_data/schema", "TpUICollectCriteria"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("minLength");
        elemField.setXmlName(new javax.xml.namespace.QName("", "MinLength"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.csapi.org/osa/schema", "TpInt32"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maxLength");
        elemField.setXmlName(new javax.xml.namespace.QName("", "MaxLength"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.csapi.org/osa/schema", "TpInt32"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("endSequence");
        elemField.setXmlName(new javax.xml.namespace.QName("", "EndSequence"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.csapi.org/osa/schema", "TpString"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("startTimeout");
        elemField.setXmlName(new javax.xml.namespace.QName("", "StartTimeout"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.csapi.org/osa/schema", "TpDuration"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("interCharTimeout");
        elemField.setXmlName(new javax.xml.namespace.QName("", "InterCharTimeout"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.csapi.org/osa/schema", "TpDuration"));
        elemField.setNillable(false);
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
