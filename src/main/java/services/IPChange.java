package services;

import clients.ShellClient;

/**
 * @author Patrick Ubelhor
 * @version 2/27/2020
 */
public class IPChange extends Service {
	
	private final ShellClient shellClient;
	private String ip = "";
	
	public IPChange(long period) {
		super("IPChange", period);
		
		this.shellClient = new ShellClient();
	}
	
	
	@Override
	protected boolean init() {
		ip = shellClient.getIp();
		return true;
	}
	
	
	@Override
	protected void execute() {
		String currentIp = shellClient.getIp();
		
		if (!ip.equals(currentIp)) {
			ip = currentIp;
			MessageSubscriber.getInstance().sendMessage(this.getName(), "New IP: " + currentIp);
		}
	}
	
	
	@Override
	protected boolean end() {
		return true;
	}
	
	
	@Override
	public String getInfo() {
		// TODO: Make this specify how frequently it checks using `period`
		return "Periodically checks to see if the IP of the server has changed.";
	}
	
}
