package com.traffic.gat1049.protocol.model.core;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
public class AddressWrapper implements Serializable {

    @XmlElement(name = "Address", required = true)
    private Address address;

    public AddressWrapper() {}

    public AddressWrapper(Address address) {
        this.address = address;
    }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
}
