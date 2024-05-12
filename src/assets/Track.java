package assets;

import java.io.File;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.mpatric.mp3agic.Mp3File;

public class Track {
	private String title;
	private String artist;
	private Long length;
	private String filePath;
	private Double framerate;
	private Mp3File mp3File;
	
	public Track(String filePath) {
		this.filePath = filePath;
		
		try {			
			mp3File = new Mp3File(filePath);
			length = mp3File.getLengthInSeconds();
			framerate = (double)mp3File.getFrameCount() / mp3File.getLengthInMilliseconds();
			
			AudioFile audioFile = AudioFileIO.read(new File(filePath));
			
			Tag tag = audioFile.getTag();
			if (tag != null) {
				this.title = tag.getFirst(FieldKey.TITLE);
				this.artist = tag.getFirst(FieldKey.ARTIST);
			} else {
				this.title = mp3File.getFilename();
				this.artist = "N/A";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Track() {
		filePath = "";
		length = 0L;
		title = "N/A";
		artist = "N/A";
	}
	
	public void saveTrack() {
		ObjectMapper objectMapper = new ObjectMapper();
		ArrayNode jsonArray = null;
		
		try {
			File file = new File("src/assets/tracks.json");;
			file.createNewFile();
			
			JsonNode objectNode = objectMapper.readTree(file);
			if (objectNode instanceof ArrayNode)
				jsonArray = (ArrayNode)objectMapper.readTree(file);
			
			if (jsonArray == null) {
				jsonArray = objectMapper.createArrayNode();
			}
			
			jsonArray.add(filePath);
			objectMapper.writeValue(file, jsonArray);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getTitle() {
		return title;
	}

	public String getArtist() {
		return artist;
	}

	public Long getLength() {
		return length;
	}

	public Mp3File getMp3File() { return mp3File; }
	
	public String getFormatedLength() {
		Long minutes = length / 60;
		Long seconds = length % 60;
		
		return String.format("%02d:%02d", minutes, seconds);
	}

	public String getFilePath() {
		return filePath;
	}
	
	public double getFramerate() {
		return framerate;
	}
	
}
