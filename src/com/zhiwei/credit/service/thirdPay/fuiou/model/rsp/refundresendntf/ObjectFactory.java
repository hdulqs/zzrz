//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-147 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.12.14 at 02:46:40 ���� CST 
//


package com.zhiwei.credit.service.thirdPay.fuiou.model.rsp.refundresendntf;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.zhiwei.credit.service.thirdPay.fuiou.model.rsp.refundresendntf package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Refundresendntfrsp_QNAME = new QName("", "refundresendntfrsp");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.zhiwei.credit.service.thirdPay.fuiou.model.rsp.refundresendntf
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ReFundResendntfRspType }
     * 
     */
    public ReFundResendntfRspType createReFundResendntfRspType() {
        return new ReFundResendntfRspType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReFundResendntfRspType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "refundresendntfrsp")
    public JAXBElement<ReFundResendntfRspType> createRefundresendntfrsp(ReFundResendntfRspType value) {
        return new JAXBElement<ReFundResendntfRspType>(_Refundresendntfrsp_QNAME, ReFundResendntfRspType.class, null, value);
    }

}
