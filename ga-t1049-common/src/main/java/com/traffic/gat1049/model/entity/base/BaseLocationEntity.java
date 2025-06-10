package com.traffic.gat1049.model.entity.base;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * 带坐标信息的实体基类
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class BaseLocationEntity extends BaseEntity {

    /**
     * 经度
     */
    @XmlElement(name = "Longitude")
    private Double longitude;

    /**
     * 纬度
     */
    @XmlElement(name = "Latitude")
    private Double latitude;

    /**
     * 海拔高度（米）
     */
    @XmlElement(name = "Altitude")
    private Double altitude;

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    @Override
    public String toString() {
        return "BaseLocationEntity{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", altitude=" + altitude +
                "} " + super.toString();
    }
}
