package assets;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.*;


import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    static Player player;

    @BeforeAll
    static void backupFiles() {
        File origPlaylists = new File("src/assets/playlists.json");
        File origTracks = new File("src/assets/tracks.json");
        File copyPlaylists = new File("src/assets/_playlists");
        File copyTracks = new File("src/assets/_tracks");
        try {
            if (origPlaylists.exists() && origTracks.exists()) {
                InputStream in = new BufferedInputStream(new FileInputStream(origPlaylists));
                OutputStream out = new BufferedOutputStream(new FileOutputStream(copyPlaylists));

                byte[] buffer = new byte[1024];
                int lengthRead;
                while ((lengthRead = in.read(buffer)) > 0) {
                    out.write(buffer, 0, lengthRead);
                    out.flush();
                }

                in = new BufferedInputStream(new FileInputStream(origTracks));
                out = new BufferedOutputStream(new FileOutputStream(copyTracks));

                while ((lengthRead = in.read(buffer)) > 0) {
                    out.write(buffer, 0, lengthRead);
                    out.flush();
                }
            }

            try (PrintWriter writer = new PrintWriter(origPlaylists)) {
                writer.print("");
            }

            try (PrintWriter writer = new PrintWriter(origTracks)) {
                writer.print("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        player = new Player();

        player.addTrack(new Track());
        player.addTrack(new Track());
        player.addTrack(new Track());

        player.createPlaylist("Test1");
        player.addToPlaylist(1, 1);
        player.addToPlaylist(1, 2);
        player.addToPlaylist(1, 3);

        player.createPlaylist("Test2");
        player.addToPlaylist(2, 1);
        player.addToPlaylist(2, 2);
        player.addToPlaylist(2, 3);
    }

    @AfterAll
    static void restoreFiles() {
        File origPlaylists = new File("src/assets/playlists.json");
        File origTracks = new File("src/assets/tracks.json");
        File copyPlaylists = new File("src/assets/_playlists");
        File copyTracks = new File("src/assets/_tracks");
        try {
            if (origPlaylists.exists() && origTracks.exists()) {
                InputStream in = new BufferedInputStream(new FileInputStream(copyPlaylists));
                OutputStream out = new BufferedOutputStream(new FileOutputStream(origPlaylists));

                byte[] buffer = new byte[1024];
                int lengthRead;
                while ((lengthRead = in.read(buffer)) > 0) {
                    out.write(buffer, 0, lengthRead);
                    out.flush();
                }

                in = new BufferedInputStream(new FileInputStream(copyTracks));
                out = new BufferedOutputStream(new FileOutputStream(origTracks));

                while ((lengthRead = in.read(buffer)) > 0) {
                    out.write(buffer, 0, lengthRead);
                    out.flush();
                }
            }

            if (!copyPlaylists.delete() || !copyTracks.delete())
                System.out.println("Something went wrong trying to delete backup files");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @org.junit.jupiter.api.Test
    void testGetPlaylistAtIndexMethod() {
        int last = player.getPlaylists().size();

        // can get playlist at the start of arraylist
        assertInstanceOf(Playlist.class, player.getPlaylistAtIndex(0));
        // can get playlist at the end of arraylist
        assertInstanceOf(Playlist.class, player.getPlaylistAtIndex(last - 1));
        // can't get playlist out of bounds, returns null
        assertNull(player.getPlaylistAtIndex(last + 1));
    }

    @org.junit.jupiter.api.Test
    void testCreatePlaylistMethod() {
        String title1 = "Test3";
        String title2 = "Test4";

        // can create since no playlist with such name
        assertTrue(player.createPlaylist(title1));
        // can't create since no playlist with such name
        assertFalse(player.createPlaylist(title1));
        // can create since no playlist with such name
        assertTrue(player.createPlaylist(title2));
    }

    @org.junit.jupiter.api.Test
    void testDeletePlaylistMethod() {
        // can delete since there is playlist with such name
        assertTrue(player.deletePlaylist("Test4"));
        // can't delete since playlist with this name was deleted therefore doesn't exist
        assertFalse(player.deletePlaylist("Test4"));

        // can delete since there is playlist with index 3
        assertTrue(player.deletePlaylist(3));
        // can't delete since playlist with index of 3 was deleted therefore doesn't exist
        assertFalse(player.deletePlaylist(3));
    }

    @org.junit.jupiter.api.Test
    void testAddToPlaylistMethod() {
        // can add existing track to existing playlist
        assertTrue(player.addToPlaylist(1, 1));
        // can't add existing track to not existing playlist
        assertFalse(player.addToPlaylist(100, 1));
        // can't add not existing track to existing playlist
        assertFalse(player.addToPlaylist(1, 100));
        // can't add not existing track to not existing playlist
        assertFalse(player.addToPlaylist(100, 100));

        // can add existing track to existing playlist
        assertTrue(player.addToPlaylist("Test1", "N/A"));
        // can't add existing track to not existing playlist
        assertFalse(player.addToPlaylist("Qwerty", "N/A"));
        // can't add not existing track to existing playlist
        assertFalse(player.addToPlaylist("Test1", "Qwerty"));
        // can't add not existing track to not existing playlist
        assertFalse(player.addToPlaylist("Qwerty", "Qwerty"));
    }


    @org.junit.jupiter.api.Test
    void testRemoveFromPlaylistMethod() {
        // can delete existing track from existing playlist
        assertTrue(player.removeFromPlaylist(1, 1));
        // can't delete existing track to not existing playlist
        assertFalse(player.removeFromPlaylist(100, 1));
        // can't delete not existing track to existing playlist
        assertFalse(player.removeFromPlaylist(1, 100));
        // can't delete not existing track to not existing playlist
        assertFalse(player.removeFromPlaylist(100, 100));

        // can delete existing track from existing playlist
        assertTrue(player.removeFromPlaylist("Test1", "N/A"));
        // can't delete existing track to not existing playlist
        assertFalse(player.removeFromPlaylist("Qwerty", "N/A"));
        // can't delete not existing track to existing playlist
        assertFalse(player.removeFromPlaylist("Test1", "Qwerty"));
        // can't delete not existing track to not existing playlist
        assertFalse(player.removeFromPlaylist("Qwerty", "Qwerty"));
    }

}