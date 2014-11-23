package de.devfestberlin.wearlyrics;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jmusixmatch.MusixMatch;
import org.jmusixmatch.MusixMatchException;
import org.jmusixmatch.entity.track.Track;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private MusixMatch musixMatch;

    private String artist = "Tool";
    private String track = "Jambi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.enableStrictMode();
        this.musixMatch = new MusixMatch("fa8dd8ef6bc0c4555960ead3062c2c28");

            String lyrics = this.fetchLyricsForArtistAndTrack(this.artist, this.track);
            this.createNotificationForTrack(this.artist, this.track, lyrics);


        final EditText editArtist = (EditText) findViewById(R.id.editArtist);
        final EditText editTrack = (EditText) findViewById(R.id.editTrack);
        Button buttonSearch = (Button) findViewById(R.id.buttonSearch);
        final TextView textLyrics = (TextView) findViewById(R.id.textLyrics);

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editArtist.getText().length() <= 0) {
                    Toast.makeText(getApplicationContext(), "Choose an artist.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (editTrack.getText().length() <= 0) {
                    Toast.makeText(getApplicationContext(), "Choose a track.", Toast.LENGTH_SHORT).show();
                    return;
                }

                artist = editArtist.getText().toString();
                track = editTrack.getText().toString();
                String lyrics = fetchLyricsForArtistAndTrack(editArtist.getText().toString(), editTrack.getText().toString());

                if (lyrics == null || lyrics.length() <= 0)
                {
                    Toast.makeText(getApplicationContext(), "Sorry, no lyrics found.", Toast.LENGTH_SHORT).show();
                }

                textLyrics.setText(lyrics);
                createNotificationForTrack(artist, track, lyrics);
            }
        });

    }

    private String fetchLyricsForArtistAndTrack(String artist, String track) {
        List<Track> tracks = null;
        try {
            tracks = this.musixMatch.searchTracks("", artist, track, 1, 1, true);
        } catch (MusixMatchException e) {
            e.printStackTrace();
            return null;
        }

        if(tracks.size() > 0)
        {
            int trackId = tracks.get(0).getTrack().getTrackId();
            try {
                return this.musixMatch.getLyrics(trackId).getLyricsBody();
            } catch (MusixMatchException e) {
                e.printStackTrace();
                return null;
            }
        }

        return null;
    }

    private void createNotificationForTrack(String artist, String track, String lyrics) {
        int notificationID = 1;

        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(getApplicationContext())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentTitle(artist + " - " + track)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentText(lyrics);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(notificationID, notification.build());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void enableStrictMode()
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

}
