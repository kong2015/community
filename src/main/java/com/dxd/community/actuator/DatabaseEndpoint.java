package com.dxd.community.actuator;

import com.dxd.community.util.CommunityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author dxd
 * @create 2021-07-20 11:24
 */
@Slf4j
@Component
@Endpoint(id = "database")
public class DatabaseEndpoint {
    @Autowired
    private DataSource dataSource;

    //表示是get请求
    //注意这里需要做权限控制
    @ReadOperation
    public String checkConnection() {
        try (
                Connection conn = dataSource.getConnection();
        ) {
            return CommunityUtil.getJSONString(0, "获取链接成功");
        } catch (SQLException e) {
            log.error("获取连接失败：" + e.getMessage());
            return CommunityUtil.getJSONString(1, "获取链接失败");
        }
    }
}
