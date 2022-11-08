package top.zxqs.quartz.controller;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.zxqs.quartz.domain.SysJob;
import top.zxqs.quartz.service.QuartzService;
import top.zxqs.quartz.utils.CronUtils;
import top.zxqs.quartz.utils.ScheduleUtils;
import top.zxqs.quartz.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zxq
 * @date: 2022-11-04 14:33
 */
@RestController
@RequestMapping("/quartz")
public class QuartzController {
    /**
     * 定时任务违规的字符
     */
    public static final String[] JOB_ERROR_STR = {"java.net.URL", "javax.naming.InitialContext", "org.yaml.snakeyaml","org.springframework", "org.apache"};
    @Autowired
    private QuartzService quartzService;

    @GetMapping("/selectJobAll")
    public List<SysJob> selectJobAll() {
        List<SysJob> jobList = quartzService.selectJobAll();
        return jobList;
    }

    @PostMapping("/addJob")
    public List<Object> addJob(@RequestBody SysJob sysJob) throws Exception {
        ArrayList<Object> list = new ArrayList<>();

        StringBuffer stringBuffer = new StringBuffer();
        if(!CronUtils.isValid(sysJob.getCronExpression())){
            stringBuffer.append("新增任务 ").append(sysJob.getJobName()).append(" 失败，Cron表达式不正确");
            list.add(stringBuffer.toString());

        } else if (StringUtils.containsIgnoreCase(sysJob.getInvokeTarget(),"rmi:")){
            stringBuffer.append("新增任务 ").append(sysJob.getJobName()).append(" 失败，目标方法不允许 rmi 调用");
        } else if (StringUtils.containsAnyIgnoreCase(sysJob.getInvokeTarget(),new String[]{"ldap:", "ldaps:"})){
            stringBuffer.append("新增任务 ").append(sysJob.getJobName()).append(" 失败，目标方法不允许 ldap(s) 调用");
        } else if (StringUtils.containsAnyIgnoreCase(sysJob.getInvokeTarget(),new String[]{"http:", "https:"})){
            stringBuffer.append("新增任务 ").append(sysJob.getJobName()).append(" 失败，目标方法不允许 http(s) 调用");
        }else if (StringUtils.containsAnyIgnoreCase(sysJob.getInvokeTarget(), JOB_ERROR_STR)) {
            stringBuffer.append("新增任务 ").append(sysJob.getJobName()).append(" 失败，目标字符串存在违规");
        } else if (!ScheduleUtils.whiteList(sysJob.getInvokeTarget())) {
            stringBuffer.append("新增任务 ").append(sysJob.getJobName()).append(" 失败，目标字符串不在白名单内");
        }

        // 有错误直接返回
        if(StringUtils.isNotEmpty(stringBuffer.toString())){
            list.add(stringBuffer.toString());
            return list;
        }

        boolean result = quartzService.insertJob(sysJob);

        list.add(result);

        return list;
    }

    @PostMapping("/changeStatus")
    public int changeStatus(@RequestBody SysJob sysJob) throws SchedulerException {
        SysJob newJob = quartzService.selectJobById(sysJob.getJobId());

        newJob.setStatus(sysJob.getStatus());
        int row = quartzService.changeStatus(newJob);

        return row;
    }

    /**
     * 根据id 查询任务
     * @param jobId
     * @return
     */
    @GetMapping("selectJobById/{jobId}")
    public SysJob selectJobById(@PathVariable("jobId") Long jobId){
        SysJob sysJob = quartzService.selectJobById(jobId);
        return sysJob;
    }
}
