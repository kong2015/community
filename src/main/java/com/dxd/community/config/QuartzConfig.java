package com.dxd.community.config;

import com.dxd.community.quartz.PostScoreRefreshJob;
import com.dxd.community.quartz.dxdJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

/**
 * @author dxd
 * @create 2021-07-17 22:13
 */
// 配置 -> 数据库 -> 调用
// 默认是读取内存的，但是当进行了Quartz的配置以后会村发哦数据库中(第一次从配置中获得，以后都是数据库中读取)

    //需要配置两个东西一个是配置JobDetail，还有一个是trigger
    //服务启动以后，配置类默认被加载，Quartz就会根据detail和trigger向数据库中增加数据
    // 当数据库汇总有数据以后，Quartz底层的调度器scheduler根据数据进行调度

@Configuration
public class QuartzConfig {

    // FactoryBean可简化Bean的实例化过程:
    // 1.通过FactoryBean封装Bean的实例化过程.
    // 2.将FactoryBean装配到Spring容器里.
    // 3.将FactoryBean注入给其他的Bean.
    // 4.该Bean得到的是FactoryBean所管理的对象实例.

    // 配置JobDetail  @Bean表示程序一启动就会被初始化，因此就会执行调度任务。我们不想让调度任务启动，注释即可
//     @Bean
    public JobDetailFactoryBean dxdJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(dxdJob.class);
        factoryBean.setName("dxdJob");
        factoryBean.setGroup("dxdJobGroup");
        factoryBean.setDurability(true);//持久的保存
        factoryBean.setRequestsRecovery(true);//可以恢复的
        return factoryBean;
    }

    // 配置Trigger(SimpleTriggerFactoryBean, CronTriggerFactoryBean).
//     @Bean
    public SimpleTriggerFactoryBean dxdTrigger(JobDetail dxdJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(dxdJobDetail);
        factoryBean.setName("dxdTrigger");
        factoryBean.setGroup("dxdTriggerGroup");
        factoryBean.setRepeatInterval(3000);    //每3s调度一次任务
        factoryBean.setJobDataMap(new JobDataMap());//trigger中存储job的状态
        return factoryBean;
    }

    // 刷新帖子分数任务
    @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(PostScoreRefreshJob.class);
        factoryBean.setName("postScoreRefreshJob");
        factoryBean.setGroup("communityJobGroup");
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    //设置每2小时执行一次
    @Bean
    public SimpleTriggerFactoryBean postScoreRefreshTrigger(JobDetail postScoreRefreshJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(postScoreRefreshJobDetail);
        factoryBean.setName("postScoreRefreshTrigger");
        factoryBean.setGroup("communityTriggerGroup");
//        factoryBean.setRepeatInterval(1000 * 60 * 60 * 2);   //2小时执行一次
        factoryBean.setRepeatInterval(1000 * 60 * 2);   //2分钟执行一次
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }
}