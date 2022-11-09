package top.zxqs.quartz.service;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.zxqs.quartz.common.SpringUtils;
import top.zxqs.quartz.constant.ScheduleConstants;
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

    @Transactional(rollbackFor = Exception.class)
    public boolean insertJob(SysJob sysJob) throws Exception {
        sysJob.setStatus("0");

        int row = quartzMapper.insertJob(sysJob);

        if(row > 0){
            ScheduleUtils.createScheduleJob(scheduler,sysJob);
            return true;
        }
        return false;
    }

    /**
     * 任务调度状态修改
     *
     * @param sysJob 调度信息
     */
    public int changeStatus(SysJob sysJob) throws SchedulerException {

        int rows = 0;
        String status = sysJob.getStatus();
        if (ScheduleConstants.Status.NORMAL.getValue().equals(status)) {
            rows = resumeJob(sysJob);
        } else if (ScheduleConstants.Status.PAUSE.getValue().equals(status)) {
            rows = pauseJob(sysJob);
        }
        return rows;

    }

    /**
     * 恢复任务
     *
     * @param sysJob 调度信息
     */
    @Transactional(rollbackFor = Exception.class)
    public int resumeJob(SysJob sysJob) throws SchedulerException {
        Long jobId = sysJob.getJobId();
        String jobGroup = sysJob.getJobGroup();
        sysJob.setStatus(ScheduleConstants.Status.NORMAL.getValue());
        int rows = quartzMapper.updateJob(sysJob);
        if (rows > 0) {
            // 恢复任务
            scheduler.resumeJob(ScheduleUtils.getJobKey(jobId, jobGroup));
        }
        return rows;
    }

    /**
     * 暂停任务
     *
     * @param job 调度信息
     */
    @Transactional(rollbackFor = Exception.class)
    public int pauseJob(SysJob job) throws SchedulerException {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        job.setStatus(ScheduleConstants.Status.PAUSE.getValue());
        int rows = quartzMapper.updateJob(job);
        if (rows > 0) {
            scheduler.pauseJob(ScheduleUtils.getJobKey(jobId, jobGroup));
        }
        return rows;
    }

    /**
     * 根据 id 查询SysJob
     * @param jobId
     * @return
     */
    public SysJob selectJobById(Long jobId) {
        return quartzMapper.selectJobById(jobId);
    }

    /**
     * 修改任务
     * @param sysJob
     * @return
     */
    public boolean updateJob(SysJob sysJob) throws Exception {
        SysJob properties = selectJobById(sysJob.getJobId());
        int row = quartzMapper.updateJob(sysJob);
        if(row > 0){

            updateSchedulerJob(sysJob, properties.getJobGroup());
            return true;
        }
        return false;
    }

    /**
     * 更新任务
     *
     * @param sysJob      任务对象
     * @param jobGroup 任务组名
     */
    public void updateSchedulerJob(SysJob sysJob,String jobGroup) throws Exception {
        Long jobId = sysJob.getJobId();
        // 判断是否存在
        JobKey jobKey = ScheduleUtils.getJobKey(jobId, jobGroup);
        if (scheduler.checkExists(jobKey)) {
            // 防止创建时存在数据问题 先移除，然后在执行创建操作
            scheduler.deleteJob(jobKey);
        }
        ScheduleUtils.createScheduleJob(scheduler, sysJob);
    }
}
