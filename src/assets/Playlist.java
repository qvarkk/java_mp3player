package assets;

import java.io.File;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;

public class Playlist {	
	private String title;
	private ArrayList<Track> tracklist;
	
	public Playlist(String title) {
		this.title = title;
		this.tracklist = new ArrayList<Track>();
	}
	
	public void addTrack(Track track) {
		tracklist.add(track);
	}
	
	public void deleteTrack(Track track) {
		tracklist.remove(track);
	}
	
//	public void addToQueue(Playlist playlist) {
//		ArrayList<Track> newList = playlist.getTracklist();
//		for (Track track : newList)
//			tracklist.add(track);
//	}
	
	public void savePlaylist() {
		ObjectMapper objectMapper = new ObjectMapper();
		ArrayNode jsonArray = null;
		boolean found = false;
		
		try {
			File file = new File("src/assets/playlists.json");
			file.createNewFile();
			
			JsonNode objectNode = objectMapper.readTree(file);
			if (objectNode instanceof ArrayNode)
				jsonArray = (ArrayNode)objectMapper.readTree(file);
			
			if (jsonArray == null) {
				jsonArray = objectMapper.createArrayNode();
			}
			
			int i;
			for (i = 0; i < jsonArray.size(); i++) {
	            JsonNode node = jsonArray.get(i);
				if (node.has("title") && node.get("title").asText().equals(title)) {
					found = true;
					ArrayNode tracksNode = (ArrayNode) node.get("tracks");
	                tracksNode.removeAll();
	                for (String trackPath : getTracksPaths()) {
	                    tracksNode.add(trackPath);
	                }
	                ((ObjectNode) node).set("tracks", tracksNode);
					break;
				}
			}
			
			if (!found) {
				ObjectNode playlistNode = objectMapper.createObjectNode();
	            playlistNode.put("title", getTitle());

	            ArrayNode tracksNode = objectMapper.createArrayNode();
	            for (String trackPath : getTracksPaths()) {
	                tracksNode.add(trackPath);
	            }
	            playlistNode.set("tracks", tracksNode);

	            jsonArray.add(playlistNode);
			}
			
			objectMapper.writeValue(file, jsonArray);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean deletePlaylist(Integer index) {
		ObjectMapper objectMapper = new ObjectMapper();
		ArrayNode jsonArray = null;
		
		try {
			File file = new File("src/assets/playlists.json");
			if (!file.exists())
				return false;
			
			JsonNode objectNode = objectMapper.readTree(file);
			if (objectNode instanceof ArrayNode) {
				jsonArray = (ArrayNode)objectMapper.readTree(file);
			} else {
				return false;
			}
			
			jsonArray.remove(index);
			objectMapper.writeValue(file, jsonArray);
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public ArrayList<Track> getTracklist() {
		return this.tracklist;
	}
	
	public ArrayList<String> getTracksPaths() {
		ArrayList<String> arr = new ArrayList<>();
		for (Track track : tracklist)
			arr.add(track.getFilePath());
		return arr;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public Track getTrackAtIndex(Integer index) {
		for (Integer i = 0; i < tracklist.size(); i++) {
			if (i.equals(index))
				return tracklist.get(i);
		}
		return null;
	}
}
