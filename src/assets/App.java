package assets;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.*;

public class App {
	private static void MP3PlayerCLI() {
		Player player = new Player();
		Scanner scanner = new Scanner(System.in);

		while (true) {
			Track curr = player.getCurrentTrack();
			System.out.println("\n\nNow playing: " + (player.getCurrentTrackIndex() + 1) + ". " + curr.getArtist() + " - " + curr.getTitle());
			System.out.println("Length: " + curr.getFormatedLength());
			System.out.println("Playlist: " + player.getCurrentPlaylist().getTitle());
			System.out.println("\n\t--- Menu ---");
			System.out.println("0 - Exit");
			System.out.println("1 - Play Next Track");
			System.out.println("2 - Play Previous Track");
			System.out.println("3 - Play Current Track Again");
			System.out.println("4 - View Track List");
			System.out.println("5 - View Playlists");
			System.out.println("6 - Play Playlist");
			System.out.println("7 - Add Track To Playlist");
			System.out.println("8 - Remove Track From Playlist");
			System.out.println("9 - Create Playlist");
			System.out.println("10 - Delete Playlist");
			int selection = scanner.nextInt();
			int playlist, track;
			String stringBuf;

			switch (selection) {
				case 0:
					player.stopPlaying();
					scanner.close();
					return;
				case 1:
					player.playNext();
					break;
				case 2:
					player.playPrevious();
					break;
				case 3:
					player.replayCurrent();
					break;
				case 4:
					player.printTracks();
					break;
				case 5:
					player.printPlaylistsWithTracks();
					break;
				case 6:
					System.out.println("Enter the number of the playlist: ");
					playlist = scanner.nextInt();
					if (!player.playPlaylist(playlist))
						System.out.println("Wrong playlist number");
					break;
				case 7:
					System.out.println("Enter the number of the playlist: ");
					playlist = scanner.nextInt();
					System.out.println("Enter the number of the track: ");
					track = scanner.nextInt();
					if (!player.addToPlaylist(playlist, track))
						System.out.println("Wrong playlist or track number");
					break;
				case 8:
					System.out.println("Enter the number of the playlist: ");
					playlist = scanner.nextInt();
					System.out.println("Enter the number of the track: ");
					track = scanner.nextInt();
					if (!player.removeFromPlaylist(playlist, track))
						System.out.println("Wrong playlist or track number");
					break;
				case 9:
					System.out.println("Enter the name of the playlist: ");
					scanner.nextLine();
					stringBuf = scanner.nextLine();
					if (!player.createPlaylist(stringBuf))
						System.out.println("Playlist with such name already exists");
					break;
				case 10:
					System.out.println("Enter the number of the playlist: ");
					playlist = scanner.nextInt();
					if (!player.deletePlaylist(playlist))
						System.out.println("Wrong playlist number");
					break;
				default:
					System.out.println("Wrong option!");
					break;
			}
		}
	}

	private static void MP3PlayerGUI() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new MP3PlayerGui().setVisible(true);
			}
		});
	}

	public static void main(String[] args) {
		MP3PlayerGUI();
	}
}
