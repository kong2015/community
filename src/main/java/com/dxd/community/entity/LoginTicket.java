package com.dxd.community.entity;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @author dxd
 * @create 2021-06-20 16:02
 */
@ToString
@Data
public class LoginTicket {
    private int id;
    private int userId;
    private String ticket;
    private int status;//0表示 1表示
    private Date expired;
}
