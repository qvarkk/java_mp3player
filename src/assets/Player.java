package assets;

import javazoom.jl.player.advanced.PlaybackListener;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class Player extends PlaybackListener {
	private Track currentTrack;
	
	private final ArrayList<Playlist> playlists;
	
	private Integer currentPlaylistIndex;
	private Integer currentTrackIndex;
	private Integer currentFrame = 0;
	private Integer currentTime = 0;
	
	private AdvancedPlayer advancedPlayer;
	private Boolean isPaused = false;
	
	public Player() {
		playlists = new ArrayList<>();
        loadPlaylists();
    }
	
	public ArrayList<Playlist> getPlaylists() {
		return playlists;
	}
	
	public Playlist getPlaylistAtIndex(Integer index) {
		return playlists.get(index);
	}
	
	public Track getCurrentTrack() {
		return currentTrack;
	}
	
	public Playlist getCurrentPlaylist() {
		return playlists.get(currentPlaylistIndex);
	}
	
	public Boolean isPaused() {
		return isPaused;
	}
	
	public Integer getCurrentTrackIndex() {
		return currentTrackIndex;
	}
	
	public String getFormattedCurrentTime() {
		Integer minutes = currentTime / 60000;
		Integer seconds = (currentTime / 1000) % 60;
		
		return String.format("%02d:%02d", minutes, seconds);
	}
	
	public void loadTrack(Track track) {
		currentTrack = track;
		
		if (currentTrack != null) {
			playCurrentTrack();
		}
	}
	
	public void pauseTrack() {
		if (advancedPlayer != null) {
			isPaused = true;
			stopPlaying();
		}
	}
	
	public void stopPlaying() {
		if (advancedPlayer != null) {
			advancedPlayer.stop();
			advancedPlayer.close();
			advancedPlayer = null;
		}
	}
	
	public boolean playCurrentTrack() {
		if (currentTrack == null) 
			return false;
		
		try {
			FileInputStream fileInputStream = new FileInputStream(currentTrack.getFilePath());
			BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
			
			advancedPlayer = new AdvancedPlayer(bufferedInputStream);
			advancedPlayer.setPlayBackListener(this);
			
			startMusicThread();
			startTimeTrackingThread();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private void startMusicThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (isPaused) {
						advancedPlayer.play(currentFrame, Integer.MAX_VALUE);
					} else {
						advancedPlayer.play();
					}
					isPaused = false;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	private void startTimeTrackingThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				currentTime = 0;
				while (!isPaused) {						
					currentTime++;
					try {
						Thread.sleep(1);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	public void loadPlaylists() {
		playlists.clear();
		
		Playlist library = new Playlist("My Library");
		
		ObjectMapper objectMapper = new ObjectMapper();
		ArrayNode trackArray = null;
		ArrayNode playlistArray = null;
		
		try {
			File file = new File("src/assets/tracks.json");
			file.createNewFile();
			
			JsonNode objectNode = objectMapper.readTree(file);
			if (objectNode instanceof ArrayNode)
				trackArray = (ArrayNode)objectMapper.readTree(file);
			
			if (trackArray == null) {
				trackArray = objectMapper.createArrayNode();
			}
			
			for (int i = 0; i < trackArray.size(); i++) {
				String path = trackArray.get(i).asText();
				File f = new File(path);
				
				if (!f.exists()) {
					System.out.println("Couldn't find track at " + path);
					continue;
				}
				
				library.addTrack(new Track(path));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		playlists.add(library);
		currentPlaylistIndex = 0;
		currentTrackIndex = 0;
		currentTrack = library.getTrackAtIndex(0);
		
		
		try {
			Playlist tmpPlaylist = null;
			File file = new File("src/assets/playlists.json");
			file.createNewFile();
			
			JsonNode objectNode = objectMapper.readTree(file);
			if (objectNode instanceof ArrayNode)
				playlistArray = (ArrayNode)objectMapper.readTree(file);
			
			if (playlistArray == null) {
				playlistArray = objectMapper.createArrayNode();
			}
			
			for (JsonNode node : playlistArray) {
				if (node.has("title") && node.has("tracks")) {
					tmpPlaylist = new Playlist(node.get("title").asText());
					
	                JsonNode tracksNode = node.get("tracks");
	                
	                if (tracksNode != null && tracksNode.isArray()) {
	                    for (JsonNode trackNode : tracksNode) {
	                    	String path = trackNode.asText();
	        				File f = new File(path);
	        				
	        				if (!f.exists()) {
	        					System.out.println("Couldn't find track at " + path);
	        					continue;
	        				}
	                    	
	                        tmpPlaylist.addTrack(new Track(path));
	                    }
	                }
	                
	                playlists.add(tmpPlaylist);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void replayCurrent() {
		stopPlaying();
		loadTrack(currentTrack);
	}
	
	public void playPrevious() {
		Track prevTrack = null;
		prevTrack = playlists.get(currentPlaylistIndex).getTrackAtIndex(currentTrackIndex - 1);
		stopPlaying();
		currentTrackIndex--;
		loadTrack(prevTrack);
		
		if (prevTrack == null) {
			currentTrackIndex = playlists.get(currentPlaylistIndex).getTracklist().size() - 1;
			currentTrack = playlists.get(currentPlaylistIndex).getTrackAtIndex(currentTrackIndex);
		}
		
	}
	
	public void playNext() {
		Track nextTrack = null;
		
		nextTrack = playlists.get(currentPlaylistIndex).getTrackAtIndex(currentTrackIndex + 1);
		stopPlaying();
		currentTrackIndex++;
		loadTrack(nextTrack);
		
		if (nextTrack == null) {
			currentTrackIndex = 0;
			currentTrack = playlists.get(currentPlaylistIndex).getTrackAtIndex(0);
		}
	}
	
	public void printPlaylists() {
		for (int i = 1; i < playlists.size(); i++) {
			System.out.println(i + ". " + playlists.get(i).getTitle());
		}
	}
	
	public void printTracks() {
		for (int i = 0; i < playlists.get(0).getTracklist().size(); i++) {
			System.out.println((i + 1) + ". " + playlists.get(0).getTracklist().get(i).getArtist() + " - " + playlists.get(0).getTracklist().get(i).getTitle());
		}
	}
	
	public void printPlaylistsWithTracks() {
		for (int i = 1; i < playlists.size(); i++) {
			System.out.println(i + ". " + playlists.get(i).getTitle());
			for (int j = 0; j < playlists.get(i).getTracklist().size(); j++) {
				Track t = playlists.get(i).getTracklist().get(j);
				System.out.println("\t" + (j + 1) + ". " + t.getArtist() + " - " + t.getTitle());
			}
		}
	}
	
	public boolean createPlaylist(String title) {
		for (Playlist p : playlists) {
			if (p.getTitle().equals(title)) {
				return false;
			}
		}
		playlists.add(new Playlist(title));
		playlists.get(playlists.size() - 1).savePlaylist();
		return true;
	}
	
	public boolean deletePlaylist(String title) {
		for (int i = 1; i < playlists.size(); i++) {
			if (playlists.get(i).getTitle().equals(title)) {
				playlists.get(i).deletePlaylist(i);
				return true;
			}
		}
		return false;
	}
	
	public boolean deletePlaylist(Integer index) {
		for (Integer i = 1; i < playlists.size(); i++) {
			if (i.equals(index)) {
				if (playlists.get(i).deletePlaylist(i - 1)) {
					playlists.remove((int)i);
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean playPlaylist(String title) {
		for (int i = 1; i < playlists.size(); i++) {
			if (playlists.get(i).getTitle().equals(title)) {
				currentPlaylistIndex = i;
				currentTrackIndex = 0;
				stopPlaying();
				loadTrack(playlists.get(i).getTrackAtIndex(0));
				return true;
			}
		}
		return false;
	}
	
	public boolean playPlaylist(Integer index)	 {
		for (Integer i = 1; i < playlists.size(); i++) {
			if (i.equals(index)) {
				currentPlaylistIndex = i;
				currentTrackIndex = 0;
				stopPlaying();
				loadTrack(playlists.get(i).getTrackAtIndex(0));
				return true;
			}
		}
		return false;
	}
	
	public boolean addToPlaylist(Integer pIndex, Integer tIndex) {
		if (tIndex > playlists.get(0).getTracklist().size())
			return false;
		
		for (Integer i = 1; i < playlists.size(); i++) {
			if (i.equals(pIndex)) {
				playlists.get(i).addTrack(playlists.get(0).getTrackAtIndex(tIndex - 1));
				playlists.get(i).savePlaylist();
				return true;
			}
		}
		return false;
	}
	
	public boolean addToPlaylist(String pTitle, String tTitle) {
		for (int i = 1; i < playlists.size(); i++) {
			if (playlists.get(i).getTitle().equals(pTitle)) {
				for (int j = 0; j < playlists.get(0).getTracklist().size(); j++) {
					if (playlists.get(0).getTracklist().get(j).getTitle().equals(tTitle)) {
						playlists.get(i).addTrack(playlists.get(0).getTrackAtIndex(j));
						playlists.get(i).savePlaylist();
					}
				}
				return true;
			}
		}
		return false;
	}
	
	public boolean removeFromPlaylist(Integer pIndex, Integer tIndex) {		
		for (Integer i = 1; i < playlists.size(); i++) {
			if (i.equals(pIndex)) {
				if (tIndex > playlists.get(i).getTracklist().size())
					return false;
					
				playlists.get(i).deleteTrack(playlists.get(i).getTrackAtIndex(tIndex - 1));
				playlists.get(i).savePlaylist();
				return true;
			}
		}
		return false;
	}
	
	public boolean removeFromPlaylist(String pTitle, String tTitle) {
		for (int i = 1; i < playlists.size(); i++) {
			if (playlists.get(i).getTitle().equals(pTitle)) {
				for (int j = 0; j < playlists.get(i).getTracklist().size(); j++) {
					if (playlists.get(i).getTracklist().get(j).getTitle().equals(tTitle)) {
						playlists.get(i).deleteTrack(playlists.get(i).getTrackAtIndex(j));
						playlists.get(i).savePlaylist();
						return true;
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public void playbackFinished(PlaybackEvent evt) {
		if (isPaused) {
			currentFrame += (int)((double)evt.getFrame() * currentTrack.getFramerate());
		}
	}

	@Override
	public void playbackStarted(PlaybackEvent evt) {
		
	}
}
