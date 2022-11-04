package top.zxqs.quartz.service;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.zxqs.quartz.common.SpringUtils;
import top.zxqs.quartz.domain.SysJob;
import top.zxqs.quartz.mapper.QuartzMapper;
import top.zxqs.quartz.utils.ScheduleUtils;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @Author: zxq
 * @date: 2022-11-04 14:35
 */
@Service
public class QuartzService {
    @Autowired
    private Scheduler scheduler;


    @Autowired
    private QuartzMapper quartzMapper;

    /**
     * 项目启动时，初始化定时器 主要是防止手动修改数据库导致未同步到定时任务处理（注：不能手动修改数据库ID和任务组名，否则会导致脏数据）
     */
    @PostConstruct
    public void init() throws SchedulerException, Exception {
        scheduler.clear();
        List<SysJob> jobList = quartzMapper.selectJobAll();
        for (SysJob job : jobList) {
            ScheduleUtils.createScheduleJob(scheduler, job);
        }
    }

    public List<SysJob> selectJobAll() {
        return quartzMapper.selectJobAll();
    }
}
