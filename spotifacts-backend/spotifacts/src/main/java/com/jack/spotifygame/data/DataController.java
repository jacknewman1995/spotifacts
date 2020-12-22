package com.jack.spotifygame.data;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.AudioFeatures;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Track;
import org.apache.hc.core5.http.ParseException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

@RestController
@CrossOrigin(origins = "*")
public class DataController {

    static String CLIENT_ID = "96028d0a2f3640e39287863bdee6db01";
    static String HOST_ADDRESS = "http://localhost:3000/";

    @GetMapping("/topArtists")
    public List<Artist> getTopArtists(@RequestParam String token) throws ParseException, SpotifyWebApiException, IOException {
        SpotifyApi spotifyApi = new SpotifyApi.Builder().setAccessToken(token).build();
        Paging<Artist> topArtists = spotifyApi.getUsersTopArtists().build().execute();
        return Arrays.asList(topArtists.getItems());
    }

    @GetMapping("/name")
    public String getUserName(@RequestParam String token) throws ParseException, SpotifyWebApiException, IOException {
        SpotifyApi spotifyApi = new SpotifyApi.Builder().setAccessToken(token).build();
        return spotifyApi.getCurrentUsersProfile().build().execute().getDisplayName();
    }


    @GetMapping("/login")
    public String getLogin() {
        return "https://accounts.spotify.com/authorize?" +
                "client_id=" + CLIENT_ID +
                "&redirect_uri=" + HOST_ADDRESS +
                "&scope=user-read-private%20user-read-email%20user-top-read" +
                "&response_type=token" +
                "&state=123" +
                "&show_dialog=true";
    }


    @GetMapping("/topTracks")
    public List<Track> getTopTracks(@RequestParam String token) throws ParseException, SpotifyWebApiException, IOException {
        SpotifyApi spotifyApi = new SpotifyApi.Builder().setAccessToken(token).build();
        return Arrays.asList(spotifyApi.getUsersTopTracks().build().execute().getItems());
    }


    @GetMapping("/radarGraph")
    public AudioFeatures getRadarGraph(@RequestParam String token) throws ParseException, SpotifyWebApiException, IOException {
        SpotifyApi spotifyApi = new SpotifyApi.Builder().setAccessToken(token).build();
        String[] ids = Arrays.stream(spotifyApi.getUsersTopTracks().build().execute().getItems()).map(x -> x.getId()).toArray(String[]::new);
        AudioFeaturesCollection audioFeatures = new AudioFeaturesCollection(Arrays.asList(spotifyApi.getAudioFeaturesForSeveralTracks(ids).build().execute()));

        return new AudioFeatures.Builder()
                .setAcousticness(audioFeatures.average(AudioFeatures::getAcousticness))
                .setDanceability(audioFeatures.average(AudioFeatures::getDanceability))
                .setEnergy(audioFeatures.average(AudioFeatures::getEnergy))
                .setInstrumentalness(audioFeatures.average(AudioFeatures::getInstrumentalness))
                .setLiveness(audioFeatures.average(AudioFeatures::getLiveness))
                .setLoudness(-audioFeatures.average(AudioFeatures::getLoudness)/60)
                .setSpeechiness(audioFeatures.average(AudioFeatures::getSpeechiness))
                .build();
    }


    class AudioFeaturesCollection {
        private final Collection<AudioFeatures> audioFeatures;

        AudioFeaturesCollection(Collection<AudioFeatures> audioFeatures) {
            this.audioFeatures = audioFeatures;
        }

        Float average(Function<AudioFeatures, Float> function) {
            return BigDecimal.valueOf(audioFeatures.stream().mapToDouble(x -> function.apply(x)).average().orElse(0D)).floatValue();
        }
    }


}
