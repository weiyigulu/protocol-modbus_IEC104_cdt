package wei.yigulu.threadpool;


import lombok.Getter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 104工具自带线程池
 *
 * @author 修唯xiuwei
 * @version 3.0
 */

public class LocalThreadPool {

	private static class LazyHolder {
		private static final LocalThreadPool INSTANCE = new LocalThreadPool();
	}

	/**
	 * Local pool
	 */
	@Getter
	ExecutorService localPool;


	/**
	 * Gets instance *
	 *
	 * @return the instance
	 */
	public static final LocalThreadPool getInstance() {
		return LazyHolder.INSTANCE;
	}

	private LocalThreadPool() {

        /*
          由于本场景下应用的线程任务均是长时间的线程任务，不涉及线程的超时和回收，这样选择缓存线程池
         */
		localPool = Executors.newCachedThreadPool();
	}


}


