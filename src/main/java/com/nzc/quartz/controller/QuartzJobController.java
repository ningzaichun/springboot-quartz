package com.nzc.quartz.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nzc.quartz.common.CommonConstant;
import com.nzc.quartz.common.Result;
import com.nzc.quartz.entity.QuartzJob;
import com.nzc.quartz.exception.MyException;
import com.nzc.quartz.service.IQuartzJobService;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 定时任务在线管理
 * @author nzc
 */
@RestController
@RequestMapping("/quartzJob")
@Slf4j
public class QuartzJobController {

	@Autowired
	private IQuartzJobService quartzJobService;
	@Autowired
	private Scheduler scheduler;

	/**
	 * 分页列表查询
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Result<?> queryPageList(QuartzJob quartzJob, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
								   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
		Page<QuartzJob> page = new Page<QuartzJob>(pageNo, pageSize);
		IPage<QuartzJob> pageList = quartzJobService.page(page);
        return Result.ok(pageList);

	}

	/**
	 * 添加定时任务
	 * 
	 * @param quartzJob
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public Result<?> add(@RequestBody QuartzJob quartzJob) {
		List<QuartzJob> list = quartzJobService.list(new QueryWrapper<QuartzJob>().eq("job_class_name", quartzJob.getJobClassName()));
		if (list != null && list.size() > 0) {
			return Result.error("该定时任务类名已存在");
		}
		quartzJobService.saveAndScheduleJob(quartzJob);
		return Result.ok("创建定时任务成功");
	}

	/**
	 * 更新定时任务
	 * 
	 * @param quartzJob
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	public Result<?> eidt(@RequestBody QuartzJob quartzJob) {
		try {
			quartzJobService.editAndScheduleJob(quartzJob);
		} catch (SchedulerException e) {
			log.error(e.getMessage(),e);
			return Result.error("更新定时任务失败!");
		}
	    return Result.ok("更新定时任务成功!");
	}

	/**
	 * 通过id删除
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		QuartzJob quartzJob = quartzJobService.getById(id);
		if (quartzJob == null) {
			return Result.error("未找到对应实体");
		}
		quartzJobService.deleteAndStopJob(quartzJob);
        return Result.ok("删除成功!");

	}

	/**
	 * 批量删除
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
	public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		if (ids == null || "".equals(ids.trim())) {
			return Result.error("参数不识别！");
		}
		for (String id : Arrays.asList(ids.split(","))) {
			QuartzJob job = quartzJobService.getById(id);
			quartzJobService.deleteAndStopJob(job);
		}
        return Result.ok("删除定时任务成功!");
	}

	/**
	 * 暂停定时任务
	 * 
	 * @param jobClassName
	 * @return
	 */
	@GetMapping(value = "/pause")
	public Result<Object> pauseJob(@RequestParam(name = "jobClassName", required = true) String jobClassName) {
		QuartzJob job = null;
		try {
			job = quartzJobService.getOne(new LambdaQueryWrapper<QuartzJob>().eq(QuartzJob::getJobClassName, jobClassName));
			if (job == null) {
				return Result.error("定时任务不存在！");
			}
			scheduler.pauseJob(JobKey.jobKey(jobClassName.trim()));
		} catch (SchedulerException e) {
			throw new MyException("暂停定时任务失败");
		}
		job.setStatus(CommonConstant.STATUS_DISABLE);
		quartzJobService.updateById(job);
		return Result.ok("暂停定时任务成功");
	}

	/**
	 * 恢复定时任务
	 * 
	 * @param jobClassName
	 * @return
	 */
	@GetMapping(value = "/resume")
	public Result<Object> resumeJob(@RequestParam(name = "jobClassName", required = true) String jobClassName) {
		QuartzJob job = quartzJobService.getOne(new LambdaQueryWrapper<QuartzJob>().eq(QuartzJob::getJobClassName, jobClassName));
		if (job == null) {
			return Result.error("定时任务不存在！");
		}
		quartzJobService.resumeJob(job);
		//scheduler.resumeJob(JobKey.jobKey(job.getJobClassName().trim()));
		return Result.ok("恢复定时任务成功");
	}

	/**
	 * 通过id查询
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/queryById", method = RequestMethod.GET)
	public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
		QuartzJob quartzJob = quartzJobService.getById(id);
        return Result.ok(quartzJob);
	}

}
