package top.zxqs.quartz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.zxqs.quartz.domain.SysJob;
import top.zxqs.quartz.service.QuartzService;

import java.util.List;

/**
 * @Author: zxq
 * @date: 2022-11-04 14:33
 */
@RestController
@RequestMapping("/quartz")
public class QuartzController {

    @Autowired
    private QuartzService quartzService;

    @GetMapping("/selectJobAll")
    public List<SysJob> selectJobAll() {
        List<SysJob> jobList = quartzService.selectJobAll();
        return jobList;
    }
}
