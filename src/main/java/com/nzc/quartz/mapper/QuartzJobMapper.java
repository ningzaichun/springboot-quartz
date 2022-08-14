package com.nzc.quartz.mapper;

import java.util.List;

import com.nzc.quartz.entity.QuartzJob;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Description: 定时任务在线管理
 * @Author: nzc
 */
@Mapper
public interface QuartzJobMapper extends BaseMapper<QuartzJob> {


}
