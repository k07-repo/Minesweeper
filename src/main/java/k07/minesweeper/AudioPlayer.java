package k07.minesweeper;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * Audio player class to play the sound files when necessary
 */
public class AudioPlayer {
    protected AudioInputStream audioInputStream;
    protected Clip clip;

    public AudioPlayer(String filePath) throws Exception {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(filePath);
        audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));

        this.clip = AudioSystem.getClip();
        clip.open(audioInputStream);
    }

    public void soften(float decibels) {
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(-1.0F * decibels);
    }

    public void start() {
        this.clip.setFramePosition(0);
        this.clip.start();

    }

    public void stop() {
        this.clip.stop();
    }
}
