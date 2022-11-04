package top.zxqs.quartz.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import top.zxqs.quartz.utils.QuartzDisallowConcurrentExecution;

/**
 * @Author: zxq
 * @date: 2022-11-04 17:01
 */
@Component("ryTask")
public class JobTask {
    private static final Logger log = LoggerFactory.getLogger(JobTask.class);

    //    ryTask.ryNoParams
    public void ryNoParams() {

        log.info("我执行了");
    }

    public void ryMultipleParams(String s, Boolean b, Long l, Double d, Integer i) {
        System.out.println(String.format("%s-%b-%d-%f-%d",s,b,l,d,i));
    }
}
