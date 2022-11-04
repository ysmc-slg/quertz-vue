package top.zxqs.quartz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("top.zxqs.**.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class QuertzApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuertzApplication.class, args);
    }

}
