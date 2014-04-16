package jach.msthesis.scheduler.statistics;

public interface IStatistics {
	/**
	 * Returns the percentage load of a student. 
	 */
	public double getPercentageLoad(String stdNum);
	
	/**
	 * Returns the average percentage load
	 */
	public double getAveragePercentageLoad();
	
	/**
	 * Returns the percentage class size 
	 */
	public double getPercentageClassSize(String subjectColonSection);
	
	/**
	 * Returns the average percentage class size 
	 */
	public double getAveragePercentageClassSize();
	
	/**
	 * Returns the number of students with full load
	 */
	public int getFullLoadCount();
	
	/**
	 * Returns the number of students with underload 
	 */
	public int getUnderloadCount();
	
	/**
	 * Returns the number of students with overload 
	 */
	public int getOverloadCount();
	
	/**
	 * Returns the number of students with zero load 
	 */
	public int getZeroLoadCount();
	
	/**
	 * Returns the number of writeins
	 */
	public int getWriteInCount();
	
	/**
	 * Returns the number of schedules
	 */
	public int getSchedulesCount();
	
	
	/**
	 * Returns the number of schedules with writein
	 */
	public int getScheduleWithWriteInCount();
	
	/**
	 * Returns the number of writeins with schedule
	 * @return
	 */
	public int getWriteInWithScheduleCount();
	
	/**
	 * Returns the number of slots available for subject
	 * @param subject
	 * @return
	 */
	public int getSlotCount(String subject);
	
	/**
	 * Returns the number of students in need of subject
	 * @param subject
	 * @return
	 */
	public int getDemandCount(String subject);

	/**
	 * Returns the number of students accommodated
	 * @param subject
	 * @return
	 */
	public int getAccommodatedCount(String subject);
}
