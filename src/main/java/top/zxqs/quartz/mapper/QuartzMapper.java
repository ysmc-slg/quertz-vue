package top.zxqs.quartz.mapper;

import top.zxqs.quartz.domain.SysJob;

import java.util.List;

/**
 * @Author: zxq
 * @date: 2022-11-04 14:41
 */
public interface QuartzMapper {

    List<SysJob> selectJobAll();
}
