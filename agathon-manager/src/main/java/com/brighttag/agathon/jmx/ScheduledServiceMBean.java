package com.brighttag.agathon.jmx;

/**
 * JMX MBean for running one iteration of a scheduled service.
 *
 * @author codyaray
 * @since 4/1/2014
 */
public interface ScheduledServiceMBean {

  /**
   * Run one iteration of the scheduled task.
   */
  void runOneIteration();

}
