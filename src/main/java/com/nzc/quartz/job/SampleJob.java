package com.nzc.quartz.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * 不带参的简单定时任务
 * 
 * @Author nzc
 */
@Slf4j
public class SampleJob implements Job {

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

		log.info(String.format("Ning zaichun的 普通定时任务 SampleJob !  时间:" + new Date()));
	}
}
