package com.traffic.gat1049.protocol.model.sdo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * 用户信息对象 (SDO_User)
 * 对应文档中的 SDO_User
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "SDO_User")
@XmlAccessorType(XmlAccessType.FIELD)
public class SdoUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @XmlElement(name = "UserName", required = true)
    @JsonProperty("UserName")
    private String userName;

    /**
     * 口令
     */
    @XmlElement(name = "Pwd")
    @JsonProperty("Pwd")
    private String pwd;

    // 构造函数
    public SdoUser() {}

    public SdoUser(String userName, String pwd) {
        this.userName = userName;
        this.pwd = pwd;
    }

    // Getters and Setters
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getPwd() { return pwd; }
    public void setPwd(String pwd) { this.pwd = pwd; }

    @Override
    public String toString() {
        return "SdoUser{" +
                "userName='" + userName + '\'' +
                ", pwd='[HIDDEN]'" +
                '}';
    }
}
