package commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author PatrickUbelhor
 * @version 8/15/2017
 */
public class AddPicture extends Command {
	
	private static final String PATH = "/Users/Patrick/Desktop/";
	
	public AddPicture() {
		super("pic");
	}
	
	
	public void run(MessageReceivedEvent event, String[] args) {
		
		List<Message.Attachment> attachments = event.getMessage().getAttachments();
		ArrayList<File> files = new ArrayList<>(attachments.size());
		
		String message = "";
		int i = 1;
		int errors = 0;
		
		for (Message.Attachment a : attachments) {
			if (!a.isImage()) {
				message += "File " + i + " is not an image!\n";
				i++;
				errors++;
				continue;
			}
			
			// TODO: this is where you specify file name vvv
			File image = new File(PATH + a.getFileName());
			if (a.download(image)) {
				files.add(image);
			}
			
		}
		
		message += "Added " + (i - errors) + " files to the group folder";
		
		
		// TODO: Rename file to match proper format
		// TODO: Put image in Drive
		
		event.getChannel().sendMessage(message).queue();
	}
	
	
	@Override
	public String getUsage() {
		return getName() + " <folder> {file}";
	}
	
	
	@Override
	public String getDescription() {
		return "Saves the attached image into the group's Drive folder";
	}
	
}
