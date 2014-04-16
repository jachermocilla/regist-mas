package jach.msthesis.regmas;

public interface Constants {
	//Action constants student agent
	public static final int ACTION_SEARCH_MAIN_SCHEDULER=0;
	public static final int ACTION_GET_FORM5=1;
	public static final int ACTION_ENLIST=2;
	public static final int ACTION_CANCEL_SLOT=3;
	public static final int ACTION_HAPPY=4;
	public static final int ACTION_GET_SECTIONS_WITH_SLOTS=5;
	public static final int ACTION_POST_SWAP_REQUEST=6;
	public static final int ACTION_END=7;
	
	//Action constants scheduler
	public static final int ACTION_SCHEDULE=0;
	public static final int ACTION_START_ENLISTORS=1;
	public static final int ACTION_WAIT_FOR_REQUESTS = 2;
	
	
	//String message constants
	public static final String MSG_SEARCH_MAIN_SCHEDULER="Search Main Scheduler";
	public static final String MSG_GET_FORM5="Get Form 5";
	public static final String MSG_ENLIST="Enlist";
	public static final String MSG_CANCEL_SLOT="Cancel Slot";
	public static final String MSG_HAPPY="Happy";
	public static final String MSG_GET_SECTIONS_WITH_SLOTS="Get sections with slots";
	public static final String MSG_CONFIRM="Confirm";
}
