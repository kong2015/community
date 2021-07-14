package com.dxd.community.dao;

import com.dxd.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

/**
 * @author dxd
 * @create 2021-06-20 16:05
 */
@Mapper
@Deprecated
public interface LoginTicketMappper {
    @Insert({"insert into login_ticket(user_id, ticket, status, expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"}
    )
    @Options(keyProperty = "id", useGeneratedKeys = true)
    int insertLoginTicket(LoginTicket loginTicket);

    @Select({"select id, user_id, ticket, status, expired ",
            "from login_ticket ",
            "where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    @Update({"update login_ticket set status=#{status} where ticket=#{ticket}"
    })
    int updateStatus(String ticket, int status);

}
