package book.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer{

		@Override
		public Executor getAsyncExecutor() {
			// TODO Auto-generated method stub
			  ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		        int processors = Runtime.getRuntime().availableProcessors();
		        log.info("processors count {}", processors);
		        executor.setCorePoolSize(processors);
		        executor.setMaxPoolSize(processors * 2);
		        executor.setQueueCapacity(50);
		        executor.setKeepAliveSeconds(60);
		        executor.setThreadNamePrefix("AsyncExecutor-");
		        executor.initialize();
		        return executor;
		}
}
