package com.fhd.sys.business.param;

import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class AddedJob implements Job{

    private Log logger = LogFactory.getLog(getClass()) ;

    @Autowired
	public void run() {
        logger.info("*****addedJob:"+new Date(System.currentTimeMillis()));
	}

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("AddedJob.execute:....");
	}
}
