package pl.whipsoft.kindleturn;

public enum COMMAND {
	//NEXT_PAGE("xdotool mousemove 500 500 click 1"),
	//PREVIOUS_PAGE("xdotool mousemove 100 500 click 1");
	NEXT_PAGE("export DISPLAY=:0.0 && xdotool mousemove 500 500 click 1"),
	PREVIOUS_PAGE("export DISPLAY=:0.0 && xdotool mousemove 100 500 click 1");
	private String linux_command;

	public String getLinux_command() {
		return linux_command;
	}

	private COMMAND(String linux_command) {
		this.linux_command = linux_command;
	}
}
