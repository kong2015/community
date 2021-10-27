package com.dxd.community.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author dxd
 * @create 2021-07-17 22:12
 */
public class dxdJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println(Thread.currentThread().getName() + ": execute a quartz job.");
    }
}
