package app;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Locale;

import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.swing.JButton;
import javax.swing.JFrame;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class Application {
	static String result;

	public static void main(String[] args) throws EngineException {
		// Set property as Kevin Dictionary
		System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us" + ".cmu_us_kal.KevinVoiceDirectory");

		// Register Engine
		Central.registerEngineCentral("com.sun.speech.freetts" + ".jsapi.FreeTTSEngineCentral");

		final Webcam webcam = Webcam.getDefault();
		webcam.setViewSize(WebcamResolution.VGA.getSize());

		JButton takePictureButton = new JButton("Take picture");
		takePictureButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				BufferedImage image = webcam.getImage();
				Tesseract tesseract = new Tesseract();
				tesseract.setDatapath("src/main/resources/tessdata");
				tesseract.setLanguage("eng");
				tesseract.setPageSegMode(1);
				tesseract.setOcrEngineMode(1);
				try {
					result = tesseract.doOCR(image);
					System.out.println(result);
				} catch (TesseractException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		JButton speakButton = new JButton("Speak");
		speakButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					// Create a Synthesizer
					Synthesizer synthesizer = Central.createSynthesizer(new SynthesizerModeDesc(Locale.US));

					// Allocate synthesizer
					synthesizer.allocate();

					// Resume Synthesizer
					synthesizer.resume();

					// Speaks the given text
					// until the queue is empty.
					synthesizer.speakPlainText(result, null);
					synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);

					// Deallocate the Synthesizer.
					// synthesizer.deallocate();
					System.gc();

				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		});

		WebcamPanel panel = new WebcamPanel(webcam);
		panel.setImageSizeDisplayed(true);
		panel.add(takePictureButton);
		panel.add(speakButton);

		JFrame window = new JFrame("Webcam");
		window.add(panel);
		window.setResizable(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.setVisible(true);
	}
}
